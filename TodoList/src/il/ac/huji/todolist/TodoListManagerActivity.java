package il.ac.huji.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListManagerActivity extends Activity {



	private static final int CMD_DELETE = 0;
	private static final int ADD_TODO_ITEM = 0;
	protected static final String ITEM_DATE = "dueDate";
	protected static final String ITEM_TITLE = "title";
	private static final CharSequence CALL = "Call ";
	private static final int CMD_CALL = 1;
	private ListView mTodoList;
	private TodoAdapter mTodoAdapter;
	private String mPhoneNum;
	private TodoDatabaseHandler mSqlDbHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		initViews();
		bindDB();
	}

	protected void bindDB() {
		// set the from and to arrays for the use of the todoListAdapter
		String from[] = { TodoDatabaseHandler.COLUMN_TITLE,
				TodoDatabaseHandler.COLUMN_DUE_DATE };
		int to[] = { R.id.tv_todo_item_text, R.id.tv_todo_item_date };

		// open the SQL database
		mSqlDbHandler = new TodoDatabaseHandler(this);
		mSqlDbHandler.open();

		// get all data from local db
		Cursor cursor = mSqlDbHandler.getAllTodos();
		mTodoAdapter = new TodoAdapter(this, R.layout.todo_list_item, cursor,
				from, to, 2);
		mTodoList.setAdapter(mTodoAdapter);
		mSqlDbHandler.close();
	}


	private void initViews() {

		mTodoList = (ListView) findViewById(R.id.list_todo);
		registerForContextMenu(mTodoList);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		String currTitle = ((TextView) ((LinearLayout) info.targetView)
				.findViewById(R.id.tv_todo_item_text)).getText().toString();
		menu.setHeaderTitle(currTitle);

		if (currTitle.contains(CALL)) {
			mPhoneNum = currTitle.replaceAll("Call ", "tel:");
			menu.add(0, CMD_CALL, 0, currTitle);
		}

		menu.add(0, CMD_DELETE, 0, R.string.context_menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getItemId() == CMD_DELETE) {
			
			new GetListFromDB().execute(info.id);
			return true;

		} else if (item.getItemId() == CMD_CALL) {

			Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse(mPhoneNum));
			startActivity(dial);
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.add_item) {
			Intent intent = new Intent(TodoListManagerActivity.this,
					AddNewTodoItemActivity.class);
			startActivityForResult(intent, ADD_TODO_ITEM);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_TODO_ITEM) {
			if (resultCode == RESULT_OK) {
				new AddNewItemDB().execute(data);
				
			}
		}
	}

	public class TodoAdapter extends SimpleCursorAdapter {
		Context mCtxt;
		private LayoutInflater mLayoutInflater;

		public TodoAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) {
			super(context, layout, c, from, to, flags);

			mCtxt = context;
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return mLayoutInflater.inflate(R.layout.todo_list_item, parent,
					false);
		}

		@Override
		public void bindView(View vi, Context ctxt, Cursor cursor) {
			TextView txtTodoDueDate = (TextView) vi
					.findViewById(R.id.tv_todo_item_date);
			TextView txtTodoTitle = (TextView) vi
					.findViewById(R.id.tv_todo_item_text);

			Long longTime = cursor.getLong(2);
			Calendar newItemDate = Calendar.getInstance();
			newItemDate.setTimeInMillis(longTime);
			txtTodoDueDate.setText(getDate(newItemDate));
			txtTodoTitle.setText(cursor.getString(1));
			if ((txtTodoTitle != null) && (txtTodoDueDate != null)) {
				int currDay = Calendar.getInstance(new Locale("he")).get(
						Calendar.DAY_OF_YEAR);
				int currYear = Calendar.getInstance(new Locale("he")).get(
						Calendar.YEAR);
				int itemDay = newItemDate.get(Calendar.DAY_OF_YEAR);
				int itemYear = newItemDate.get(Calendar.YEAR);

				if (itemYear < currYear || itemDay < currDay) {
					txtTodoTitle.setTextColor(getResources().getColor(
							R.color.red));
					txtTodoDueDate.setTextColor(getResources().getColor(
							R.color.red));
				}
			}
		}
	}

	private String getDate(Calendar item) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale(
				"he"));

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(item.getTimeInMillis());
		return formatter.format(calendar.getTime());
	}

	private class AddNewItemDB extends AsyncTask<Intent, Cursor, Void> {

		@Override
		protected Void doInBackground(Intent... data) {
			String newItemText = data[0].getStringExtra(ITEM_TITLE);

			Long newItemDate = data[0].getLongExtra(ITEM_DATE, -1);

			ToDoItem item = new ToDoItem(newItemText, newItemDate);
			// add the new todoItem to the SQL dataBase
			mSqlDbHandler.open();
			mSqlDbHandler.addTodo(item);

			Cursor cursor = mSqlDbHandler.getAllTodos();
			publishProgress(cursor);
			mSqlDbHandler.close();
			return null;
		}

		@Override
		protected void onProgressUpdate(Cursor... values) {
			mTodoAdapter.changeCursor(values[0]);
		}
	}

	private class GetListFromDB extends AsyncTask<Long, Cursor, Void> {


		@Override
		protected Void doInBackground(Long... mPressedId) {

			mSqlDbHandler.open();
			mSqlDbHandler.removeTodo(mPressedId[0]);

			// update the view bound to the database (by updating the
			// adapter)
			Cursor cursor = mSqlDbHandler.getAllTodos();
			publishProgress(cursor);

			mSqlDbHandler.close();

			return null;
		}

		@Override
		protected void onProgressUpdate(Cursor... values) {
			mTodoAdapter.changeCursor(values[0]);
		}
	}
}