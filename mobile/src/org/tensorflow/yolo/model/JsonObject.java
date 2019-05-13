package org.tensorflow.yolo.model;

import com.google.gson.annotations.SerializedName;

import org.tensorflow.yolo.util.ImageUtils;

import java.io.File;

public class JsonObject {
    @SerializedName("type")
    public String type;
    @SerializedName("provider")
    public String provider;
    @SerializedName("longitude")
    public String longitude;
    @SerializedName("latitude")
    public String latitude;
    @SerializedName("file")
    public String file;

    public JsonObject(String type, String provider, double longitude, double latitude, File file) {
        this.type = type;
        this.provider = provider;
        this.longitude = String.valueOf(longitude);
        this.latitude = String.valueOf(latitude);
        this.file = ImageUtils.fileToString(file);
    }

}
