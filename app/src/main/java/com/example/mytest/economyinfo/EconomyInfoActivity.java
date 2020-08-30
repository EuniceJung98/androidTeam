package com.example.mytest.economyinfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.CalendarActivity;
import com.example.mytest.R;
import com.example.mytest.SettingsActivity;
import com.example.mytest.daily.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class EconomyInfoActivity extends AppCompatActivity {
    ViewPager pager;
    private Context mContext;
    private TabLayout mTabLayout;
    Toolbar toolbar;
    TextView textView;

    Handler handler = new Handler();
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_economy_info);
        mContext=getApplicationContext();
        mTabLayout=(TabLayout)findViewById(R.id.tablayout);

        pager = findViewById(R.id.viewpager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        ExchangeRateFragment exchangeRateFragment = new ExchangeRateFragment();
        adapter.addPage(exchangeRateFragment);
        StockFragment stockFragment = new StockFragment();
        adapter.addPage(stockFragment);
        NewsFragment newsFragment = new NewsFragment();
        adapter.addPage(newsFragment);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });


        //아래 네비게이션 바 클릭
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.tab4);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    //menu_bottom.xml에 있는 tab id로 구분함
                    case R.id.tab1:
                        Toast.makeText(getApplicationContext(),"첫번째 탭",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        textView = findViewById(R.id.titleText);
        //제목입력
        textView.setText("킴앤정보");

    }//온크리에이트 끝

    class  MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> plist = new ArrayList<>();

        public MyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public  void addPage(Fragment page){
            plist.add(page);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return plist.get(position);
        }

        @Override
        public int getCount() {
            return plist.size();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.month_main, menu);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override//좌상단-세팅클릭했을때
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
}