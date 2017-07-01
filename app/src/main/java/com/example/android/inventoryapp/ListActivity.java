package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.Contract.Entry;

public class ListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    InventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        ListView inventoryListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        adapter = new InventoryAdapter(this, null);
        inventoryListView.setAdapter(adapter);

        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, DetailsActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(Entry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }


    private void insertProduct() {
        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_NAME, "Sample");
        values.put(Entry.COLUMN_QUANTITY, 1);
        values.put(Entry.COLUMN_PRICE, 1);
        values.put(Entry.COLUMN_SUP_NAME, "Supplier");
        values.put(Entry.COLUMN_SUP_EMAIL, "E.mail@mail.mail");
        values.put(Entry.COLUMN_IMAGE, String.valueOf(findViewById(R.id.image_details)));

        Uri newUri = getContentResolver().insert(Entry.CONTENT_URI, values);
    }

    private void deleteAllProducts(){
        int rowsDeleted = getContentResolver().delete(Entry.CONTENT_URI, null, null);
        Log.v("ListActivity", rowsDeleted + " rows deleted from inventory database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert:
                insertProduct();
                return true;
            case R.id.action_delete:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                Entry._ID,
                Entry.COLUMN_NAME,
                Entry.COLUMN_QUANTITY,
                Entry.COLUMN_PRICE };
        return new CursorLoader(this,
                Entry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
