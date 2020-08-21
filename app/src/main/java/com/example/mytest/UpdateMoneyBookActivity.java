package com.example.mytest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class UpdateMoneyBookActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    boolean isExpenseChecked=true;
    private Spinner spinner2,spinner;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<String> arrayAdapter2;
    int catNum,assetNum;

    ArrayList<String> incomeCat = new ArrayList<>();
    ArrayList<String> expenseCat = new ArrayList<>();
    ArrayList<String> assetList = new ArrayList<>();

    Button selecIncomeButton, selecExpenseButton,selecDayButton;
    Cursor cursor;
    LocalDate today = LocalDate.now();

    String inputDay,inputAsset,inputCategory,inputAmount,inputMemo;

    EditText amountEdit,memoEdit;

    DailyInAndOut data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_money_book);

        selecIncomeButton = findViewById(R.id.selectInButton);
        selecExpenseButton = findViewById(R.id.selectExButton);
        selecDayButton = findViewById(R.id.selectDayButton);
        spinner = findViewById(R.id.assetSpinner);
        spinner2 = findViewById(R.id.selectCategorySpinner);
        amountEdit = findViewById(R.id.editTextNumber);
        memoEdit = findViewById(R.id.editTextMemo);

        selecDayButton.setText(today.toString());

        dbHelper = new DatabaseHelper(getApplicationContext());
        database = dbHelper.getWritableDatabase();
        Intent intent = getIntent();
        if(intent != null){
            //인텐트 속에 있는 데이터들을 번들(묶음)로 가져옴
            Bundle bundle =intent.getExtras();
            data = (DailyInAndOut) bundle.getSerializable("contents");//저장해 놓은 키값을 입력, protected MyData(Parcel in)를 호출해서 넣어주는 거

            if(data!= null){
                if(data.getType().equals("수입")){
                    isExpenseChecked=false;
                    selecIncomeButton.setBackgroundColor(Color.parseColor("#ffcccc"));
                    selecExpenseButton.setBackgroundColor(Color.parseColor("#d6d7d7"));

                }
                selecDayButton.setText(data.getDate());
                amountEdit.setText(String.valueOf(data.getAmount()));
                memoEdit.setText(data.getMemo());
            }
        }
        setCategory();
        setCategoryName();
        setAsset();
        //원래 작성된 내용 선택하기
        spinner.setSelection(assetNum);
        spinner2.setSelection(catNum);


        //날짜 버튼 클릭
        selecDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(UpdateMoneyBookActivity.this
                        ,listener, today.getYear(),today.getMonthValue()-1,today.getDayOfMonth());
                dialog.show();
            }
        });


        //수정버튼
        Button updateMoneybookButton = findViewById(R.id.buttonUPdate);
        updateMoneybookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDay = selecDayButton.getText().toString();
                inputAmount = amountEdit.getText().toString();
                inputMemo = memoEdit.getText().toString();
                confirm();
            }
        });

        Button deleteMoneybookButton = findViewById(R.id.buttonDelete);
        deleteMoneybookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelete();
            }
        });

        //수입,지출 선택버튼
        selecIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecIncomeButton.setBackgroundColor(Color.parseColor("#ffcccc"));
                selecExpenseButton.setBackgroundColor(Color.parseColor("#d6d7d7"));
                isExpenseChecked=false;
                setCategoryName();
            }
        });
        selecExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecExpenseButton.setBackgroundColor(Color.parseColor("#ffcccc"));
                selecIncomeButton.setBackgroundColor(Color.parseColor("#d6d7d7"));
                isExpenseChecked=true;
                setCategoryName();
            }
        });
    }

    private void setAsset() {
        arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item,
                assetList);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),assetList.get(position)+"가 선택되었습니다.",
