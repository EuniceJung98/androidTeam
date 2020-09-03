package com.example.mytest.chart;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.daily.DailyInAndOut;

import java.util.ArrayList;

public class AssetChartAdapter extends RecyclerView.Adapter<AssetChartAdapter.ViewHolder> {

    ArrayList<DailyInAndOut> items = new ArrayList<>();
    ArrayList<String> perList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pie_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DailyInAndOut item = items.get(position);
        holder.setItem(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView assetTypeText, assetText, perText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            assetTypeText = itemView.findViewById(R.id.assetTypeText);
            assetText = itemView.findViewById(R.id.assetText);
            perText = itemView.findViewById(R.id.assetPer);
        }

        public void setItem(DailyInAndOut item, int position) {
            assetTypeText.setText(item.getAssetName());
            assetText.setText(String.valueOf(item.getAmount()));
            //Log.d("TAG", "setItem: " + perList.get(position));
            perText.setText(" ( " + perList.get(position) + " )");
        }
    }

    public void addItem(DailyInAndOut item){items.add(item);}

    public void addPer(String per){perList.add(per);}

    public void setItems(ArrayList<DailyInAndOut> item) {this.items = items;}

    public DailyInAndOut getItem(int position) {return items.get(position);}

    public void setItem(int position, DailyInAndOut item) {items.set(position, item);}

    public void clear(){
        items.clear();
        perList.clear();
    }
}
