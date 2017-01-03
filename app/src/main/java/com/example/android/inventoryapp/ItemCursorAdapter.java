package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.android.inventoryapp.data.ItemContract.ItemEntry;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Currency;

import static com.example.android.inventoryapp.data.ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY;

/**
 * {@link ItemCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of item data as its data source. This adapter knows
 * how to create list items for each row of item data in the {@link Cursor}.
 */
public class ItemCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private Context generalContext;
    private Cursor generalCursor;

    /**
     * Constructs a new {@link ItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Most recent code version
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        generalContext = context;
        generalCursor = cursor;

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.item_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.item_price);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);

        nameTextView.setSelected(true);

        Button sellButton = (Button) view.findViewById(R.id.sell_button);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = quantityTextView.getText().toString().trim();
                String[] quantityList = quantityString.split(" ");
                int quantityInt = Integer.parseInt(quantityList[0].trim());

                if (quantityInt > 0) {
                    // Request data base and decrease quantity by 1
                    quantityInt = quantityInt - 1;

                    int quantityIdColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
                    final long itemId = cursor.getLong(quantityIdColumnIndex);
                    Uri mCurrentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, itemId);

                    // Defines an object to contain the updated values
                    ContentValues values = new ContentValues();
                    values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantityInt);

                    quantityTextView.setText(String.valueOf(quantityInt));

                    int rowsUpdate = context.getContentResolver().update(mCurrentItemUri, values, null, null);
                }
            }
        });

        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_ITEM_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_IMAGE);

        // Read the item attributes from the Cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        int itemQuantity = cursor.getInt(quantityColumnIndex);
        double itemPrice = cursor.getDouble(priceColumnIndex);
        String itemImage = cursor.getString(imageColumnIndex);

        if (!TextUtils.isEmpty(itemImage)) {
            imageView.setImageURI(Uri.parse(itemImage));
        } else {
            imageView.setImageResource(R.drawable.no_image);
        }

        DecimalFormat priceFormat = new DecimalFormat("##.00");
        String priceFormatted = priceFormat.format(itemPrice);
        String fullPrice = Currency.getInstance(Locale.getDefault()).getSymbol() + priceFormatted;

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(itemName);
        quantityTextView.setText(String.valueOf(itemQuantity) + context.getString(R.string.units));
        priceTextView.setText(fullPrice);
    }
}