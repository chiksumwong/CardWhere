package com.cs.cardwhere.Controller;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cs.cardwhere.Bean.CardBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class CardController {

    public CardController(){

    }

    public void getCards(final String userId, final CallBack onCallBack) {
        String url = "https://us-central1-cardwhere.cloudfunctions.net/api/api/v1/cards";
        final ArrayList<CardBean> cards = new ArrayList<>();

        // get request
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
                                    CardBean card = new CardBean();
                                    card.setCardId(key);
                                    card.setCompany(innerJObject.getString("company"));
                                    card.setName(innerJObject.getString("name"));
                                    card.setTel(innerJObject.getString("tel"));
                                    card.setEmail(innerJObject.getString("email"));
                                    card.setAddress(innerJObject.getString("address"));
                                    card.setUserId(innerJObject.getString("user_id"));
                                    card.setImageUri(innerJObject.getString("image_url"));
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
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "json_obj_req");
    }

    public void addCard(CardBean card) {
        String url = "https://us-central1-cardwhere.cloudfunctions.net/api/api/v1/card";
        final String requestBody;

        // body
        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("user_id", card.getUserId());
            jsonBodyObj.put("company", card.getCompany());
            jsonBodyObj.put("name", card.getName());
            jsonBodyObj.put("tel", card.getTel());
            jsonBodyObj.put("email", card.getEmail());
            jsonBodyObj.put("address", card.getAddress());
            jsonBodyObj.put("image_url", card.getImageUri());
            jsonBodyObj.put("latitude", card.getLatitude());
            jsonBodyObj.put("longitude", card.getLongitude());
        }catch (JSONException e){
            Log.d(TAG, "addCardRequest: add address go wrong");
            e.printStackTrace();
        }
        requestBody = jsonBodyObj.toString();


        // sent request
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
            public Map<String, String> getHeaders() {
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
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "json_obj_request");
    }

    public void deleteCard(String cardId){
        String url = "https://us-central1-cardwhere.cloudfunctions.net/api/api/v1/card/" + cardId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url,null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d(TAG, "delete card success :" +response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "delete card fail :" + error.toString());
                    }
                }
        );
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "json_obj_request");
    }


}
