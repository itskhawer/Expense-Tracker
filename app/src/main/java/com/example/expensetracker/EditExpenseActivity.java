package com.example.expensetracker;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditExpenseActivity extends AppCompatActivity {

    private EditText edtAmount, edtDate, edtDescription;
    private Spinner spinnerCategory;
    private Button btnUpdateExpense, btnDeleteExpense;
    private ExpenseDbHelper dbHelper;
    private Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize views
        edtAmount = findViewById(R.id.edtAmount);
        edtDate = findViewById(R.id.edtDate);
        edtDescription = findViewById(R.id.edtDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnUpdateExpense = findViewById(R.id.btnSaveExpense);
        btnDeleteExpense = findViewById(R.id.btnDeleteExpense);
        dbHelper = new ExpenseDbHelper(this);

        // Get the expense from the intent
        expense = getIntent().getParcelableExtra("expense");

        // Populate the fields with the expense data
        edtAmount.setText(String.valueOf(expense.getAmount()));
        edtDate.setText(expense.getDate());
        edtDescription.setText(expense.getDescription());

        // Set up the spinner with expense categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set the selected category in the spinner
        int categoryPosition = adapter.getPosition(expense.getCategory());
        spinnerCategory.setSelection(categoryPosition);

        // Show the delete button
        btnDeleteExpense.setVisibility(View.VISIBLE);

        // Update expense
        btnUpdateExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountText = edtAmount.getText().toString();
                String category = spinnerCategory.getSelectedItem().toString();
                String date = edtDate.getText().toString();
                String description = edtDescription.getText().toString();

                // Data validation
                if (amountText.isEmpty() || Double.parseDouble(amountText) <= 0) {
                    Toast.makeText(EditExpenseActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(amountText);

                // Update the expense object
                expense.setCategory(category);
                expense.setAmount(amount);
                expense.setDate(date);
                expense.setDescription(description);

                // Update the expense in the database
                int rowsAffected = dbHelper.updateExpense(expense);
                if (rowsAffected > 0) {
                    Toast.makeText(EditExpenseActivity.this, "Expense updated!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditExpenseActivity.this, "Failed to update expense", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Delete expense
        btnDeleteExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowsDeleted = dbHelper.deleteExpense(expense.getId());
                if (rowsDeleted > 0) {
                    Toast.makeText(EditExpenseActivity.this, "Expense deleted!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditExpenseActivity.this, "Failed to delete expense", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Hide the keyboard before navigating back
        hideKeyboard();
        super.onBackPressed();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    // Hide the keyboard if the touch is outside the input field
                    hideKeyboard();
                    view.clearFocus(); // Clear focus from the input field
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Helper method to hide the keyboard
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}