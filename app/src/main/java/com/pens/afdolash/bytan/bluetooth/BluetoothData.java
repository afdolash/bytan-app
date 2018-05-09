package com.pens.afdolash.bytan.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by afdol on 5/9/2018.
 */

public class BluetoothData implements Parcelable {
    private String ambTemp;
    private String objTemp;
    private String heartRate;
    private String spO2;
    private String code;

    public BluetoothData(String ambTemp, String objTemp, String heartRate, String spO2, String code) {
        this.ambTemp = ambTemp;
        this.objTemp = objTemp;
        this.heartRate = heartRate;
        this.spO2 = spO2;
        this.code = code;
    }

    public String getAmbTemp() {
        return ambTemp;
    }

    public String getObjTemp() {
        return objTemp;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public String getSpO2() {
        return spO2;
    }

    public String getCode() {
        return code;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ambTemp);
        dest.writeString(this.objTemp);
        dest.writeString(this.heartRate);
        dest.writeString(this.spO2);
        dest.writeString(this.code);
    }

    protected BluetoothData(Parcel in) {
        this.ambTemp = in.readString();
        this.objTemp = in.readString();
        this.heartRate = in.readString();
        this.spO2 = in.readString();
        this.code = in.readString();
    }

    public static final Parcelable.Creator<BluetoothData> CREATOR = new Parcelable.Creator<BluetoothData>() {
        @Override
        public BluetoothData createFromParcel(Parcel source) {
            return new BluetoothData(source);
        }

        @Override
        public BluetoothData[] newArray(int size) {
            return new BluetoothData[size];
        }
    };
}
