package org.vkedco.mobappdev.todos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoteDBHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "my_todo_list.db";
  private static final int DATABASE_VERSION = 1;

  public NoteDBHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(NoteTableInfo.CREATE_NOTE_TABLE_SQL_STMNT);
  }

  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
	    Log.v(NoteTableInfo.class.getName(), "Upgrading database from version "
	        + oldVersion + " to " + newVersion
	        + " and destroying old data");
	   database.execSQL("DROP TABLE IF EXISTS " + NoteTableInfo.NOTE_TABLE);
	   database.execSQL(NoteTableInfo.CREATE_NOTE_TABLE_SQL_STMNT);
  }

}
 
