package com.michael.weekpickerdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.michael.weekpickerdemo.entity.DateUtil;
import com.michael.weekpickerdemo.entity.Week;
import com.michael.weekpickerdemo.entity.WeekHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeekPickerDialog extends AlertDialog implements DialogInterface.OnClickListener{

    private final OnWeekSelectListener onWeekSelectListener;
    private NumberPicker yearPicker;
    private NumberPicker weekPicker;
    private String[] yearDisplayValues;
    private String[] weekDisplayValues;
    private int selectYearIndex = 0;
    private int initSelectYear;
    private List<Week> selectWeeks = new ArrayList<>();
    private Calendar currentCalendar;
    private static final String TAG = "WeekPickerDialog";
    private int selectWeekIndex = 0;

    public interface OnWeekSelectListener {
        void onWeekSelect(Week week);
    }

    public WeekPickerDialog(Context context, Calendar calendar, OnWeekSelectListener listener) {
        this(context, 0,calendar, listener);
    }

    static int resolveDialogTheme(Context context, int resId) {
        if (resId == 0) {
            final TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.timePickerDialogTheme, outValue, true);
            return outValue.resourceId;
        } else {
            return resId;
        }
    }

    public WeekPickerDialog(Context context, int themeResId, Calendar calendar, OnWeekSelectListener listener ) {
        super(context, resolveDialogTheme(context, themeResId));
        yearDisplayValues = listToArray(initDisplayYears());
        this.currentCalendar = calendar;
        initSelectYear = WeekHelper.getSelectYear(currentCalendar);
        initData(initSelectYear);
        onWeekSelectListener = listener;

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.week_picker_dialog, null);
        yearPicker = (NumberPicker) view.findViewById(R.id.yearPicker);
        weekPicker = (NumberPicker) view.findViewById(R.id.weekPicker);
        initPickerView();
        setView(view);
        setButton(BUTTON_POSITIVE, themeContext.getString(R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.cancel), this);
        setButton(BUTTON_NEUTRAL, themeContext.getString(R.string.clear),this);
    }

    private void initPickerView(){
        yearPicker.setDisplayedValues(yearDisplayValues);
        yearPicker.setMinValue(0);
        yearPicker.setMaxValue(yearDisplayValues.length - 1);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        yearPicker.setValue(selectYearIndex);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectYearIndex = newVal;
                updateWearPickerValue();
            }
        });

        weekPicker.setDisplayedValues(weekDisplayValues);
        weekPicker.setMinValue(0);
        weekPicker.setMaxValue(weekDisplayValues.length - 1);
        weekPicker.setValue(selectWeekIndex);
        weekPicker.setWrapSelectorWheel(false);
        weekPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        weekPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            }
        });
    }

    // 设置现在的时间在哪个周区间
    private void setSelectWeekIndex(){
        for(int i = 0;i < selectWeeks.size(); i++){
            Week week = selectWeeks.get(i);
            if(DateUtil.isEffectiveDate(currentCalendar.getTime(), week.getWeekBeginDate(), week.getWeekEndDate())){
                selectWeekIndex = i;
                break;
            }
        }
    }

    // 设置选中的年下标
    private void setSelectYearIndex(){
        for(int i = 0;i < yearDisplayValues.length; i++){
            if(initSelectYear == Integer.valueOf(yearDisplayValues[i])){
                selectYearIndex = i;
            }
        }
    }

    private List<String> initDisplayYears(){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> yearstrList = new ArrayList<>();
        for(int i = 3;i > 0;i --){
            int now = currentYear - i;
            yearstrList.add(String.valueOf(now));
        }
        for(int i = 0;i < 3;i ++){
            int now = currentYear + i;
            yearstrList.add(String.valueOf(now));
        }
        return yearstrList;
    }

    private void updateWearPickerValue(){
        String[] content = yearPicker.getDisplayedValues();
        if (content != null) {
            String value = content[selectYearIndex - weekPicker.getMinValue()];
            Log.d(TAG,"[updateYearPickerValue] yearPicker选择的年份："+value);
            setSelectWeeksByYear(Integer.parseInt(value));
            String[] resultWeeks = listToArray(getWeeks());
            int selectWeekIndex = weekPicker.getValue();
            if(resultWeeks.length >= weekDisplayValues.length){
                try{
                    weekPicker.setDisplayedValues(resultWeeks);
                    weekPicker.setMinValue(0);
                    weekPicker.setMaxValue(resultWeeks.length - 1);
                    if(selectWeekIndex > resultWeeks.length -1){
                        selectWeekIndex = resultWeeks.length -1;
                    }
                    weekPicker.setValue(selectWeekIndex);
                    weekPicker.invalidate();
                }catch (Exception e){
                    e.printStackTrace();
                    weekPicker.setMinValue(0);
                    weekPicker.setMaxValue(resultWeeks.length - 1);
                    weekPicker.setDisplayedValues(resultWeeks);
                    if(selectWeekIndex > resultWeeks.length -1){
                        selectWeekIndex = resultWeeks.length -1;
                    }
                    weekPicker.setValue(selectWeekIndex);
                    weekPicker.invalidate();
                }
            }else {
               try{
                   weekPicker.setMinValue(0);
                   weekPicker.setMaxValue(resultWeeks.length - 1);
                   weekPicker.setDisplayedValues(resultWeeks);
                   if(selectWeekIndex > resultWeeks.length -1){
                       selectWeekIndex = resultWeeks.length -1;
                   }
                   weekPicker.setValue(selectWeekIndex);
                   weekPicker.invalidate();
               }catch (Exception e){
                   e.printStackTrace();
                   weekPicker.setDisplayedValues(resultWeeks);
                   weekPicker.setMinValue(0);
                   weekPicker.setMaxValue(resultWeeks.length - 1);
                   if(selectWeekIndex > resultWeeks.length -1){
                       selectWeekIndex = resultWeeks.length -1;
                   }
                   weekPicker.setValue(selectWeekIndex);
                   weekPicker.invalidate();
               }
            }

        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                Week week = selectWeeks.get(weekPicker.getValue());
                if (onWeekSelectListener != null) {
                    onWeekSelectListener.onWeekSelect(week);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
            case BUTTON_NEUTRAL:
                if (onWeekSelectListener != null) {
                    onWeekSelectListener.onWeekSelect(null);
                }
                break;
        }
    }

    private void initData(int currentYear){
        setSelectWeeksByYear(currentYear);
        weekDisplayValues = listToArray(getWeeks());
        setSelectWeekIndex();
        setSelectYearIndex();
    }

    private String[] listToArray(List<String> list){
        String[] strings = new String[list.size()];
        list.toArray(strings);
        return strings;
    }

    private void setSelectWeeksByYear(int currentYear){
        selectWeeks = WeekHelper.getWeeksByYear(currentYear);
    }

    private List<String> getWeeks(){
        List<String> weekStrList = new ArrayList<>();
        for(Week week:selectWeeks){
            weekStrList.add(week.toString());
        }
        return weekStrList;
    }
}