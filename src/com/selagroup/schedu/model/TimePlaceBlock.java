/**
 * @author Nick Huebner and Mark Redden
 * @version 1.0
 */
package com.selagroup.schedu.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.selagroup.schedu.database.DBHelper;

import android.content.ContentValues;

/**
 * The Class TimePlaceBlock.
 */
public class TimePlaceBlock extends ContentValueItem implements Comparable<TimePlaceBlock> {

	// Masks for the days of the week: Sun, Mon, Tue, Wed, Thur, Fri, Sat, Sun
	public static final int sDayMasks[] = new int[] { 0x0001, 0x0002, 0x0004, 0x0008, 0x0010, 0x0020, 0x0040 };
	
	private static final long serialVersionUID = 127932472161863783L;
	private static final int sDaysInWeek = 7;
	private static final String[] sDayAbbreviations = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
	private static final SimpleDateFormat sTimeFormat = new SimpleDateFormat("h:mma");

	private Location mLocation;
	private Calendar mStartTime;
	private Calendar mEndTime;
	private int mDayFlag;

	public TimePlaceBlock(int iID, Location iLocation, Calendar iStartTime, Calendar iEndTime, int iDayFlag) {
		super(iID);
		mLocation = iLocation;
		mStartTime = iStartTime;
		mEndTime = iEndTime;
		mDayFlag = iDayFlag;
	}

	public TimePlaceBlock(int iID, Location iLocation, Calendar iStartTime, Calendar iEndTime, boolean[] iDayFlag) {
		super(iID);
		mLocation = iLocation;
		mStartTime = iStartTime;
		mEndTime = iEndTime;
		mStartTime.set(Calendar.MILLISECOND, 0);
		mEndTime.set(Calendar.MILLISECOND, 0);
		setDayFlagArray(iDayFlag);
	}

	public Location getLocation() {
		return mLocation;
	}
	
	public void setLocation(Location iLocation) {
		mLocation = iLocation;
	}

	public Calendar getStartTime() {
		return mStartTime;
	}
	
	public void setStartTime(Calendar iStartTime) {
		mStartTime = iStartTime;
	}

	public Calendar getEndTime() {
		return mEndTime;
	}
	
	public void setEndTime(Calendar iEndTime) {
		mEndTime = iEndTime;
	}

	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		values.put(DBHelper.COL_TIME_PLACE_BLOCK_StartTime, mStartTime.getTimeInMillis());
		values.put(DBHelper.COL_TIME_PLACE_BLOCK_EndTime, mEndTime.getTimeInMillis());
		values.put(DBHelper.COL_TIME_PLACE_BLOCK_LocationID, mLocation.getID());
		values.put(DBHelper.COL_TIME_PLACE_BLOCK_DayFlag, mDayFlag);
		return values;
	}

	/**
	 * Gets an array of booleans for each day of the week (Sun - Sat)
	 * @return
	 */
	public boolean[] getDayFlagArray() {
		boolean[] flagArray = new boolean[7];

		for (int i = 0; i < sDaysInWeek; ++i) {
			flagArray[i] = (mDayFlag & sDayMasks[i]) > 0;
		}

		return flagArray;
	}

	/**
	 * Sets the flag array for days of the week (Sun - Sat)
	 * @param flagArray
	 */
	public void setDayFlagArray(boolean[] flagArray) {
		mDayFlag = 0;
		for (int i = 0; i < sDaysInWeek; ++i) {
			mDayFlag = (mDayFlag | (sDayMasks[i] & (flagArray[i] ? 0xFFFF : 0x0000)));
		}
	}

	private String daysToString() {
		StringBuilder dayString = new StringBuilder();

		boolean dayFlagArray[] = getDayFlagArray();

		for (int i = 0; i < sDaysInWeek; ++i) {
			if (dayFlagArray[i]) {
				dayString.append(", ");
				dayString.append(sDayAbbreviations[i]);
			}
		}
		dayString.append("\n");

		return dayString.substring(2);
	}
	
	public int getMinutesAfterMidnight() {
		return mStartTime.get(Calendar.MINUTE) + 60 * mStartTime.get(Calendar.HOUR_OF_DAY);
	}
	
	public int getMinutesElapsed() {
		return (int)((mEndTime.getTimeInMillis() - mStartTime.getTimeInMillis()) / 60000);
	}
	
	public String toTimeString() {
		return (sTimeFormat.format(mStartTime.getTime()) + " - " + sTimeFormat.format(mEndTime.getTime())).toLowerCase();
	}
	
	public String toDateTimeString() {
		return daysToString() + toTimeString();
	}

	@Override
	public String toString() {
		return toDateTimeString() + "\n" + mLocation.toString();
	}

	public int compareTo(TimePlaceBlock another) {
		return mStartTime.compareTo(another.mStartTime);
	}
}
