package com.example.mytest.daily;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.mytest.DatabaseHelper;
import com.example.mytest.R;

import java.util.ArrayList;

public class AssetSpinerActivity extends Activity {
    Spinner assetSpinner;
    String selectAssetStr;
    ArrayAdapter<String> arrayAdapter2;
    ArrayList<String> assetList = new ArrayList<>();

    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_asset_spiner);

        dbHelper = new DatabaseHelper(getApplicationContext());
        database = dbHelper.getReadableDatabase();

        assetSpinner = findViewById(R.id.assetUpdateSpinner);
        setAsset();
        findViewById(R.id.confirmAssetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.putExtra("selectAssetStr", selectAssetStr);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    private void setAsset() {
        cursor = database.rawQuery("select asset_name from asset",null);
        //자산리스트 저장시킴
        int assetChangeNum=-1;
        int assetNum=-1;
        String beforeAsset= getIntent().getStringExtra("beforeAsset");
        while(cursor.moveToNext()){
            assetChangeNum++;
            String name = cursor.getString(0);
            if(name.equals(beforeAsset)){
                assetNum=assetChangeNum;
            }
            assetList.add(name);
        }
        cursor.close();

        arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item,
                assetList);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assetSpinner.setAdapter(arrayAdapter2);
        if(assetNum!=-1){
            assetSpinner.setSelection(assetNum);
        }
        assetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectAssetStr=assetList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

}