package com.example.android.pets.data;

import android.provider.BaseColumns;

public final class PetContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PetContract() {}

    /* Inner class that defines the "pets" table contents */
    public static final class PetEntry implements BaseColumns {
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
         * Default breed for the pet
         */
        public static final String BREED_DEFAULT = "Unknown";

        /**
         * Default weight for the pet
         */
        public static final int WEIGHT_DEFAULT = 0;
    }
}
