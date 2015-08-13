package com.android.projectphone;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hnocturna on 7/6/2015.
 */
public class SpecCardAdapter extends RecyclerView.Adapter<SpecCardAdapter.CardViewHolder> {
    private List<CardInfo> specCardList;
    OnItemClickListener mItemClickListener;

    public SpecCardAdapter(List<CardInfo> specCardList) {
        this.specCardList = new LinkedList<>(specCardList);
    }

    @Override
    public int getItemCount() {
        return specCardList.size();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        CardInfo ci = specCardList.get(i);
        String title = ci.getTitle();
        String section = ci.getTitle();
        List<String> specList = ci.getSpecList();
        Map<String, String> phoneSpecMap = ci.getPhoneSpecMap();

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

        for (int l = 0; l < cardViewHolder.specTextViewList.size(); l++) {
            // Erases the values from the recycled view so that there are no extraneous values in the card.
            cardViewHolder.specTextViewList.get(l).setVisibility(View.VISIBLE);
            cardViewHolder.specTextViewList.get(l).setText("");
            cardViewHolder.phoneSpecTextViewList.get(l).setVisibility(View.VISIBLE);
            cardViewHolder.phoneSpecTextViewList.get(l).setText("");
        }

        int j = 0;
        for (String spec : ci.getSpecList()) {
            // Set the text for the specs of each card.
            // Log.d("SpecCardAdapter", ci.getSpecList().toString());
            if (spec.equals("phone_model")) {
                continue;
            } else if (phoneSpecMap.get(section+spec).equals("No")) {
                continue;
            } else {
                cardViewHolder.specTextViewList.get(j).setText(spec.replaceAll("_", " "));
                // Log.d("SpecCardAdapter", "Setting " + spec + " attributes!");
                cardViewHolder.phoneSpecTextViewList.get(j).setText(phoneSpecMap.get(section + spec));
                j++;
            }
        }

        for (int l = 0; l < cardViewHolder.specTextViewList.size(); l++) {
            // Makes all textViews without content GONE so that they are hidden from view.
            if (cardViewHolder.specTextViewList.get(l).getText().equals("")) {
                cardViewHolder.specTextViewList.get(l).setVisibility(View.GONE);
                cardViewHolder.phoneSpecTextViewList.get(l).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.spec_card_layout, viewGroup, false);
        return new CardViewHolder(itemView);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView cardSection;

        protected TextView spec1Text; protected TextView spec2Text; protected TextView spec3Text; protected TextView spec4Text;
        protected TextView spec5Text; protected TextView spec6Text; protected TextView spec7Text; protected TextView spec8Text;
        protected TextView spec9Text; protected TextView spec10Text; protected TextView spec11Text; protected TextView spec12Text;
        protected TextView spec13Text; protected TextView spec14Text;

        protected TextView phoneSpec1Text; protected TextView phoneSpec2Text; protected TextView phoneSpec3Text; protected TextView phoneSpec4Text;
        protected TextView phoneSpec5Text; protected TextView phoneSpec6Text; protected TextView phoneSpec7Text; protected TextView phoneSpec8Text;
        protected TextView phoneSpec9Text; protected TextView phoneSpec10Text; protected TextView phoneSpec11Text; protected TextView phoneSpec12Text;
        protected TextView phoneSpec13Text; protected TextView phoneSpec14Text;

        protected List<TextView> specTextViewList = new LinkedList<>();
        protected List<TextView> phoneSpecTextViewList = new LinkedList<>();

        public CardViewHolder(View v) {
            super(v);
            cardSection = (TextView) v.findViewById(R.id.card_title);

            spec1Text = (TextView) v.findViewById(R.id.spec_text1); spec2Text = (TextView) v.findViewById(R.id.spec_text2); spec3Text = (TextView) v.findViewById(R.id.spec_text3);
            spec4Text = (TextView) v.findViewById(R.id.spec_text4); spec5Text = (TextView) v.findViewById(R.id.spec_text5); spec6Text = (TextView) v.findViewById(R.id.spec_text6);
            spec7Text = (TextView) v.findViewById(R.id.spec_text7); spec8Text = (TextView) v.findViewById(R.id.spec_text8); spec9Text = (TextView) v.findViewById(R.id.spec_text9);
            spec10Text = (TextView) v.findViewById(R.id.spec_text10); spec11Text = (TextView) v.findViewById(R.id.spec_text11); spec12Text = (TextView) v.findViewById(R.id.spec_text12);
            spec13Text = (TextView) v.findViewById(R.id.spec_text13); spec14Text = (TextView) v.findViewById(R.id.spec_text14);

            this.specTextViewList.add(spec1Text); this.specTextViewList.add(spec2Text); this.specTextViewList.add(spec3Text); this.specTextViewList.add(spec4Text);
            this.specTextViewList.add(spec5Text); this.specTextViewList.add(spec6Text); this.specTextViewList.add(spec7Text); this.specTextViewList.add(spec8Text);
            this.specTextViewList.add(spec9Text); this.specTextViewList.add(spec10Text); this.specTextViewList.add(spec11Text); this.specTextViewList.add(spec12Text);
            this.specTextViewList.add(spec13Text); this.specTextViewList.add(spec14Text);

            phoneSpec1Text = (TextView) v.findViewById(R.id.phone_spec_text1); phoneSpec2Text = (TextView) v.findViewById(R.id.phone_spec_text2);
            phoneSpec3Text = (TextView) v.findViewById(R.id.phone_spec_text3); phoneSpec4Text = (TextView) v.findViewById(R.id.phone_spec_text4);
            phoneSpec5Text = (TextView) v.findViewById(R.id.phone_spec_text5); phoneSpec6Text = (TextView) v.findViewById(R.id.phone_spec_text6);
            phoneSpec7Text = (TextView) v.findViewById(R.id.phone_spec_text7); phoneSpec8Text = (TextView) v.findViewById(R.id.phone_spec_text8);
            phoneSpec9Text = (TextView) v.findViewById(R.id.phone_spec_text9); phoneSpec10Text = (TextView) v.findViewById(R.id.phone_spec_text10);
            phoneSpec11Text = (TextView) v.findViewById(R.id.phone_spec_text11); phoneSpec12Text = (TextView) v.findViewById(R.id.phone_spec_text12);
            phoneSpec13Text = (TextView) v.findViewById(R.id.phone_spec_text13); phoneSpec14Text = (TextView) v.findViewById(R.id.phone_spec_text14);

            this.phoneSpecTextViewList.add(phoneSpec1Text); this.phoneSpecTextViewList.add(phoneSpec2Text); this.phoneSpecTextViewList.add(phoneSpec3Text); this.phoneSpecTextViewList.add(phoneSpec4Text);
            this.phoneSpecTextViewList.add(phoneSpec5Text); this.phoneSpecTextViewList.add(phoneSpec6Text); this.phoneSpecTextViewList.add(phoneSpec7Text); this.phoneSpecTextViewList.add(phoneSpec8Text);
            this.phoneSpecTextViewList.add(phoneSpec9Text); this.phoneSpecTextViewList.add(phoneSpec10Text); this.phoneSpecTextViewList.add(phoneSpec11Text); this.phoneSpecTextViewList.add(phoneSpec12Text);
            this.phoneSpecTextViewList.add(phoneSpec13Text); this.phoneSpecTextViewList.add(phoneSpec14Text);

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
