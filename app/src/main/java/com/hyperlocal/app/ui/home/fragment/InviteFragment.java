package com.hyperlocal.app.ui.home.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.hyperlocal.app.R;
import com.hyperlocal.app.model.AbuseWordsData;
import com.hyperlocal.app.utlity.FunctionUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author ${Umesh} on 05-04-2018.
 */

public class InviteFragment extends Fragment  {
    //@BindView(R.id.linear_layout) LinearLayout linearLayout;

    @BindView(R.id.btn_next) Button btnNext;
    @BindView(R.id.calendarView) MaterialCalendarView calendarView;
    @BindView(R.id.bottom_sheet) LinearLayout layoutBottomSheet;
    @BindView(R.id.numberPicker1) NumberPicker numberPicker1;
    @BindView(R.id.numberPicker2) NumberPicker numberPicker2;
    @BindView(R.id.numberPicker3) NumberPicker numberPicker3;
    @BindView(R.id.text_label)TextView labelTextView;
    @BindView(R.id.linear_container)FrameLayout liFrameLayout;
    @BindView(R.id.edit_invite_neighbors)EditText editText;
    @BindView(R.id.main_layout)CoordinatorLayout coordinatorLayout;
    @BindView(R.id.text_label_2)TextView labelTextView2;

    private String arr[] = {"AM", "PM"};
    private String minutes[] = {"0","01", "02", "03", "04", "05", "06", "07", "08", "09", "10","11","12","13",
            "14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30"
            ,"31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49",
           "50", "52","52","53","54","55","56","57","58","59","60"
    };
    private String hours[] = {"0","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private String selectedDate;
    private BottomSheetBehavior sheetBehavior;

    String timeInHours;
    String timeInMinutes;
    String timeUnit;
    private long timeOut;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invite_fragment, container, false);
        ButterKnife.bind(this, view);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        calendarView
                .state()
                .edit()
                .setMinimumDate(Calendar.getInstance().getTime())
                .commit();
        setDataSelectionListener();
        setBottomSheetLayout();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initializeNumberPicker() {
        selectedDate = getCurrentDate();
        labelTextView.setText(getCurrentDate());
        numberPicker1.setMaxValue(hours.length - 1);
        numberPicker1.setMinValue(0);
        numberPicker1.setWrapSelectorWheel(false);
        numberPicker1.setDisplayedValues(hours);
        numberPicker1.getDisplayedValues();
        numberPicker1.setOnScrollListener((numberPicker, i) -> {
            numberPicker.playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
        });
        numberPicker1.setOnValueChangedListener((numberPicker, i, i1) -> {
            if(hours[i].equalsIgnoreCase("11")){
                    numberPicker.setMinValue(0);
                    numberPicker.setMaxValue(hours.length-1);
            }
            String hours1 = hours[numberPicker1.getValue()];
            String min = minutes[numberPicker2.getValue()];
            String time = arr[numberPicker3.getValue()];
            String s = hours1+":"+min+": "+time;
            labelTextView.setText(selectedDate);
            labelTextView2.setText(s);
        });

        numberPicker2.setMaxValue(minutes.length - 1);
        numberPicker2.setMinValue(0);
        numberPicker2.setWrapSelectorWheel(false);
        numberPicker2.setDisplayedValues(minutes);
        numberPicker2.getDisplayedValues();
        numberPicker2.setOnScrollListener((numberPicker, i) -> {
        });

        numberPicker2.setOnValueChangedListener((numberPicker, i, i1) -> {
            if(minutes[i].equalsIgnoreCase("59")){
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(minutes.length-1);
            }

            String hours1 = hours[numberPicker1.getValue()];
            String min = minutes[numberPicker2.getValue()];
            String time = arr[numberPicker3.getValue()];
            String s = hours1+":"+min+": "+time;

            labelTextView.setText(selectedDate);
            labelTextView2.setText(s);

        });

        numberPicker3.setMaxValue(arr.length - 1);
        numberPicker3.setMinValue(0);
        numberPicker3.setWrapSelectorWheel(false);
        numberPicker3.setDisplayedValues(arr);
        numberPicker3.getDisplayedValues();
        numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String hours1 = hours[numberPicker1.getValue()];
                String min = minutes[numberPicker2.getValue()];
                String time = arr[numberPicker3.getValue()];
                String s = hours1+":"+min+": "+time;
                labelTextView.setText(selectedDate);
                labelTextView2.setText(s);
            }
        });

        numberPicker3.setOnScrollListener((numberPicker, i) -> {
            numberPicker.playSoundEffect(SoundEffectConstants.NAVIGATION_LEFT);
            numberPicker.playSoundEffect(android.view.SoundEffectConstants.CLICK);
        });

        String hours12 = hours[numberPicker1.getValue()];
        String min1 = minutes[numberPicker2.getValue()];
        String time1 = arr[numberPicker3.getValue()];
        String s1 = hours12+":"+min1+": "+time1;
        labelTextView2.setText(s1);
    }

    private void setDataSelectionListener() {
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = getFormattedDate(date.getDate());
                labelTextView.setText(selectedDate);
            }
        });
    }


    private void setBottomSheetLayout() {
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        // btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        //btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }



    public String getFormattedDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    @OnClick(R.id.btn_next)
    public void onNextButtonClick() {
        FunctionUtil.hideKeyboardOnOutSideTouch(coordinatorLayout,getActivity());
        String message = editText.getText().toString().trim();
        if(TextUtils.isEmpty(message)){
            Toast.makeText(getActivity(), "Please enter the invitation message. ", Toast.LENGTH_SHORT).show();
        }else if (AbuseWordsData.isAnyAbuseInText(message)) {
            Toast.makeText(getActivity(), "There are some vulgar words in your text please remove them", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(getActivity(), "Please select data", Toast.LENGTH_SHORT).show();
        } else {
            goNext(message);
        }
    }


    private void goNext(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("selected_date", selectedDate);
        bundle.putString("hours",timeInHours);
        bundle.putString("minutes",timeInMinutes);
        bundle.putString("timeUnit",timeUnit);
        bundle.putString("message",message);
        bundle.putLong("TimeOut",timeOut);
        Fragment childFragment = new InviteFragmentNext();
        childFragment.setArguments(bundle);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.left_to_right, R.anim.right_to_left);
        transaction.replace(R.id.linear_container, childFragment,"invitenext").commit();
        editText.setText("");
    }



    @OnClick(R.id.btn_done)
    public void onDoneButtonClick() {
        String date = labelTextView.getText().toString();
        String time = labelTextView2.getText().toString();
        String dateAndTime = date+" "+time;
        dateFormat(dateAndTime);
        FunctionUtil.hideKeyboardOnOutSideTouch(coordinatorLayout,getActivity());
        if(BottomSheetBehavior.STATE_COLLAPSED==4){
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            timeInHours = hours[numberPicker1.getValue()];
            timeInMinutes= minutes[numberPicker2.getValue()];
            timeUnit = arr[numberPicker3.getValue()];
        }
    }

    private long dateFormat(String dateAndTime) {
        DateFormat format = new SimpleDateFormat("dd MMM yyyy hh:mm: a");
        Date date = null;
        try {
            date = format.parse(dateAndTime);
            long currentTime = System.currentTimeMillis();
            Date date2= new Date(currentTime);
            long difference = date.getTime() - date2.getTime();
            timeOut = System.currentTimeMillis()+difference;
            
            } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeOut;
    }


    @OnClick(R.id.btn_choose_date)
    public void selectDate() {
        FunctionUtil.hideKeyboardOnOutSideTouch(coordinatorLayout,getActivity());
        initializeNumberPicker();
        sheetBehavior.setPeekHeight(getScreenHeight());
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            sheetBehavior.setHideable(true);
            sheetBehavior.setSkipCollapsed(true);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }



    public int getScreenHeight() {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) dpHeight;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = (Fragment) getChildFragmentManager().findFragmentByTag("invitenext");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public String getCurrentDate(){
        long currentTime = System.currentTimeMillis();
        Date date=new Date(currentTime);
        String dateFormat = "dd MMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }
}
