/**
 * @author Nick Huebner and Mark Redden
 * @version 1.0
 */
package com.selagroup.schedu.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.selagroup.schedu.R;
import com.selagroup.schedu.ScheduApplication;
import com.selagroup.schedu.Utility;
import com.selagroup.schedu.managers.CourseManager;
import com.selagroup.schedu.managers.InstructorManager;
import com.selagroup.schedu.model.Course;
import com.selagroup.schedu.model.Instructor;
import com.selagroup.schedu.model.Location;
import com.selagroup.schedu.model.TimePlaceBlock;

/**
 * The Class InstructorActivity.
 */
public class InstructorActivity extends Activity {

	private static final int ADDTIME_REQUEST_CODE = 1;
	InstructorManager mInstructorManager;
	CourseManager mCourseManager;

	TextView instructor_course_code;
	EditText instructor_name;
	EditText instructor_email;
	EditText instructor_phone;
	EditText instructor_building;
	EditText instructor_room;
	Button instructor_btn_add_time;
	Button instructor_btn_clear_time;
	Button instructor_btn_done;
	Button instructor_btn_cancel;

	Course thisCourse;
	Instructor thisInstructor;
	Location thisLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructor);

		ScheduApplication app = (ScheduApplication) getApplication();
		int courseID = getIntent().getIntExtra("courseID", -1);

		mInstructorManager = app.getInstructorManager();
		mCourseManager = app.getCourseManager();
		thisCourse = app.getCourseManager().get(courseID);
		
		thisInstructor = thisCourse.getInstructor();
		if (thisInstructor == null) {
			thisInstructor = new Instructor(-1, "", "", "");
		}
		thisLocation = thisInstructor.getLocation();

		initWidgets();
		setWidgetValues();
		initListeners();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADDTIME_REQUEST_CODE && resultCode == RESULT_OK) {
			// update location and time block displays
			TimePlaceBlock block = (TimePlaceBlock) data.getSerializableExtra("block");
			//block.setLocation(thisLocation); // commented out so that when deleting hours, location won't go with it
			thisInstructor.addOfficeBlock(block);
			setWidgetValues();
		}
	}

	@Override
	public void onBackPressed() {
		cancel();
	}

	private void cancel() {
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	private void initWidgets() {

		// Initialize widget handles
		instructor_course_code = (TextView) findViewById(R.id.instructor_course_code);
		instructor_name = (EditText) findViewById(R.id.instructor_name);
		instructor_email = (EditText) findViewById(R.id.instructor_email);
		instructor_phone = (EditText) findViewById(R.id.instructor_phone);
		instructor_building = (EditText) findViewById(R.id.instructor_building);
		instructor_room = (EditText) findViewById(R.id.instructor_room);
		instructor_btn_add_time = (Button) findViewById(R.id.instructor_btn_add_time);
		instructor_btn_clear_time = (Button) findViewById(R.id.instructor_btn_clear_time);
		instructor_btn_done = (Button) findViewById(R.id.instructor_btn_done);
		instructor_btn_cancel = (Button) findViewById(R.id.instructor_btn_cancel);

	}

	private void setWidgetValues() {
		instructor_course_code.setText(thisCourse.getCode());
		if (thisInstructor != null) {
			instructor_name.setText(thisInstructor.getName());
			instructor_email.setText(thisInstructor.getEmail());
			instructor_phone.setText(thisInstructor.getPhone());
			instructor_building.setText(thisLocation.getBuilding());
			instructor_room.setText(thisLocation.getRoom());
			ScrollView sv = (ScrollView) findViewById(R.id.instructor_sv_hours);
			Utility.populateInstructorHours(sv, thisInstructor.getOfficeBlocks());
		}
	}

	private void collectWidgetValues() {
		String name = instructor_name.getText().toString();
		String email = instructor_email.getText().toString();
		String phone = instructor_phone.getText().toString();
		if (thisInstructor != null) {
			thisInstructor.setName(name);
			thisInstructor.setEmail(email);
			thisInstructor.setPhone(phone);
		} else {
			thisInstructor = new Instructor(-1, name, email, phone);
			thisLocation = thisInstructor.getLocation();
		}
		thisLocation.setBuilding(instructor_building.getText().toString());
		thisLocation.setRoom(instructor_room.getText().toString());
	}

	private void initListeners() {
		instructor_btn_add_time.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				collectWidgetValues(); // need to do this so that bldg/room don't get clobbered when we come back
				Intent intent = new Intent(InstructorActivity.this, AddTimeActivity.class);
				intent.putExtra("instructor", true);
				startActivityForResult(intent, ADDTIME_REQUEST_CODE);
			}
		});
		instructor_btn_clear_time.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!thisInstructor.getOfficeBlocks().isEmpty()) {
					AlertDialog.Builder dlg = new AlertDialog.Builder(InstructorActivity.this);
					dlg
						.setMessage(R.string.instructor_clear_hours_confirm_text)
						.setPositiveButton(R.string.instructor_clear_hours_btn_confirm, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								mInstructorManager.clearOfficeHours(thisInstructor);
								setWidgetValues();
							}
						})
						.setNegativeButton(R.string.instructor_cancel_btn, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.show();
				}
			}
		});
		instructor_btn_done.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				collectWidgetValues();
				mInstructorManager.insert(thisInstructor);
				thisCourse.setInstructor(thisInstructor);
				mCourseManager.update(thisCourse);

				// Intent intent = new Intent();
				// intent.putExtra("instructor", thisInstructor);
				setResult(RESULT_OK);
				finish();
			}
		});
		instructor_btn_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				cancel();
			}
		});
	}

	void mockup() {
		LinearLayout hours = (LinearLayout) findViewById(R.id.instructor_ll_hours);
		TextView tv1 = new TextView(this);
		tv1.setText("Mon, Wed -- 1pm - 3pm");
		TextView tv2 = new TextView(this);
		tv2.setText("Tue, Thu -- 3pm-4pm");
		TextView tv3 = new TextView(this);
		tv3.setText("Fri -- 11am - 12pm");
		TextView tv4 = new TextView(this);
		tv4.setText("Sat -- 11am - 12pm");
		TextView tv5 = new TextView(this);
		tv5.setText("Sat -- 11am - 12pm");
		TextView tv6 = new TextView(this);
		tv6.setText("Sat -- 11am - 12pm");
		TextView tv7 = new TextView(this);
		tv7.setText("Sat -- 11am - 12pm");
		hours.addView(tv1);
		hours.addView(tv2);
		hours.addView(tv3);
		hours.addView(tv4);
		hours.addView(tv5);
		hours.addView(tv6);
		hours.addView(tv7);
	}
}
