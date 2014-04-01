package il.ac.huji.todolist;

import com.parse.ParseObject;

public class ToDoItem {
	long _itemDate;
	String _itemText;
	ParseObject _object;
	String _objectId;

	public ToDoItem(String txt, long date) {
		_itemDate = date;
		_itemText = txt;
	}

	public ToDoItem(ParseObject obj, String id) {
		_object = obj;
		_objectId = id;
	}

	public String getObjId() {
		return _objectId;
	}

	public ParseObject getParseObj() {
		return _object;
	}

	public String getText() {
		return _itemText;
	}

	public long getDate() {
		return _itemDate;
	}
}
