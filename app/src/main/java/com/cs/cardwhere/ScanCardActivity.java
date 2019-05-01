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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cs.cardwhere.Controller.AppController;
import com.cs.cardwhere.GraphicUtils.CloudTextGraphic;
import com.cs.cardwhere.GraphicUtils.GraphicOverlay;
import com.cs.cardwhere.GraphicUtils.TextGraphic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanCardActivity extends AppCompatActivity {

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
    GraphicOverlay mGraphicOverlay;

    // Max width (portrait mode)
    private Integer mImageMaxWidth = 640;
    // Max height (portrait mode)
    private Integer mImageMaxHeight = 480;

    ArrayList<String> outputLine = new ArrayList<>();

    String inputCompany = "";
    String inputName = "";
    String inputTel = "";
    String inputEmail = "";
    String inputAddress = "";

    Uri imageUpload;
    String imageUrl;

    private static final String TAG = "ScanCardActivity";

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

        mGraphicOverlay = findViewById(R.id.graphic_overlay);

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
        String [] options = {"Camera", "Gallery"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        // set dialog's title
        dialog.setTitle("Get Card From?");
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

                // Resize bitmap
                mSelectedImage = bitmap;

                int targetWidth = mImageMaxWidth;
                int maxHeight = mImageMaxHeight;

                float scaleFactor =
                        Math.max(
                                (float) mSelectedImage.getWidth() / (float) targetWidth,
                                (float) mSelectedImage.getHeight() / (float) maxHeight);

                Bitmap resizedBitmap =
                        Bitmap.createScaledBitmap(
                                mSelectedImage,
                                (int) (mSelectedImage.getWidth() / scaleFactor),
                                (int) (mSelectedImage.getHeight() / scaleFactor),
                                true);

                cardIv.setImageBitmap(resizedBitmap);


                // set mSelectedImage then run Google ML kit Text Recognizer
                mSelectedImage = resizedBitmap;
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
            showToast("No text found");
            Log.d("TAG", "No text found");
            return;
        }

        // clear text previously display on the screen
        mGraphicOverlay.clear();

        //blocks
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            // lines
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();

                // get each line for auto input
                outputLine.add(lines.get(j).getText());

                //element
                for (int k = 0; k < elements.size(); k++) {
                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);
                }
            }
        }

        // Get Text
        if(outputLine.size() > 0){
            inputCompany = outputLine.get(0);
            inputName = outputLine.get(1);
            inputTel = outputLine.get(2);
            inputEmail = outputLine.get(3);
            inputAddress = outputLine.get(4);
        }

        // Set Text
        companyEt.setText(inputCompany);
        nameEt.setText(inputName);
        telEt.setText(inputTel);
        emailEt.setText(inputEmail);
        addressEt.setText(inputAddress);

        // Connect to API
        AddCard();
    }

    private String requestBody;

    private void AddCard(){

        // init CLOUDINARY for upload card image
        MediaManager.init(this);

        String requestId = MediaManager.get().upload(imageUpload)
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
                        addCardRequest();
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
    }

    private void addCardRequest(){
        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("user_id", getUserIdFromLocalStorage());
            jsonBodyObj.put("company", inputCompany);
            jsonBodyObj.put("name", inputName);
            jsonBodyObj.put("tel", inputTel);
            jsonBodyObj.put("email", inputEmail);
            jsonBodyObj.put("address", inputAddress);
            jsonBodyObj.put("image_url", imageUrl);
        }catch (JSONException e){
            e.printStackTrace();
        }

        requestBody = jsonBodyObj.toString();

        // Tag used to cancel the request
        String tag_json_object = "json_obj_request";
        String url = "https://us-central1-cardwhere.cloudfunctions.net/api/api/v1/card";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "add card success :" +response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "add card fail :" + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            public byte[] getBody() {
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }


        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_object);
    }

    private String getUserIdFromLocalStorage(){
        String userId;
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        userId = sharedPreferences.getString("USER_ID", "");

        return userId;
    }

    // Cloud Text Recognition
    private void runCloudTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionDocumentText>() {
                            @Override
                            public void onSuccess(FirebaseVisionDocumentText texts) {
                                processCloudTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });
    }

    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        // Task completed successfully
        if (text == null) {
            showToast("No text found");
            return;
        }
        mGraphicOverlay.clear();
        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {
                    CloudTextGraphic cloudDocumentTextGraphic = new CloudTextGraphic(mGraphicOverlay,
                            words.get(l));
                    mGraphicOverlay.add(cloudDocumentTextGraphic);
                }
            }
        }
    }


    // Helper Functions
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
