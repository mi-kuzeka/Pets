package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PetContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PETS = "pets";


    /* Inner class that defines the "pets" table contents */
    public static final class PetEntry implements BaseColumns {
        /**
         * The content URI to access the pet data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        // Table name
        public static final String TABLE_NAME = "pets";

        // Pets primary key
        //public static final String _ID = BaseColumns._ID;
        // Column for the pet's name
        public static final String COLUMN_PET_NAME = "name";
        // Column for the pet's breed
        public static final String COLUMN_PET_BREED = "breed";
        // Column for the pet's gender
        public static final String COLUMN_PET_GENDER = "gender";
        // Column for the pet's weight
        public static final String COLUMN_PET_WEIGHT = "weight";

        /**
         * Possible values for the pet's gender
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN},
         * {@link #GENDER_MALE}, or {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            return gender == GENDER_UNKNOWN ||
                    gender == GENDER_MALE ||
                    gender == GENDER_FEMALE;
        }

        /**
         * Default weight for the pet
         */
        public static final int WEIGHT_DEFAULT = 0;
    }
}
