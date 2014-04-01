package il.ac.huji.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.net.Uri;
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

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class TodoListManagerActivity extends Activity {

	private static final String PARSE_CLASS_NAME = "todo";
	private static final String PARSE_TODO_TITLE = "title";
	private static final String PARSE_TODO_DUE = "due";
	private static final String PARSE_TODO_DEVICE = "deviceId";

	private static final int CMD_DELETE = 0;
	private static final int ADD_TODO_ITEM = 0;
	protected static final String ITEM_DATE = "dueDate";
	protected static final String ITEM_TITLE = "title";
	private static final CharSequence CALL = "Call ";
	private static final int CMD_CALL = 1;
	private ListView mTodoList;
	private ArrayList<ToDoItem> todoArray = new ArrayList<ToDoItem>();
	private TodoAdapter mTodoAdapter;
	private String mPhoneNum;
	private String mDeviceId;
	private TodoDatabaseHandler mSqlDbHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		MyApplication tdlApp = (MyApplication) getApplication();
		mDeviceId = tdlApp.getDeviceId(this);
		initViews();
		bindDB();
		getParseItems();
	}

	protected void bindDB() {
		 //set the from and to arrays for the use of the todoListAdapter
		 String from[] = {TodoDatabaseHandler.COLUMN_TITLE,
				 TodoDatabaseHandler.COLUMN_DUE_DATE};
		 int to[] = {R.id.tv_todo_item_text, R.id.tv_todo_item_date};
		
		 //open the SQL database
		 mSqlDbHandler = new TodoDatabaseHandler(this);
		 mSqlDbHandler.open();
		
		// get all data from local db
		 Cursor cursor = mSqlDbHandler.getAllTodos();
		 mTodoAdapter = new TodoAdapter(this,
		 R.layout.todo_list_item, cursor, from, to, 2);
		 mTodoList.setAdapter(mTodoAdapter);
		 mSqlDbHandler.close();
	}

	protected void getParseItems() {
		this.todoArray = new ArrayList<ToDoItem>();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				PARSE_CLASS_NAME);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> parseObjects, ParseException e) {
				if (e == null) {
					for (ParseObject obj : parseObjects) {
						if (obj.getString(PARSE_TODO_DEVICE).equals(mDeviceId)) {
							ToDoItem item = new ToDoItem(obj, obj.getObjectId());
							todoArray.add(item);
						}
					}
				}
			}

		});
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

			 //remove the item from the sql database
            mSqlDbHandler.open();
            mSqlDbHandler.removeTodo(info.id);

            //update the view bound to the database (by updating the adapter)
            Cursor cursor = mSqlDbHandler.getAllTodos();
            mTodoAdapter.changeCursor(cursor);
            mSqlDbHandler.close();

			
			ToDoItem obj = todoArray.get(info.position);
			deleteParseObjFromParse(obj.getObjId());
			todoArray.remove(info.position);

			return true;

		} else if (item.getItemId() == CMD_CALL) {

			Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse(mPhoneNum));
			startActivity(dial);
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}
	
	private void deleteParseObjFromParse(String objId){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_CLASS_NAME);
		query.getInBackground(objId,new GetCallback<ParseObject>() {
		  public void done(ParseObject object, ParseException e) {
		    if (e == null) {
		     object.deleteInBackground();
		    } else {
		      // something went wrong
		    }
		  }
		});
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
				String newItemText = data.getStringExtra(ITEM_TITLE);

				Long newItemDate = data.getLongExtra(ITEM_DATE, -1);
				
				ToDoItem item = new ToDoItem(newItemText, newItemDate);
				 //add the new todoItem to the SQL dataBase
	            mSqlDbHandler.open();
	            mSqlDbHandler.addTodo(item);

	            Cursor cursor = mSqlDbHandler.getAllTodos();
	            mTodoAdapter.changeCursor(cursor);
	            mSqlDbHandler.close();
				// create a new ParseObject
				final ParseObject parseObject = new ParseObject(PARSE_CLASS_NAME);
				parseObject.put(PARSE_TODO_TITLE, newItemText);
				parseObject.put(PARSE_TODO_DUE, newItemDate);
				parseObject.put(PARSE_TODO_DEVICE, mDeviceId);
				


				// save the parseObject locally and on the cloud
				parseObject.saveInBackground(new SaveCallback() {
					  public void done(ParseException e) {
					    if (e == null) {
					      // Success!
					       String objectId = parseObject.getObjectId();
					      
					       ToDoItem item = new ToDoItem(parseObject, objectId);
					       todoArray.add(item);

					    } else {
					      // Failure!
					    }
					  }
					});
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
}
