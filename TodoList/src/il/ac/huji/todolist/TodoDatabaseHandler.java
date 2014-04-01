package il.ac.huji.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
*handles all of the todoList database actions
*/
public class TodoDatabaseHandler {
public static final String TABLE_NAME = "todos";
public static final String COLUMN_ID = "_id";
public static final String COLUMN_TITLE = "title";
public static final String COLUMN_DUE_DATE = "due";
private TodoListSQLiteHelper mSqlHelper;
private SQLiteDatabase mDb;

public TodoDatabaseHandler(Context context) {
mSqlHelper = new TodoListSQLiteHelper(context);
}

public void close() {
mDb.close();
}

public void open() {
mDb = mSqlHelper.getWritableDatabase();
}

public void addTodo(ToDoItem item) {
ContentValues currTodo = new ContentValues();
currTodo.put(COLUMN_DUE_DATE, item.getDate());
currTodo.put(COLUMN_TITLE, item.getText());
mDb.insert(TABLE_NAME, null, currTodo);
}

public Cursor getAllTodos() {
Cursor cursor = mDb.query(TABLE_NAME, new String[] { COLUMN_ID,
COLUMN_TITLE, COLUMN_DUE_DATE }, null, null, null, null, null);
if (cursor != null) {
cursor.moveToFirst();
}

return cursor;
}

public void removeTodo(long id) {
String currId = Long.toString(id);
mDb.delete(TABLE_NAME, COLUMN_ID + " = " + currId, null);
}

public ToDoItem cursorToDataItem(Cursor cursor) {
return new ToDoItem(cursor.getString(1), cursor.getLong(2));
}

private class TodoListSQLiteHelper extends SQLiteOpenHelper {

private static final int DB_VER = 1;
private static final String DB_NAME = "todo_db";
private static final String CREATION_QUERY = "create table "
+ TABLE_NAME + "(" + COLUMN_ID
+ " integer primary key autoincrement, " + COLUMN_TITLE
+ " text not null, " + COLUMN_DUE_DATE + " long not null);";
private static final String DELETION_QUERY = "drop table if exists "
+ TABLE_NAME;

public TodoListSQLiteHelper(Context context) {
super(context, DB_NAME, null, DB_VER);
}

@Override
public void onCreate(SQLiteDatabase db) {
db.execSQL(CREATION_QUERY);
}

/**
* drops all information stored in the table and re-creates a new
* version of it
*/
@Override
public void onUpgrade(SQLiteDatabase db, int i, int i2) {
db.execSQL(DELETION_QUERY);
db.execSQL(CREATION_QUERY);
}
}
}