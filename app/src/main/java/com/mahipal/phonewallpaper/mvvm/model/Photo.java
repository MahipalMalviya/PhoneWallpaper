package com.mahipal.phonewallpaper.mvvm.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Photo implements Serializable {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("width")
    @Expose
    private long width;
    @SerializedName("height")
    @Expose
    private long height;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("photographer")
    @Expose
    private String photographer;
    @SerializedName("photographer_url")
    @Expose
    private String photographerUrl;
    @SerializedName("photographer_id")
    @Expose
    private long photographerId;
    @SerializedName("src")
    @Expose
    private ImageSrc imageSrc;
    @SerializedName("liked")
    @Expose
    private boolean liked;
    @SerializedName("avg_color")
    @Expose
    private boolean avgColor;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getPhotographerUrl() {
        return photographerUrl;
    }

    public void setPhotographerUrl(String photographerUrl) {
        this.photographerUrl = photographerUrl;
    }

    public long getPhotographerId() {
        return photographerId;
    }

    public void setPhotographerId(long photographerId) {
        this.photographerId = photographerId;
    }

    public ImageSrc getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(ImageSrc imageSrc) {
        this.imageSrc = imageSrc;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isAvgColor() {
        return avgColor;
    }

    public void setAvgColor(boolean avgColor) {
        this.avgColor = avgColor;
    }
}