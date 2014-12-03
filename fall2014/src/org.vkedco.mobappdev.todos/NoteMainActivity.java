package org.vkedco.mobappdev.todos;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/*
 * NoteMainActivity displays the existing todo items
 * in a list
 * 
 * You can create new ones via the ActionBar entry "Insert"
 * You can delete existing ones via a long press on the item
 * 
 * The table has the following schema:
 * note(id integer primary key autoincrement, category text not null, summary text not null, 
 *      description text not null)
 * select * from note; returns the following results:
 * -------------------------------------------------
 * id	| category	|	title	|	description
 * -------------------------------------------------
 * 5 	| Research  | 	note 1 	| debug my program
 * -------------------------------------------------
 * 6 	| Teaching	|   note 2 	| write a mobappdev
 * -------------------------------------------------
 * 
 */

public class NoteMainActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	static final int DELETE_ID = Menu.FIRST + 1;
	SimpleCursorAdapter mCursorAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list);
		this.getListView().setDividerHeight(2);
		getDataFromDBIntoGUI();
		registerForContextMenu(getListView());
	}

	// create the menu based on the XML definition
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			startInsertNoteActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// This is how you delete an item through a long click.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
			// This is how the context provider is accessed
			Log.v("MENUITEM", "id=" + info.id + "; position=" + info.position);
			Uri uri = Uri.parse(NoteContentProvider.CONTENT_URI + "/" + info.id);
			Log.v("URI", uri.toString());
			getContentResolver().delete(uri, null, null);
			getDataFromDBIntoGUI();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void startInsertNoteActivity() {
		Intent i = new Intent(this, InsertNoteActivity.class);
		startActivity(i);
	}

	// Opens the second activity if an entry is clicked
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, InsertNoteActivity.class);
		Uri todoUri = Uri.parse(NoteContentProvider.CONTENT_URI + "/" + id);
		i.putExtra(NoteContentProvider.CONTENT_ITEM_TYPE, todoUri);
		startActivity(i);
	}

	// This method allows you to get the data from the database.
	private void getDataFromDBIntoGUI() {
		// This is an array of table column names.
		String[] db_columns = new String[] { NoteTableInfo.COLUMN_ID, NoteTableInfo.COLUMN_TITLE, 
				NoteTableInfo.COLUMN_DATE};
		// This is an array of gui ids to which the appropriate values from the columns are
		// mapped. In other words, the values from NoteTableInfo.COLUMN_ID are mapped into
		// R.id.note_id and the values from NoteTableInfo.COLUMN_TITLE are mapped into
		// R.id.note_title.
		int[] gui_ids = new int[] { R.id.note_id, R.id.note_title, R.id.note_date };
		// The main activity uses a Loader to manage the Cursor asynchronously. 
		getLoaderManager().initLoader(0, null, this);
		mCursorAdapter = new SimpleCursorAdapter(this, R.layout.todo_row, null, db_columns, gui_ids, 0);
		setListAdapter(mCursorAdapter);
	}
	
	// This is what we need to delete an item by a long click.
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	// creates a new loader after the initLoader () call
	// This is important:  the Loader that is created and returned but the programmer,
	// according to the android documentation, does not need to manage its reference.
	// The LoadManager manages the life of the loader automatically. The LoadManager
	// starts and stops loading when necessary and maintains the state of the loader
	// automatically. The programmer most often uses only the LoaderManager.LoaderCallbacks
	// methods to manage the loading process when a particular event occurs.
	// For more details, read http://developer.android.com/guide/components/loaders.html#callback
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is how the IDs get into GUIs.
		String[] projection = { NoteTableInfo.COLUMN_ID, NoteTableInfo.COLUMN_TITLE, NoteTableInfo.COLUMN_DATE };
		CursorLoader cursorLoader = new CursorLoader(this, NoteContentProvider.CONTENT_URI, 
				projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mCursorAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		mCursorAdapter.swapCursor(null);
	}

}
