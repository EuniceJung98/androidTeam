package com.example.mytest.chart;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import com.example.mytest.R;

import java.util.Calendar;

public class YearPicker extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener;
    public Calendar cal = Calendar.getInstance();

    public void setListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    Button btnConfirm, btnCancel;

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.activity_year_picker, null);

        btnConfirm = dialog.findViewById(R.id.btn_confirm);
        btnCancel = dialog.findViewById(R.id.btn_cacncel);

        final NumberPicker yearPicker = dialog.findViewById(R.id.char_picker_year);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YearPicker.this.getDialog().cancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDateSet(null, yearPicker.getValue(), 0, 0);
                YearPicker.this.getDialog().cancel();
            }
        });

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);

        builder.setView(dialog);

        return builder.create();
    }
}