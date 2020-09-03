package com.example.mytest.chart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mytest.calendar.CalendarActivity;
import com.example.mytest.R;
import com.example.mytest.SettingsActivity;
import com.example.mytest.daily.MainActivity;
import com.example.mytest.economyinfo.EconomyInfoActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {

    ViewPager pager;
    TextView titleText;
    int yearNum;
    Bundle bundle;
    BarChartFragment barChartFragment;
    String yearStr;
    TabLayout chartTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Toolbar toolbar;
        pager = findViewById(R.id.pager);
        titleText = findViewById(R.id.titleText);
        chartTab = findViewById(R.id.chartTab);
        bundle = new Bundle();

        //하단바 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.tab3);//해당되는 하단바 색변경됨
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

        toolbar  = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //프래그먼트 지정
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        barChartFragment = new BarChartFragment();
        adapter.addItem(barChartFragment);
        AssetChartFragment assetChartFragment = new AssetChartFragment();
        adapter.addItem(assetChartFragment);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(chartTab));
        chartTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        titleBar();

    }

    //상단 셋팅
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.month_main, menu);
        return true;
    }

    //타이틀바에 있는 설정 버튼 클릭시 실행됨
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(R.id.tab5 == item.getItemId()){
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return true;
    }

    //상단바
    private void titleBar() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        SimpleDateFormat curYear = curYearFormat;

        titleText.setText(curYear.format(date) + "년");

        titleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YearPicker picker = new YearPicker();
                picker.setListener(listener);
                picker.show(getSupportFragmentManager(), "YearPicker");

            }
        });
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yearNum = year;
            titleText.setText(year + "년");
        }
    };

    //pager를 사용하기 위함
    class MyPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> items = new ArrayList<>();

        public MyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void addItem(Fragment item){items.add(item);}

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }
}