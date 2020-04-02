package com.example.aidebot;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class HistoryFragment extends Fragment {

    View mcontainer;
    private HorizontalCalendar horizontalCalendar;
    String date_choosen;
    int removing;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mcontainer = inflater.inflate(R.layout.fragment_history, container, false);
        removing = 0;
        chooseDay();
        return mcontainer;
    }

    private void chooseDay() {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 0);
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, -7);
        // Default Date set to Today.
        final Calendar defaultSelectedDate = Calendar.getInstance();
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{8, 8, 8, 8, 0, 0, 0, 0});
        shape.setColor(Color.RED);
        shape.setStroke(3, Color.MAGENTA);


        horizontalCalendar = new HorizontalCalendar.Builder(mcontainer, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)   // Number of Dates cells shown on screen (default to 5).
                .configure()    // starts configuration.
                .formatTopText("MMM")      // default to "MMM".
                .formatMiddleText("dd")    // default to "dd".
                .formatBottomText("EEE")    // default to "EEE".
                .showTopText(true)              // show or hide TopText (default to true).
                .showBottomText(true)           // show or hide BottomText (default to true).
                .textColor(Color.LTGRAY, Color.WHITE)    // default to (Color.LTGRAY, Color.WHITE).
                .selectedDateBackground(shape)      // set selected date cell background.
                .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
                .selectorColor(Color.parseColor("#c62828"))               // set selection indicator bar's color (default to colorAccent).
                .end()          // ends configuration.
                .defaultSelectedDate(defaultSelectedDate)    // Date to be selected at start (default to current day `Calendar.getInstance()`).
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                date_choosen = DateFormat.getDateInstance().format(date.getTime());
                Toast.makeText(mcontainer.getContext(), date_choosen + " is selected!", Toast.LENGTH_SHORT).show();
                getCalendar();
            }
        });
    }

    private void getCalendar() {
        //inititalize supose Reminders which we will get from DataBase
        HashMap<String, String> reminders = new HashMap<>();
        reminders.put("Ibuprofeno", "reminders=08:00:00,16:00:00,24:00:00;taken=true");
        reminders.put("Topamax", "reminders=08:00:00,12:00:00,16:00:00,20:00:00,24:00:00;taken=false");
        reminders.put("Enantyum", "reminders=24:00:00;taken=true");

        //display all reminders get from DataBase
        LinearLayout linearLayout = getView().findViewById(R.id.history_layout);

        //delete if already set the day
        if (removing != 0) {
            linearLayout.removeViews(0, removing);
            removing = 0;
        }


        for (String key : reminders.keySet()) {
            try {
                linearLayout.addView(setDataCalendar(key, reminders.get(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //SET REMOVING FOR CALENDAR:
        removing = reminders.size();
    }

    private View setDataCalendar(String name, String parameters) {
        //handles parameters. Will be changed after DB connection:
        String data = parameters.split(";")[0].split("=")[1];
        String taken = parameters.split(";")[1].split("=")[1];


        //Get XML of Reminder Calendar
        View parent = getLayoutInflater().inflate(R.layout.reminder_calendar, null);

        //Set Medicine Name to XML (View)
        TextView name_med = parent.findViewById(R.id.reminder_medicine);
        name_med.setText(name);

        //Get parent Layout of XML
        LinearLayout vertical = parent.findViewById(R.id.parent_layout);

        //to check possible need of overwhelming exceeds
        LinearLayout horitzontal[] = new LinearLayout[data.split(",").length / 4 + 1];

        //LinearLayout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(20, 20, 20, 30);

        int i = 0;
        int j = 0;
        int MAX_DISPLAY = 3; //items that fix in one screen of phone
        for (String item : data.split(",")) {
            if (i % MAX_DISPLAY == 0) {
                if (i != 0) {
                    vertical.addView(horitzontal[j - 1]);
                }
                horitzontal[j] = new LinearLayout(vertical.getContext());
                horitzontal[j].setLayoutParams(params);
                horitzontal[j].setBaselineAligned(false);
                horitzontal[j].setHorizontalGravity(Gravity.CENTER);
                horitzontal[j].setOrientation(LinearLayout.HORIZONTAL);
                j++;
            }
            RelativeLayout reminder;


            if (taken.equals("true")) {
                reminder = (RelativeLayout) getLayoutInflater().inflate(R.layout.taken_item, null);
            } else {
                reminder = (RelativeLayout) getLayoutInflater().inflate(R.layout.not_taken_item, null);
            }

            TextView text = reminder.findViewById(R.id.time_reminder);
            text.setText(item);
            horitzontal[j - 1].addView(reminder);
            i++;
        }
        //Handle "not multiple of MAX_DISPLAY item" cases
        vertical.addView(horitzontal[j - 1]);
        return parent;
    }
}
