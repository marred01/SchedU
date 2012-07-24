/**
 * @author Nick Huebner and Mark Redden
 * @version 1.0
 */
package com.selagroup.schedu.managers;

import android.database.Cursor;

import com.selagroup.schedu.Utility;
import com.selagroup.schedu.database.DatabaseHelper;
import com.selagroup.schedu.model.Location;
import com.selagroup.schedu.model.TimePlaceBlock;

/**
 * The Class TimePlaceBlockManager.
 */
public class TimePlaceBlockManager extends Manager<TimePlaceBlock> {
	
	private LocationManager mLocationManager;

	public TimePlaceBlockManager(DatabaseHelper iHelper, LocationManager iLocationManager) {
		super(iHelper);
		mLocationManager = iLocationManager;
	}

	@Override
	public int insert(TimePlaceBlock iBlock) {
		// If the term already exists, just update the entry
		if (get(iBlock.getID()) != null) {
			update(iBlock);
			return iBlock.getID();
		}
		
		iBlock.setID(mLocationManager.insert(iBlock.getLocation()));

		open(OPEN_MODE.WRITE);
		int blockID = (int) mDB.insert(DatabaseHelper.TABLE_TimePlaceBlock, null, iBlock.getValues());
		iBlock.setID(blockID);
		close();
		return blockID;
	}

	@Override
	public void delete(TimePlaceBlock iBlock) {
		mLocationManager.delete(iBlock.getLocation());
		
		open(OPEN_MODE.WRITE);
		mDB.delete(DatabaseHelper.TABLE_TimePlaceBlock, DatabaseHelper.COL_TIME_PLACE_BLOCK_ID + "=?", new String[] { "" + iBlock.getID() });
		close();
	}

	@Override
	public void update(TimePlaceBlock iBlock) {
		mLocationManager.update(iBlock.getLocation());
		
		open(OPEN_MODE.WRITE);
		mDB.update(DatabaseHelper.TABLE_TimePlaceBlock, iBlock.getValues(), DatabaseHelper.COL_TIME_PLACE_BLOCK_ID + "=?", new String[] { "" + iBlock.getID() });
		close();
	}

	@Override
	protected TimePlaceBlock itemFromCurrentPos(Cursor iCursor) {
		int id = iCursor.getInt(iCursor.getColumnIndex(DatabaseHelper.COL_TIME_PLACE_BLOCK_ID));
		long startTime = iCursor.getInt(iCursor.getColumnIndex(DatabaseHelper.COL_TIME_PLACE_BLOCK_StartTime));
		long endTime = iCursor.getInt(iCursor.getColumnIndex(DatabaseHelper.COL_TIME_PLACE_BLOCK_EndTime));
		int dayFlag = iCursor.getInt(iCursor.getColumnIndex(DatabaseHelper.COL_TIME_PLACE_BLOCK_DayFlag));
		int locationID = iCursor.getInt(iCursor.getColumnIndex(DatabaseHelper.COL_TIME_PLACE_BLOCK_LocationID));
		
		Location location = mLocationManager.get(locationID);
		
		return new TimePlaceBlock(id, location, Utility.calendarFromInt(startTime), Utility.calendarFromInt(endTime), dayFlag);
	}
	
	@Override
	protected String getTableName() {
		return DatabaseHelper.TABLE_TimePlaceBlock;
	}

	@Override
	protected String getIDColumnName() {
		return DatabaseHelper.COL_TIME_PLACE_BLOCK_ID;
	}
}
