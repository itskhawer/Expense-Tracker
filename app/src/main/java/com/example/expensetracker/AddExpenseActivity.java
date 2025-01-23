package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
    private static final String TAG = "AddExpenseActivity";
    private EditText edtAmount, edtDate, edtDescription;
    private Spinner spinnerCategory;
    private ExpenseDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize views
        edtAmount = findViewById(R.id.edtAmount);
        edtDate = findViewById(R.id.edtDate);
        edtDescription = findViewById(R.id.edtDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        Button btnSave = findViewById(R.id.btnSaveExpense);
        dbHelper = new ExpenseDbHelper(this);

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.expense_categories, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Date picker
        edtDate.setOnClickListener(v -> showDatePicker());

        // Save button click
        btnSave.setOnClickListener(v -> {
            String category = spinnerCategory.getSelectedItem().toString().trim();
            String date = edtDate.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            String amountStr = edtAmount.getText().toString().trim();

            // Validate inputs
            if (amountStr.isEmpty() || date.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Parse amount with locale-aware parsing
                NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                double amount = format.parse(amountStr).doubleValue();

                // Create expense object
                Expense expense = new Expense(category, amount, date, description);

                // Insert into database
                long id = dbHelper.insertExpense(expense);
                if (id != -1) {
                    Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
                    clearInputs();
                } else {
                    Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Database insertion failed for: " + expense.toString());
                }
            } catch (ParseException e) {
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error parsing amount: " + e.getMessage());
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(
                this,
                (view, year, month, day) -> edtDate.setText(String.format("%d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        picker.show();
    }

    private void clearInputs() {
        edtAmount.setText("");
        edtDate.setText("");
        edtDescription.setText("");
        spinnerCategory.setSelection(0);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}