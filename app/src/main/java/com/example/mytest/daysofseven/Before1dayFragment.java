package com.example.mytest.daysofseven;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.mytest.daily.DailyAdapter;
import com.example.mytest.daily.DailyInAndOut;
import com.example.mytest.DatabaseHelper;
import com.example.mytest.daily.MainActivity;
import com.example.mytest.daily.OnSwipeTouchListener;
import com.example.mytest.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.id.home;
import static com.example.mytest.R.id.textView13;
import static com.example.mytest.R.id.textView14;
import static com.example.mytest.R.id.textView15;
import static com.example.mytest.R.id.titleText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Before1dayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Before1dayFragment extends Fragment {
    TextView before3,before2,before1,select,next1,next2,next3;
    TextView title;
    LocalDate thisday;
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    RecyclerView recyclerView;
    ArrayList<DailyInAndOut> outList,inList;
    DailyAdapter adapter;
    Cursor cursor;
    TextView incomeT,totalT,expenseT;
    LocalDate today =LocalDate.now();
    After1dayFragment after1dayFragment;
    SelectDayFragment selectDayFragment;
    Before1dayFragment before1dayFragment;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Before1dayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Before1dayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Before1dayFragment newInstance(String param1, String param2) {
        Before1dayFragment fragment = new Before1dayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_before1day, container, false);

        final MainActivity activity = (MainActivity)getActivity();
        //헤드부분 날짜
        title = activity.findViewById(titleText);
        before3 = view.findViewById(R.id.textView4);
        before2 = view.findViewById(R.id.textView5);
        before1 = view.findViewById(R.id.textView6);
        select = view.findViewById(R.id.textView7);
        next1 = view.findViewById(R.id.textView8);
        next2 = view.findViewById(R.id.textView9);
        next3 = view.findViewById(R.id.textView10);

        //7일 날짜 구성
        setSevenDays();
        //7일 클릭시 클릭한곳으로 이동
        dayclickEvent();

        //내역보여줄 리사이클러뷰
        recyclerView = view.findViewById(R.id.dailyRecyclerView);
        adapter = new DailyAdapter(activity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        outList = new ArrayList<>();
        inList = new ArrayList<>();

        dbHelper = new DatabaseHelper(getContext());
        database = dbHelper.getWritableDatabase();

        incomeT= view.findViewById(textView13);
        totalT = view.findViewById(textView14);
        expenseT = view.findViewById(textView15);

        //하루내역 보여주기
        showDailyResult();

        view.setOnTouchListener(new OnSwipeTouchListener(getContext()){

            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onSwipeRight() {
                String selectDayStr = title.getText().toString();
                MainActivity activity = (MainActivity)getActivity();
                LocalDate selectDay = LocalDate.parse(selectDayStr);
                title.setText(selectDay.minusDays(1).toString());
                setSevenDays();
                showDailyResult();
                activity.onFragementChanged(1);
                //Toast.makeText(getContext(), "전날임", Toast.LENGTH_SHORT).show();
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onSwipeLeft() {
                String selectDayStr = title.getText().toString();
                MainActivity activity = (MainActivity)getActivity();
                LocalDate selectDay = LocalDate.parse(selectDayStr);
                title.setText(selectDay.plusDays(1).toString());
                setSevenDays();
                showDailyResult();
                activity.onFragementChanged(3);

                //Toast.makeText(getContext(), "다음날로 이동", Toast.LENGTH_SHORT).show();
            }
        });
        //헤드가운데 부분 클릭이벤트 테스트
        after1dayFragment = new After1dayFragment();
        selectDayFragment = new SelectDayFragment();
        before1dayFragment = new Before1dayFragment();

        title.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Log.d("TAG", "전날프레그먼크 클릭이벤트");
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), listener, today.getYear(), today.getMonthValue()-1, today.getDayOfMonth());
                datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.show();

            }
        });
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);//원래 상단바의 이름을 감춤
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.today);
        setHasOptionsMenu(true);


        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void dayclickEvent() {
        before3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                thisday = LocalDate.parse(title.getText().toString());
                title.setText(thisday.minusDays(3).toString());
                activity.onFragementChanged(2);
                setSevenDays();
                showDailyResult();

            }
        });
        before2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                thisday = LocalDate.parse(title.getText().toString());
                title.setText(thisday.minusDays(2).toString());
                activity.onFragementChanged(2);
                setSevenDays();
                showDailyResult();
            }
        });
        before1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                thisday = LocalDate.parse(title.getText().toString());
                title.setText(thisday.minusDays(1).toString());
                activity.onFragementChanged(2);
                setSevenDays();
                showDailyResult();
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                thisday = LocalDate.parse(title.getText().toString());
                title.setText(thisday.toString());
                activity.onFragementChanged(2);
                setSevenDays();
                showDailyResult();
            }
        });
        next1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                thisday = LocalDate.parse(title.getText().toString());
                title.setText(thisday.plusDays(1).toString());
                activity.onFragementChanged(2);
                setSevenDays();
                showDailyResult();
            }
        });
        next2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                thisday = LocalDate.parse(title.getText().toString());
                title.setText(thisday.plusDays(2).toString());
                activity.onFragementChanged(2);
                setSevenDays();
                showDailyResult();
            }
        });
        next3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                thisday = LocalDate.parse(title.getText().toString());
                title.setText(thisday.plusDays(3).toString());
                activity.onFragementChanged(2);
                setSevenDays();
                showDailyResult();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setSevenDays(){
        String selectDayStr = title.getText().toString();
        LocalDate selecday = LocalDate.parse(selectDayStr);
        before3.setText(selecday.minusDays(3).getDayOfMonth()+"");
        before2.setText(selecday.minusDays(2).getDayOfMonth()+"");
        before1.setText(selecday.minusDays(1).getDayOfMonth()+"");
        select.setText(selecday.getDayOfMonth()+"");
        next1.setText(selecday.plusDays(1).getDayOfMonth()+"");
        next2.setText(selecday.plusDays(2).getDayOfMonth()+"");
        next3.setText(selecday.plusDays(3).getDayOfMonth()+"");
    }


    private void showDailyResult() {
        inList.clear();
        outList.clear();
        String[] expense = new String[]{"expense_date","asset_name","expensecategory_name","amount","memo"};
        String[] income = new String[]{"income_date","asset_name","incomecategory_name","amount","memo"};
        String selectDayStr = title.getText().toString();
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

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override//좌상단-오늘 클릭했을때
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d("TAG", "전날프레그먼트에서 실행한 오늘클릭: ");
        Date current = Calendar.getInstance().getTime();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(current);
        if(home == item.getItemId()){
            title.setText(date);
            //Toast.makeText(this, "오늘 날짜" + current, Toast.LENGTH_SHORT).show();
            setSevenDays();
            showDailyResult();

            onFragementChanged(2);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //헤드 가운데 날짜 선택했을때
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
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
            title.setText(year + "-" + (month) + "-"+ day);
            setSevenDays();
            showDailyResult();
            onFragementChanged(2);
        }
    };

    public void onFragementChanged(int index) {

        if (index == 3) {
            //프래그먼트 변경
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, after1dayFragment).commit();
        } else if (index == 2) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, selectDayFragment).commit();
        } else if (index == 1) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, before1dayFragment).commit();
        }
    }

}