package com.android.projectphone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by Hnocturna on 6/3/2015.
 */
public class ThumbnailCardAdapter extends RecyclerView.Adapter<ThumbnailCardAdapter.CardViewHolder> {
    private List<CardInfo> cardInfoList;
    OnItemClickListener mItemClickListener;
    private ThumbnailCardLoader thumbnailCardLoader;

    public ThumbnailCardAdapter(ThumbnailCardLoader thumbnailCardLoader) {
        this.thumbnailCardLoader = thumbnailCardLoader;
    }

    @Override
    public int getItemCount() {
        return thumbnailCardLoader.getPhoneCardListSize();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        CardInfo ci = thumbnailCardLoader.getCurrentPhoneCard(i);

        String phone = ci.getTitle();
        String imageURL = ci.getImageURL();

        ThumbnailCardAdapterParams thumbnailCardAdapterParams = new ThumbnailCardAdapterParams(phone, imageURL, cardViewHolder.phoneThumbnail);

        cardViewHolder.phoneTitle.setText(phone.replace("_", " "));
        cardViewHolder.phoneThumbnail.setBackgroundResource(R.drawable.white_rectangle);

        if (getBitmapFromMemCache(phone) != null) {
            cardViewHolder.phoneThumbnail.setImageBitmap(getBitmapFromMemCache(phone));
        } else {
            new ThumbnailDownload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, thumbnailCardAdapterParams);
        }

    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.thumbnail_card, viewGroup, false);
        return new CardViewHolder(itemView);
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView phoneTitle;
        protected ImageView phoneThumbnail;
        protected android.support.v7.widget.CardView cardView;

        public CardViewHolder(View v) {
            super(v);
            cardView = (android.support.v7.widget.CardView) v.findViewById(R.id.thumbnail_card);
            phoneTitle = (TextView) v.findViewById(R.id.phone_title_text);
            phoneThumbnail = (ImageView) v.findViewById(R.id.phone_image);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.OnItemClick(v, getPosition());
            }
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return MainActivity.memCache.get(key);
    }

    public void addBitmapToMemCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            MainActivity.memCache.put(key, bitmap);
        }
    }

    public class ThumbnailDownload extends AsyncTask<ThumbnailCardAdapterParams, Void, ThumbnailCardAdapterParams> {
        String phone;
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ThumbnailCardAdapterParams doInBackground(ThumbnailCardAdapterParams...params) {
            Bitmap bmp = null;

            String phone = params[0].getPhone();
            String imageURL = params[0].getImageURL();


            if (getBitmapFromMemCache(phone) != null) {
                // Skip downloading phone thumbnail if it already exists. (Secondary check).
                Log.d("ThumbnailAdapter.Download", "Thumbnail download skipped!");
                return null;
            }

            Log.d("SpecsPhoneResults", phone + " thumbnail downloading!");

            try {
                URL url = new URL(imageURL); // Turns the phone's image URL string to URL
                Log.d("ImageURL", imageURL);
                try {
                    // Downloads and saves each bitmap to an ArrayList to be accessed when creating buttons
                    URLConnection conn = url.openConnection();
                    // Log.d("Conn URL", conn.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();    // Open a connection and store it's value
                    connection.setDoInput(true);                                                // Confirms read from website.
                    connection.connect();                                                       // Connect to the previously defined connection
                    InputStream bmpStream = conn.getInputStream();                              // Store InputStream as bmpStream
                    bmp = BitmapFactory.decodeStream(bmpStream);                                // Grab the bitmap
                    // phoneBitmapArray.add(bmp);                                               // Add bitmap to an array to be accessed when creating buttons

                    // BitmapDrawable bmpDrawable = new BitmapDrawable(bmp);                    // An attempt to change to drawable to image can be set as background w/ overlaying text
                    bmpStream.close(); // Close the InputStream

                } catch (IOException ioe) {
                    // Catch any IOE
                    Log.d("Image Download", "IO Exception!");
                    ioe.printStackTrace(); // Debug purposes
                }
            } catch (MalformedURLException mue) {
                Log.d("ThumbnailAdapter", "Malformed URL");
            }

            if (bmp != null) {
                addBitmapToMemCache(phone, bmp);
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(ThumbnailCardAdapterParams params) {
            if (params != null) {
                String phone = params.getPhone();
                String imageURL = params.getImageURL();
                ImageView imageView = params.getImageView();

                if (getBitmapFromMemCache(phone) != null) {
                    Log.i("ThumbnailAdapter", phone + " bitmap set!");
                    imageView.setImageBitmap(getBitmapFromMemCache(phone));          // Set imageView as the thumbnail downloaded above and stored in the ThumbnailMap.
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);         // Still figuring this out, but ideally will blow up the image a bit to fit the frame.
                    // imageView.setPadding(8, 8, 8, 8);                                // Set a small padding around the images so they don't overlap.
                } else {
                    new ThumbnailDownload().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
                    imageView.setImageResource(R.drawable.white_rectangle);
                    Log.i("ThumbnailAdapter", "No thumbnail found for " + phone);
                }
                /* if (Phone.phoneThumbnailMap.get(phone) != null) {
                    Log.d("ThumbnailAdapter", "Phone: " + phone + " thumbnail set!");
                    viewHolder.imageView.setImageBitmap(Phone.phoneThumbnailMap.get(phone));    // Set imageView as the thumbnail downloaded above and stored in the ThumbnailMap.
                    viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);         // Still figuring this out, but ideally will blow up the image a bit to fit the frame.
                    viewHolder.imageView.setPadding(8, 8, 8, 8);                                // Set a small padding around the images so they don't overlap.
                } else {
                    Log.d("ThumbnailAdapter", "Phone: " + phone + " no thumbnail found!");
                } */
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
