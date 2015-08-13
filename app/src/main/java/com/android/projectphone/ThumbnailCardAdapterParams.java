package com.android.projectphone;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

/**
 * Created by hnoct on 7/29/2015.
 */
public class ThumbnailCardAdapterParams {
    String phone;
    String imageURL;
    ImageView imageView;
    RecyclerView.ViewHolder viewHolder;

    public ThumbnailCardAdapterParams(String phone, String imageURL, ImageView imageView) {
        this.phone = phone;
        this.imageURL = imageURL;
        this.imageView = imageView;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageURL() {
        return imageURL;
    }

    public RecyclerView.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
