package il.ac.huji.todolist;

import il.ac.huji.todolist.R.id;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListManagerActivity extends Activity {

	private static final int CMD_DELETE = 0;
	private EditText mEditNewItem;
	private ListView mTodoList;
	private ArrayList<String> todoArray = new ArrayList<String>();
	private TodoAdapter mTodoAdapter;
	private Menu mMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		initViews();
	}

	private void initViews() {
		mEditNewItem = (EditText) findViewById(R.id.et_new_item);
		mEditNewItem.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().trim().equals("")) {
					MenuItem addItem = mMenu.findItem(R.id.add_item);
					if (addItem != null) {
						addItem.setVisible(true);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
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
		} else {
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		mMenu = menu;
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.add_item) {
			String newItemText = mEditNewItem.getText().toString();
			if (!mEditNewItem.getText().toString().trim().equals("")) {
				todoArray.add(newItemText);
				mEditNewItem.setText("");
				MenuItem addItem = mMenu.findItem(R.id.add_item);
				if (addItem != null) {
					addItem.setVisible(false);
				}
				mTodoAdapter.notifyDataSetChanged();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public class TodoAdapter extends BaseAdapter {
		private static final int EVEN = 2;
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
			TextView todoItem = (TextView) vi
					.findViewById(R.id.tv_todo_item_text);
			todoItem.setText(todoArray.get(position));
			if (position % EVEN == 0) {
				todoItem.setTextColor(getResources().getColor(R.color.red));
			} else {
				todoItem.setTextColor(getResources().getColor(R.color.blue));
			}
			return vi;
		}

		@Override
		public int getCount() {
			return todoArray.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
}
