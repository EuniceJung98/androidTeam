package com.example.mytest.chart;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.DatabaseHelper;
import com.example.mytest.R;
import com.example.mytest.YearPicker;
import com.example.mytest.daily.DailyInAndOut;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class BarChartFragment extends Fragment {

    RecyclerView recyclerView;
    BarChartAdapter adapter;
    DatabaseHelper dbhelper;
    SQLiteDatabase database;
    String year, month;
    ArrayList<String> monthArr;
    int day;
    String year_month_day;
    String year_month;
    String exSql, inSql;
    BarChart barChart;
    ArrayList<DailyInAndOut> inList, exList;
    ArrayList inData, exData;
    TextView titleText;
    BarData barData;
    BarDataSet exDataSet, inDataSet;
    String amountIn, amountEx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_bar_chart, container, false);

        //막대그래프 설정
        barChart = view.findViewById(R.id.barChart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        //barChart.setTouchEnabled(false);//터치반응 막기(이것도 최대값 안되면 다시 실행)
        barChart.setDescription(null);//description 안보이도록
        //y축 오른쪽설정
        barChart.getAxisRight().setDrawLabels(false);
        //x축설정
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);//x축 라벨 위치
        //barChart.getXAxis().setSpaceBetweenLabels(3);//x축 간격(최대값 지정하는게 안되면 다시 실행)


        monthArr = new ArrayList<>();
        inList = new ArrayList<>();
        exList = new ArrayList<>();
        inData = new ArrayList();
        exData = new ArrayList();

        recyclerView = view.findViewById(R.id.barRecyclerView);
        titleText = getActivity().findViewById(R.id.titleText);
        adapter = new BarChartAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //DB사용
        dbhelper = new DatabaseHelper(getActivity());
        database = dbhelper.getWritableDatabase();

        year=titleText.getText().toString();
        year = year.substring(0, 4);
        Log.d("TAG", "onDateSet: " + year);

        //각 월을 리스트에 추가해줌
        for(int i = 1; i <= 12; i++){
            if(String.valueOf(i).length() == 1){
                month = "0"+i;
            } else {
                month = String.valueOf(i);
            }
            monthArr.add(month);
        }

        //아래 상세내역보이도록
        chartContent();
        //그래프
        multiBarChart();

        //타이틀 클릭시
        titleText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YearPicker picker = new YearPicker();
                picker.setListener(listener);
                picker.show(getFragmentManager(), "yearPicker");
            }
        });

        return view;
    }

    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int yearListener, int month, int dayOfMonth) {
            year = String.valueOf(yearListener);
            Log.d("TAG", "onDateSetyear: " + year);
            titleText.setText(year + "년");

            chartContent();
            multiBarChart();
        }
    };

    public void chartContent() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        exList.clear();
        inList.clear();

        Log.d("TAG", "chartContent: ");
        if(database != null){
            //일
            for(int i = 0; i < monthArr.size(); i++){
                //Log.d("TAG", "chartContent: " + monthArr.get(i));
                if(monthArr.get(i).equals("01") || monthArr.get(i).equals("03") || monthArr.get(i).equals("05") || monthArr.get(i).equals("07") || monthArr.get(i).equals("08") || monthArr.get(i).equals("10") || monthArr.get(i).equals("12")){
                    day = 31;
                    Log.d("TAG", "chartContent: " + year + "년 " + monthArr.get(i) + "월 " + day + "일");
                } else if(monthArr.get(i).equals("02")){
                    if (Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0 || Integer.parseInt(year) % 400 == 0){
                        day = 29;
                        Log.d("TAG", "chartContent: " + year + "년 " + monthArr.get(i) + "월 " + day + "일");
                    } else {
                        day = 28;
                        Log.d("TAG", "chartContent: " + year + "년 " + monthArr.get(i) + "월 " + day + "일");
                    }
                } else if(monthArr.get(i).equals("04") || monthArr.get(i).equals("06") || monthArr.get(i).equals("09") || monthArr.get(i).equals("11")){
                    day = 30;
                    Log.d("TAG", "chartContent: " + year + "년 " + monthArr.get(i) + "월 " + day + "일");
                }
                year_month_day = year + "-" + monthArr.get(i) + "-" + day;
                year_month = year + "-" + monthArr.get(i);
                inSql = "select sum(amount) from income where income_date >= '" + year_month + "-01' and income_date <= '"+ year_month_day + "'";
                exSql = "select sum(amount) from expense where expense_date >= '" + year_month + "-01' and expense_date <= '"+ year_month_day + "'";
                Log.d("TAG", "chartContent: " + exSql);
                Log.d("TAG", "chartContent: " + inSql);
                if(exSql != null && inSql != null) {
                    Cursor cursorEx = database.rawQuery(exSql, null);
                    Cursor cursorIn = database.rawQuery(inSql, null);
                    while (cursorEx.moveToNext()) {
                        amountEx = cursorEx.getString(0);
                        if (amountEx != null) {
                            Log.d("TAG", "chartContentEx: " + amountEx);
                            DailyInAndOut d = new DailyInAndOut(0, null, year_month, null, null, Integer.parseInt(amountEx), null);
                            adapter.addItem(d);
                            exList.add(d);
                        } else if(amountEx == null) {
                            Log.d("TAG", "chartContentEx : 없음");
                            DailyInAndOut d = new DailyInAndOut(0, null, year_month, null, null, 0, null);
                            adapter.addItem(d);
                            exList.add(d);
                        }
                    }
                    cursorEx.close();

                    while(cursorIn.moveToNext()){
                        amountIn = cursorIn.getString(0);
                        if (amountIn != null) {
                            Log.d("TAG", "chartContentIn: " + amountIn);
                            DailyInAndOut d = new DailyInAndOut(0, null, monthArr.get(i), null, null, Integer.parseInt(amountIn), null);
                            adapter.incomeItem(d);
                            inList.add(d);
                        }  else if(amountIn == null) {
                            Log.d("TAG", "chartContentIn : 없음");
                            DailyInAndOut d = new DailyInAndOut(0, null, monthArr.get(i), null, null, 0, null);
                            adapter.incomeItem(d);
                            inList.add(d);
                        }
                    }
                    cursorIn.close();
                }

            }
        }
    }

    private void multiBarChart() {

        //월에 대한 값 그래프 데이터에 추가
        barData = new BarData(monthArr);

        //차트 초기화
        barChart.invalidate();

        exData.clear();
        inData.clear();

        for(int i = 0; i < exList.size(); i++) {
            Log.d("TAG", "multiBarChart: " + i + ", " + exList.get(i).getAmount());
            //표에 표현할 데이터추가
            exData.add(new BarEntry(exList.get(i).getAmount(), i));
            inData.add(new BarEntry(inList.get(i).getAmount(), i));
            Log.d("TAG", "multiBarChart: exList " + exList.get(i).getAmount() + ", inList " + inList.get(i).getAmount());
        }


        Log.d("TAG", "multiBarChart: exList " + exList.size() + ", inList " + inList.size());

        Log.d("TAG", "multiBarChart: inDataSet " + inDataSet);
        //위에서 추가한 데이터를 그래프로 표현 셋팅해줌
        exDataSet = new BarDataSet(exData, "지출");
        inDataSet = new BarDataSet(inData, "수입");
        //그래프 색깔
        exDataSet.setColor(Color.RED);
        inDataSet.setColor(Color.BLUE);
//        exDataSet.setValueTextSize(0.0f);
//        inDataSet.setValueTextSize(0.0f);
        //그래프로 표현할 수 있도록 데이터 추가
        barData.addDataSet(inDataSet);
        barData.addDataSet(exDataSet);
        //그래프에 데이터 추가
        barChart.setData(barData);
        //차트 내용 잘 보이도록 1~6월까지만 보이도록 해줌
        barChart.setVisibleXRangeMaximum(14);
        barChart.notifyDataSetChanged();
    }

}