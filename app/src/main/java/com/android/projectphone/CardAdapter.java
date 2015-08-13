package com.android.projectphone;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hnocturna on 6/3/2015.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<CardInfo> cardInfoList;
    OnItemClickListener mItemClickListener;

    public CardAdapter(List<CardInfo> cardInfoList) {
        this.cardInfoList = new LinkedList<CardInfo>(cardInfoList);
    }

    @Override
    public int getItemCount() {
        return cardInfoList.size();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        CardInfo ci = cardInfoList.get(i);
        if (ci.getClicked()) {
            cardViewHolder.clickedIcon.setVisibility(View.VISIBLE);
        } else {
            cardViewHolder.clickedIcon.setVisibility(View.GONE);
        }
        cardViewHolder.cardTitle.setText(ci.getTitle());
        cardViewHolder.cardText.setText(ci.getText());
        cardViewHolder.cardIcon.setImageResource(ci.getIcon());
        Log.d("Image", String.valueOf(ci.getIcon()));
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.cardlayout, viewGroup, false);
        return new CardViewHolder(itemView);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView cardTitle;
        protected TextView cardText;
        protected ImageView cardIcon;
        protected RelativeLayout cardLayout;
        protected ImageView clickedIcon;

        public CardViewHolder(View v) {
            super(v);
            cardTitle = (TextView) v.findViewById(R.id.card_title);
            cardText = (TextView) v.findViewById(R.id.card_text);
            cardIcon = (ImageView) v.findViewById(R.id.card_icon);
            cardLayout = (RelativeLayout) v.findViewById(R.id.card_rl);
            clickedIcon = (ImageView) v.findViewById(R.id.clicked_icon);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.OnItemClick(v, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void OnItemClick(View v, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
}
