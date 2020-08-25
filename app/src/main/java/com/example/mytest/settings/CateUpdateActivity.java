package com.example.mytest.settings;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.DatabaseHelper;
import com.example.mytest.R;

import java.util.ArrayList;

public class CateUpdateActivity extends Activity {

    CheckBox exCheckBox,inCheckBox;

    RecyclerView cateRecyclerView;
    CategoryAdapter cateAdapter;
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    boolean isExChecked=true;
    Button addCateButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cate_update);

        cateRecyclerView = findViewById(R.id.cateItemRecyclerView);
        cateAdapter = new CategoryAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        cateRecyclerView.setLayoutManager(layoutManager);
        cateRecyclerView.setAdapter(cateAdapter);
        dbHelper = new DatabaseHelper(getApplicationContext());
        database = dbHelper.getWritableDatabase();

        exCheckBox= findViewById(R.id.excheckBox);
        inCheckBox = findViewById(R.id.incheckBox);
        exCheckBox.setChecked(true);
        addCateButton= findViewById(R.id.addCateButton);
        addCateButton.setText("지출 카테고리 추가");

        //지출 선택했을때
        exCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    inCheckBox.setChecked(false);
                }
                if(inCheckBox.isChecked()==false && isChecked==false){

                    exCheckBox.setChecked(true);
                }
                isExChecked=true;
                //Log.d("뭐가 실행되냐? 지출파트", "onCheckedChanged: ");
                if (isChecked){
                    addCateButton.setText("지출 카테고리 추가");
                    setCategoryName();
                }
            }
        });
        //수입 선택했을때
        inCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    exCheckBox.setChecked(false);
                }
                if(exCheckBox.isChecked()==false && isChecked==false){
                    inCheckBox.setChecked(true);
                }
                isExChecked=false;
                //Log.d("뭐가 실행되냐? 수입파트", "onCheckedChanged: ");
                if (isChecked){
                    addCateButton.setText("수입 카테고리 추가");
                    setCategoryName();
                }
            }
        });
        //카테추가 버튼
        addCateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regDBCategory();
            }
        });

        setCategoryName();

    }//온크리에이트 끝



    //리사이클러 뷰에 카테고리 이름 뿌려주기
    private void setCategoryName() {

        //Log.d("실행이 되나요?", "setCategoryName: ");
        cateAdapter.clear();
        String exSelecSql="select category_id,expensecategory_name from expensecategory";
        String inSelecSql="select category_id,incomecategory_name from incomecategory";
        if(isExChecked){//지출 선택시
            cursor= database.rawQuery(exSelecSql,null);
            while(cursor.moveToNext()){
                //Log.d("지출 커서 안쪽입니다.", "setCategoryName: ");
                int cateId= cursor.getInt(0);
                String cateitem =cursor.getString(1);
                cateAdapter.addItem(new UpdateSetting(cateId,"지출",cateitem));
            }

        }else {
            cursor= database.rawQuery(inSelecSql,null);
            while(cursor.moveToNext()){
                //Log.d("수입 커서 안쪽입니다.", "setCategoryName: ");
                int cateId= cursor.getInt(0);
                String cateitem =cursor.getString(1);
                cateAdapter.addItem(new UpdateSetting(cateId,"수입",cateitem));
            }
        }
        cateAdapter.notifyDataSetChanged();
        cursor.close();
    }

    //카테고리 추가 다이얼로그로
    void regDBCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText addCateEditText = new EditText(CateUpdateActivity.this);
        addCateEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(10);
        addCateEditText.setFilters(FilterArray);
        addCateEditText.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //Editable inputStr=passwordEditText.getText();
                if (addCateEditText.getText().toString().length()>9)
                {
                    // Not allowed
                    Toast.makeText(getApplicationContext(),"10자이상 입력할수 없습니다",Toast.LENGTH_SHORT).show();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void afterTextChanged(Editable s){}
        });
        builder.setView(addCateEditText);
        if (isExChecked){
            builder.setTitle("지출 카테고리 추가");
        }else {
            builder.setTitle("수입 카테고리 추가");
        }
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
                String inputRegCategory= addCateEditText.getText().toString();
                if (inputRegCategory.equals("")) {
                    addCateEditText.post(new Runnable() {
                        @Override
                        public void run() {
                            addCateEditText.setFocusableInTouchMode(true);
                            addCateEditText.requestFocus();
                        }
                    });
                    Toast.makeText(CateUpdateActivity.this, "추가할 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                }else {
                    String exInsertsql="insert into expensecategory(expensecategory_name) values('"+inputRegCategory+"')";
                    String inInsertsql="insert into incomecategory(incomecategory_name) values('"+inputRegCategory+"')";
                    try {
                        if (isExChecked){
                            database.execSQL(exInsertsql);
                        }else {
                            database.execSQL(inInsertsql);
                        }
                        Toast.makeText(CateUpdateActivity.this, "카테고리등록완료", Toast.LENGTH_SHORT).show();
                        setCategoryName();
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



    //////////////////////////////////////////////////////////////////

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        ArrayList<UpdateSetting> items = new ArrayList<>();

        //CateUpdateActivity cateUpdateActivity;
        // MainActivity mActivity;

        public CategoryAdapter() {
        }

//    public CategoryAdapter(CateUpdateActivity activity) {
//        cateUpdateActivity = activity;
//    }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cate_andasset_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
//                        Log.d("카테아답터", "어쨌든 클릭 누름" + items.get(pos).toString());
                            updateCategory();
                        }
                    }

                    private void updateCategory() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        final EditText updateCateEditText = new EditText(itemView.getContext());
                        updateCateEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(10);
                        updateCateEditText.setFilters(FilterArray);
                        updateCateEditText.addTextChangedListener(new TextWatcher(){
                            public void onTextChanged(CharSequence s, int start, int before, int count)
                            {
                                if (updateCateEditText.getText().toString().length()>9)
                                {
                                    // Not allowed
                                    Toast.makeText(itemView.getContext(),"10자이상 입력할수 없습니다",Toast.LENGTH_SHORT).show();
                                }
                            }
                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                            public void afterTextChanged(Editable s){}
                        });
                        updateCateEditText.setText(items.get(getAdapterPosition()).getCategoryName());
                        builder.setView(updateCateEditText);
                        builder.setTitle("카테고리 수정");
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
                        if(items.get(getAdapterPosition()).getId()>1){//카테고리 하나는 반드시 있어야 함, 수정은 가능
                            builder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String inDeleteSql="delete from incomecategory where category_id="+items.get(getAdapterPosition()).getId();
                                    String exDeleteSql="delete from expensecategory where category_id="+items.get(getAdapterPosition()).getId();
                                    try {
                                        if(isExChecked){
                                            database.execSQL(exDeleteSql);
                                        }else{
                                            database.execSQL(inDeleteSql);
                                        }
                                        Toast.makeText(itemView.getContext(), "카테고리 삭제", Toast.LENGTH_SHORT).show();
                                        setCategoryName();
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
                                String inputRegCategory= updateCateEditText.getText().toString();
                                if (inputRegCategory.equals("")) {
                                    updateCateEditText.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateCateEditText.setFocusableInTouchMode(true);
                                            updateCateEditText.requestFocus();
                                        }
                                    });
                                    Toast.makeText(itemView.getContext(), "수정할 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                                }else {
                                    String exCateUpdatesql="update expensecategory set expensecategory_name='"+
                                            updateCateEditText.getText().toString()+"' where category_id="+items.get(getAdapterPosition()).getId();
                                    String inCateUpdatesql="update incomecategory set incomecategory_name='"+
                                            updateCateEditText.getText().toString()+"' where category_id="+items.get(getAdapterPosition()).getId();
                                    try {
                                        if(isExChecked){
                                            database.execSQL(exCateUpdatesql);
                                        }else{
                                            database.execSQL(inCateUpdatesql);
                                        }
                                        Toast.makeText(itemView.getContext(), "카테고리수정완료", Toast.LENGTH_SHORT).show();
                                        setCategoryName();
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
                //Log.d("카테아답터", "setItem실행" + item);
                itembutton.setText(item.getCategoryName());
            }


        }
    }

}