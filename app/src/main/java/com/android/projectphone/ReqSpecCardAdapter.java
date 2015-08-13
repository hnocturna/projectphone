package com.android.projectphone;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hnocturna on 7/6/2015.
 */
public class ReqSpecCardAdapter extends RecyclerView.Adapter<ReqSpecCardAdapter.CardViewHolder> {
    private List<CardInfo>  reqSpecCardList;
    // RecyclerItemClickListener.OnItemClickListener mItemClickListener;
    Map<Integer, Integer> colorMap = new LinkedHashMap<>();
    int colorGroup = 0;

    public ReqSpecCardAdapter(List<CardInfo> reqSpecCardList) {
        this.reqSpecCardList = reqSpecCardList;
        colorMap.put(0, R.drawable.circle_red); colorMap.put(1, R.drawable.circle_orange); colorMap.put(2, R.drawable.circle_yellow);
        colorMap.put(3, R.drawable.circle_green); colorMap.put(4, R.drawable.circle_blue); colorMap.put(5, R.drawable.circle_purple);
    }

    @Override
    public int getItemCount() {
        return reqSpecCardList.size();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        CardInfo ci = reqSpecCardList.get(i);
        String title = ci.getTitle();
        List<Map<UserReq, String>> reqSpecList = ci.getReqSpecList();

        Log.d("ReqSpecCardAdapter", "List of requirements: " + reqSpecList.toString());

        // Converts the table name to a suitable name to display be capitalizing each part of the name.
        title = title.replace("_", " ");
        StringBuffer sb = new StringBuffer();
        String[] stringArray = title.split(" ");
        for (String string : stringArray) {
            char[] charArray = string.trim().toCharArray();
            charArray[0] = Character.toUpperCase(charArray[0]);
            string = new String(charArray);
            sb.append(string).append(" ");
        }

        title = sb.toString().trim();
        cardViewHolder.cardSection.setText(title);

        for (int l = 0; l < cardViewHolder.reqSpecTextList.size(); l++) {
            // Erases the values from the recycled view so that there are no extraneous values in the card.
            cardViewHolder.reqSpecCardRLList.get(l).setVisibility(View.VISIBLE);
            cardViewHolder.reqSpecTextList.get(l).setVisibility(View.VISIBLE);
            cardViewHolder.reqSpecTextList.get(l).setText("");
            cardViewHolder.editReqSpecButtonList.get(l).setVisibility(View.VISIBLE);
            cardViewHolder.linkIconList.get(l).setVisibility(View.VISIBLE);
        }

        int j = 0;  // Keeps track of which TextView is being populated.
        for (Map<UserReq, String> cardTextMap : reqSpecList) {
            // Set the text each user requirement.
            for (final UserReq userReq : cardTextMap.keySet()) {
                String cardText = cardTextMap.get(userReq);
                final String category = userReq.getCategory();
                final String spec = userReq.getSpec();
                final String type = userReq.getType();

                cardViewHolder.reqSpecTextList.get(j).setText(cardText);
                if (userReq.getColorGroup() != 6) {
                    Log.d("ReqSpecCardAdapter", "Color group: " + colorGroup + ". Setting background color!");
                    colorGroup = userReq.getColorGroup();
                    cardViewHolder.linkIconList.get(j).setBackgroundResource(colorMap.get(colorGroup));
                } else {
                    cardViewHolder.linkIconList.get(j).setVisibility(View.GONE);
                }
                cardViewHolder.editReqSpecButtonList.get(j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(view.getContext(), SelectSpec.class);
                        intent.putExtra("category", category);
                        intent.putExtra("spec", spec);
                        intent.putExtra("type", type);

                        for (int specGroup : SpecsActivity.specReqMap.keySet()) {
                            List<UserReq> userReqList = new LinkedList<>(SpecsActivity.specReqMap.get(specGroup));
                            if (userReqList.contains(userReq)) {
                                intent.putExtra("specGroup", specGroup);
                            }
                        }

                        if (type.equals("cat") || type.equals("num")) {
                            view.getContext().startActivity(intent);
                        } else {
                            Log.d("ReqSpecCardAdapter", "Unknown type: " + type);
                        }

                        for (int specGroup : SpecsActivity.specReqMap.keySet()) {
                            List<UserReq> userReqList = new LinkedList<>(SpecsActivity.specReqMap.get(specGroup));
                            if (userReqList.contains(userReq)) {
                                userReqList.remove(userReq);
                                SpecsActivity.specReqMap.put(specGroup, userReqList);
                            }
                        }

                        Log.d("ReqSpecCardAdapter", "Category: " + category + " | Spec: " + spec + " | Type: " + userReq.getType());
                    }
                });

                Log.d("ReqSpecCardAdapter", "Card text: " + cardText);
                j++;
            }
        }

        for (int l = 0; l < cardViewHolder.reqSpecTextList.size(); l++) {
            // Makes all textViews without content GONE so that they are hidden from view.
            if (cardViewHolder.reqSpecTextList.get(l).getText().equals("")) {
                cardViewHolder.reqSpecCardRLList.get(l).setVisibility(View.GONE);
                cardViewHolder.reqSpecTextList.get(l).setVisibility(View.GONE);
                cardViewHolder.editReqSpecButtonList.get(l).setVisibility(View.GONE);
                cardViewHolder.linkIconList.get(l).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.req_spec_card_layout, viewGroup, false);
        return new CardViewHolder(itemView);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        protected TextView cardSection;

        protected RelativeLayout reqSpecCardRL1; protected RelativeLayout reqSpecCardRL2; protected RelativeLayout reqSpecCardRL3; protected RelativeLayout reqSpecCardRL4;
        protected RelativeLayout reqSpecCardRL5; protected RelativeLayout reqSpecCardRL6; protected RelativeLayout reqSpecCardRL7; protected RelativeLayout reqSpecCardRL8;
        protected RelativeLayout reqSpecCardRL9; protected RelativeLayout reqSpecCardRL10; protected RelativeLayout reqSpecCardRL11; protected RelativeLayout reqSpecCardRL12;
        // protected RelativeLayout reqSpecCardRL13; protected RelativeLayout reqSpecCardRL14;

        protected TextView reqSpec1Text; protected TextView reqSpec2Text; protected TextView reqSpec3Text; protected TextView reqSpec4Text;
        protected TextView reqSpec5Text; protected TextView reqSpec6Text; protected TextView reqSpec7Text; protected TextView reqSpec8Text;
        protected TextView reqSpec9Text; protected TextView reqSpec10Text; protected TextView reqSpec11Text; protected TextView reqSpec12Text;
        // protected TextView reqSpec13Text; protected TextView reqSpec14Text;

        protected ImageView linkIcon1; protected ImageView linkIcon2; protected ImageView linkIcon3; protected ImageView linkIcon4;
        protected ImageView linkIcon5; protected ImageView linkIcon6; protected ImageView linkIcon7; protected ImageView linkIcon8;
        protected ImageView linkIcon9; protected ImageView linkIcon10; protected ImageView linkIcon11; protected ImageView linkIcon12;
        // protected ImageView linkIcon1; protected ImageView linkIcon1;

        protected ImageView editReqSpecButton1; protected ImageView editReqSpecButton2; protected ImageView editReqSpecButton3; protected ImageView editReqSpecButton4;
        protected ImageView editReqSpecButton5; protected ImageView editReqSpecButton6; protected ImageView editReqSpecButton7; protected ImageView editReqSpecButton8;
        protected ImageView editReqSpecButton9; protected ImageView editReqSpecButton10; protected ImageView editReqSpecButton11; protected ImageView editReqSpecButton12;

        protected List<RelativeLayout> reqSpecCardRLList = new LinkedList<>();
        protected List<TextView> reqSpecTextList = new LinkedList<>();
        protected List<ImageView> editReqSpecButtonList = new LinkedList<>();
        protected List<ImageView> linkIconList = new LinkedList<>();

        public CardViewHolder(View v) {
            super(v);
            cardSection = (TextView) v.findViewById(R.id.card_title);

            reqSpecCardRL1 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl1); reqSpecCardRL2 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl2);
            reqSpecCardRL3 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl3); reqSpecCardRL4 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl4);
            reqSpecCardRL5 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl5); reqSpecCardRL6 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl6);
            reqSpecCardRL7 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl7); reqSpecCardRL8 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl8);
            reqSpecCardRL9 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl9); reqSpecCardRL10 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl10);
            reqSpecCardRL11 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl11); reqSpecCardRL12 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl12);
            // reqSpecCardRL1 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl1); reqSpecCardRL1 = (RelativeLayout) v.findViewById(R.id.req_spec_card_rl1);

            this.reqSpecCardRLList.add(reqSpecCardRL1); this.reqSpecCardRLList.add(reqSpecCardRL2); this.reqSpecCardRLList.add(reqSpecCardRL3); this.reqSpecCardRLList.add(reqSpecCardRL4);
            this.reqSpecCardRLList.add(reqSpecCardRL5); this.reqSpecCardRLList.add(reqSpecCardRL6); this.reqSpecCardRLList.add(reqSpecCardRL7); this.reqSpecCardRLList.add(reqSpecCardRL8);
            this.reqSpecCardRLList.add(reqSpecCardRL9); this.reqSpecCardRLList.add(reqSpecCardRL10); this.reqSpecCardRLList.add(reqSpecCardRL11); this.reqSpecCardRLList.add(reqSpecCardRL12);
            // this.reqSpecCardRLList.add(reqSpecCardRL13); this.reqSpecCardRLList.add(reqSpecCardRL14);

            reqSpec1Text = (TextView) v.findViewById(R.id.card_spec_text1); reqSpec2Text = (TextView) v.findViewById(R.id.card_spec_text2); reqSpec3Text = (TextView) v.findViewById(R.id.card_spec_text3);
            reqSpec4Text = (TextView) v.findViewById(R.id.card_spec_text4); reqSpec5Text = (TextView) v.findViewById(R.id.card_spec_text5); reqSpec6Text = (TextView) v.findViewById(R.id.card_spec_text6);
            reqSpec7Text = (TextView) v.findViewById(R.id.card_spec_text7); reqSpec8Text = (TextView) v.findViewById(R.id.card_spec_text8); reqSpec9Text = (TextView) v.findViewById(R.id.card_spec_text9);
            reqSpec10Text = (TextView) v.findViewById(R.id.card_spec_text10); reqSpec11Text = (TextView) v.findViewById(R.id.card_spec_text11); reqSpec12Text = (TextView) v.findViewById(R.id.card_spec_text12);
            // spec13Text = (TextView) v.findViewById(R.id.card_spec_text13); spec14Text = (TextView) v.findViewById(R.id.card_spec_text14);

            this.reqSpecTextList.add(reqSpec1Text); this.reqSpecTextList.add(reqSpec2Text); this.reqSpecTextList.add(reqSpec3Text); this.reqSpecTextList.add(reqSpec4Text);
            this.reqSpecTextList.add(reqSpec5Text); this.reqSpecTextList.add(reqSpec6Text); this.reqSpecTextList.add(reqSpec7Text); this.reqSpecTextList.add(reqSpec8Text);
            this.reqSpecTextList.add(reqSpec9Text); this.reqSpecTextList.add(reqSpec10Text); this.reqSpecTextList.add(reqSpec11Text); this.reqSpecTextList.add(reqSpec12Text);
            // this.reqSpecTextList.add(reqSpec13Text); this.reqSpecTextList.add(reqSpec14Text);

            editReqSpecButton1 = (ImageView) v.findViewById(R.id.edit_req_button1); editReqSpecButton2 = (ImageView) v.findViewById(R.id.edit_req_button2);
            editReqSpecButton3 = (ImageView) v.findViewById(R.id.edit_req_button3); editReqSpecButton4 = (ImageView) v.findViewById(R.id.edit_req_button4);
            editReqSpecButton5 = (ImageView) v.findViewById(R.id.edit_req_button5); editReqSpecButton6 = (ImageView) v.findViewById(R.id.edit_req_button6);
            editReqSpecButton7 = (ImageView) v.findViewById(R.id.edit_req_button7); editReqSpecButton8 = (ImageView) v.findViewById(R.id.edit_req_button8);
            editReqSpecButton9 = (ImageView) v.findViewById(R.id.edit_req_button9); editReqSpecButton10 = (ImageView) v.findViewById(R.id.edit_req_button10);
            editReqSpecButton11 = (ImageView) v.findViewById(R.id.edit_req_button11); editReqSpecButton12 = (ImageView) v.findViewById(R.id.edit_req_button12);
            // editReqSpecButton1 = (ImageView) v.findViewById(R.id.edit_req_button13); editReqSpecButton1 = (ImageView) v.findViewById(R.id.edit_req_button1);

            this.editReqSpecButtonList.add(editReqSpecButton1); this.editReqSpecButtonList.add(editReqSpecButton2); this.editReqSpecButtonList.add(editReqSpecButton3); this.editReqSpecButtonList.add(editReqSpecButton4);
            this.editReqSpecButtonList.add(editReqSpecButton5); this.editReqSpecButtonList.add(editReqSpecButton6); this.editReqSpecButtonList.add(editReqSpecButton7); this.editReqSpecButtonList.add(editReqSpecButton8);
            this.editReqSpecButtonList.add(editReqSpecButton9); this.editReqSpecButtonList.add(editReqSpecButton10); this.editReqSpecButtonList.add(editReqSpecButton11); this.editReqSpecButtonList.add(editReqSpecButton12);
            // this.editReqSpecButtonList.add(editReqSpecButton14); this.editReqSpecButtonList.add(editReqSpecButton1);

            linkIcon1 = (ImageView) v.findViewById(R.id.link_icon1); linkIcon2 = (ImageView) v.findViewById(R.id.link_icon2); linkIcon3 = (ImageView) v.findViewById(R.id.link_icon3);
            linkIcon4 = (ImageView) v.findViewById(R.id.link_icon4); linkIcon5 = (ImageView) v.findViewById(R.id.link_icon5); linkIcon6 = (ImageView) v.findViewById(R.id.link_icon6);
            linkIcon7 = (ImageView) v.findViewById(R.id.link_icon7); linkIcon8 = (ImageView) v.findViewById(R.id.link_icon8); linkIcon9 = (ImageView) v.findViewById(R.id.link_icon9);
            linkIcon10 = (ImageView) v.findViewById(R.id.link_icon10); linkIcon11 = (ImageView) v.findViewById(R.id.link_icon11); linkIcon12 = (ImageView) v.findViewById(R.id.link_icon12);
            // linkIcon13 = (ImageView) v.findViewById(R.id.link_icon13); linkIcon14 = (ImageView) v.findViewById(R.id.link_icon14);\

            this.linkIconList.add(linkIcon1); this.linkIconList.add(linkIcon2); this.linkIconList.add(linkIcon3); this.linkIconList.add(linkIcon4);
            this.linkIconList.add(linkIcon5); this.linkIconList.add(linkIcon6); this.linkIconList.add(linkIcon7); this.linkIconList.add(linkIcon8);
            this.linkIconList.add(linkIcon9); this.linkIconList.add(linkIcon10); this.linkIconList.add(linkIcon11); this.linkIconList.add(linkIcon12);
            // this.reqSpecTextList.add(reqSpec13Text); this.reqSpecTextList.add(reqSpec14Text);

            // v.setOnClickListener(this);
        }

        /* @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.OnItemClick(v, getPosition());
            }
        } */
    }
}