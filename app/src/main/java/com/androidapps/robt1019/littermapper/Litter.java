package com.androidapps.robt1019.littermapper;


import java.util.Date;

/**
 * Created by rob on 20/06/15.
 */
public class Litter {
    private long mId;
    private String mBrand;
    private Date mDate;

    public Litter() {
        mId = -1;
        mDate = new Date();
    }

    public void setBrand(String mName) {
        this.mBrand = mName;
    }

    public String getBrand() {
        return mBrand;
    }

    public long getId() {
        return mId;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public void setId(long mId) {
        this.mId = mId;
    }
}