//                        Toast.LENGTH_SHORT).show();
                    inputAsset=assetList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setCategoryName() {
        if(isExpenseChecked){
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_item,
                    expenseCat);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(arrayAdapter);
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    inputCategory=expenseCat.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }else{
            arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                    android.R.layout.simple_spinner_item,
                    incomeCat);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(arrayAdapter);
            spinner2.setSelection(2);
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    inputCategory=incomeCat.get(position);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }

    }

    private void setCategory() {
        int iNum=-1;
        int eNum =-1;
        int assNum=-1;

        cursor = database.rawQuery("select incomecategory_name from incomecategory",null);
        while(cursor.moveToNext()){
            iNum++;
            String name = cursor.getString(0);
            if(data.getType().equals("수입") && data.getCategoryName().equals(name)){
                catNum=iNum;
            }
            incomeCat.add(name);
        }
        cursor = database.rawQuery("select expensecategory_name from expensecategory",null);
        while(cursor.moveToNext()){
            eNum++;
            String name = cursor.getString(0);
            if(data.getType().equals("지출") && data.getCategoryName().equals(name)){
                catNum=eNum;
            }
            expenseCat.add(name);
        }
        cursor = database.rawQuery("select asset_name from asset",null);
        //자산리스트

        while(cursor.moveToNext()){
            assNum++;
            String name = cursor.getString(0);
            if(data.getAssetName().equals(name)){
                assetNum=assNum;
            }
            assetList.add(name);
        }
        cursor.close();
    }

    private void updateMoneybook(){
        //수입,지출이 변동있으면 삭제하고 입력하기
        String exDelsql="delete from expense where expense_id="+data.getId();
        String inInsertsql="insert into income(income_date, asset_name ,incomecategory_name,amount,memo)"+
                " values('"+inputDay+"','"+inputAsset+"','"+inputCategory+"',"+
                Integer.parseInt(inputAmount)+",'"+inputMemo+"')";

        String inDelsql="delete from income where income_id="+data.getId();
        String exInsertsql="insert into expense(expense_date,asset_name,expensecategory_name,amount,memo)"+
                " values('"+inputDay+"','"+inputAsset+"','"+inputCategory+"',"+
                Integer.parseInt(inputAmount)+",'"+inputMemo+"')";


        //수입,지출 변동없을때 바로 수정하기
        String exUpsql="update expense set expense_date=" +inputDay+
                ",asset_name=" +inputAsset+
                ",expensecategory_name=" +inputCategory+
                ",amount=" +Integer.parseInt(inputAmount)+
                ",memo="+inputMemo+
                " where expense_id="+data.getId();
        String inUpsql= "update income set income_date=" +inputDay+
                ",asset_name=" +inputAsset+
                ",expensecategory_name=" +inputCategory+
                ",amount=" +Integer.parseInt(inputAmount)+
                ",memo="+inputMemo+
                " where income_id="+data.getId();


        if(data.getType().equals("지출")){
            if(isExpenseChecked){//처음입력한값이 지출, 선택한값도 지출일때 수정만
                database.execSQL(exUpsql);
            }else{//처음입력한값이 지출, 선택한값은 수입일때 지출값을 삭제하고 수입값으로 입력
                database.beginTransaction();
                try {
                    database.execSQL(exDelsql);
                    database.execSQL(inInsertsql);
                    database.setTransactionSuccessful();
                }finally {
                    database.endTransaction();
                }
            }
        }else if (data.getType().equals("수입")) {
            if (isExpenseChecked) {//처음입력한값이 수입, 선택한값이 지출일때 수입값 삭제후 지출값으로 입력
                database.beginTransaction();
                try {
                    database.execSQL(inDelsql);
                    database.execSQL(exInsertsql);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
            } else {//처음입력한값이 수입, 선택한값은 수입일때 수정만
                database.execSQL(inUpsql);
            }
        }

    }

    private void deleteMoneybook() {
        String exDelsql="delete from expense where expense_id="+data.getId();
        String inDelsql="delete from income where income_id="+data.getId();
        if(data.getType().equals("지출")){
            database.execSQL(exDelsql);
        }else if (data.getType().equals("수입")) {
            database.execSQL(inDelsql);
        }
    }

    void confirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("수정확인");
        builder.setMessage("정말 수정하시겠습니까?");
        builder.setPositiveButton("수정함", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateMoneybook();
                database.close();
                MainActivity MA = (MainActivity) MainActivity.activity;
                MA.finish();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP
                        |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                MA.startActivity(intent);
            }
        });
        builder.setNegativeButton("수정안하고 돌아감", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("삭제확인");
        builder.setMessage("정말 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMoneybook();
                database.close();
                MainActivity MA = (MainActivity) MainActivity.activity;
                MA.finish();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP
                        |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                MA.startActivity(intent);
            }
        });
        builder.setNegativeButton("삭제 안하고 돌아감", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }



    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String dayStr = String.valueOf(dayOfMonth);
            String monthStr = String.valueOf(month+1);
            if(dayStr.length()==1){
                dayStr="0"+dayStr;
            }
            if(monthStr.length()==1){
                monthStr="0"+monthStr;
            }
            selecDayButton.setText(year + "-" + monthStr + "-" + dayStr);

        }
    };
}