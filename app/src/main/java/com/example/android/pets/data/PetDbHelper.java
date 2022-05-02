package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.pets.data.PetContract.PetEntry;

public class PetDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Shelter.db";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + PetEntry.TABLE_NAME + " ("
                        + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                        + PetEntry.COLUMN_PET_BREED + " TEXT NOT NULL DEFAULT \""
                        + PetEntry.BREED_DEFAULT + "\", "
                        + PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                        + PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT "
                        + PetEntry.WEIGHT_DEFAULT + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
