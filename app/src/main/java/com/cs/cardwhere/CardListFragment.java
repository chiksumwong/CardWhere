package com.cs.cardwhere;

import android.os.Bundle;
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

import com.cs.cardwhere.Models.Card;

import java.util.ArrayList;

public class CardListFragment extends Fragment {

    View view;
    RecyclerView cardRecyclerView;

    ArrayList<Card> cards = new ArrayList<>();
    public static final String[] Cards= {"Breaking Bad","Rick and Morty", "FRIENDS","Sherlock","Stranger Things"};
    public static final int[] CardsImgs = {R.drawable.ic_account_box_black_24dp,R.drawable.ic_account_box_black_24dp,R.drawable.ic_account_box_black_24dp,R.drawable.ic_account_box_black_24dp,R.drawable.ic_account_box_black_24dp};

    CardAdapter cardAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_card_list, container, false);

        initRecyclerView();
        initData();

        return view;
    }

    private void initData() {

        for(int i=0;i<Cards.length;i++){
            Card card = new Card(Cards[i], CardsImgs[i]);
            cards.add(card);
        }

    }

    private void initRecyclerView() {
        cardRecyclerView = view.findViewById(R.id.card_recycler_view);
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
