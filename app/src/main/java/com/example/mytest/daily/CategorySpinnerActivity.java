package com.example.mytest.daily;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mytest.DatabaseHelper;
import com.example.mytest.R;

import java.util.ArrayList;

public class CategorySpinnerActivity extends Activity {
    Spinner categorySpinner;
    String selectAssetStr;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> incomeCat = new ArrayList<>();
    ArrayList<String> expenseCat = new ArrayList<>();

    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    String type;
    String updateCategoryStr;
    String beforeCategory;
    int catNum=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_category_spinner);

        type = getIntent().getStringExtra("Type");
        beforeCategory= getIntent().getStringExtra("beforeCategory");

        dbHelper = new DatabaseHelper(getApplicationContext());
        database = dbHelper.getReadableDatabase();
        categorySpinner = findViewById(R.id.categoryUpdateSpinner);

        setCategory();

        findViewById(R.id.confirmCategoryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.putExtra("updateCategoryStr", updateCategoryStr);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void setCategory() {
        int iNum=-1;
        int eNum =-1;

        cursor = database.rawQuery("select incomecategory_name from incomecategory",null);
        while(cursor.moveToNext()){
            iNum++;
            String name = cursor.getString(0);
            if(type.equals("수입") && beforeCategory.equals(name)){
                catNum=iNum;
            }
            incomeCat.add(name);
        }
        cursor = database.rawQuery("select expensecategory_name from expensecategory",null);
        while(cursor.moveToNext()){
            eNum++;
            String name = cursor.getString(0);
            if(type.equals("지출") && beforeCategory.equals(name)){
                catNum=eNum;
            }
            expenseCat.add(name);
        }

        cursor.close();

        if(type.equals("지출")){
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_item,
                    expenseCat);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(arrayAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateCategoryStr=expenseCat.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }else{
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_item,
                    incomeCat);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(arrayAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    updateCategoryStr=incomeCat.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
        if(catNum!=-1){
            categorySpinner.setSelection(catNum);
        }

    }
}