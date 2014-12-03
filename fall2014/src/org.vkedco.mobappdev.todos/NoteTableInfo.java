package org.vkedco.mobappdev.todos;

public class NoteTableInfo {

  // Public constants that describe the notes table in our
  // database; if there are mutiple tables, their info should
  // be published for easy of programmatic access.
  public static final String NOTE_TABLE 		= "notes";
  public static final String COLUMN_ID 			= "_id";
  public static final String COLUMN_CATEGORY 	= "category";
  public static final String COLUMN_TITLE 		= "title";
  public static final String COLUMN_DATE		= "date";
  public static final String COLUMN_DESCRIPTION = "description";

  // Note table creation SQL statement
  public static final String CREATE_NOTE_TABLE_SQL_STMNT = "create table " 
      + NOTE_TABLE
      + "(" 
      + COLUMN_ID 			+ " integer primary key autoincrement, " 
      + COLUMN_CATEGORY 	+ " text not null, " 
      + COLUMN_TITLE 		+ " text not null, " 
      + COLUMN_DATE			+ " text not null, "
      + COLUMN_DESCRIPTION 	+ " text not null" 
      + ");";
} 
