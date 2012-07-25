package com.selagroup.schedu.model;

import java.io.Serializable;

import android.content.ContentValues;

public abstract class ContentValueItem implements Serializable {
    private static final long serialVersionUID = -4617555052531285311L;
    
	protected int mID;
	
	public ContentValueItem(int iID) {
		mID = iID;
	}
	
	public final int getID() {
		return mID;
	}
	
	public final void setID(int iID) {
		mID = iID;
	}
	
	public abstract ContentValues getValues();
}
