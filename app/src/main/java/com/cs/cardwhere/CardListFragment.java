package com.cs.cardwhere;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cs.cardwhere.Controller.AppController;
import com.cs.cardwhere.Models.Card;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.android.volley.VolleyLog.TAG;

public class CardListFragment extends Fragment {

    private View view;

    private ArrayList<Card> cards = new ArrayList<>();

    private static String[] Cards= {"Breaking Bad","Rick and Morty", "FRIENDS","Sherlock","Stranger Things"};
    private static final int[] CardsImgs = {R.drawable.ic_account_box_black_24dp,R.drawable.ic_account_box_black_24dp,R.drawable.ic_account_box_black_24dp,R.drawable.ic_account_box_black_24dp,R.drawable.ic_account_box_black_24dp};

    private String cards_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_card_list, container, false);

        // Init Data in Recycler View
        initData(new CallBack() {
            @Override
            public void onSuccess(ArrayList<Card> cards) {
                // Init Recycler View
                initRecyclerView();
            }

            @Override
            public void onFail(String msg) {
                // Do Stuff
            }
        });

        return view;
    }

    private void initData(final CallBack onCallBack) {
        // Tag used to cancel the request
        String tag_json_object = "json_obj_req";
        String url = "https://us-central1-cardwhere.cloudfunctions.net/api/api/v1/cards";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "get result success"+response.toString());
                        cards_data = response.toString();

                        int i = 0;

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

                                Card card = new Card(innerJObject.getString("company"), CardsImgs[i]);
                                cards.add(card);

                                i++;
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

    private void initRecyclerView() {
        RecyclerView cardRecyclerView;
        cardRecyclerView = view.findViewById(R.id.card_recycler_view);
        CardAdapter cardAdapter;
        cardAdapter = new CardAdapter(getActivity(), cards);
        cardRecyclerView.setAdapter(cardAdapter);

        // recycler view setting
        cardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cardRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        // OnClick Listener
        cardAdapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Card data) {
                Toast.makeText(getActivity(),"i am " + data.getCard(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
