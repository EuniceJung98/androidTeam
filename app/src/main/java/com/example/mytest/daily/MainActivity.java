package com.example.mytest.daily;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.CalendarActivity;
import com.example.mytest.DatabaseHelper;
import com.example.mytest.R;
import com.example.mytest.SettingsActivity;
import com.example.mytest.daysofseven.After1dayFragment;
import com.example.mytest.daysofseven.Before1dayFragment;
import com.example.mytest.daysofseven.SelectDayFragment;
import com.example.mytest.economyinfo.EconomyInfoActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.mytest.R.id.textView13;
import static com.example.mytest.R.id.textView14;
import static com.example.mytest.R.id.textView15;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DatePicker datePicker;
    TextView textView;
    LocalDate today =LocalDate.now();

    Fragment fragment;

    After1dayFragment after1dayFragment;
    SelectDayFragment selectDayFragment;
    Before1dayFragment before1dayFragment;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    RecyclerView recyclerView;
    ArrayList<DailyInAndOut> outList,inList;
    DailyAdapter adapter = new DailyAdapter(this);
    Cursor cursor;

    TextView incomeT,totalT,expenseT;

    public static Activity activity;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = MainActivity.this;
        //Log.d("메인엑티비티", "onCreate: ");
        // Toast.makeText(getApplicationContext(),"onCreate",Toast.LENGTH_SHORT).show();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//원래 상단바의 이름을 감춤
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.today);


        textView = findViewById(R.id.titleText);
        Date current = Calendar.getInstance().getTime();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(current);
        textView.setText(date);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, listener, today.getYear(), today.getMonthValue()-1, today.getDayOfMonth());
//                datePickerDialog.getDatePicker().setCalendarViewShown(false);
//                datePickerDialog.show();
//
//            }
//        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegMoneyBookActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("regDate",textView.getText().toString());
                startActivity(intent);
            }
        });

        //슬라이드로 날짜이동
        FragmentManager manager = getSupportFragmentManager();

        after1dayFragment = new After1dayFragment();
        selectDayFragment = new SelectDayFragment();
        before1dayFragment = new Before1dayFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("selectDay",textView.getText().toString());
//        selectDayFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.container,selectDayFragment).commit();

        //아래 네비게이션 바 클릭
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    //menu_bottom.xml에 있는 tab id로 구분함
                    case R.id.tab1:
                        Toast.makeText(getApplicationContext(),"첫번째 탭",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;
                    case R.id.tab2:
                        Toast.makeText(getApplicationContext(),"두째 탭",Toast.LENGTH_SHORT).show();
                        intent = new Intent(getApplicationContext(), CalendarActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;

                    case R.id.tab3:
                        Toast.makeText(getApplicationContext(),"세번째 탭",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.tab4:
                        Toast.makeText(getApplicationContext(),"네번째 탭",Toast.LENGTH_SHORT).show();
                        intent = new Intent(getApplicationContext(), EconomyInfoActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        return true;


                }
                return false;
            }
        });
        Intent loadedIntent=getIntent();
        if(loadedIntent!=null){
            String monthSelctDate=loadedIntent.getStringExtra("date");
            //Log.d("메인액티비티, 크리에이트", "전달받은값: "+monthSelctDate);
            if (monthSelctDate!=null){
                textView.setText(monthSelctDate);
            }
            loadedIntent.removeExtra("date");
        }

    }//onCreate끝

