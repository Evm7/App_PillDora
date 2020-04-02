package com.example.aidebot;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.example.aidebot.data.adapters.CalendarDayPicker;
import com.example.aidebot.data.adapters.CalendarRangePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

//in order to improve agility of user. Same screen with different displays.
public class CalendarFragment extends Fragment {

    public static Boolean mode;
    Calendar myCalendar;
    View mcontainer;

    String mDateStart;
    String mDateEnd;
    int removing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mcontainer = inflater.inflate(R.layout.fragment_calendar, container, false);
        Button btnOpenDatePicker = mcontainer.findViewById(R.id.btnOpenDatePicker);
        Switch simpleSwitch = (Switch) mcontainer.findViewById(R.id.simpleSwitch);
        final LinearLayout general_calendar_layout = (LinearLayout) mcontainer.findViewById(R.id.general_calendar_layout);
        final LinearLayout set_items_layout = (LinearLayout) mcontainer.findViewById(R.id.set_items_layout);
        final LinearLayout set_activty_layout = (LinearLayout) mcontainer.findViewById(R.id.set_activty_layout);
        //Switch starts enabled --> Calendar mode
        this.mode = true;

        //firs time does not remove (used to delete created views on swapping)
        removing = 0;
        LinearLayout day_calendar = (LinearLayout) inflater.inflate(R.layout.day_calendar, null);
        set_activty_layout.addView(day_calendar);
        //Get instance of Calendar
        myCalendar = Calendar.getInstance();

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView modeselector = general_calendar_layout.findViewById(R.id.calendar_mode);
                if (isChecked) {
                    // The toggle is enabled --> Calendar
                    mode = true;
                    modeselector.setText(getString(R.string.calendar));
                    LinearLayout range_calendar = (LinearLayout) set_activty_layout.findViewById(R.id.range_calendar_layout);
                    set_activty_layout.removeView(range_calendar);
                    LinearLayout day_calendar = (LinearLayout) getLayoutInflater().inflate(R.layout.day_calendar, null);
                    set_activty_layout.addView(day_calendar);

                    //in case of more searches without changing fragment, need to delete Reminders already searched.
                    // ---->OJOOO fer en tenir com mostrar journey  OOOJO<---- \\ Potser és així nose.
                    set_items_layout.removeViews(0, removing);
                    removing = 0;
                } else {
                    // The toggle is disabled --> Journey
                    mode = false;
                    modeselector.setText(getString(R.string.journey));
                    LinearLayout day_calendar = (LinearLayout) set_activty_layout.findViewById(R.id.day_calendar_layout);
                    set_activty_layout.removeView(day_calendar);
                    LinearLayout range_calendar = (LinearLayout) getLayoutInflater().inflate(R.layout.ranger_calendar, null);
                    set_activty_layout.addView(range_calendar);

                    //in case of more searches without changing fragment, need to delete Reminders already searched.
                    set_items_layout.removeViews(0, removing);
                    removing = 0;
                }
            }
        });
        btnOpenDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The toggle is enabled --> Calendar
                if (mode == true) {
                    //in case of more searches without changing fragment, need to delete Reminders already searched.
                    set_items_layout.removeViews(0, removing);
                    removing = 0;

                    //get Calendar day and displays it
                    String date = showDatePickerDialog();
                }
                // The toggle is disabled --> Journey
                else {
                    set_items_layout.removeViews(0, removing);
                    removing = 0;

                    //get Calendar range days
                    openDateRangePicker();
                }
            }
        });
        return mcontainer;
    }

    //Handlers Calendar uniq day selection
    private String showDatePickerDialog() {
        CalendarDayPicker newFragment = CalendarDayPicker.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + " / " + (month + 1) + " / " + year;
                TextView selection_textdate = getView().findViewById(R.id.selection_date);
                selection_textdate.setText(getString(R.string.reminders_of));
                TextView selection_date = getView().findViewById(R.id.day_of_reminders);
                selection_date.setText(selectedDate);

                //Method that gets all reminders for this specific day and displays it
                getCalendar(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        TextView selection_date = getView().findViewById(R.id.day_of_reminders);
        return ((String) selection_date.getText());
    }

    //Handlers Calendar range days selection
    private void openDateRangePicker() {
        CalendarRangePicker pickerFrag = new CalendarRangePicker();
        SublimeOptions options = new SublimeOptions();
        options.setCanPickDateRange(true);
        options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);

        Bundle bundle = new Bundle();
        bundle.putParcelable("SUBLIME_OPTIONS", options);
        pickerFrag.setArguments(bundle);

        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        pickerFrag.show(getActivity().getSupportFragmentManager(), "SUBLIME_PICKER");

        pickerFrag.setCallback(new CalendarRangePicker.Callback() {
            @Override
            public void onCancelled() {
                Toast.makeText(mcontainer.getContext(), getString(R.string.user_cancel), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDateTimeRecurrenceSet(final SelectedDate selectedDate, int hourOfDay, int minute,
                                                SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                                String recurrenceRule) {

                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                System.out.println(selectedDate.getStartDate().getTime());
                System.out.println(selectedDate.getEndDate().getTime());
                mDateStart = formatDate.format(selectedDate.getStartDate().getTime());
                mDateEnd = formatDate.format(selectedDate.getEndDate().getTime());

                // set date start ke textview
                TextView tvStartDate = getView().findViewById(R.id.tvStartDate);
                tvStartDate.setText(mDateStart);

                // set date end ke textview
                TextView tvEndDate = getView().findViewById(R.id.tvEndDate);
                tvEndDate.setText(mDateEnd);

                //method that sum up all the pills user needs to take for the journey
                getJourney();
            }
        });
    }

    private void getCalendar(String date) {
        //inititalize supose Reminders which we will get from DataBase
        HashMap<String, String> reminders = new HashMap<>();
        reminders.put("Ibuprofeno", "08:00:00,16:00:00,24:00:00");
        reminders.put("Topamax", "08:00:00,12:00:00,16:00:00,20:00:00,24:00:00");
        reminders.put("Enantyum", "24:00:00");
        reminders.put("Paracetamol", "08:00:00,12:00:00,16:00:00,20:00:00,24:00:00");


        //display all reminders get from DataBase
        LinearLayout set_items_layout = getView().findViewById(R.id.set_items_layout);
        for (String key : reminders.keySet()) {
            try {
                set_items_layout.addView(setDataCalendar(key, reminders.get(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //SET REMOVING FOR CALENDAR:
        removing = reminders.size();
    }

    private View setDataCalendar(String name, String data) {
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
            RelativeLayout reminder = (RelativeLayout) getLayoutInflater().inflate(R.layout.reminder_item, null);
            TextView text = reminder.findViewById(R.id.time_reminder);
            text.setText(item);
            horitzontal[j - 1].addView(reminder);
            i++;
        }
        //Handle "not multiple of MAX_DISPLAY item" cases
        vertical.addView(horitzontal[j - 1]);
        return parent;
    }

    private void getJourney() {
        //inititalize supose Reminders which we will get from DataBase
        HashMap<String, String> reminders = new HashMap<>();
        reminders.put("Ibuprofeno", "9");
        reminders.put("Topamax", "14");
        reminders.put("Enantyum", "21");


        //display all reminders get from DataBase
        LinearLayout set_items_layout = getView().findViewById(R.id.set_items_layout);
        for (String key : reminders.keySet()) {
            try {
                set_items_layout.addView(setDataJourney(key, reminders.get(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //SET REMOVING FOR CALENDAR:
        removing = reminders.size();
    }

    private View setDataJourney(String name, String quantity) {
        //Get XML of Reminder Calendar
        View parent = getLayoutInflater().inflate(R.layout.journey_items, null);
        TextView name_med = parent.findViewById(R.id.name_item_journey);
        name_med.setText(name);

        TextView quantity_med = parent.findViewById(R.id.quantity_item_journey);
        quantity_med.setText(quantity);

        return parent;
    }

}
