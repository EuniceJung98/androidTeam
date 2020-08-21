package com.example.mytest;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private TextView titleText;

    private GridAdapter gridAdapter, weekAdapter;

    private ArrayList<String> dayList, weekList;

    private GridView gridView, gridWeek;

    private Calendar mCal;

    SimpleDateFormat curYear, curMonth, curDay;
    Date date;

    SQLiteDatabase database;

    DatabaseHelper dbhelper;

    int month;

    DatePickerDialog datePickerDialog;

    String daySelector;

    String selectDay, selectym;

    String dayStr;

    String monthStr;

    int dayNum, monthNum, yearNum;

    String day;

    RecyclerView recyclerView;

    CalendarAdapter adapter;

    String daytest="";

    int daycheck;

    String sqlDaytest;

    TextView expenseText;

    private View header;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar;
        titleText = findViewById(R.id.titleText);
        gridView = findViewById(R.id.gridview);
        gridWeek = findViewById(R.id.gridWeek);
        recyclerView = findViewById(R.id.recyclerView);

        header = getLayoutInflater().inflate(R.layout.item_calendar_gridview, null, false);
        expenseText = header.findViewById(R.id.expenseText);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab1:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.tab2:
                        Intent intent2 = new Intent(getApplicationContext(), CalendarActivity.class);
                        startActivity(intent2);
                        return true;
                }
                return true;
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//원래 상단바의 이름을 감춤
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//오늘버튼 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.today);

        //처음타이틀바에 있는 날짜 지정
        long now = System.currentTimeMillis();
        date = new Date(now);
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);
        curYear = curYearFormat;
        curMonth = curMonthFormat;
        curDay = curDayFormat;

        titleText.setText(curYearFormat.format(date) + "년 " + curMonthFormat.format(date) + "월");

        //타이틀바인 데이터피커클릭시
        titleText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                YearMonthPicker picker = new YearMonthPicker();
                picker.setListener(listener);
                picker.show(getSupportFragmentManager(), "YearMonthPicker");
