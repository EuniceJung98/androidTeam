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
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.daily.MainActivity;
import com.example.mytest.settings.AssetUpdateActivity;
import com.example.mytest.settings.CateUpdateActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

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
                passwordEditText.addTextChangedListener(new TextWatcher(){
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        //Editable inputStr=passwordEditText.getText();
                        if (passwordEditText.getText().toString().matches("^0") )
                        {
                            // Not allowed
                            passwordEditText.setText("");
                        }
                    }
                    public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                    public void afterTextChanged(Editable s){}
                });
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
                        //Toast.makeText(SettingsActivity.this, "0넣을때"+inputRegPassword, Toast.LENGTH_SHORT).show();
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
                            String sql="update user set password='"+inputRegPassword+"'";
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

        //카테고리 수정
        findViewById(R.id.buttonUpdateDBCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cateIntent = new Intent(SettingsActivity.this, CateUpdateActivity.class);
                cateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(cateIntent);
            }
        });

        //자산수정
        findViewById(R.id.buttonUpdateDBAsset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent assetIntent = new Intent(SettingsActivity.this, AssetUpdateActivity.class);
                assetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(assetIntent);
            }
        });

        //데이터 백업, 가져오기
        findViewById(R.id.backup_load_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(SettingsActivity.this);

                ad.setTitle("데이터");       // 제목 설정


                ad.setPositiveButton("백업하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        exportDB();
                    }
                });
                ad.setNegativeButton("데이터불러오기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        importDB();
                    }
                });
                ad.show();

            }
        });



   }//onCreate끝나는 부분

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==home){
            Intent intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void importDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.example.mytest"
                        + "//databases//" + "moneybook.db";
                String backupDBPath ="/moneybook_backup.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();

                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getApplicationContext(), "Import Successful!",
                        Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Import Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    private void exportDB(){

        final String inFileName = "/data/data/com.example.mytest/databases/moneybook.db";
        try {
            String resetPasswordSql="update user set password=''";
            database.execSQL(resetPasswordSql);
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory()+"/moneybook_backup.db";

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            Toast.makeText(getApplicationContext(), "백업 성공!",
                    Toast.LENGTH_SHORT).show();
            // Close the streams
            output.flush();
            output.close();
            fis.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "백업 실패!",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "백업 실패!",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}