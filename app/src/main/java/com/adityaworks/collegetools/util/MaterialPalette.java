/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adityaworks.collegetools.util;

import android.os.Parcel;
import android.os.Parcelable;

public class MaterialPalette implements Parcelable {
    public static final Creator<MaterialPalette> CREATOR = new Creator<MaterialPalette>() {
        @Override
        public MaterialPalette createFromParcel(Parcel in) {
            return new MaterialPalette(in);
        }

        @Override
        public MaterialPalette[] newArray(int size) {
            return new MaterialPalette[size];
        }
    };
    public final int mPrimaryColor;
    public final int mSecondaryColor;

    public MaterialPalette(int primaryColor, int secondaryColor) {
        mPrimaryColor = primaryColor;
        mSecondaryColor = secondaryColor;
    }

    private MaterialPalette(Parcel in) {
        mPrimaryColor = in.readInt();
        mSecondaryColor = in.readInt();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MaterialPalette other = (MaterialPalette) obj;
        if (mPrimaryColor != other.mPrimaryColor) {
            return false;
        }
        if (mSecondaryColor != other.mSecondaryColor) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mPrimaryColor;
        result = prime * result + mSecondaryColor;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPrimaryColor);
        dest.writeInt(mSecondaryColor);
    }
}