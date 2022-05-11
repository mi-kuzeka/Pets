/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * Pet's name value from database for editing mode
     */
    private String mPetName;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;

    /**
     * Pet's breed value from database for editing mode
     */
    private String mPetBreed;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    /**
     * Pet's weight value from database for editing mode
     */
    private int mPetWeight;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mGenderSpinner;

    /**
     * Pet's gender value from database for editing mode
     */
    private int mPetGender;

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_PET_LOADER = 0;

    private Uri mCurrentPetUri;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    private boolean mIsNewPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mCurrentPetUri = getIntent().getData();
        mIsNewPet = (mCurrentPetUri == null);

        if (mIsNewPet) {
            this.setTitle(R.string.editor_activity_title_new_pet);
        } else {
            this.setTitle(R.string.editor_activity_title_edit_pet);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mGenderSpinner = findViewById(R.id.spinner_gender);

        if (!mIsNewPet) {
            LoaderManager.getInstance(this)
                    .initLoader(EXISTING_PET_LOADER, null, this);
        }

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; //Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; //Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; //Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    // Check if user has not changed any value in the fields
    private boolean petHasNotChanged() {
        boolean petNameHasNotChanged =
                mPetName.equals(getStringFromEditText(mNameEditText, ""));
        boolean petBreedHasNotChanged =
                mPetBreed.equals(getStringFromEditText(mBreedEditText, PetEntry.BREED_DEFAULT));
        boolean petGenderHasNotChanged = (mPetGender == mGender);
        boolean petWeightHasNotChanged =
                (mPetWeight == getIntFromEditText(mWeightEditText, PetEntry.WEIGHT_DEFAULT));
        return petNameHasNotChanged && petBreedHasNotChanged &&
                petGenderHasNotChanged && petWeightHasNotChanged;
    }

    /**
     * Get user input from editor and save pet into database.
     */
    private boolean savePet() {
        if (dataIsInvalid()) {
            showToast("Fill the name of pet");
            return false;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME,
                getStringFromEditText(mNameEditText, ""));
        values.put(PetEntry.COLUMN_PET_BREED,
                getStringFromEditText(mBreedEditText, PetEntry.BREED_DEFAULT));
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT,
                getIntFromEditText(mWeightEditText, PetEntry.WEIGHT_DEFAULT));

        String toastMessage;
        if (mIsNewPet) {
            // Insert a new pet row into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
            toastMessage = (newUri == null) ?
                    // Toast message when new pet has failed to be inserted
                    getString(R.string.editor_insert_pet_failed) :
                    // Toast message when new pet has been successfully inserted
                    getString(R.string.editor_insert_pet_successful);
        } else {
            int newRowId = getContentResolver().update(mCurrentPetUri, values,
                    null, null);
            toastMessage = (newRowId == 0) ?
                    // Toast message when current pet has failed to be updated
                    getString(R.string.editor_update_pet_failed) :
                    // Toast message when current pet was successfully updated
                    getString(R.string.editor_update_pet_successful);
        }

        showToast(toastMessage);
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean dataIsInvalid() {
        String nameText = mNameEditText.getText().toString().trim();
        return TextUtils.isEmpty(nameText);
    }

    /**
     * Get trimmed text from EditText
     */
    private String getStringFromEditText(EditText editText, String defaultText) {
        String resultText = editText.getText().toString().trim();
        if (TextUtils.isEmpty(resultText)) return defaultText;
        return resultText;
    }

    /**
     * Get integer from EditText
     */
    private int getIntFromEditText(EditText editText, int defaultValue) {
        String resultText = editText.getText().toString().trim();
        if (TextUtils.isEmpty(resultText)) return defaultValue;
        return Integer.parseInt(resultText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        // If this is a new pet, hide the "Delete" menu item;
        if (mIsNewPet) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                if (savePet()) {
                    // Exit activity
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (petHasNotChanged()) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        (dialogInterface, i) -> {
                            // User clicked "Discard" button, navigate to parent activity.
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == EXISTING_PET_LOADER) {
            String[] projection = {
                    PetEntry._ID,
                    PetEntry.COLUMN_PET_NAME,
                    PetEntry.COLUMN_PET_BREED,
                    PetEntry.COLUMN_PET_GENDER,
                    PetEntry.COLUMN_PET_WEIGHT
            };
            return new CursorLoader(
                    this,
                    mCurrentPetUri,
                    projection,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            // Extract properties from cursor
            mPetName = data.getString(
                    data.getColumnIndexOrThrow(PetEntry.COLUMN_PET_NAME));
            mPetBreed = data.getString(
                    data.getColumnIndexOrThrow(PetEntry.COLUMN_PET_BREED));
            mPetWeight = data.getInt(
                    data.getColumnIndexOrThrow(PetEntry.COLUMN_PET_WEIGHT));
            mPetGender = data.getInt(
                    data.getColumnIndexOrThrow(PetEntry.COLUMN_PET_GENDER));
            // Populate fields with extracted properties
            mNameEditText.setText(mPetName);
            mBreedEditText.setText(mPetBreed);
            mWeightEditText.setText(String.valueOf(mPetWeight));
            mGenderSpinner.setSelection(mPetGender);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Clear all fields
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (petHasNotChanged()) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                (dialogInterface, i) -> {
                    // User clicked "Discard" button, close the current activity.
                    finish();
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }
}