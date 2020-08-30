package com.example.mytest.daily;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.mytest.R;

import java.util.ArrayList;

public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {
    ArrayList<DailyInAndOut> items = new ArrayList<>();

    MainActivity mActivity;

    public DailyAdapter() {
    }

    public DailyAdapter(MainActivity activity) {
        mActivity = activity;
    }

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
        //Log.d("book", "addItem: ");
        items.add(item);
    }

    public DailyInAndOut getItem(int position){
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
                        Log.d("수정한번 한뒤", "수정버튼누름: "+items.get(pos).toString());
                        Intent intent = new Intent(itemView.getContext(), UpdateMoneyBookActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("contents",items.get(pos));
                        itemView.getContext().startActivity(intent);
                    }
                }
            });
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
