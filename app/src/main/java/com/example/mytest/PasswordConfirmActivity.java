package com.example.mytest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.daily.MainActivity;

public class PasswordConfirmActivity extends AppCompatActivity {
    EditText password;
    String inputPassword;
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    String dbPassword="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_confirm);

        password = findViewById(R.id.password);

        dbHelper = new DatabaseHelper(getApplicationContext());
        database= dbHelper.getReadableDatabase();
        cursor = database.rawQuery("select password from user",null);
        while(cursor.moveToNext()){
            dbPassword= cursor.getString(0);
        }
        if (dbPassword.equals("")){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            cursor.close();
            finish();
        }

        findViewById(R.id.loginbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals("")){
                    password.post(new Runnable() {
                        @Override
                        public void run() {
                            password.setFocusableInTouchMode(true);
                            password.requestFocus();
                        }
                    });
                    Toast.makeText(getApplicationContext(),"비번입력하세요",Toast.LENGTH_SHORT).show();
                }else{
                    inputPassword=password.getText().toString();
                    if (inputPassword.equals(dbPassword)){
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        cursor.close();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"비번틀렸슈",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}