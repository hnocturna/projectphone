package com.android.projectphone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.projectphone.data.PhoneDbHelperTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hnocturna on 5/21/2015.
 */
public class ThumbnailAdapter extends BaseAdapter {
    private LayoutInflater layoutinflater;
    private Context context;
    private ThumbnailLoader thumbnailLoader;

    public ThumbnailAdapter(Context context, ThumbnailLoader thumbnailLoader) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.thumbnailLoader = thumbnailLoader;

    }

    @Override
    public int getCount() {
        return thumbnailLoader.getPhoneListSize();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String phone = thumbnailLoader.getCurrentPhone(position);
        PhoneDbHelperTest db = new PhoneDbHelperTest(context);

        List<String> phoneList = new LinkedList<>();
        phoneList.add(phone);
        Map<String, String> imageURLMap = new LinkedHashMap<>(db.getImageURL(phoneList));
        String imageURL = imageURLMap.get(phone);

        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutinflater.inflate(R.layout.thumbnail_card, parent, false);
            // viewHolder.textView = (TextView)convertView.findViewById(R.id.btn_textview);
            // viewHolder.imageView = (ImageView)convertView.findViewById(R.id.btn_imageview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.textView.setText(phone.replace("_", " "));       // Set the text as the name of the phone.

        // Sets the image according to the ThumbnailMap if notifyDataSetChanged() is called. Otherwise, it is a recycled view and can proceed with downloading thumbnail.
        if (viewHolder.phone == phone) {
            Log.d("ThumbnailAdapter.getView", phone + " thumbnail found!");
            viewHolder.imageView.setImageBitmap(getBitmapFromMemCache(phone));
            viewHolder.phone = phone;
        } else {

            viewHolder.imageView.setImageResource(R.drawable.white_rectangle);

            if (getBitmapFromMemCache(phone) != null) {
                // Apply the saved thumbnail from cache to reduce bandwidth usage. (Primary check)

                Log.d("ThumbnailAdapter.getView", phone + " thumbnail found!");
                viewHolder.imageView.setImageBitmap(getBitmapFromMemCache(phone));

            } else {
                // Pass required variables to a new container class and use that as variable passed to AsyncTask to download & set the thumbnail.

                Log.d("ThumbnailAdapter.getView", phone + " thumbnail not found!");
                ThumbnailAdapterParams params = new ThumbnailAdapterParams(phone, imageURL, viewHolder);
                new ThumbnailDownload().execute(params);
                viewHolder.phone = phone;
            }
        }
        return convertView;
    }

    static class ViewHolder{
        String phone;
        TextView textView;
        ImageView imageView;
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return MainActivity.memCache.get(key);
    }

    public void addBitmapToMemCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            MainActivity.memCache.put(key, bitmap);
        }
    }

    public class ThumbnailDownload extends AsyncTask<ThumbnailAdapterParams, Void, ThumbnailAdapterParams> {
        String phone;
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ThumbnailAdapterParams doInBackground(ThumbnailAdapterParams...params) {
            Bitmap bmp = null;

            String phone = params[0].getPhone();
            String imageURL = params[0].getImageURL();
            ViewHolder viewHolder = params[0].getViewHolder();

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
                    connection.setDoInput(true);
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
        protected void onPostExecute(ThumbnailAdapterParams params) {
            if (params != null) {
                String phone = params.getPhone();
                String imageURL = params.getImageURL();
                ViewHolder viewHolder = params.getViewHolder();

                if (getBitmapFromMemCache(phone) != null) {
                    Log.i("ThumbnailAdapter", phone + " bitmap set!");
                    viewHolder.imageView.setImageBitmap(getBitmapFromMemCache(phone));          // Set imageView as the thumbnail downloaded above and stored in the ThumbnailMap.
                    viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);         // Still figuring this out, but ideally will blow up the image a bit to fit the frame.
                    viewHolder.imageView.setPadding(8, 8, 8, 8);                                // Set a small padding around the images so they don't overlap.
                } else {
                    viewHolder.imageView.setImageResource(R.drawable.white_rectangle);
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
}
