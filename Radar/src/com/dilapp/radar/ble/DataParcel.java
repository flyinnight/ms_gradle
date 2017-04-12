package com.dilapp.radar.ble;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class DataParcel implements Parcelable {

	public int repre;
	public int op;
	public int sessionId = 0xF;
	public boolean tail = true;
	public boolean option = false;
	public byte[] body;

    public static final Parcelable.Creator<DataParcel> CREATOR = new
            Parcelable.Creator<DataParcel>() {
                public DataParcel createFromParcel(Parcel in) {
                    return new DataParcel(in);
                }

                public DataParcel[] newArray(int size) {
                    return new DataParcel[size];
                }
            };

    public DataParcel() {
    }

    private DataParcel(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        repre = in.readInt();
        
        op = in.readInt();
        sessionId = in.readInt();
        tail = in.readInt() == 1;
        option = in.readInt() == 1;
        Log.d("Gaodongdong", "the Gaodongdong's read parcel repre " + repre);
        Log.d("Gaodongdong", "the Gaodongdong's read parcel op " + op);
        Log.d("Gaodongdong", "the Gaodongdong's read parcel sessionId " + sessionId);
        Log.d("Gaodongdong", "the Gaodongdong's read parcel tail" + tail);
        Log.d("Gaodongdong", "the Gaodongdong's read parcel option" + option);
        in.readByteArray(body);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(repre);
        dest.writeInt(op);
        dest.writeInt(sessionId);
        dest.writeInt(tail ? 1 : 0);
        dest.writeInt(option ? 1 : 0);
        dest.writeByteArray(body);
        Log.d("Gaodongdong", "the Gaodongdong's write parcel repre " + repre);
        Log.d("Gaodongdong", "the Gaodongdong's write parcel op " + op);
        Log.d("Gaodongdong", "the Gaodongdong's write parcel sessionId " + sessionId);
        Log.d("Gaodongdong", "the Gaodongdong's write parcel tail" + tail);
        Log.d("Gaodongdong", "the Gaodongdong's write parcel option" + option);
    }

}
