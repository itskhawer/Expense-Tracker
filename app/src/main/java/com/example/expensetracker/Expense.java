package com.example.expensetracker;

import android.os.Parcel;
import android.os.Parcelable;

public class Expense implements Parcelable {
    private int id;
    private String category;
    private double amount;
    private String date;
    private String description;

    public Expense(String category, double amount, String date, String description) {
        this.category = category;
        setAmount(amount); // Use setter for validation
        this.date = date;
        this.description = description;
    }

    public Expense(int id, String category, double amount, String date, String description) {
        this.id = id;
        this.category = category;
        setAmount(amount); // Use setter for validation
        this.date = date;
        this.description = description;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("%s: $%.2f on %s", category, amount, date);
    }

    // Parcelable implementation
    protected Expense(Parcel in) {
        id = in.readInt();
        category = in.readString();
        amount = in.readDouble();
        date = in.readString();
        description = in.readString();
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(category);
        dest.writeDouble(amount);
        dest.writeString(date);
        dest.writeString(description);
    }
}