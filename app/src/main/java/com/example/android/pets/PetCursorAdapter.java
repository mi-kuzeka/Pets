package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * {@link PetCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */
public class PetCursorAdapter extends CursorAdapter {


    /**
     * Constructs a new {@link PetCursorAdapter}.
     *
     * @param context the context
     * @param c       the cursor from which to get the data
     */
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context   app context.
     * @param cursor    the cursor from which to get the data. The cursor is already
     *                  moved to the correct position.
     * @param viewGroup the parent to which the new view is attached to.
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context)
                .inflate(R.layout.list_item, viewGroup, false);
    }

    /**
     * This method binds the pet data (in the current row pointed by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    existing view, returned earlier by newView() method.
     * @param context app context.
     * @param cursor  the cursor from which to get the data. The cursor is already moved
     *                to the correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvPetName = view.findViewById(R.id.pet_name);
        TextView tvPetSummary = view.findViewById(R.id.pet_summary);
        // Extract properties from cursor
        String petName = cursor.getString(
                cursor.getColumnIndexOrThrow(PetEntry.COLUMN_PET_NAME));
        String petSummary = cursor.getString(
                cursor.getColumnIndexOrThrow(PetEntry.COLUMN_PET_BREED));
        // Populate fields with extracted properties
        tvPetName.setText(petName);
        if (TextUtils.isEmpty(petSummary)) {
            tvPetSummary.setText(PetEntry.BREED_DEFAULT);
        } else {
            tvPetSummary.setText(petSummary);
        }
    }
}
