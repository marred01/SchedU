/**
 * @author Nick Huebner and Mark Redden
 * @version 1.0
 */
package com.selagroup.schedu.activities;

import com.selagroup.schedu.R;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

/**
 * The Class CourseWorkActivity.
 */
public class CourseWorkActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_work);
		
		mockup();
	}
	
	private void mockup() {
		final Dialog dlgNewAssignment = new Dialog(this);
		//dlgNewAssignment.setTitle(R.string.dialog_new_assignment_title);
		dlgNewAssignment.requestWindowFeature(Window.FEATURE_NO_TITLE);
 		dlgNewAssignment.setContentView(R.layout.dialog_new_assignment);
		((ImageButton) findViewById(R.id.course_work_btn_add)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dlgNewAssignment.show();
			}
		});
	}
}
