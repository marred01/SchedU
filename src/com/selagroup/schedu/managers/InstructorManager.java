/**
 * @author Nick Huebner and Mark Redden
 * @version 1.0
 */
package com.selagroup.schedu.managers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import com.selagroup.schedu.database.DBHelper;
import com.selagroup.schedu.model.Instructor;
import com.selagroup.schedu.model.Location;
import com.selagroup.schedu.model.TimePlaceBlock;

/**
 * The Class InstructorManager.
 */
public class InstructorManager extends Manager<Instructor> {
	private TimePlaceBlockManager mTimePlaceBlockManager;
	private LocationManager mLocationManager;

	public InstructorManager(DBHelper iHelper, LocationManager iLocationManager, TimePlaceBlockManager iTimePlaceBlockManager) {
		super(iHelper);
		mTimePlaceBlockManager = iTimePlaceBlockManager;
		mLocationManager = iLocationManager;
	}

	@Override
	public int insert(Instructor iInstructor) {
		if (iInstructor == null) {
			return -1;
		}
		
		// If the instructor already exists, just update the entry
		if (get(iInstructor.getID()) != null) {
			update(iInstructor);
			return iInstructor.getID();
		}

		Location location = iInstructor.getLocation();
		if (location != null) {
			mLocationManager.insert(location);
		}
		
		open(OPEN_MODE.WRITE);
		int instructorID = (int) mDB.insert(DBHelper.TABLE_Instructor, null, iInstructor.getValues());
		iInstructor.setID(instructorID);
		close();

		// Insert or update office time blocks
		ContentValues officeBlock = null;
		for (TimePlaceBlock block : iInstructor.getOfficeBlocks()) {
			block.setID(mTimePlaceBlockManager.insert(block));

			officeBlock = new ContentValues();
			officeBlock.put(DBHelper.COL_OFFICE_TIME_PLACE_BLOCK_InstructorID, instructorID);
			officeBlock.put(DBHelper.COL_OFFICE_TIME_PLACE_BLOCK_TimePlaceBlockID, block.getID());

			open(OPEN_MODE.WRITE);
			mDB.insert(DBHelper.TABLE_OfficeTimePlaceBlock, null, officeBlock);
			close();
		}

		return instructorID;
	}

	@Override
	public void delete(Instructor iInstructor) {
		// Don't allow deletes for now
		// If implemented, would need to update all classes taught by this instructor with NULLs
	}

	@Override
	public void update(Instructor iInstructor) {
		if (iInstructor == null) {
			return;
		}
		
		Location location = iInstructor.getLocation();
		if (location != null) {
			mLocationManager.insert(location);
		}
		
		open(OPEN_MODE.WRITE);
		mDB.update(DBHelper.TABLE_Instructor, iInstructor.getValues(), DBHelper.COL_INSTRUCTOR_ID + "=?", new String[] { "" + iInstructor.getID() });
		close();

		// Insert or update office time blocks
		ContentValues officeBlock = null;
		for (TimePlaceBlock block : iInstructor.getOfficeBlocks()) {
			mTimePlaceBlockManager.insert(block);

			officeBlock = new ContentValues();
			officeBlock.put(DBHelper.COL_OFFICE_TIME_PLACE_BLOCK_InstructorID, iInstructor.getID());
			officeBlock.put(DBHelper.COL_OFFICE_TIME_PLACE_BLOCK_TimePlaceBlockID, block.getID());

			open(OPEN_MODE.WRITE);
			try {
				mDB.insertOrThrow(DBHelper.TABLE_OfficeTimePlaceBlock, null, officeBlock);
			} catch (SQLiteConstraintException e) {
				// already exists
			}
			close();
		}
	}

	public void clearOfficeHours(Instructor iInstructor) {
		
		for (TimePlaceBlock block : iInstructor.getOfficeBlocks()) {
			mTimePlaceBlockManager.delete(block);
		}
		iInstructor.clearOfficeBlocks();
		
		open(OPEN_MODE.WRITE);
		mDB.delete(DBHelper.TABLE_OfficeTimePlaceBlock, DBHelper.COL_OFFICE_TIME_PLACE_BLOCK_InstructorID + "=?",
				new String [] {iInstructor.getID() + ""} );
		close();
	}
	
	@Override
	protected Instructor itemFromCurrentPos(Cursor iCursor) {
		int id = iCursor.getInt(iCursor.getColumnIndex(DBHelper.COL_INSTRUCTOR_ID));
		String name = iCursor.getString(iCursor.getColumnIndex(DBHelper.COL_INSTRUCTOR_Name));
		String email = iCursor.getString(iCursor.getColumnIndex(DBHelper.COL_INSTRUCTOR_Email));
		String phone = iCursor.getString(iCursor.getColumnIndex(DBHelper.COL_INSTRUCTOR_Phone));
		int locationId = iCursor.getInt(iCursor.getColumnIndex(DBHelper.COL_INSTRUCTOR_LocationID));

		Instructor instructor = new Instructor(id, name, phone, email);

		instructor.setLocation(mLocationManager.get(locationId));
		
		// Get block IDs for the office hours
		open(OPEN_MODE.READ);
		Cursor officeBlockCursor = mDB.query(DBHelper.TABLE_OfficeTimePlaceBlock, new String[] { DBHelper.COL_OFFICE_TIME_PLACE_BLOCK_TimePlaceBlockID },
				DBHelper.COL_OFFICE_TIME_PLACE_BLOCK_InstructorID + "=?", new String[] { "" + instructor.getID() }, null, null, null);
		// Add office blocks
		if (officeBlockCursor.moveToFirst()) {
			do {
				int blockID = officeBlockCursor.getInt(officeBlockCursor.getColumnIndex(DBHelper.COL_OFFICE_TIME_PLACE_BLOCK_TimePlaceBlockID));
				TimePlaceBlock block = mTimePlaceBlockManager.get(blockID);
				instructor.addOfficeBlock(block);
			} while (officeBlockCursor.moveToNext());
		}
		officeBlockCursor.close();
		close();
		
		return instructor;
	}

	@Override
	protected String getTableName() {
		return DBHelper.TABLE_Instructor;
	}

	@Override
	protected String getIDColumnName() {
		return DBHelper.COL_INSTRUCTOR_ID;
	}
}
