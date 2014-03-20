package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
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
	private ArrayList<ToDoItem> todoArray = new ArrayList<ToDoItem>();
	private TodoAdapter mTodoAdapter;
	private String mPhoneNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		initViews();
	}

	private void initViews() {
		
		mTodoList = (ListView) findViewById(R.id.list_todo);
		registerForContextMenu(mTodoList);
		mTodoAdapter = new TodoAdapter(this);
		mTodoList.setAdapter(mTodoAdapter);
		mTodoAdapter.notifyDataSetChanged();

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

			todoArray.remove(info.position);
			mTodoAdapter.notifyDataSetChanged();
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
				String newItemText = data.getStringExtra(ITEM_TITLE);

				Calendar newItemDate = Calendar.getInstance();
				newItemDate.setTimeInMillis(data.getLongExtra(ITEM_DATE, -1));
				ToDoItem tmp = new ToDoItem(newItemDate, newItemText);
				todoArray.add(tmp);
				mTodoAdapter.notifyDataSetChanged();

			}
		}
	}

	public class TodoAdapter extends BaseAdapter {
		Activity mActivity;

		public TodoAdapter(Activity a) {
			mActivity = a;

		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			View vi = v;
			if (v == null) {
				LayoutInflater inflater = (LayoutInflater) mActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				vi = inflater.inflate(R.layout.todo_list_item, null);
			}
			TextView txtTodoDueDate = (TextView) vi
					.findViewById(R.id.tv_todo_item_date);
			TextView txtTodoTitle = (TextView) vi
					.findViewById(R.id.tv_todo_item_text);

			txtTodoDueDate.setText(todoArray.get(position)
					.getDateInStringText());
			txtTodoTitle.setText((CharSequence) todoArray.get(position)
					.getItemText());

			Calendar itemDate = (Calendar) todoArray.get(position)
					.getItemDate();
			int currDay = Calendar.getInstance(new Locale("he")).get(
					Calendar.DAY_OF_YEAR);
			int currYear = Calendar.getInstance(new Locale("he")).get(
					Calendar.YEAR);
			int itemDay = itemDate.get(Calendar.DAY_OF_YEAR);
			int itemYear = itemDate.get(Calendar.YEAR);

			if (itemYear < currYear || itemDay < currDay) {
				txtTodoTitle.setTextColor(getResources().getColor(R.color.red));
				txtTodoDueDate.setTextColor(getResources()
						.getColor(R.color.red));
			}

			return vi;
		}

		@Override
		public int getCount() {
			return todoArray.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}
	}
}
