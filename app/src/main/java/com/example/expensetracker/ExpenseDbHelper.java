package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "ExpenseDbHelper";
    private static final String DATABASE_NAME = "expenses.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_EXPENSES = "expenses";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DESCRIPTION = "description";

    // Create table query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_EXPENSES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CATEGORY + " TEXT NOT NULL," +
                    COLUMN_AMOUNT + " REAL NOT NULL," +
                    COLUMN_DATE + " TEXT NOT NULL," +
                    COLUMN_DESCRIPTION + " TEXT" +
                    ")";

    public ExpenseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
            Log.d(TAG, "Database table created");
        } catch (Exception e) {
            Log.e(TAG, "Error creating table: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    // Insert expense
    public long insertExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY, expense.getCategory());
            values.put(COLUMN_AMOUNT, expense.getAmount());
            values.put(COLUMN_DATE, expense.getDate());
            values.put(COLUMN_DESCRIPTION, expense.getDescription());

            id = db.insert(TABLE_EXPENSES, null, values);
            if (id == -1) {
                Log.e(TAG, "Failed to insert expense: " + expense.toString());
            } else {
                Log.d(TAG, "Expense inserted with ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error inserting expense: " + e.getMessage());
        } finally {
            db.close();
        }

        return id;
    }

    // Retrieve all expenses
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_EXPENSES,
                    new String[]{COLUMN_ID, COLUMN_CATEGORY, COLUMN_AMOUNT, COLUMN_DATE, COLUMN_DESCRIPTION},
                    null, null, null, null, COLUMN_DATE + " DESC" // Sort by date descending
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Expense expense = new Expense(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    );
                    expenses.add(expense);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving expenses: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return expenses;
    }

    // Update expense
    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY, expense.getCategory());
            values.put(COLUMN_AMOUNT, expense.getAmount());
            values.put(COLUMN_DATE, expense.getDate());
            values.put(COLUMN_DESCRIPTION, expense.getDescription());

            rowsAffected = db.update(
                    TABLE_EXPENSES,
                    values,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(expense.getId())}
            );

            if (rowsAffected > 0) {
                Log.d(TAG, "Expense updated: " + expense.toString());
            } else {
                Log.e(TAG, "Failed to update expense: " + expense.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating expense: " + e.getMessage());
        } finally {
            db.close();
        }

        return rowsAffected;
    }

    // Delete expense
    public int deleteExpense(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = 0;

        try {
            rowsDeleted = db.delete(
                    TABLE_EXPENSES,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            if (rowsDeleted > 0) {
                Log.d(TAG, "Expense deleted with ID: " + id);
            } else {
                Log.e(TAG, "Failed to delete expense with ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error deleting expense: " + e.getMessage());
        } finally {
            db.close();
        }

        return rowsDeleted;
    }
}