//                LocalDate today =LocalDate.now();
//                datePickerDialog = new DatePickerDialog(CalendarActivity.this, listener, today.getYear(), today.getMonthValue()-1, today.getDayOfMonth());
//                datePickerDialog.getDatePicker().setCalendarViewShown(false);
//                datePickerDialog.show();
            }
        });

        //+버튼 클릭시 추가페이지로 넘어감
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegMoneyBookActivity.class);
                startActivity(intent);
            }
        });

        //요일리스트를 따로 제작
        weekList = new ArrayList<String>();
        weekList.add("일");
        weekList.add("월");
        weekList.add("화");
        weekList.add("수");
        weekList.add("목");
        weekList.add("금");
        weekList.add("토");

        //날짜만 넣을 리스트 제작
        dayList = new ArrayList<String>();
        mCal = Calendar.getInstance();//현재 날짜

        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date))-1, 1);

        dayNum = mCal.get(Calendar.DAY_OF_WEEK);//매달 1일의 요일을 숫자로 가져옴
        monthNum = mCal.get(Calendar.MONTH)+1;
        yearNum = mCal.get(Calendar.YEAR);
        for(int i = 1; i < dayNum; i++){//요일의 숫자까지 빈칸으로 남겨놓음
            dayList.add("");
        }

        //해당 월에 표현할 일 수를 구함
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);

        //gridView와 gridWeek에 dayList, weekList를 추가해줌
        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        weekAdapter = new GridAdapter(getApplicationContext(), weekList);
        gridView.setAdapter(gridAdapter);
        gridWeek.setAdapter(weekAdapter);

        //DB를 사용하기 위해서 helper사용(없는 db이면 새로 생성해주지만 있는 db면 사용할 수 있도록 해줌)
        dbhelper = new DatabaseHelper(this);
        database = dbhelper.getWritableDatabase();

        //gridView 클릭시 상세내역볼 수 있음
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                monthStr = String.valueOf(monthNum);//월
                dayStr = String.valueOf(position+2-dayNum);//gridview의 각 칸을 position이라고 함
                //위에처럼하면 10이 안넘어가면 앞에 0을 붙혀서 동일하게 해줘야함
                if(monthStr.length() == 1){
                    monthStr = "0"+monthNum;
                }
                if(dayStr.length() == 1){
                    dayStr = "0"+(position+2-dayNum);
                }
                day = yearNum + "-" + monthStr + "-" + dayStr;//조회시 조건에 사용하기 위함
                //Toast.makeText(getApplication(), "day" + day, Toast.LENGTH_SHORT).show();
                adapter.clear();
                select();
            }
        });

        adapter = new CalendarAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


    }

    //상세내역클릭시 보이는 내용
    //리사이클러뷰
    private void select() {
        if(database != null){
            String exSql = "select expensecategory_name, amount, memo, expense_date from expense where expense_date = '"+day+"'";
            String inSql = "select incomecategory_name, amount, memo, income_date from income where income_date = '" + day + "'";

            Cursor cursorEx = database.rawQuery(exSql, null);
            Cursor cursorIn = database.rawQuery(inSql, null);

            while (cursorIn.moveToNext()){
                String incomecategory_name = cursorIn.getString(0);
                int amount = cursorIn.getInt(1);
                String memo = cursorIn.getString(2);
                String income_date = cursorIn.getString(3);
                if(memo==null || memo.equals("")){
                    DailyInAndOut d = new DailyInAndOut(0, null, income_date, null, incomecategory_name, amount, null);
                    adapter.addItem(d);
                } else {
                    DailyInAndOut d = new DailyInAndOut(0, null, income_date, null, incomecategory_name, amount, memo);
                    adapter.addItem(d);
                }
            }
            adapter.notifyDataSetChanged();
            cursorIn.close();

            while (cursorEx.moveToNext()){
                String expensecategory_name = cursorEx.getString(0);
                int amount = cursorEx.getInt(1);
                String memo = cursorEx.getString(2);
                String expense_date = cursorEx.getString(3);
                if(memo==null || memo.equals("")){
                    //Toast.makeText(getApplicationContext(), "클릭됨"+ec_num, Toast.LENGTH_SHORT).show();
                    DailyInAndOut d = new DailyInAndOut(0, null, expense_date, null, expensecategory_name, amount, null);
//                    for(CalendarDto cd : adapter.getList()){
//                        Log.i("TAG", "c1:"+cd.getExAmount());
//                    }
                    adapter.addItem(d);

                    //category.append(ec_num+"\n");

                } else {
//                    Toast.makeText(getApplicationContext(), "클릭됨"+ec_num+ex_memo, Toast.LENGTH_SHORT).show();
                    //category.append(ec_num + "(" + ex_memo + ")\n" );
                    DailyInAndOut d = new DailyInAndOut(0, null, expense_date, null, expensecategory_name, amount, memo);
//                    for(CalendarDto cd : adapter.getList()){
//                        Log.i("TAG", "c2:"+cd.getExAmount());
//                    }
                    adapter.addItem(d);
                }

            }
//            for (CalendarDto cd: adapter.getList()
//            ) {
//                Log.i("TAG", "select: " + cd.getExAmount());
//            }
            adapter.notifyDataSetChanged();

            //money.append(ex_amount+"원\n");
            //recyclerView.setAdapter(adapter);
            cursorEx.close();
        } else if(database == null){
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }
    }

    //DatePickerDialog 기능
    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //데이터피커를 통해서 날짜를 받았을 떄
            yearNum = year;
            monthNum = monthOfYear;
            monthStr = String.valueOf(monthOfYear);
            dayStr = String.valueOf(dayOfMonth);
            if(monthStr.length() == 1){
                monthStr = "0"+(monthOfYear);
            }
            if(dayStr.length() == 1){
                dayStr = "0"+dayOfMonth;
            }
            titleText.setText(year + "년 " + monthStr + "월");

            //데이터피커로 날짜 클릭시 새로 캘린더 만들어줌
            weekList = new ArrayList();
            weekList.add("일");
            weekList.add("월");
            weekList.add("화");
            weekList.add("수");
            weekList.add("목");
            weekList.add("금");
            weekList.add("토");

            dayList = new ArrayList();
            mCal = Calendar.getInstance();
            mCal.set(Calendar.YEAR, Integer.parseInt(String.valueOf(year)));
            mCal.set(Calendar.MONTH, Integer.parseInt(String.valueOf(monthOfYear))-1);
            mCal.set(Calendar.DATE, Integer.parseInt(String.valueOf(1)));
            dayNum = mCal.get(Calendar.DAY_OF_WEEK);
            for(int i = 1;  i < dayNum; i++){
                dayList.add("");
            }
            setCalendarDate(mCal.get(Calendar.MONTH)+1);
            gridAdapter = new GridAdapter(getApplicationContext(), dayList);
            weekAdapter = new GridAdapter(getApplicationContext(), weekList);
            gridView.setAdapter(gridAdapter);
            gridWeek.setAdapter(weekAdapter);

            month = monthOfYear;

            datePickerDialog = new DatePickerDialog(CalendarActivity.this, listener, year, monthOfYear, dayOfMonth);
            datePickerDialog.getDatePicker().setCalendarViewShown(false);

            daySelector = String.valueOf(dayOfMonth);
            selectDay = year + "-" + monthStr + "-" + dayStr;
            selectym = year + "-" + monthStr + "-";
        }
    };

    //상단 셋팅
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.month_main, menu);
        return true;
    }


    //일별 계산
    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);


        for(int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
            dayList.add("" + (i + 1));
            //expenseText.setText(i+"test");

        }
    }

    private class GridAdapter extends BaseAdapter {

        private final List<String> list;
        private final LayoutInflater inflater;

        private GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            ViewHolder holder = null;

            if(convertView == null){
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                holder = new ViewHolder();

                //날짜를 표현하는 tvItemGridView
                //그래서 날짜는 gridview의 item임
//                holder.tvItemGridView = convertView.findViewById(R.id.tv_item_gridview);
//                holder.incomeText = convertView.findViewById(R.id.incomeText);
//                holder.expenseText = convertView.findViewById(R.id.expenseText);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.tvItemGridView = convertView.findViewById(R.id.tv_item_gridview);
            holder.incomeText = convertView.findViewById(R.id.incomeText);
            holder.expenseText = convertView.findViewById(R.id.expenseText);


            holder.tvItemGridView.setText("" + getItem(position));

            //selectym가 일단 datepicker을 해야지 뜨기때문에 처음의 값이 null로 나옴
            //db에 ex_date의 값이 있는것을 원함?

            String dayStr="0";
            mCal = Calendar.getInstance();
            String monthStr = String.valueOf(mCal.get(Calendar.MONTH)+1);
            if(monthStr.length() == 1){
                monthStr = "0"+monthStr;
            }
            String now = mCal.get(Calendar.YEAR) + "-" + monthStr + "-";
            //Log.i("TAG", now);
            daycheck= position-dayNum+2;
            if(daycheck<10){
                dayStr=dayStr+daycheck;
            }else {
                dayStr=String.valueOf(daycheck);
            }
            //datepicker하기 전, 초기값
            if(selectym==null){
                daytest=now+dayStr;//현재년월
                //Log.i("TAG", "daytest현재"+daytest);
                //datepicker하고 난 뒤에 날짜
            }else {
                daytest=selectym+dayStr;
                //Log.i("TAG", "daytest"+daytest);
            }

            String exSql = "select sum(amount) from expense where expense_date = '" + daytest + "'";
            if(exSql != null){
                //Log.i("TAG", "sql" + sql);
                Cursor cursor = database.rawQuery(exSql, null);
                while (cursor.moveToNext()) {
                    String amount = cursor.getString(0);
                    //Toast.makeText(getApplicationContext(), ex_amount + "과연", Toast.LENGTH_SHORT).show();
                    if(amount != null) {
                        //Log.d("TAG", "널이아니다: ");
                        //Toast.makeText(getApplicationContext(), dayStr+"select", Toast.LENGTH_SHORT).show();
                        holder.expenseText.setText(amount + "\n");
                    } else {
                        //Log.d("TAG", "널이다!!!: ");
                        holder.expenseText.setText("");
                    }
                }
            }

            String inSql = "select sum(amount) from income where income_date = '" + daytest + "'";
            if(inSql != null){
                //Log.i("TAG", "sql" + sql);
                Cursor cursor = database.rawQuery(inSql, null);
                while (cursor.moveToNext()) {
                    String amount = cursor.getString(0);
                    //Toast.makeText(getApplicationContext(), ex_amount + "과연", Toast.LENGTH_SHORT).show();
                    if(amount != null) {
                        //Log.d("TAG", "널이아니다: ");
                        //Toast.makeText(getApplicationContext(), dayStr+"select", Toast.LENGTH_SHORT).show();
                        holder.incomeText.setText(amount + "\n");
                    } else {
                        //Log.d("TAG", "널이다!!!: ");
                        holder.incomeText.setText("");
                    }
                }
            }
           // holder.expenseText.append("p"+position);

            mCal = Calendar.getInstance();
            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);

            if(sToday.equals(getItem(position))) {
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        return convertView;
        }
    }

    private class ViewHolder {
        TextView tvItemGridView;
        TextView incomeText;
        TextView expenseText;

    }

    //위에 오늘 버튼 클릭시 실행됨
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        long now = System.currentTimeMillis();
        date = new Date(now);
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        titleText.setText(curYearFormat.format(date) + "년 " + curMonthFormat.format(date) + "월");//이 값들을 데이터가 나오게 끔 넣어줌

        weekList = new ArrayList<String>();
        weekList.add("일");
        weekList.add("월");
        weekList.add("화");
        weekList.add("수");
        weekList.add("목");
        weekList.add("금");
        weekList.add("토");

        dayList = new ArrayList<String>();
        mCal = Calendar.getInstance();

        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        //매달 1일의 요일을 가져옴
        dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        monthNum = mCal.get(Calendar.MONTH) + 1;
        yearNum = mCal.get(Calendar.YEAR);
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }

        setCalendarDate(mCal.get(Calendar.MONTH) + 1);

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        weekAdapter = new GridAdapter(getApplicationContext(), weekList);
        gridView.setAdapter(gridAdapter);
        gridWeek.setAdapter(weekAdapter);

        //Log.d("TAG", "onOptionsItemSelectedcc: "+ daycheck);
        for(int i = 1; i <= (daycheck+1); i++){
            if(String.valueOf(i).length() == 1){
                String iStr = "0" + i;
                daytest = curYearFormat.format(date) + "-" + curMonthFormat.format(date) + "-" + iStr;
            } else {
                daytest = curYearFormat.format(date) + "-" + curMonthFormat.format(date) + "-" + i;
            }
            Log.d("TAG", "onOptionsItemSelecteddayStr: "+ daytest);
            sqlDaytest = "select sum(amount) from expense where expense_date = '" + daytest + "'";
            Log.d("TAG", "onOptionsItemSelected: " + sqlDaytest);

            if(sqlDaytest != null){
                Cursor cursor = database.rawQuery(sqlDaytest, null);
                expenseText.setText("");
                while (cursor.moveToNext()) {
                    String amount = cursor.getString(0);
                    //Toast.makeText(getApplicationContext(), ex_amount + "과연", Toast.LENGTH_SHORT).show();
                    if(amount != null) {
                        //Log.d("TAG", "널이아니다: " + ex_amount);
                        //Toast.makeText(getApplicationContext(), dayStr+"select", Toast.LENGTH_SHORT).show();
                        expenseText.setText(amount + "\n");
                        //Log.d("TAG", "널이아니다: ex" + expenseText);
                    } else {
                        //Log.d("TAG", "널이다!!!: " + daytest);
                        expenseText.setText("");
                    }
                }
                cursor.close();
            }
        }
        return true;
    }

}