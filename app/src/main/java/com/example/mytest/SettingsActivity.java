package com.example.mytest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.daily.MainActivity;

import static android.R.id.home;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView titleTextView;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    String dbPassword="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        toolbar = findViewById(R.id.settingstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        titleTextView = findViewById(R.id.settingtitleText);
        titleTextView.setText("설정");

        dbHelper = new DatabaseHelper(getApplicationContext());
        database= dbHelper.getReadableDatabase();
        cursor = database.rawQuery("select password from user",null);
        while(cursor.moveToNext()){
            dbPassword= cursor.getString(0);
        }

        //비밀번호 설정 버튼
        findViewById(R.id.buttonSettingPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(SettingsActivity.this);

                ad.setTitle("비밀번호 설정");       // 제목 설정
                //ad.setMessage("Message");   // 내용 설정

                // EditText 삽입하기
                final EditText passwordEditText = new EditText(SettingsActivity.this);
                passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(4);
                passwordEditText.setFilters(FilterArray);
                ad.setView(passwordEditText);

                // 확인 버튼 설정
                ad.setPositiveButton("암호등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {}
                });
                ad.setNeutralButton("돌아가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                // 취소 버튼 설정
                ad.setNegativeButton("암호사용취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String resetPasswordSql="update user set password=''";
                            database.execSQL(resetPasswordSql);
                            Toast.makeText(SettingsActivity.this, "암호제거", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog= ad.create();
                // 창 띄우기
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Boolean wantToCloseDialog = false;
                        String inputRegPassword= passwordEditText.getText().toString();
                        if (inputRegPassword.equals("")) {
                            passwordEditText.post(new Runnable() {
                                @Override
                                public void run() {
                                    passwordEditText.setFocusableInTouchMode(true);
                                    passwordEditText.requestFocus();
                                }
                            });
                            Toast.makeText(SettingsActivity.this, "비번입력하세요", Toast.LENGTH_SHORT).show();
                        }else if (inputRegPassword.length()<4){
                            passwordEditText.post(new Runnable() {
                                @Override
                                public void run() {
                                    passwordEditText.setFocusableInTouchMode(true);
                                    passwordEditText.requestFocus();
                                }
                            });
                            Toast.makeText(SettingsActivity.this, "4자리 숫자 입력하세요", Toast.LENGTH_SHORT).show();
                        }else {
                            String sql="update user set password="+inputRegPassword;
                            try {
                                database.execSQL(sql);
                                Toast.makeText(SettingsActivity.this, "비번등록완료", Toast.LENGTH_SHORT).show();
                                wantToCloseDialog = true;
                                //dialog.dismiss();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        if(wantToCloseDialog)
                            dialog.dismiss();
                        //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                    }
                });


            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==home){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}