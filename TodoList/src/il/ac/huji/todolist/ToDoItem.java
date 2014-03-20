package il.ac.huji.todolist;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ToDoItem {
	Calendar _itemDate;
	String _itemText;

	public ToDoItem(Calendar inDate, String inText) {
		_itemDate = inDate;
		_itemText = inText;
	}

	public Calendar getItemDate() {
		return _itemDate;
	}

	public String getItemText() {
		return _itemText;
	}

	public String getDateInStringText() {
		return getDate();
	}

	private String getDate() {
		// Create a DateFormatter object for displaying date in specified
		// format.
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale(
				"he"));

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(_itemDate.getTimeInMillis());
		return formatter.format(calendar.getTime());
	}
}
