package com.example.android.inventoryapp;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.Contract.Entry;
import com.example.android.inventoryapp.data.DbHelper;

import java.io.ByteArrayOutputStream;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_LOADER = 0;

    private Uri currentUri;

    private EditText nameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private EditText supplierNameEditText;
    private EditText emailEditText;

    private Button incrementButton;
    private Button decrementButton;
    private Button orderButton;

    private ImageView imageView;

    private boolean hasChanged = false;

    DbHelper detailsDbHelper;

    private Bitmap bitmap;
    public byte[] photo;

    private String intentEmail;
    private String intentProduct;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            hasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);

        detailsDbHelper = new DbHelper(this);

        Intent intent = getIntent();
        currentUri = intent.getData();
        if (currentUri == null) {
            setTitle(getString(R.string.details_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.details_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }

        nameEditText = (EditText) findViewById(R.id.name_details);
        priceEditText = (EditText) findViewById(R.id.price_details);
        quantityEditText = (EditText) findViewById(R.id.quantity_details);
        supplierNameEditText = (EditText) findViewById(R.id.name_supplier);
        emailEditText = (EditText) findViewById(R.id.email_supplier);
        incrementButton = (Button) findViewById(R.id.increment_button);
        decrementButton = (Button) findViewById(R.id.decrement_button);
        imageView = (ImageView) findViewById(R.id.image_details);
        orderButton = (Button) findViewById(R.id.order_button);

        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        emailEditText.setOnTouchListener(touchListener);

        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementMethod();
                hasChanged = true;
            }
        });

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementMethod();
                hasChanged = true;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                hasChanged = true;
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderMore(v);
                hasChanged = true;
            }
        });
    }

    private void incrementMethod() {
        String oldValueString = quantityEditText.getText().toString();
        int oldValueInt;
        if (oldValueString.isEmpty()) {
            oldValueInt = 0;
        } else {

            oldValueInt = Integer.parseInt(oldValueString);
        }
        quantityEditText.setText(String.valueOf(oldValueInt + 1));
    }

    public void decrementMethod() {
        String oldValueString = quantityEditText.getText().toString();
        int oldValueInt;
        if (oldValueString.isEmpty()) {
            return;
        } else if
                (oldValueString.equals("0")) {
            return;
        } else {
            oldValueInt = Integer.parseInt(oldValueString);
            quantityEditText.setText(String.valueOf(oldValueInt - 1));
        }
    }


    private void saveProduct() {
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String suppNameString = supplierNameEditText.getText().toString().trim();
        String emailString = emailEditText.getText().toString().trim();
        photo = productImage(bitmap);

        if (currentUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(suppNameString) && TextUtils.isEmpty(emailString) &&
                TextUtils.isEmpty(quantityString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_NAME, nameString);
        values.put(Entry.COLUMN_PRICE, priceString);
        values.put(Entry.COLUMN_QUANTITY, quantityString);
        values.put(Entry.COLUMN_SUP_NAME, suppNameString);
        values.put(Entry.COLUMN_SUP_EMAIL, emailString);
        values.put(Entry.COLUMN_IMAGE, photo);

        if (currentUri == null) {
            Uri newUri = getContentResolver().insert(Entry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.details_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.details_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(currentUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.details_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.details_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_settings);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case R.id.action_settings:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!hasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!hasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                Entry._ID,
                Entry.COLUMN_NAME,
                Entry.COLUMN_PRICE,
                Entry.COLUMN_QUANTITY,
                Entry.COLUMN_IMAGE,
                Entry.COLUMN_SUP_NAME,
                Entry.COLUMN_SUP_EMAIL};
        return new CursorLoader(this, currentUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(Entry.COLUMN_NAME);
            int priceIndex = cursor.getColumnIndex(Entry.COLUMN_PRICE);
            int quantityIndex = cursor.getColumnIndex(Entry.COLUMN_QUANTITY);
            int supNameIndex = cursor.getColumnIndex(Entry.COLUMN_SUP_NAME);
            int emailIndex = cursor.getColumnIndex(Entry.COLUMN_SUP_EMAIL);
            int imageIndex = cursor.getColumnIndex(Entry.COLUMN_IMAGE);

            String name = cursor.getString(nameIndex);
            int price = cursor.getInt(priceIndex);
            String suppName = cursor.getString(supNameIndex);
            String email = cursor.getString(emailIndex);
            int currentQuantity = cursor.getInt(quantityIndex);
            byte[] currentImage = cursor.getBlob(imageIndex);

            intentEmail = email;
            intentProduct = name;

            nameEditText.setText(name);
            priceEditText.setText(String.valueOf(price));
            quantityEditText.setText(String.valueOf(currentQuantity));
            supplierNameEditText.setText(suppName);
            emailEditText.setText(email);
            imageView.setImageBitmap(convertToBitmap(currentImage));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameEditText.setText("");
        emailEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentUri != null) {
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.details_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.details_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public void orderMore(View view) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, intentEmail);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Order request for product - " + intentProduct);
        intent.putExtra(Intent.EXTRA_TEXT, "Hello, " + "\nI am currently looking to order an additional quantity of your exceptional product - " + intentProduct + "\nPlease send your best price offer.");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    public void selectImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 2:
                if (resultCode == RESULT_OK){
                    Uri chosenImage = data.getData();

                    if (chosenImage != null) {
                        bitmap = decodeUri(chosenImage, 400);
                        imageView.setImageBitmap(bitmap);
                    }
                }
        }
    }

    //Convert and resize our image to 400dp for faster uploading our images to DB
    protected Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE){
        try {
            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, options);

            // The new size that we want to scale to
            // final int REQUIRED_SIZE = size;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;
            while (true){
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE){
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, options2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Convert Bitmap to bytes
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private byte[] productImage(Bitmap b){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }

    private Bitmap convertToBitmap(byte[] b){
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }
}


