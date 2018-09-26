package com.onoapps.hanan.verticalseekbar

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class SavedState : View.BaseSavedState {

    var savedPercentage: Float = 0.toFloat()
    var savedLabel: String? = null

    constructor(superState: Parcelable) : super(superState) {}

    private constructor(`in`: Parcel) : super(`in`) {
        savedPercentage = `in`.readFloat()
        savedLabel = `in`.readString()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeFloat(savedPercentage)
        out.writeString(savedLabel)
    }

    companion object {

        val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

            override fun createFromParcel(`in`: Parcel): SavedState {
                return SavedState(`in`)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}
