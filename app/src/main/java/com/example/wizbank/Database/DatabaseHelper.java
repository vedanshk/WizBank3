package com.example.wizbank.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    public static final String  DB_NAME ="fb_wiz_bank";

    public static final int  DB_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: started");

        String createUserTable = "CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT , email  text not null , password TEXT not null, " +
                "first_name TEXT , last_name Text , address text , image_url text , remained_amount DOUBLE   )";

        String createShoppingTable = "CREATE TABLE shopping(_id integer primary key autoincrement ," +
                "item_id integer , user_id integer , transaction_id integer , price double , date date , description text)";

        String createInvestmentTable = "create table investment (_id integer primary key autoincrement , amount double ," +
                "monthly_roi  double , name text , init_table date , finish_date date , user_id integer  , transaction_id integer)";

        String createLoanTable = "create table loans(_id integer primary key autoincrement ,  init_date date," +
                "finish_date date , init_amount double , remained_amount double , monthly_payment double , monthly_roi double ," +
                "name text , user_id integer) ";

        String createTransactionTable = "create table transcations (_id integer primary key autoincrement , amount double ," +
        "date Date , type text , user_id integer , recipient text , description text )";

        String createItemsTable = "create table items(_id integer primary key autoincrement , name text , image_url text" +
                ", description text)";
        db.execSQL(createUserTable);
        db.execSQL(createShoppingTable);
        db.execSQL(createInvestmentTable);
        db.execSQL(createLoanTable);
        db.execSQL(createTransactionTable);
        db.execSQL(createItemsTable);

        addInitialItem(db);
    }

    private void addInitialItem (SQLiteDatabase db){
        Log.d(TAG, "addInitialItem: started");
        ContentValues values = new ContentValues();
        values.put("name" , "Bike");
        values.put("image_url" , "https://cdn.shopify.com/s/files/1/0304/3177/2811/products/marin-bikes-mountain-mtb-marin-bikes-mountain-bikes-bolinas-ridge-1-36375285235924_1024x1024.gif?v=1662173909");
        values.put("description" , "The Perfect Mountain Bike");


        db.insert("items" , null , values);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