//    @Override
//    protected void onStart() {
//        super.onStart();
//        //Log.d("달에서 전달받은값이 오나?", "onCreate: "+getIntent().getStringExtra("date"));
//        Intent loadedIntent=getIntent();
//        if(loadedIntent!=null){
//            String monthSelctDate=loadedIntent.getStringExtra("date");
//            Log.d("메인액티비티, onStart", "전달받은값: "+monthSelctDate);
//            if (monthSelctDate!=null){
//                textView.setText(monthSelctDate);
//            }
//            loadedIntent.removeExtra("date");
//        }
//    }

    //헤드 가운데 날짜 선택했을때
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String day = String.valueOf(dayOfMonth);
            String month = String.valueOf(monthOfYear+1);
            if(day.length()==1){
                day="0"+day;
            }
            if(month.length()==1){
                month="0"+month;
            }
            textView.setText(year + "-" + (month) + "-"+ day);
            setSevenDays();
            showDailyResult();
            onFragementChanged(2);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.month_main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override//좌상단-오늘 클릭했을때
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(R.id.tab5 == item.getItemId()){
            //Toast.makeText(this, "설정 눌렀지" , Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onFragementChanged(int index) {
        //Log.d("TAG", "메인 엑티비티에서 실행된 onFragementChanged: ");
        if (index == 3) {
            //프래그먼트 변경
            getSupportFragmentManager().beginTransaction().replace(R.id.container, after1dayFragment).commit();
        } else if (index == 2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, selectDayFragment).commit();
        } else if (index == 1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, before1dayFragment).commit();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setSevenDays(){
        Log.d("TAG", "메인 엑티비티에서 실행된 setSevenDays: ");

        String selectDayStr = textView.getText().toString();
        LocalDate selecday = LocalDate.parse(selectDayStr);
        TextView before3,before2,before1,select,next1,next2,next3;
        before3 =findViewById(R.id.textView4);
        before2 = findViewById(R.id.textView5);
        before1 = findViewById(R.id.textView6);
        select = findViewById(R.id.textView7);
        next1 = findViewById(R.id.textView8);
        next2 = findViewById(R.id.textView9);
        next3 = findViewById(R.id.textView10);
        before3.setText(selecday.minusDays(3).getDayOfMonth()+"");
        before2.setText(selecday.minusDays(2).getDayOfMonth()+"");
        before1.setText(selecday.minusDays(1).getDayOfMonth()+"");
        select.setText(selecday.getDayOfMonth()+"");
        next1.setText(selecday.plusDays(1).getDayOfMonth()+"");
        next2.setText(selecday.plusDays(2).getDayOfMonth()+"");
        next3.setText(selecday.plusDays(3).getDayOfMonth()+"");
    }

    private void showDailyResult() {
        Log.d("TAG", "메인엑티비티에서 실행한 showDailyResult");
        //내역보여줄 리사이클러뷰
        recyclerView = findViewById(R.id.dailyRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        outList = new ArrayList<>();
        inList = new ArrayList<>();
        dbHelper = new DatabaseHelper(getApplicationContext());
        database = dbHelper.getWritableDatabase();
        incomeT= findViewById(textView13);
        totalT = findViewById(textView14);
        expenseT = findViewById(textView15);

        inList.clear();
        outList.clear();
        String[] expense = new String[]{"expense_date","asset_name","expensecategory_name","amount","memo"};
        String[] income = new String[]{"income_date","asset_name","incomecategory_name","amount","memo"};
        String selectDayStr = textView.getText().toString();
        //지출 넣기
        cursor = database.rawQuery("select expense_id,expense_date,asset_name,expensecategory_name,amount,memo"+
                " from expense where expense_date=?",new String[]{selectDayStr});
        while(cursor.moveToNext()){
            int ex_id= cursor.getInt(0);
            String date =cursor.getString(cursor.getColumnIndex(expense[0]));
            String asset = cursor.getString(cursor.getColumnIndex(expense[1]));
            String category= cursor.getString(cursor.getColumnIndex(expense[2]));
            int amount=cursor.getInt(cursor.getColumnIndex(expense[3]));
            String memo =cursor.getString(cursor.getColumnIndex(expense[4]));
            DailyInAndOut d = new DailyInAndOut(ex_id,"지출",date,asset,category,amount,memo);
            outList.add(d);
        }
        //수입넣기
        cursor = database.rawQuery("select income_id,income_date,asset_name,incomecategory_name,amount,memo"+
                " from income where income_date=?",new String[]{selectDayStr});
        while(cursor.moveToNext()){
            int in_id= cursor.getInt(0);
            String date =cursor.getString(cursor.getColumnIndex(income[0]));
            String asset = cursor.getString(cursor.getColumnIndex(income[1]));
            String category= cursor.getString(cursor.getColumnIndex(income[2]));
            int amount=cursor.getInt(cursor.getColumnIndex(income[3]));
            String memo =cursor.getString(cursor.getColumnIndex(income[4]));
            inList.add(new DailyInAndOut(in_id,"수입",date,asset,category,amount,memo));
        }
        cursor.close();
        adapter.clear();
        int incomeTotal=0;
        int expenseTotal=0;
        int dayTotal=0;
        for (DailyInAndOut ex: outList ) {
            adapter.addItem(ex);
            expenseTotal+=ex.getAmount();
        }
        for (DailyInAndOut in: inList ) {
            adapter.addItem(in);
            incomeTotal+=in.getAmount();
        }
        for (DailyInAndOut d :
                adapter.getList()) {
            Log.d("NEWMYTEST", d.getCategoryName());
        }
        adapter.notifyDataSetChanged();
        //하루 전체 내역
        dayTotal=incomeTotal-expenseTotal;
        incomeT.setText(incomeTotal+" 원");
        expenseT.setText(expenseTotal+" 원");

        if (dayTotal>0){
            totalT.setText(Html.fromHtml("<font color=\"#2196F3\">"
                    +dayTotal +"</font>"
                    + "원"));
        }else if (dayTotal==0){
            totalT.setText(dayTotal+" 원");
        }else if (dayTotal<0){
            totalT.setText(Html.fromHtml("<font color=\"#ff0000\">"
                    +dayTotal +"</font>"
                    + "원"));
        }
        database.close();
    }





}