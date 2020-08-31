package com.example.mytest;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.daily.DailyInAndOut;
import com.example.mytest.daily.MainActivity;
import com.example.mytest.daily.RegMoneyBookActivity;
import com.example.mytest.economyinfo.EconomyInfoActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.R.id.home;

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

    String ym;

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

        //하단네비바 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.tab2);//해당되는 하단바 색변경됨
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab1:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                    case R.id.tab2:
                        Intent intent2 = new Intent(getApplicationContext(), CalendarActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        return true;
                    case R.id.tab3:
                        Intent intent3 = new Intent(getApplicationContext(), ChartActivity.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        return true;
                    case R.id.tab4:
                        Intent intent4 = new Intent(getApplicationContext(), EconomyInfoActivity.class);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent4);
                        return true;
                }
                return true;
            }
        });


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//원래 상단바의 이름을 감춤
        //오늘버튼 생성
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        //타이틀바 텍스트 지정
        titleText.setText(curYearFormat.format(date) + "년 " + curMonthFormat.format(date) + "월");

        //타이틀바인 데이터피커클릭시(년, 월만 선택가능한 데이터피커)
        titleText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                YearMonthPicker picker = new YearMonthPicker();
                picker.setListener(listener);
                picker.show(getSupportFragmentManager(), "YearMonthPicker");
            }
        });

        //+버튼 클릭시 추가페이지로 넘어감
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegMoneyBookActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("regDate", day);//선택한 날짜로 입력가능
                startActivity(intent);
            }
        });

        //상세내역에서 스크롤시 +버튼이 사라지고 나타나고
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy>0){
                    fab.hide();
                } else {
                    fab.show();
                }
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
        //월마다 제일 마지막 날짜를 제대로 해주기 위해서 +1해줌
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);

        //gridView와 gridWeek에 dayList, weekList를 추가해줌
        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        weekAdapter = new GridAdapter(getApplicationContext(), weekList);
        gridView.setAdapter(gridAdapter);
        gridWeek.setAdapter(weekAdapter);

        //DB를 사용하기 위해서 helper사용(없는 db이면 새로 생성해주지만 있는 db면 사용할 수 있도록 해줌)
        dbhelper = new DatabaseHelper(this);
        database = dbhelper.getWritableDatabase();


        adapter = new CalendarAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //gridView 클릭시 상세내역볼 수 있음(현재 날짜로)
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
                ym = yearNum + "-" + monthStr + "-";
                day = ym + dayStr;//조회시 조건에 사용하기 위함
                adapter.clear();
                select();
            }
        });

        //통계에서 클릭시 넘어오는 날짜에 맞는 캘린더 보여줌
        Intent barChartIntent = getIntent();
        if(barChartIntent != null){
            String monthBarChart = barChartIntent.getStringExtra("month");
            if(monthBarChart != null){
                String yearBarChart = monthBarChart.substring(0,4);
                String monthBC = monthBarChart.substring(monthBarChart.indexOf("-")+1, monthBarChart.lastIndexOf("-"));
                selectym = yearBarChart + "-" + monthBC + "-";
                titleText.setText(yearBarChart + "년 " + monthBC + "월");
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dayStr = String.valueOf(position+2-dayNum);//gridview의 각 칸을 position이라고 함
                        //위에처럼하면 10이 안넘어가면 앞에 0을 붙혀서 동일하게 해줘야함
                        if(dayStr.length() == 1){
                            dayStr = "0"+(position+2-dayNum);
                        }
                        day = selectym + dayStr;
                        adapter.clear();
                        select();
                    }
                });
            }
        }


    }

    //상세내역클릭시 보이는 내용
    //리사이클러뷰
    private void select() {
        if(database != null){
            String exSql = "select expensecategory_name, amount, memo, expense_date from expense where expense_date = '"+day+"'";
            String inSql = "select incomecategory_name, amount, memo, income_date from income where income_date = '" + day + "'";

            Cursor cursorEx = database.rawQuery(exSql, null);
            Cursor cursorIn = database.rawQuery(inSql, null);

            //수입내용
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

            //지출내용
            while (cursorEx.moveToNext()){
                String expensecategory_name = cursorEx.getString(0);
                int amount = cursorEx.getInt(1);
                String memo = cursorEx.getString(2);
                String expense_date = cursorEx.getString(3);
                if(memo==null || memo.equals("")){
                    DailyInAndOut d = new DailyInAndOut(0, null, expense_date, null, expensecategory_name, amount, null);
                    adapter.addItem(d);
                } else {
                    DailyInAndOut d = new DailyInAndOut(0, null, expense_date, null, expensecategory_name, amount, memo);
                    adapter.addItem(d);
                }

            }
            adapter.notifyDataSetChanged();
            cursorEx.close();
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
            //월과 일의 길이를 계산해서 한자리 수 일 때는 앞에 0을 붙혀줌
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
            //데이터피커의 년과 월과 일
            selectDay = year + "-" + monthStr + "-" + dayStr;
            //데이터피커로 얻은 년과 월
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
        }
    }

    //달력에 들어갈 내용
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
                //각 캘린더에 들어갈 날짜와 수입합계, 지출합계를 view객체로 반환해줌
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            //
            holder.tvItemGridView = convertView.findViewById(R.id.tv_item_gridview);
            holder.incomeText = convertView.findViewById(R.id.incomeText);
            holder.expenseText = convertView.findViewById(R.id.expenseText);

            //해당되는 날짜를 붙혀줌
            holder.tvItemGridView.setText("" + getItem(position));

            String dayStr="0";
            mCal = Calendar.getInstance();
            String monthStr = String.valueOf(mCal.get(Calendar.MONTH)+1);
            //10월 전까지의 월 앞에 0을 붙혀줌
            if(monthStr.length() == 1){
                monthStr = "0"+monthStr;
            }
            String now = mCal.get(Calendar.YEAR) + "-" + monthStr + "-";
            //뷰에 있는 포지선으로 숫자 앞에 0을 붙힐 일자를 구함
            daycheck= position-dayNum+2;
            if(daycheck<10){
                dayStr=dayStr+daycheck;
            }else {
                dayStr=String.valueOf(daycheck);
            }
            //datepicker하기 전, 초기값
            //datepicker전 데이터를 뿌려줌
            if(selectym==null){
                daytest=now+dayStr;//현재년월
                //datepicker를 통해서 날짜를 받았을 때
            }else {
                daytest=selectym+dayStr;
                //selectym는 년, 월이 들어가 있음
            }

            //지출의 합계
            String exSql = "select sum(amount) from expense where expense_date = '" + daytest + "'";
            if(exSql != null){
                Cursor cursor = database.rawQuery(exSql, null);
                while (cursor.moveToNext()) {
                    String amount = cursor.getString(0);
                    if(amount != null) {
                        holder.expenseText.setText(amount + "\n");
                    } else {
                        holder.expenseText.setText("");
                    }
                }
            }

            //수입의 합계
            String inSql = "select sum(amount) from income where income_date = '" + daytest + "'";
            if(inSql != null){
                Cursor cursor = database.rawQuery(inSql, null);
                while (cursor.moveToNext()) {
                    String amount = cursor.getString(0);
                    if(amount != null) {
                        holder.incomeText.setText(amount + "\n");
                    } else {
                        holder.incomeText.setText("");
                    }
                }
            }

            //현재날짜 색깔 변경
            mCal = Calendar.getInstance();
            String sToday= String.valueOf(mCal.get(Calendar.DAY_OF_MONTH));

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

    //타이틀바에 있는 오늘 버튼 클릭시 실행됨
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (home == item.getItemId()) {
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

            //화면에 뿌려줄 수 있게 view에 있는 변수
            String monthnumstr="";
            if(monthNum<10){
                monthnumstr=monthnumstr+"0"+monthNum;
            }else{
                monthnumstr=monthNum+"";
            }
            selectym=yearNum+"-"+monthnumstr+"-";
            return true;
        } else if (R.id.tab5 == item.getItemId()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return true;
    }

}