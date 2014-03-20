package il.ac.huji.todolist;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddNewTodoItemActivity extends Activity {
	EditText edtNewItem;
	DatePicker datePicker;
	Button btnCancel;
	Button btnOK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_item_layout);
		setTitle(R.string.add_new_item);
		initViews();
		setViewsActions();

	}

	private void setViewsActions() {
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		btnOK.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				if(edtNewItem.getText().toString().trim().equals("")){
					return;
				}
				intent.putExtra(TodoListManagerActivity.ITEM_TITLE, edtNewItem.getText().toString());
				intent.putExtra(TodoListManagerActivity.ITEM_DATE, getDateFromDatePicker(datePicker));
				
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	private void initViews() {
		edtNewItem = (EditText) findViewById(R.id.et_new_item_title);
		datePicker = (DatePicker) findViewById(R.id.dp_new_item_date);
		btnCancel = (Button) findViewById(R.id.button_cancel);
		btnOK = (Button) findViewById(R.id.button_ok);
	}
	public static long getDateFromDatePicker(DatePicker datePicker){
	    int day = datePicker.getDayOfMonth();
	    int month = datePicker.getMonth();
	    int year =  datePicker.getYear();

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(year, month, day);

	    return calendar.getTimeInMillis();
	}
}
