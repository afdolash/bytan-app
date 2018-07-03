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
    private int code;

    public BluetoothData(String ambTemp, String objTemp, String heartRate, int code) {
        this.ambTemp = ambTemp;
        this.objTemp = objTemp;
        this.heartRate = heartRate;
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

    public int getCode() {
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
        dest.writeInt(this.code);
    }

    protected BluetoothData(Parcel in) {
        this.ambTemp = in.readString();
        this.objTemp = in.readString();
        this.heartRate = in.readString();
        this.code = in.readInt();
    }

    public static final Creator<BluetoothData> CREATOR = new Creator<BluetoothData>() {
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
