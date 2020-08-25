package com.example.mytest.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.DatabaseHelper;
import com.example.mytest.R;

import java.util.ArrayList;

public class AssetUpdateActivity extends Activity {
    RecyclerView assetRecyclerView;
    AssetUpdateAdapter assetUpdateAdapter;
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    Button addCateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_asset_update);

        assetRecyclerView = findViewById(R.id.assetItemRecyclerView);
        assetUpdateAdapter = new AssetUpdateAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        assetRecyclerView.setLayoutManager(layoutManager);
        assetRecyclerView.setAdapter(assetUpdateAdapter);
        dbHelper = new DatabaseHelper(getApplicationContext());
        database = dbHelper.getWritableDatabase();

        findViewById(R.id.addAssetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regDBAsset();
            }
        });

        setAssetName();
    }//onCreate끝부분

    private void regDBAsset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText addAssetEditText = new EditText(AssetUpdateActivity.this);
        addAssetEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(10);
        addAssetEditText.setFilters(FilterArray);
        addAssetEditText.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //Editable inputStr=passwordEditText.getText();
                if (addAssetEditText.getText().toString().length()>9)
                {
                    // Not allowed
                    Toast.makeText(getApplicationContext(),"10자이상 입력할수 없습니다",Toast.LENGTH_SHORT).show();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void afterTextChanged(Editable s){}
        });
        builder.setView(addAssetEditText);
        builder.setTitle("자산 추가");

        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {}
        });

        builder.setNegativeButton("돌아감", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog= builder.create();
        // 창 띄우기
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Boolean wantToCloseDialog = false;
                String inputRegAsset= addAssetEditText.getText().toString();
                if (inputRegAsset.equals("")) {
                    addAssetEditText.post(new Runnable() {
                        @Override
                        public void run() {
                            addAssetEditText.setFocusableInTouchMode(true);
                            addAssetEditText.requestFocus();
                        }
                    });
                    Toast.makeText(AssetUpdateActivity.this, "추가할 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                }else {
                    String assetInsertsql="insert into asset(asset_name) values('"+inputRegAsset+"')";
                    try {
                        database.execSQL(assetInsertsql);
                        Toast.makeText(AssetUpdateActivity.this, "자산등록완료", Toast.LENGTH_SHORT).show();
                        setAssetName();
                        wantToCloseDialog = true;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(wantToCloseDialog)
                    dialog.dismiss();

            }
        });
    }

    private void setAssetName() {
        assetUpdateAdapter.clear();
        String assetSelecSql="select asset_id,asset_name from asset";
        try {
            cursor = database.rawQuery(assetSelecSql,null);
            while(cursor.moveToNext()){
                int assetId= cursor.getInt(0);
                String assetitem =cursor.getString(1);
                assetUpdateAdapter.addItem(new UpdateSetting(assetId,assetitem));
            }
            assetUpdateAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            cursor.close();
        }
    }


    ///////////////////////////////////////////////////

    public class AssetUpdateAdapter extends RecyclerView.Adapter<AssetUpdateActivity.AssetUpdateAdapter.ViewHolder> {
        ArrayList<UpdateSetting> items = new ArrayList<>();

        public AssetUpdateAdapter() { }

        @NonNull
        @Override
        public AssetUpdateActivity.AssetUpdateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cate_andasset_item, parent, false);
            return new AssetUpdateActivity.AssetUpdateAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AssetUpdateActivity.AssetUpdateAdapter.ViewHolder holder, int position) {
            UpdateSetting item = items.get(position);
            holder.setItem(item);
        }


        @Override
        public int getItemCount() {
            return items.size();
        }

        public void addItem(UpdateSetting item) {
            items.add(item);
        }

        public UpdateSetting getItem(int position) {
            return items.get(position);
        }

        //특정포지션에 넣어준다
        public void setItem(int position, UpdateSetting item) {
            items.set(position, item);
        }

        public void clear() {
            items.clear();
        }

        public ArrayList<UpdateSetting> getList() {
            return items;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView itembutton;

            public ViewHolder(@NonNull final View itemView) {
                super(itemView);
                itembutton = itemView.findViewById(R.id.settingItemsButton);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            updateCategory();
                        }
                    }

                    private void updateCategory() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        final EditText updateAssetEditText = new EditText(itemView.getContext());
                        updateAssetEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(10);
                        updateAssetEditText.setFilters(FilterArray);
                        updateAssetEditText.addTextChangedListener(new TextWatcher(){
                            public void onTextChanged(CharSequence s, int start, int before, int count)
                            {
                                if (updateAssetEditText.getText().toString().length()>9)
                                {
                                    // Not allowed
                                    Toast.makeText(itemView.getContext(),"10자이상 입력할수 없습니다",Toast.LENGTH_SHORT).show();
                                }
                            }
                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                            public void afterTextChanged(Editable s){}
                        });
                        updateAssetEditText.setText(items.get(getAdapterPosition()).getAssetName());
                        builder.setView(updateAssetEditText);
                        builder.setTitle("자산 수정");
                        builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {}
                        });

                        builder.setNeutralButton("돌아감", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        if(items.get(getAdapterPosition()).getId()>1){//자산 하나는 반드시 있어야 함, 수정은 가능
                            builder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String assetDeleteSql="delete from asset where asset_id="+items.get(getAdapterPosition()).getId();
                                    try {
                                        database.execSQL(assetDeleteSql);
                                        Toast.makeText(itemView.getContext(), "자산 삭제", Toast.LENGTH_SHORT).show();
                                        setAssetName();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        final AlertDialog dialog= builder.create();
                        // 창 띄우기
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Boolean wantToCloseDialog = false;
                                String inputRegCategory= updateAssetEditText.getText().toString();
                                if (inputRegCategory.equals("")) {
                                    updateAssetEditText.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateAssetEditText.setFocusableInTouchMode(true);
                                            updateAssetEditText.requestFocus();
                                        }
                                    });
                                    Toast.makeText(itemView.getContext(), "수정할 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                                }else {
                                    String assetUpdatesql="update asset set asset_name='"+
                                            updateAssetEditText.getText().toString()+"' where asset_id="+items.get(getAdapterPosition()).getId();
                                    try {
                                    database.execSQL(assetUpdatesql);
                                    Toast.makeText(itemView.getContext(), "자산수정완료", Toast.LENGTH_SHORT).show();
                                    setAssetName();
                                        wantToCloseDialog = true;
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                if(wantToCloseDialog)
                                    dialog.dismiss();

                            }
                        });
                    }
                });


            }

            public void setItem(UpdateSetting item) {
                Log.d("자산", "setItem실행" + item);
                itembutton.setText(item.getAssetName());
            }
        }
    }


}