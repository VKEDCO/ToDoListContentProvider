package org.vkedco.mobappdev.todos;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class NoteContentProvider extends ContentProvider {

	// mNoteHelper
	private NoteDBHelper mNoteHelper;

	// used for the UriMacher
	private static final int NOTES   = 1;
	private static final int NOTE_ID = 2;

	private static final String AUTHORITY = "org.vkedco.mobappdev.notecontentprovider";

	private static final String BASE_PATH = "notes";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/notes";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/note";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTE_ID);
	}

	@Override
	public boolean onCreate() {
		mNoteHelper = new NoteDBHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// check if the caller has requested a column which does not exists
		validateProjectedColumns(projection);

		// Set the table
		queryBuilder.setTables(NoteTableInfo.NOTE_TABLE);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case NOTES:
			break;
		case NOTE_ID:
			// adding the ID to the original query
			queryBuilder.appendWhere(NoteTableInfo.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = mNoteHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mNoteHelper.getWritableDatabase();
		//int rowsDeleted = 0;
		long id = 0;
		switch (uriType) {
		case NOTES:
			id = sqlDB.insert(NoteTableInfo.NOTE_TABLE, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mNoteHelper.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case NOTES:
			rowsDeleted = sqlDB.delete(NoteTableInfo.NOTE_TABLE, selection,
					selectionArgs);
			break;
		case NOTE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(NoteTableInfo.NOTE_TABLE,
						NoteTableInfo.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(NoteTableInfo.NOTE_TABLE,
						NoteTableInfo.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mNoteHelper.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case NOTES:
			rowsUpdated = sqlDB.update(NoteTableInfo.NOTE_TABLE, values, selection,
					selectionArgs);
			break;
		case NOTE_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(NoteTableInfo.NOTE_TABLE, values,
						NoteTableInfo.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(NoteTableInfo.NOTE_TABLE, values,
						NoteTableInfo.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	final static String[] validColumNames = {
		NoteTableInfo.COLUMN_CATEGORY,
		NoteTableInfo.COLUMN_TITLE, 
		NoteTableInfo.COLUMN_DESCRIPTION,
		NoteTableInfo.COLUMN_DATE,
		NoteTableInfo.COLUMN_ID };
	final HashSet<String> validColumnSet = new HashSet<String>(Arrays.asList(validColumNames));
	
	private void validateProjectedColumns(String[] projection) {
		if (projection != null) {
			HashSet<String> projectedColumnSet = new HashSet<String>(Arrays.asList(projection));
			// make sure that all projected column names are legal
			if (!validColumnSet.containsAll(projectedColumnSet)) {
				throw new IllegalArgumentException("Unknown column names in projection");
			}
		}
	}

}
