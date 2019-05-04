package com.cs.cardwhere;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cs.cardwhere.Controller.AppController;
import com.cs.cardwhere.Models.Card;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.android.volley.VolleyLog.TAG;

public class MapsFragment extends Fragment {
    View view;

    private ArrayList<Card> cards = new ArrayList<>();

    double mLatitude;
    double mLongitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        // Init Data in Recycler View
        initData(new CardListFragment.CallBack() {
            @Override
            public void onSuccess(ArrayList<Card> cards) {
                // Init Map
                setMap();
            }

            @Override
            public void onFail(String msg) {
                // Do Stuff
            }
        });


        return view;
    }

    private void initData(final CardListFragment.CallBack onCallBack) {
        // get current user id
        SharedPreferences sharedPreferences;
        sharedPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String userId = sharedPreferences.getString("USER_ID", "");

        // Tag used to cancel the request
        String tag_json_object = "json_obj_req";
        String url = "https://us-central1-cardwhere.cloudfunctions.net/api/api/v1/cards";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "get result success"+response.toString());
                        String cards_data = response.toString();
                        try
                        {
                            JSONObject jObject= new JSONObject(cards_data);

                            Iterator<String> keys = response.keys();
                            while( keys.hasNext() ) {
                                String key = keys.next();
                                Log.v("**********", "**********");
                                Log.v("firebase id key", key);

                                JSONObject innerJObject = jObject.getJSONObject(key);
                                Log.d(TAG, "innerJobject: " + innerJObject.toString());

                                if (userId.equals(innerJObject.getString("user_id"))) {
                                    Card card = new Card();
                                    card.setCompany(innerJObject.getString("company"));
                                    card.setName(innerJObject.getString("name"));
                                    card.setLatitude(innerJObject.getDouble("latitude"));
                                    card.setLongitude(innerJObject.getDouble("longitude"));
                                    cards.add(card);
                                }
                            }
                            onCallBack.onSuccess(cards);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onCallBack.onFail(e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "get result fail" + error.toString());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_object);
    }

    public interface CallBack {
        void onSuccess(ArrayList<Card> detailsMovies);
        void onFail(String msg);
    }

    private void setMap(){
        // get current location
        SharedPreferences sharedPreferences;
        sharedPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        mLatitude = Double.parseDouble(sharedPreferences.getString("LOCATION_LATITUDE", "0"));
        mLongitude = Double.parseDouble(sharedPreferences.getString("LOCATION_LONGITUDE", "0"));

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_fragment);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(new LatLng(mLatitude,mLongitude))
                        .zoom(10)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

                for (int i=0; i < cards.size(); i++){
                    // mark all the location of card's company
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(cards.get(i).getLatitude(),cards.get(i).getLongitude()))
                            .title(cards.get(i).getName())
                            .snippet(cards.get(i).getCompany()));
                }

            }
        });
    }
}
