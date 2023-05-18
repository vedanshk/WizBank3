package com.example.wizbank.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizbank.Database.DatabaseHelper;
import com.example.wizbank.MainActivity;
import com.example.wizbank.Models.User;
import com.example.wizbank.R;
import com.example.wizbank.Utils;
import com.example.wizbank.WebsiteActivity;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText edtEmail , edtPassword , edtAddress , edtName;
    private Button btnRegister;
    private ImageView  firstImage , secondImage , thirdImage , fourthImage , fifthImage;
    private TextView  txtWarning , txtLogin , txtLic;

    private DatabaseHelper databaseHelper;
    private String image_url;

    private  DoesUserExist doesUserExist;
    private RegisterUser registerUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();

        databaseHelper = new DatabaseHelper(this);

        btnRegister.setOnClickListener( v->{

            initRegister();
            image_url ="first";
            handleImageUrl();
        });

        txtLogin.setOnClickListener( v->{
            Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
            startActivity(intent);
        });
        txtLic.setOnClickListener( v ->{
            Intent intent = new Intent(RegisterActivity.this , WebsiteActivity.class);
            startActivity(intent);
        });
    }

    private void handleImageUrl() {
        Log.d(TAG, "handleImageUrl: started");
        firstImage.setOnClickListener(v ->{
            image_url ="first";
        });
        secondImage.setOnClickListener(v ->{
            image_url = "second";
        });
        thirdImage.setOnClickListener(v ->{
            image_url ="third";
        });
        fourthImage.setOnClickListener(v ->{
            image_url = "fourth";
        });
        fifthImage.setOnClickListener(v ->{
            image_url = "fifth";
        });
    }

    private void initRegister() {
        Log.d(TAG, "initRegister: started");

        String email = edtEmail.getText().toString();
        String password  = edtPassword.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            txtWarning.setVisibility(View.VISIBLE);
            txtWarning.setText("Please enter email and password");
        }else{
            txtWarning.setVisibility(View.GONE);
               doesUserExist = new DoesUserExist();
               doesUserExist.execute(email);

        }

    }

    private class DoesUserExist extends AsyncTask<String , Void , Boolean>{

        @SuppressLint("Range")
        @Override
        protected Boolean doInBackground(String... strings) {
            try {

                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                Cursor cursor =  db.query("users" , new String[]{"_id ", "email"} , "email=?" , new String[] {strings[0] } ,null , null , null);

                if( null !=cursor){
                    if(cursor.moveToFirst()){

                        if(cursor.getString(cursor.getColumnIndex("email")).equals(strings[0])){
                            cursor.close();
                            db.close();
                            return true;
                        }else{
                            cursor.close();
                            db.close();
                            return false;
                        }


                    }else{
                        cursor.close();
                        db.close();
                        return false;
                    }
                }else{
                    db.close();
                    return true;
                }
            }catch (SQLException e){

                e.printStackTrace();
                return true;

            }


        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if(aBoolean){
                txtWarning.setVisibility(View.VISIBLE);
                txtWarning.setText("There is user with this email , please try another email.");

            }else{
                txtWarning.setVisibility(View.GONE);
                registerUser = new RegisterUser();
                registerUser.execute();
            }
        }
    }

    private  class RegisterUser extends AsyncTask<Void , Void , User>{

        private String email , password , lastName ,firstName , address;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String name = edtName.getText().toString();
            String[] names = name.split(" ");
            firstName = names[0];
            if(names.length > 1){
                for( int i =0 ;  i <names.length; ++i){
                    if( i >1){
                        lastName += " " + names[i];

                    }else{
                        lastName +=names[i];
                    }
                }

            }
             email = edtEmail.getText().toString();
             password  = edtPassword.getText().toString();

             address = edtAddress.getText().toString();

        }

        @SuppressLint("Range")
        @Override
        protected User doInBackground(Void... voids) {
            try{
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("email" , email);
                values.put("password" , password);
                values.put("address" , address);
                values.put("first_name" , firstName);
                values.put("last_name" , lastName);
                values.put("remained_amount" , 0.0);
                values.put("image_url" , image_url);
                long user_id = db.insert("users" , null , values);
                Log.d(TAG, "doInBackground: userId" + user_id);


                Cursor cursor = db.query("users" , null , "_id=?" , new String[]{
                        String.valueOf(user_id)} , null , null , null
                );
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

                    }else{
                        cursor.close();
                        db.close();
                        return null;
                    }

                }else{
                    db.close();
                    return null;
                }


            }catch(SQLException e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if(null !=user){
                Toast.makeText(RegisterActivity.this, user.getEmail() +" registered successfully", Toast.LENGTH_SHORT).show();
                Utils utils = new Utils(RegisterActivity.this);
                utils.addUserToSharedPreferences(user);
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                Toast.makeText(RegisterActivity.this, "Something went wrong . Please try again later", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews() {

        Log.d(TAG, "initViews: started");
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtAddress = findViewById(R.id.edtAddress);
        edtName = findViewById(R.id.edtName);
        btnRegister = findViewById(R.id.btnRegister);
        firstImage = findViewById(R.id.firstImage);
        secondImage = findViewById(R.id.secondImage);
        thirdImage = findViewById(R.id.thirdImage);
        fourthImage = findViewById(R.id.fourthImage);
        fifthImage = findViewById(R.id.fifthImage);
        txtWarning = findViewById(R.id.txtWarning);
        txtLogin = findViewById(R.id.txtLogin);
        txtLic = findViewById(R.id.txtLic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != doesUserExist){
            if( !doesUserExist.isCancelled()){
                doesUserExist.cancel(true);
            }
        }
        if(null != registerUser){
            if( !registerUser.isCancelled()){
                registerUser.cancel(true);
            }
        }
    }
}