package com.cs.cardwhere;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cs.cardwhere.Controller.CardController;
import com.cs.cardwhere.Bean.CardBean;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScanCardActivity extends AppCompatActivity {

    private static final String TAG = "ScanCardActivity";

    EditText nameEt;
    EditText companyEt;
    EditText telEt;
    EditText addressEt;
    EditText emailEt;
    ImageView cardIv;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    private Bitmap mSelectedImage;

    ArrayList<String> outputLine = new ArrayList<>();

    CardBean card = new CardBean();

    Uri imageUpload;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_card);

        companyEt = findViewById(R.id.et_company);
        nameEt = findViewById(R.id.et_name);
        telEt = findViewById(R.id.et_tel);
        emailEt = findViewById(R.id.et_email);
        addressEt = findViewById(R.id.et_address);
        cardIv = findViewById(R.id.iv_card_image);

        // Camera Permission
        cameraPermission = new String [] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //Storage Permission
        storagePermission = new String [] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }


    // actionbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_card_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle actionbar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done_button:
                // Connect to API
                AddCard();
                // Go to home page
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.camera_button:
                showImageImportDialog();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


    private void showImageImportDialog(){
        String Camera = this.getString(R.string.scan_camera);
        String Gallery = this.getString(R.string.scan_gallery);
        String messageTitle = this.getString(R.string.scan_message);


        String [] options = {Camera, Gallery};

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        // set dialog's title
        dialog.setTitle(messageTitle);
        dialog.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){
                    // select camera
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickCamera();
                    }
                }

                if(which == 1){
                    // select gallery
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickGallery();
                    }
                }

            }
        });
        dialog.create().show();
    }

    private void pickGallery() {
        // intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "CardWhere"); // title of picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Scan by CardWhere"); // description of picture
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result_storage_permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result_storage_permission;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result_camera_permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        boolean result_storage_permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result_camera_permission && result_storage_permission;
    }

    // handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if(grantResults.length >0){

                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (writeStorageAccepted){
                        pickGallery();
                    }else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    // handle image result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // got image from camera
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
        }

        //got cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                // get image uri
                Uri resultUri = result.getUri();
                imageUpload = resultUri;

                // set image to image view
                cardIv.setImageURI(resultUri);

                // get drawable bitmap from text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) cardIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                // text recognition
                mSelectedImage = bitmap;
                runTextRecognition();

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                // if there is any error
                Exception error = result.getError();
                Toast.makeText(this, " "+ error, Toast.LENGTH_LONG).show();
            }
        }
    }


    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);

        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {

        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();

        if (blocks.size() == 0) {
            Toast.makeText(this, "No text found", Toast.LENGTH_LONG).show();
            Log.d("TAG", "No text found");
            return;
        }

        //blocks
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            // lines
            for (int j = 0; j < lines.size(); j++) {
                // get each line for auto input
                outputLine.add(lines.get(j).getText());
            }
        }

        // Get Text
        if(outputLine.size() > 0){
            card.setCompany(outputLine.get(0));
            card.setName(outputLine.get(1));
            card.setTel(outputLine.get(2));
            card.setEmail(outputLine.get(3));
            card.setAddress(outputLine.get(4));
        }

        // Set Text
        companyEt.setText(card.getCompany());
        nameEt.setText(card.getName());
        telEt.setText(card.getTel());
        emailEt.setText(card.getEmail());
        addressEt.setText(card.getAddress());
    }


    private void AddCard(){
        final Context context = this;

        // get address latitude and longitude
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> geoResults = geoCoder.getFromLocationName(card.getAddress(), 1);
            while (geoResults.size()==0) {
                geoResults = geoCoder.getFromLocationName(card.getAddress(), 1);
            }
            if (geoResults.size()>0) {
                Address address = geoResults.get(0);
                card.setLatitude(address.getLatitude());
                card.setLongitude(address.getLongitude());
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        if(imageUpload != null){
            // init Cloudinary for upload card image
            MediaManager.init(this);

            // upload card to Cloudinary
            MediaManager.get().upload(imageUpload)
                    .unsigned("drfll21r")
                    .option("resource_type", "image")
                    .option("folder", "CardWhere")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d(TAG, "onStart: Image Upload");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            imageUrl = resultData.get("url").toString();
                            Log.d(TAG, "Image upload success: result Url :" + imageUrl);
                            card.setImageUri(imageUrl);
                            card.setUserId(getUserIdFromLocalStorage());
                            //connect Api
                            CardController cardController = new CardController(context);
                            cardController.addCard(card);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.d(TAG, "onError: image upload" + error.getDescription());
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                        }
                    })
                    .dispatch();
        }else {
            //connect Api
            CardController cardController = new CardController(context);
            cardController.addCard(card);
        }


    }

    private String getUserIdFromLocalStorage(){
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getString("USER_ID", "");
    }

}
