package com.example.mytest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    ArrayList<DailyInAndOut> items = new ArrayList<>();

    DailyInAndOut item;

    @NonNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.calendar_item, parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.ViewHolder holder, final int position) {
        item = items.get(position);
        holder.setItem(item);
        //Log.d("TAG", "onBindViewHolder: " + item.getAmount());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick:position " + item.getDate());
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra("date", item.getDate());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public  ArrayList<DailyInAndOut> getList(){
        return items;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView category, money;

        public ViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            money = itemView.findViewById(R.id.money);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Log.d("TAG", "onClick: " + pos);
                    }
                }
            });
        }

        public void setItem(DailyInAndOut item) {
            Log.d("TAG", "setItem: " + item);
            category.setText(item.getCategoryName());
            money.setText(String.valueOf(item.getAmount()));
            if(item.getMemo() != null){
                category.setText(item.getCategoryName()+"(" + item.getMemo() + ")");
              }
//            else if(!(item.getExMemo().equals(""))){
//                category.append(item.getEcNum()+"(" + item.getExMemo() + ")");
//            }
        }
    }

    public void addItem(DailyInAndOut item){items.add(item);}

    public void setItems(ArrayList<DailyInAndOut> items) {this.items = items;}

    public DailyInAndOut getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, DailyInAndOut item){items.set(position, item);}

    public void clear(){
         items.clear();
    }
}
