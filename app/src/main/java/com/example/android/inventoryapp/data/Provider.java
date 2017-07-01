package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventoryapp.data.Contract.Entry;

public class Provider extends ContentProvider {

    public static final String LOG_TAG = Provider.class.getSimpleName();

    private static final int INVENTORY = 100;

    private static final int INVENTORY_ID = 101;

    private static final UriMatcher newUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        newUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INVENTORY, INVENTORY);
        newUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private DbHelper newDbHelper;

    @Override
    public boolean onCreate() {
        newDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = newDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = newUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(Entry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = Entry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(Entry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = newUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(Entry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer quantity = values.getAsInteger(Entry.COLUMN_QUANTITY);
        if (quantity == null && quantity <= 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }

        Integer price = values.getAsInteger(Entry.COLUMN_PRICE);
        if (price == null && price <= 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        SQLiteDatabase database = newDbHelper.getWritableDatabase();
        long id = database.insert(Entry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = newUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = Entry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(Entry.COLUMN_NAME)) {
            String name = values.getAsString(Entry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(Entry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(Entry.COLUMN_QUANTITY);
            if (quantity == null && quantity <= 0) {
                throw new IllegalArgumentException("Product requires a valid quantity");
            }
        }

        if (values.containsKey(Entry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(Entry.COLUMN_PRICE);
            if (price == null && price <= 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = newDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(Entry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = newDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = newUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                rowsDeleted = database.delete(Entry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = Entry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(Entry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = newUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return Entry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return Entry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
