/**
 * @author Nick Huebner and Mark Redden
 * @version 1.0
 */
package com.selagroup.schedu.activities;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.selagroup.schedu.R;
import com.selagroup.schedu.ScheduApplication;
import com.selagroup.schedu.Utility;
import com.selagroup.schedu.adapters.ExamArrayAdapter;
import com.selagroup.schedu.adapters.ExamArrayAdapter.ExamEditListener;
import com.selagroup.schedu.managers.ExamManager;
import com.selagroup.schedu.model.Course;
import com.selagroup.schedu.model.Exam;

/**
 * The Class CourseExamsActivity.
 */
public class CourseExamsActivity extends ListActivity {
	
	private ExamManager mExamManager;
	private ExamArrayAdapter mExamAdapter;
	private List<Exam> mExamList;
	private int mCourseID;
	private Course mCourse;
	
	private ImageView course_exams_btn_add;
	
	private int EXAM_ADD_CODE = 1;
	private int EXAM_EDIT_CODE = 2;
	
	private ExamEditListener mExamEditListener = new ExamEditListener() {
		public void onExamEdit(Exam iExam) {
			Intent intent = new Intent(CourseExamsActivity.this, NewExamActivity.class);
			intent.putExtra("examID", iExam.getID());
			startActivityForResult(intent, EXAM_EDIT_CODE);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_exams);
		getListView().setCacheColorHint(0); // make listview bg transparent
		
		initActivity();
		
		initWidgets();
		initListeners();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		// Course or term could have been deleted, and then this activity backed into
		mCourse = ((ScheduApplication) getApplication()).getCourseManager().get(mCourseID);
		if (mCourse == null) {
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			mExamList.clear();
			mExamList.addAll(mExamManager.getAllForCourse(mCourse.getID()));
			mExamAdapter.notifyDataSetChanged();
		}
	}
		
	private void initActivity() {
		mCourseID = getIntent().getIntExtra("courseID", -1);
		mCourse = ((ScheduApplication) getApplication()).getCourseManager().get(mCourseID);
		((TextView) findViewById(R.id.course_exams_course_code)).setText(mCourse.getCode());
		
		mExamManager = ((ScheduApplication) getApplication()).getExamManager();
		mExamList = mExamManager.getAllForCourse(mCourse.getID());
		mExamAdapter = new ExamArrayAdapter(CourseExamsActivity.this, R.layout.adapter_exam_select, mExamList, mExamEditListener);
		setListAdapter(mExamAdapter);
	}

	private void initWidgets() {
		course_exams_btn_add = (ImageView) findViewById(R.id.course_exams_btn_add);
	}

	private void initListeners() {
		course_exams_btn_add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(CourseExamsActivity.this, NewExamActivity.class);
				intent.putExtra("courseID", mCourse.getID());
				startActivityForResult(intent, EXAM_ADD_CODE);
			}
		});
	}
	
	/**
	 * Context menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Utility.buildOptionsMenu(CourseExamsActivity.this, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (Utility.handleOptionsMenuSelection(CourseExamsActivity.this, item)) {
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}
}
