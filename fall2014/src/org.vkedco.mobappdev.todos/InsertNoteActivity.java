package org.vkedco.mobappdev.todos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class InsertNoteActivity extends Activity {
  Spinner mSpnrNoteCategory;
  EditText mEdTxtxTitle;
  EditText mEdTxtDescription;
  Button mBtnSave;
  Uri mNoteUri;
  DateFormat mDateFormatUS = null;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.todo_edit);

    mSpnrNoteCategory = (Spinner) findViewById(R.id.note_category);
    mEdTxtxTitle = (EditText) findViewById(R.id.note_title);
    mEdTxtDescription = (EditText) findViewById(R.id.note_description);
    mBtnSave = (Button) findViewById(R.id.btn_save);
    mDateFormatUS = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.US);

    Bundle extras = getIntent().getExtras();

    // check if bundle is from the saved Instance
    mNoteUri = (bundle == null) ? null : (Uri) bundle
        .getParcelable(NoteContentProvider.CONTENT_ITEM_TYPE);

    // check if this activity is started by another activity
    if (extras != null) {
      mNoteUri = extras
          .getParcelable(NoteContentProvider.CONTENT_ITEM_TYPE);

      fillData(mNoteUri);
    }

    mBtnSave.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (TextUtils.isEmpty(mEdTxtxTitle.getText().toString())) {
          toastEmptyNoteTitleWarning();
        } else {
          setResult(RESULT_OK);
          finish();
        }
      }

    });
  }

  private void fillData(Uri uri) {
    String[] projection = { NoteTableInfo.COLUMN_TITLE,
        NoteTableInfo.COLUMN_DESCRIPTION, NoteTableInfo.COLUMN_CATEGORY };
    Cursor cursor = getContentResolver().query(uri, projection, null, null,
        null);
    if (cursor != null) {
      cursor.moveToFirst();
      String category = cursor.getString(cursor
          .getColumnIndexOrThrow(NoteTableInfo.COLUMN_CATEGORY));

      for (int i = 0; i < mSpnrNoteCategory.getCount(); i++) {

        String s = (String) mSpnrNoteCategory.getItemAtPosition(i);
        if (s.equalsIgnoreCase(category)) {
          mSpnrNoteCategory.setSelection(i);
        }
      }

      mEdTxtxTitle.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(NoteTableInfo.COLUMN_TITLE)));
      mEdTxtDescription.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(NoteTableInfo.COLUMN_DESCRIPTION)));

      cursor.close();
    }
  }

  protected void onSaveInstanceState(Bundle outState) {
	
    super.onSaveInstanceState(outState);
    Log.v("InsertNoteActivity", "onSaveInstanceState()");
    saveUserNote();
    outState.putParcelable(NoteContentProvider.CONTENT_ITEM_TYPE, mNoteUri);
  }
  
  // When Save button or Back button is clicked and this activity is finished,
  // onPause() is called, so the note will be saved so long as its
  // title is not empty

  @Override
  protected void onPause() {
    super.onPause();
    Log.v("InsertNoteActivity", "onPause()");
    saveUserNote();
  }

 
  private void saveUserNote() {
	Log.v("InsertNoteActivity", "saveUserNote()");
    String category = (String) mSpnrNoteCategory.getSelectedItem();
    String title = mEdTxtxTitle.getText().toString();
    String description = mEdTxtDescription.getText().toString();

    // save note only if either title or description are available

    if (description.length() == 0 && title.length() == 0) {
      return;
    }

    ContentValues values = new ContentValues();
    values.put(NoteTableInfo.COLUMN_CATEGORY, category);
    values.put(NoteTableInfo.COLUMN_TITLE, title);
    values.put(NoteTableInfo.COLUMN_DESCRIPTION, description);
    values.put(NoteTableInfo.COLUMN_DATE, mDateFormatUS.format(new Date()));

    if (mNoteUri == null) {
      // get the content resolver to insert a new item
      mNoteUri = getContentResolver().insert(NoteContentProvider.CONTENT_URI, values);
    } else {
      // Update item
      getContentResolver().update(mNoteUri, values, null, null);
    }
  }

  private void toastEmptyNoteTitleWarning() {
    Toast.makeText(InsertNoteActivity.this, "You must enter a note's title",
        Toast.LENGTH_LONG).show();
  }
} 
