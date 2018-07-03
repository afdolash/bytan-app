package com.pens.afdolash.bytan.main.group.model;

import com.abemart.wroup.common.WroupDevice;

/**
 * Created by afdol on 6/10/2018.
 */

public class MemberData {
    private WroupDevice device;
    private String heartRate;
    private String objTemp;
    private String ambTemp;
    private String code;
    private String latitude;
    private String longitude;
    private String timestamp;

    public MemberData(WroupDevice device, String heartRate, String objTemp, String ambTemp, String code, String latitude, String longitude, String timestamp) {
        this.device = device;
        this.heartRate = heartRate;
        this.objTemp = objTemp;
        this.ambTemp = ambTemp;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public WroupDevice getDevice() {
        return device;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public String getObjTemp() {
        return objTemp;
    }

    public String getAmbTemp() {
        return ambTemp;
    }

    public String getCode() {
        return code;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
