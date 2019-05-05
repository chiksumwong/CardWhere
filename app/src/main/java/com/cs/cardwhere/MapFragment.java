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

import com.cs.cardwhere.Controller.CallBack;
import com.cs.cardwhere.Controller.CardController;
import com.cs.cardwhere.Models.Card;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class MapFragment extends Fragment {
    View view;

    private ArrayList<Card> cards = new ArrayList<>();

    double mLatitude;
    double mLongitude;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        CardController cardController = new CardController();

        // get current user id
        SharedPreferences sharedPreferences;
        sharedPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String userId = sharedPreferences.getString("USER_ID", "");

        // Init Data in Recycler View
        cardController.getCards(userId, new CallBack() {
            @Override
            public void onSuccess(ArrayList<Card> CardsList) {
                cards = CardsList;
                // Init Map
                setMap();
            }
            @Override
            public void onFail(String msg) {
                Log.d(TAG, "get result fail" + msg);
            }
        });
        return view;
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
                        .zoom(12)
                        .bearing(0)
                        .tilt(45)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null);

                // mark all the location of card's company
                for (int i=0; i < cards.size(); i++){
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(cards.get(i).getLatitude(),cards.get(i).getLongitude()))
                            .title(cards.get(i).getName())
                            .snippet(cards.get(i).getCompany()));
                }
            }
        });
    }
}
