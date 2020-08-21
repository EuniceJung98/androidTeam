package com.example.mytest;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import java.time.LocalDate;
import java.util.ArrayList;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {
    ArrayList<DailyInAndOut> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.daily_inandout_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyInAndOut item = items.get(position);
        holder.setItem(item);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(DailyInAndOut item){
        Log.d("book", "addItem: ");
        items.add(item);
    }

    public DailyInAndOut getItem(int position){//특정포지션에 있는 책을 가져옴
        return items.get(position);
    }

    //특정포지션에 넣어준다
    public void setItem(int position, DailyInAndOut item){
        items.set(position, item);
    }

    public void clear(){
        items.clear();
    }

    public ArrayList<DailyInAndOut> getList(){
        return items;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView categoryT,assetT,memoT,amountT;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            categoryT = itemView.findViewById(R.id.categoryTextView);
            assetT = itemView.findViewById(R.id.assetTextView);
            memoT = itemView.findViewById(R.id.memoTextView);
            amountT = itemView.findViewById(R.id.amountTextView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos!= RecyclerView.NO_POSITION){
                        //Toast.makeText(itemView.getContext(),"클릭해찌"+pos+items.get(pos).getMemo(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(itemView.getContext(),UpdateMoneyBookActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("contents",items.get(pos));
                        itemView.getContext().startActivity(intent);
                    }
                }
            });

//            itemView.setOnTouchListener(new OnSwipeTouchListener(itemView.getContext()){
//                TextView title = itemView.findViewById(R.id.titleText);
//                TextView before3= itemView.findViewById(R.id.textView4);
//                TextView before2 = itemView.findViewById(R.id.textView5);
//                TextView before1 = itemView.findViewById(R.id.textView6);
//                TextView select = itemView.findViewById(R.id.textView7);
//                TextView next1= itemView.findViewById(R.id.textView8);
//                TextView next2 = itemView.findViewById(R.id.textView9);
//                TextView next3 = itemView.findViewById(R.id.textView10);;
//
//                @RequiresApi(api = Build.VERSION_CODES.O)
//                public void onSwipeRight() {
//                    Log.d("TAG", "onSwipeRight: ");
//                    String selectDayStr = title.getText().toString();
//                    MainActivity activity = (MainActivity)getActivity();
//                    LocalDate selectDay = LocalDate.parse(selectDayStr);
//                    title.setText(selectDay.minusDays(1).toString());
//                    LocalDate selecday = LocalDate.parse(selectDayStr);
//                    before3.setText(selecday.minusDays(3).getDayOfMonth()+"");
//                    before2.setText(selecday.minusDays(2).getDayOfMonth()+"");
//                    before1.setText(selecday.minusDays(1).getDayOfMonth()+"");
//                    select.setText(selecday.getDayOfMonth()+"");
//                    next1.setText(selecday.plusDays(1).getDayOfMonth()+"");
//                    next2.setText(selecday.plusDays(2).getDayOfMonth()+"");
//                    next3.setText(selecday.plusDays(3).getDayOfMonth()+"");
//
//                    activity.onFragementChanged(1);
//                }
//                @RequiresApi(api = Build.VERSION_CODES.O)
//                public void onSwipeLeft() {
// //                   Log.d("TAG", "onSwipeLeft: ");
////                    String selectDayStr = title.getText().toString();
////
////                    MainActivity activity = (MainActivity)getActivity();
////                    LocalDate selectDay = LocalDate.parse(selectDayStr);
////                    title.setText(selectDay.plusDays(1).toString());
////                    setSevenDays();
////                    activity.onFragementChanged(3);
//
//                    //Toast.makeText(getContext(), "다음날로 이동", Toast.LENGTH_SHORT).show();
//                }
//            });
        }

        public void setItem(DailyInAndOut item){
            categoryT.setText(item.getCategoryName());
            assetT.setText(item.getAssetName());
            memoT.setText(item.getMemo());
            amountT.setText(item.getAmount()+"원");
            //Log.d("TEST", item.getCategoryName());
        }
    }


}
