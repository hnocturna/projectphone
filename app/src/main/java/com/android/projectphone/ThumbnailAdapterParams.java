package com.android.projectphone;

/**
 * Created by Hnocturna on 5/25/2015.
 */
public class ThumbnailAdapterParams {
    String phone;
    String imageURL;
    ThumbnailAdapter.ViewHolder viewHolder;

    public ThumbnailAdapterParams(String phone, String imageURL, ThumbnailAdapter.ViewHolder viewHolder) {
        this.phone = phone;
        this.imageURL = imageURL;
        this.viewHolder = viewHolder;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageURL() {
        return imageURL;
    }

    public ThumbnailAdapter.ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setViewHolder(ThumbnailAdapter.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }
}
