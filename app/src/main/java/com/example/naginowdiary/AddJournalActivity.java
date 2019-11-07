package com.example.naginowdiary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.naginowdiary.model.JournalItem;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddJournalActivity extends AppCompatActivity {
    private EditText mEdtTitle, mEdtContent;
    private ImageButton mBtnDelete;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private JournalItem editItem;
    private int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_journal_activity);
        addComponents();
        addDefaultValues();
    }

    private void addComponents() {
        Toolbar mCustomToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mCustomToolbar);
        ImageButton mBtnBack = findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        mEdtTitle = findViewById(R.id.edt_title);
        mEdtContent = findViewById(R.id.edt_content);
        ImageButton mBtnOk = findViewById(R.id.btn_done);
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEdtTitle.getText().toString().equals("") || mEdtContent.getText().toString().equals("")) {
                    Toast.makeText(AddJournalActivity.this, "Insert information", Toast.LENGTH_SHORT).show();
                } else {
                    editItem.setTitle(mEdtTitle.getText().toString());
                    editItem.setContent(mEdtContent.getText().toString());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("journalItem", editItem);
                    Intent intent = new Intent();
                    intent.putExtra("requestConfirm", MainActivity.REQUEST_EDIT_JOURNAL);
                    intent.putExtra("bundle", bundle);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
        mBtnDelete = findViewById(R.id.btn_delete_diary);
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteJournalConfirm();
            }
        });
    }

    private void deleteJournalConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Journal Delete Confirm");
        builder.setMessage("Confirm to delete this journal?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (state == MainActivity.REQUEST_ADD_JOURNAL) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("requestConfirm", MainActivity.REQUEST_DELETE_JOURNAL);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    private void addDefaultValues() {
        state = MainActivity.REQUEST_ADD_JOURNAL;
        editItem = new JournalItem();
        Intent intent = getIntent();
        int requestCode = intent.getExtras().getInt("request");
        if (requestCode == MainActivity.REQUEST_EDIT_JOURNAL) {
            state = MainActivity.REQUEST_EDIT_JOURNAL;
            Bundle bundle = intent.getBundleExtra("package");
            JournalItem item = (JournalItem) bundle.getSerializable("journalItem");
            editItem.setId(item.getId());
            mEdtTitle.setText(item.getTitle());
            mEdtContent.setText(item.getContent());
            editItem.setDate(item.getDate());
            Log.d("hohoho", item.getDate().toString());
            editItem.setColor(item.getColor());
        } else {
            editItem.setDate(Calendar.getInstance().getTime());
            editItem.setColor(Color.parseColor("#bbdefb"));
        }
    }


    public void onClickDatePickerButton(View v) {
        final Calendar c = Calendar.getInstance();
        c.setTime(editItem.getDate());
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar itemCal = Calendar.getInstance();
                        itemCal.setTime(editItem.getDate());
                        itemCal.set(year, monthOfYear, dayOfMonth);
                        editItem.setDate(itemCal.getTime());
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void onClickTimePickerButton(View v) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        c.setTime(editItem.getDate());
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        Calendar itemCal = Calendar.getInstance();
                        itemCal.setTime(editItem.getDate());
                        itemCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        itemCal.set(Calendar.MINUTE, minute);
                        editItem.setDate(itemCal.getTime());
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void onClickColorPickerButton(View v) {
        final ColorPicker colorPicker = new ColorPicker(this);
        ArrayList<String> colorlist = new ArrayList<>();
        colorlist.add("#d9d9d9");
        colorlist.add("#ffcdd2");
        colorlist.add("#f8bbd0");
        colorlist.add("#e1bee7");
        colorlist.add("#bbdefb");
        colorlist.add("#d7ccc8");
        colorlist.add("#ffe0b2");
        colorlist.add("#fff9c4");
        colorlist.add("#c8e6c9");
        colorlist.add("#b2dfdb");
        colorPicker.setColors(colorlist);
        colorPicker.setDefaultColorButton(R.color.colorPrimary);
        colorPicker.setRoundColorButton(true)
                .setColumns(5)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        editItem.setColor(color);
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .show();
    }
}
