package com.example.app.data.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Manages the connection to and creation of the SQLite database for the application.
 */
public class DbConnection extends SQLiteOpenHelper {
    private static final String TAG = "DbConnection";
    // Database name and version
    private static final String DB_NAME = "app.db";
    private static final int DB_VERSION = 1;

    /**
     * Constructs a new instance of DbConnection.
     *
     * @param context The application context.
     */
    public DbConnection(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param db The SQLite database instance.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "onCreate called");
            Log.d(TAG, "Creating PASSWORD table...");
            db.execSQL(DbManager.CREATE_PASSWORD_TABLE);
            Log.d(TAG, "Creating USER table...");
            db.execSQL(DbManager.CREATE_USER_TABLE);
            Log.d(TAG, "Tables created successfully.");
        } catch (SQLException e) {
            Log.e(TAG, "Error creating tables: " + e.getMessage());
        }
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         The SQLite database instance.
     * @param oldVersion The old version of the database.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + DbManager.TB_PASSWORDS);
        db.execSQL("DROP TABLE IF EXISTS " + DbManager.TB_USERS);
        // Create new tables
        onCreate(db);
    }
}
