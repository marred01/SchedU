/**
 * @author Nick Huebner and Mark Redden
 * @version 1.0
 */

package com.selagroup.schedu.model.note;

import com.selagroup.schedu.model.Course;

/**
 * The Class PhotoNote.
 */
public class PhotoNote extends Note {
    private static final long serialVersionUID = -9144110006430261383L;

	public PhotoNote(int iID, Course iCourse, String iDescription) {
		super(iID, NOTE_TYPE.PHOTO, iCourse, iDescription);
	}
}
