package com.tbuonomo.jawgmapsample.data.model;

/**
 * Created by llgle on 10/11/2017.
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class IPMAnswersResponse {

    @SerializedName("pictures")
    @Expose
    private List<Picture> pictures = null;

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

}

