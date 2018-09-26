package com.onoapps.hanan.verticalseekbar;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class SavedState extends View.BaseSavedState {

    public float savedPercentage;
    public String savedLabel;

    public SavedState(Parcelable superState) {
        super(superState);
    }

    private SavedState(Parcel in){
        super(in);
        savedPercentage = in.readFloat();
        savedLabel = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeFloat(savedPercentage);
        out.writeString(savedLabel);
    }

    public static final Parcelable.Creator<SavedState> CREATOR
            = new Parcelable.Creator<SavedState>(){

        @Override
        public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
        }

        @Override
        public SavedState[] newArray(int size) {
            return new SavedState[size];
        }
    } ;
}
