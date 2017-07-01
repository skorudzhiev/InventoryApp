package com.example.android.inventoryapp;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.Contract.Entry;

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name_list);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_list);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_list);
        Button saleButton = (Button) view.findViewById(R.id.sell_button);

        int nameColIndex = cursor.getColumnIndex(Entry.COLUMN_NAME);
        int quantityColIndex = cursor.getColumnIndex(Entry.COLUMN_QUANTITY);
        int priceColIndex = cursor.getColumnIndex(Entry.COLUMN_PRICE);

        int id = cursor.getInt(cursor.getColumnIndex(Entry._ID));
        String inName = cursor.getString(nameColIndex);
        final Integer inQuantity = cursor.getInt(quantityColIndex);
        String inPrice = cursor.getString(priceColIndex);

        nameTextView.setText(inName);
        priceTextView.setText(inPrice);
        quantityTextView.setText(String.valueOf(inQuantity));

        final Uri currentProductUri = ContentUris.withAppendedId(Entry.CONTENT_URI, id);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                if (inQuantity > 0) {
                    int dq = inQuantity;
                    values.put(Entry.COLUMN_QUANTITY, -- dq);
                    resolver.update(currentProductUri, values, null, null);
                    context.getContentResolver().notifyChange(currentProductUri, null);
                } else {
                    Toast.makeText(context, "Item out of stock", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

