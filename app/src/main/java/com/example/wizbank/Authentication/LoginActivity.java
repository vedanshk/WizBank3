package com.example.wizbank.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wizbank.Database.DatabaseHelper;
import com.example.wizbank.MainActivity;
import com.example.wizbank.Models.User;
import com.example.wizbank.R;
import com.example.wizbank.Utils;
import com.example.wizbank.WebsiteActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText edtEmail , edtPassword;
    private Button btnLogin;
    private TextView txtRegister , txtWarning , txtLic;

    private DatabaseHelper databaseHelper;
    private Utils utils;
    private  LoginUser loginUser;
    private DoesEmailExists doesEmailExists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        utils = new Utils(this);
        initViews();

        btnLogin.setOnClickListener( v ->{
            initLogin();
        });

        txtRegister.setOnClickListener( v->{
            Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
            startActivity(intent);
        });
        txtLic.setOnClickListener( v ->{
            Intent intent = new Intent(LoginActivity.this , WebsiteActivity.class);
            startActivity(intent);
        });
    }

    private void initLogin() {
        Log.d(TAG, "initLogin: started");
       String  email = edtEmail.getText().toString();
       String password  = edtPassword.getText().toString();
        if(!TextUtils.isEmpty(email)){

            if(!TextUtils.isEmpty(password)){
                txtWarning.setVisibility(View.GONE);
                //Todo: login activity e
                doesEmailExists = new DoesEmailExists();
                doesEmailExists.execute(email);
            }else {
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("Please enter password");
            }
        }else{

            // TODO: 06-05-2023  login  logic
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Please enter email");

        }

    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        txtWarning = findViewById(R.id.txtWarning);
        txtLic = findViewById(R.id.txtLic);
    }

    public class  DoesEmailExists extends AsyncTask<String, Void , Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            databaseHelper = new DatabaseHelper(LoginActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                SQLiteDatabase db = databaseHelper.getReadableDatabase();

                Cursor cursor  = db.query("users", new String[] {"email"} , "email=?" , new String[] {strings[0]},null , null , null );

                if(null !=cursor){
                    if(cursor.moveToFirst()){
                        cursor.close();
                        db.close();
                        return true;

                    }else {
                        cursor.close();
                        db.close();
                        return false;
                    }

                }else {
                    db.close();
                    return  false;
                }

            }catch (SQLException e){
                e.printStackTrace();
                return false;
            }


        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(!aBoolean){
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("User does not exists with that email");

            }else {

                // TODO: 07-05-2023  Check for password

              loginUser = new LoginUser();
              loginUser.execute();
            }


        }
    }

    private class LoginUser extends AsyncTask<Void , Void , User>{

        private  String email , password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email = edtEmail.getText().toString();
            password = edtPassword.getText().toString();
        }

        @SuppressLint("Range")
        @Override
        protected User doInBackground(Void... voids) {

            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();

                Cursor cursor  = db.query("users", null, "email=? AND password=?" , new String[] {email , password},null , null , null );

                if(null !=cursor){
                    if(cursor.moveToFirst()){

                        User user = new User();
                        user.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
                        user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                        user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                        user.setFirst_name(cursor.getString(cursor.getColumnIndex("first_name")));
                        user.setLast_name(cursor.getString(cursor.getColumnIndex("last_name")));
                        user.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                        user.setImage_url(cursor.getString(cursor.getColumnIndex("image_url")));
                        user.setRemained_amount(cursor.getDouble(cursor.getColumnIndex("remained_amount")));

                        cursor.close();
                        db.close();
                        return user;


                    }else {
                        cursor.close();
                        db.close();
                        return null;
                    }

                }else {
                    db.close();
                    return null;

                }

            }catch (SQLException e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(user != null){
                utils.addUserToSharedPreferences(user);
                Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("Incorrect Password");


            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != doesEmailExists){
            if(!doesEmailExists.isCancelled()){
                doesEmailExists.cancel(true);
            }
        }
        if(null != loginUser){
            if(!loginUser.isCancelled()){
                loginUser.cancel(true);
            }
        }
    }
}