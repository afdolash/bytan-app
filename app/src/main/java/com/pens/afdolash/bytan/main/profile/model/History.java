package com.pens.afdolash.bytan.main.profile.model;

/**
 * Created by afdol on 5/29/2018.
 */

public class History {
    public static final String TABLE_NAME = "notes";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_HEART = "heart";
    public static final String COLUMN_OBJTEMP = "obj_temp";
    public static final String COLUMN_AMBTEMP = "amb_temp";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String heartRate;
    private String objTemp;
    private String ambTemp;
    private String code;
    private String latitude;
    private String longitude;
    private String timestamp;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_HEART + " TEXT,"
                    + COLUMN_OBJTEMP + " TEXT,"
                    + COLUMN_AMBTEMP + " TEXT,"
                    + COLUMN_CODE + " TEXT,"
                    + COLUMN_LATITUDE + " TEXT,"
                    + COLUMN_LONGITUDE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public History() {
    }

    public History(int id, String heartRate, String objTemp, String ambTemp, String code, String latitude, String longitude, String timestamp) {
        this.id = id;
        this.heartRate = heartRate;
        this.objTemp = objTemp;
        this.ambTemp = ambTemp;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public History(String heartRate, String objTemp, String ambTemp, String code, String latitude, String longitude) {
        this.heartRate = heartRate;
        this.objTemp = objTemp;
        this.ambTemp = ambTemp;
        this.code = code;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getObjTemp() {
        return objTemp;
    }

    public void setObjTemp(String objTemp) {
        this.objTemp = objTemp;
    }

    public String getAmbTemp() {
        return ambTemp;
    }

    public void setAmbTemp(String ambTemp) {
        this.ambTemp = ambTemp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
