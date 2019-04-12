package org.tensorflow.yolo.model;

import org.tensorflow.yolo.util.ImageUtils;

import java.io.File;

public class JsonObject {
    public String type;
    public String provider;
    public String longitude;
    public String latitude;
    public String file;

    public JsonObject(String type, String provider, double longitude, double latitude, File file) {
        this.type = type;
        this.provider = provider;
        this.longitude = String.valueOf(longitude);
        this.latitude = String.valueOf(latitude);
        this.file = ImageUtils.fileToString(file);
    }

}
