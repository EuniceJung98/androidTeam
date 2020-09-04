package com.example.mytest.chart;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.DatabaseHelper;
import com.example.mytest.R;
import com.example.mytest.daily.DailyInAndOut;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AssetChartFragment extends Fragment {

    TextView titleText, monthText, perText;
    String year, month;
    int day;
    RecyclerView recyclerView;
    AssetChartAdapter adapter;
    DatabaseHelper dbhelper;
    SQLiteDatabase database;
    String year_month_day, year_month;
    String sql;
    String assetName, amount;
    RadioButton income, expense;
    String assetType, assetDate;
    PieChart pieChart;
    ArrayList<String> assetData;
    ArrayList<String> assetTypeCnt, assetCnt;
    PieDataSet dataSet;
    ArrayList<PieEntry> pieEntry;
    PieData data;
    TextView nodata;
    DecimalFormat mFormat;
    boolean percentSignSeparated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_asset_chart, container, false);

        titleText = getActivity().findViewById(R.id.titleText);
        monthText = view.findViewById(R.id.monthText);
        recyclerView = view.findViewById(R.id.pieRecyclerView);
        adapter = new AssetChartAdapter();
        income = view.findViewById(R.id.radioButton2);
        expense = view.findViewById(R.id.radioButton);
        pieChart = (PieChart)view.findViewById(R.id.pieChart);
        assetTypeCnt = new ArrayList<>();
        assetCnt = new ArrayList<>();
        assetData = new ArrayList<>();
        pieEntry = new ArrayList<PieEntry>();
        nodata = view.findViewById(R.id.pieNoData);

        //그래프 설정
        pieChart.setUsePercentValues(true);//퍼센트 단위로 보여줌
        pieChart.setDescription(null);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(15f);
        pieChart.setMinAngleForSlices(10f);

        //지출클릭시 변수변경
        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assetType = "expense";
                assetDate = "expense_date";
                chartContent();
                pieChart();
            }
        });

        //수입클릭시 변수변경
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assetType = "income";
                assetDate = "income_date";
                chartContent();
                pieChart();
            }
        });

        //리사이클러뷰안에 들어가는 아이템 셋팅
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //DB사용
        dbhelper = new DatabaseHelper(getActivity());
        database = dbhelper.getWritableDatabase();

        //현재 년도와 데이터피커 설정
        yearText();

        //현재 월과 데이터피커 설정
        monthText();

        //제일처음지출값 적용
        assetType = "expense";
        assetDate = "expense_date";

        //상세내역
        chartContent();

        //그래프
        pieChart();

        return view;
    }

    private void pieChart() {

        pieChart.invalidate();
        pieEntry.clear();

        //아무값이 없을 때 nodata를 띄워줌
        if(assetTypeCnt.size() == 0){
            nodata.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            pieChart.setVisibility(View.INVISIBLE);
        } else {
            nodata.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            for(int i = 0; i < assetTypeCnt.size(); i++){
                pieEntry.add(new PieEntry(Float.parseFloat(assetCnt.get(i)), assetTypeCnt.get(i)));
                dataSet = new PieDataSet(pieEntry, null);
            }

            dataSet.setSliceSpace(0.5f);//파이 사이간격(이거때문에 적은 값들이 안보였었음)
            dataSet.setSelectionShift(5f);
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            //dataSet.setValueLinePart1OffsetPercentage(80.f);
            dataSet.setValueLinePart1Length(0.4f);
            dataSet.setValueLinePart2Length(1.0f);

            data = new PieData((dataSet));
            data.setValueTextSize(15f);
            data.setValueFormatter(new PercentFormatter());

            pieChart.setData(data);

            pieChart.notifyDataSetChanged();
        }

    }

    public class PercentFormatter extends ValueFormatter {

        public PercentFormatter() {
            mFormat = new DecimalFormat("###,###,##0.00");
            percentSignSeparated = true;
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value) + " %";
        }

        @Override
        public String getPieLabel(float value, PieEntry pieEntry) {
            if (pieChart != null && pieChart.isUsePercentValuesEnabled()) {
                // Converted to percent
                return getFormattedValue(value);
            } else {
                // raw value, skip percent sign
                return mFormat.format(value);
            }
        }
    }

    //년도 설정
    private void yearText() {
        //현재 년도
        year = titleText.getText().toString();
        year = year.substring(0, 4);

        //타이틀바에 있는 텍스트 변경시
        titleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                year = titleText.getText().toString().substring(0, 4);
                chartContent();
                pieChart();
            }
        });
    }

    //월 데이터피커
    private void monthText() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        SimpleDateFormat curMonth = curMonthFormat;

        //현재 월
        monthText.setText(curMonth.format(date) + "월");
        month = monthText.getText().toString();
        month = month.substring(0,2);

        //월 데이터 피커 설정
        monthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPicker picker = new MonthPicker();
                picker.setListener(mListener);
                picker.show(getFragmentManager(), "monthPicker");
            }
        });
    }

    DatePickerDialog.OnDateSetListener mListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yearListener, int monthListener, int dayOfMonth) {
            month = String.valueOf(monthListener);
            if(month.length() == 1){
                month = "0" + month;
            }
            monthText.setText(month + "월");

            chartContent();
            pieChart();
        }
    };

    //상세내역
    private void chartContent() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        assetTypeCnt.clear();
        assetCnt.clear();

        if(database != null){
            if(month.equals("01") || month.equals("03") || month.equals("05") || month.equals("07") || month.equals("08") || month.equals("10") || month.equals("12")){
                day = 31;
            } else if(month.equals("02")){
                if (Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0 || Integer.parseInt(year) % 400 == 0){
                    day = 29;
                } else {
                    day = 28;
                }
            } else if(month.equals("04") || month.equals("06") || month.equals("09") || month.equals("11")){
                day = 30;
            }
            year_month_day = year + "-" + month + "-" + day;
            year_month = year + "-" + month;
            sql = "select asset_name, sum(amount) from " + assetType + " where " + assetDate + " >= '" + year_month + "-01' and " + assetDate + " <= " + "'" + year_month_day + "' group by asset_name";
            if(sql != null){
                Cursor cursor = database.rawQuery(sql, null);
                while (cursor.moveToNext()){
                    assetName = cursor.getString(0);
                    amount = cursor.getString(1);
                    assetTypeCnt.add(assetName);
                    assetCnt.add(amount);
                    DailyInAndOut d = new DailyInAndOut(0, null, null, assetName, null, Integer.parseInt(amount), null);
                    adapter.addItem(d);
                }
                cursor.close();
                int amountSum = 0;
                for(int i = 0; i < assetTypeCnt.size(); i++){
                 amountSum += Integer.parseInt(assetCnt.get(i));
                }
               // Log.d("TAG", "amountSum: " + amountSum);
                for(int i = 0; i < assetTypeCnt.size(); i++){
                    //Log.d("TAG", "asset: " + String.format("%.2f", (Float.parseFloat(assetCnt.get(i))/amountSum)*100));
                    adapter.addPer(String.format("%.2f", (Float.parseFloat(assetCnt.get(i))/amountSum)*100) + " %");
                }
            }
        }
    }
}