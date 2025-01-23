package com.example.expensetracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ViewExpensesActivity extends AppCompatActivity {

    private RecyclerView rvExpenses;
    private ExpenseAdapter expenseAdapter;
    private ExpenseDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);

        rvExpenses = findViewById(R.id.rvExpenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new ExpenseDbHelper(this);
        List<Expense> expenses = dbHelper.getAllExpenses();

        expenseAdapter = new ExpenseAdapter(expenses, this);
        rvExpenses.setAdapter(expenseAdapter);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}