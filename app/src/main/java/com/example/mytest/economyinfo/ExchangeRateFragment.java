package com.example.mytest.economyinfo;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mytest.R;
import com.google.gson.Gson;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExchangeRateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExchangeRateFragment extends Fragment {
    Handler handler = new Handler();
    ProgressDialog progressDialog;
    TextView USARateTextView,EuroTextView,ChinaTextView,EnglandTextView,JapanTextView,workingDayTextView,exchangeRateTitle;
    ArrayList<String> list=new ArrayList<>();
    String result;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExchangeRateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExchangeRateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExchangeRateFragment newInstance(String param1, String param2) {
        ExchangeRateFragment fragment = new ExchangeRateFragment();
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

    static RequestQueue requestQueue;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_exchange_rate, container, false);


        USARateTextView = view.findViewById(R.id.USARateTextView);
        EuroTextView = view.findViewById(R.id.EuroRateTextView);
        ChinaTextView = view.findViewById(R.id.ChinaRateTextView);
        EnglandTextView = view.findViewById(R.id.EnglandRateTextView);
        JapanTextView = view.findViewById(R.id.JapanRaTetextView);
        workingDayTextView = view.findViewById(R.id.workingDayTextView);
        exchangeRateTitle = view.findViewById(R.id.exchangeRateTitle);
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getContext());
        }
        minusdays[0]=0;
        sendRequest();

        return view;
    }//onCreateView끝



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendRequest() {
        progressDialog = ProgressDialog.show(getContext(),"ing...","환율정보 가져오는 중...",true,true);
        //요청 url을 받아옴
        LocalDate today = LocalDate.now();
        exchangeRateTitle.setText("오늘("+today.toString()+")의 환율");
        findworkingDay=today;
            String date="";
            String monthStr=today.getMonthValue()+"";
                if (monthStr.length()==1){
                    monthStr="0"+monthStr;
                }
                String dayStr=today.getDayOfMonth()+"";
                if (dayStr.length()==1){
                    dayStr="0"+dayStr;
                }
                date=today.getYear()+monthStr+dayStr;
            //Log.d("날짜를 보여줘", "년도: "+today.getYear());
            String urlStr="http://ds.gscms.co.kr:8888/Rest/ExchangeRates/081?type=json&sessionID=test&date="+date;
            //Log.d("url을 보여줘", "유알엘은요: "+urlStr);

        //문자열 request객체를 생성 //문자열 요청을 함
        //인자: 요청방식, 주소, 응답리스터, 에러리스너
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    urlStr,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //응답이 왔을때 실행할 내용
                            processResponse(response);
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //에러가 왔을 때 실행할 내용
                            //에러를 화면에 뿌려준다
                            Log.d("에러났다으아", "에러: " + error.getMessage());
                        }
                    }
            );
            //위에서 만든 request객체를 큐에 추가해준다


        request.setShouldCache(false);//같은요청이 들어와도 계속 요청함
        requestQueue.add(request); //볼리가 알아서 스레드를 써

    }
//
    LocalDate findworkingDay; long[] minusdays = {0};
//    //json을 자바객체로 변환해주는 메서드
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void processResponse(String response) {
        result=response;
        repeatFindWorkingDay(response);
    }

    public void realDataProcess(String response){
        Gson gson = new Gson();
        //제이슨으로 받은 내용을 ExchangeRates 변경하여 ExchangeRates 반환함
        ExchnageRateResult exchnageRateResult = gson.fromJson(response, ExchnageRateResult.class);//제이슨으로 받은 놈을 ExchangeRates로 변경해서 반환
        if(exchnageRateResult.ExchangeRates.Row!=null) {
            //영화정보를 이제 사용할 수 있음
            String s = "영화 정보 수 :" + exchnageRateResult.ExchangeRates.Row.size();
            ArrayList<ExRate> exratelist = exchnageRateResult.ExchangeRates.Row;
            for (ExRate result : exratelist) {
                if (result.국명.equals("미국")) {
                    USARateTextView.setText(result.매매기준율 + "");
                }
                if (result.국명.equals("영국")) {
                    EnglandTextView.setText(result.매매기준율 + "");
                }
                if (result.국명.equals("일본")) {
                    JapanTextView.setText(result.매매기준율 + "");
                }
                if (result.국명.equals("중국")) {
                    ChinaTextView.setText(result.매매기준율 + "");
                }
                if (result.국명.equals("유로")) {
                    EuroTextView.setText(result.매매기준율 + "");
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void repeatFindWorkingDay(String response){
        //Log.d(minusdays[0]+"번째응답하라", "onResponse: " + response);
        int test= response.indexOf("\"@count\":\"0\"");
        //Log.d("정보가 있는지 없는지 여부", "환율정보 있으면-1이다" + test);
        if(test!=-1){
            minusdays[0]++; Log.d("마이너스데이는 몇인가", "onResponse: " + minusdays[0]);
            findworkingDay=LocalDate.now().minusDays(minusdays[0]);
            //Log.d("워킹데이를 찾아서", "findworkingDay: " + findworkingDay.toString());
            String monthStr=findworkingDay.getMonthValue()+"";
            if (monthStr.length()==1){
                monthStr="0"+monthStr;
            }
            String dayStr=findworkingDay.getDayOfMonth()+"";
            if (dayStr.length()==1){
                dayStr="0"+dayStr;
            }
            String date=findworkingDay.getYear()+monthStr+dayStr;
            String urlStr="http://ds.gscms.co.kr:8888/Rest/ExchangeRates/081?type=json&sessionID=test&date="+date;
            Log.d("TAG", "urlStr: "+urlStr);
            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    urlStr,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            repeatFindWorkingDay(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //에러가 왔을 때 실행할 내용
                            //에러를 화면에 뿌려준다
                            Log.d("에러났다으아", "에러: " + error.getMessage());
                        }
                    }
            );
            request.setShouldCache(false);//같은요청이 들어와도 계속 요청함
            requestQueue.add(request); //볼리가 알아서 스레드를 써
        }else {
            workingDayTextView.append("\n"+findworkingDay.toString()+"일의 매매기준율");
            realDataProcess(response);
            progressDialog.dismiss();
        }
    }
}