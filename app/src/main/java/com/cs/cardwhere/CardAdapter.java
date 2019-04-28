package com.cs.cardwhere;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder>{

    private List<Card> CardList;
    private Context context;

    CardAdapter(Context context, List<Card> CardList) {
        this.context = context;
        this.CardList = CardList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView  = View.inflate(context, R.layout.list_card_item,null);
        return new ViewHolder(itemView );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Card card = CardList.get(position);

        holder.textCard.setText(card.getCard());
        holder.imgCard.setImageResource(card.getCardImage());

    }

    @Override
    public int getItemCount() {
        return CardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView imgCard;
        TextView textCard;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.card_recycler_view);
            imgCard = itemView.findViewById(R.id.imgCard);
            textCard = itemView.findViewById(R.id.textCard);


            // set itemView onClick Listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, CardList.get(getLayoutPosition()));
                    }
                }
            });
        }

    }

    public interface OnItemClickListener {
        void OnItemClick(View view, Card data);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
