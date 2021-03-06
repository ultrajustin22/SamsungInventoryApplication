package com.example.android.samsunginventoryapplication;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.samsunginventoryapplication.data.PhoneContract.PhoneEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    PhoneCursorAdapter mCursorAdapter;

    private static final int PHONE_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //When the fab is clicked, it will carry an intent to EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_phone);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });

        //If there is no data for the list, setup the empty ListView
        ListView phoneListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        phoneListView.setEmptyView(emptyView);

        //Setup Adapter to create a list item for each row of data in Cursor
        mCursorAdapter = new PhoneCursorAdapter(this,null);
        phoneListView.setAdapter(mCursorAdapter);
        phoneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Creating an Intent to go from CatalogActivity to EditorActivity
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                //Form the ContentURI that was being passed through the ID
                Uri currentPhoneUri = ContentUris.withAppendedId(PhoneEntry.CONTENT_URI,id);
                //Set the Uri on the data field of the intent
                intent.setData(currentPhoneUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(PHONE_LOADER,null,this);
    }

    //Inflates the menu for the Catalog Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog,menu);
        return true;
    }

    //Private method for deleting all phone entries
    private void deletePhone() {
        int rowsDeleted = getContentResolver().delete(PhoneEntry.CONTENT_URI,null,null);
        Log.v("CatalogActivity", rowsDeleted + "rows deleted from the database");

    }

    //Whenever the user clicks on one of the options below using the menu
    //Use switch-case combo, instead of if-else statements
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_delete_all_entries):
                deletePhone();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Creates a CursorLoader which loads the data through the Cursor
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {PhoneEntry._ID,PhoneEntry.COLUMN_PHONE_BRAND,PhoneEntry.COLUMN_PHONE_MODEL,
                PhoneEntry.COLUMN_PHONE_PRICE,PhoneEntry.COLUMN_PHONE_QUANTITY,PhoneEntry.COLUMN_PHONE_PICTURE};
        return new CursorLoader(this,PhoneEntry.CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Ensure the cursor is the last cursor before closing the Cursor
        mCursorAdapter.swapCursor(null);
    }
}
