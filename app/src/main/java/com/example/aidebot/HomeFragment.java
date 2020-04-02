package com.example.aidebot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//in order to improve agility of user. Same screen with different displays.
public class HomeFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mcontainer = inflater.inflate(R.layout.fragment_home, container, false);

        // Next reminders
        TextView reminder1 = mcontainer.findViewById(R.id.reminder1);
        TextView reminder2 = mcontainer.findViewById(R.id.reminder2);
        TextView reminder3 = mcontainer.findViewById(R.id.reminder3);
        TextView time1 = mcontainer.findViewById(R.id.time1);
        TextView time2 = mcontainer.findViewById(R.id.time2);
        TextView time3 = mcontainer.findViewById(R.id.time3);

        // Current treatments
        TextView treatment1 = mcontainer.findViewById(R.id.treatment1);
        TextView treatment2 = mcontainer.findViewById(R.id.treatment2);
        TextView treatment3 = mcontainer.findViewById(R.id.treatment3);
        TextView info1 = mcontainer.findViewById(R.id.info1);
        TextView info2 = mcontainer.findViewById(R.id.info2);
        TextView info3 = mcontainer.findViewById(R.id.info3);

        CardView card_reminders = mcontainer.findViewById(R.id.card_reminders);
        card_reminders.setOnClickListener(this);
        CardView card_treatments = mcontainer.findViewById(R.id.card_treatments);
        card_treatments.setOnClickListener(this);

        ArrayList nextReminders = get3NextReminders();
        reminder1.setText((String)nextReminders.get(0));
        time1.setText((String)nextReminders.get(1));
        reminder2.setText((String)nextReminders.get(2));
        time2.setText((String)nextReminders.get(3));
        reminder3.setText((String)nextReminders.get(4));
        time3.setText((String)nextReminders.get(5));

        ArrayList<HashMap<String, String>> treatments = getInventory();
        treatment1.setText(treatments.get(0).get("Name"));
        info1.setText(treatments.get(0).get("Quantity") + " every " + treatments.get(0).get("Frequency"));
        treatment2.setText(treatments.get(1).get("Name"));
        info2.setText(treatments.get(1).get("Quantity") + " every " + treatments.get(1).get("Frequency"));
        treatment3.setText(treatments.get(2).get("Name"));
        info3.setText(treatments.get(2).get("Quantity") + " every " + treatments.get(2).get("Frequency"));

        return mcontainer;
    }

    @Override
    public void onClick(View v) {
        // default method for handling onClick Events..
        final NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

        switch (v.getId()) {

            case R.id.card_reminders:
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Calendar \uD83D\uDCC5");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CalendarFragment()).commit();
                navigationView.setCheckedItem(R.id.calendar);

                break;


            case R.id.card_treatments:
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Treatments \uD83C\uDFE5");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new TreatmentsFragment()).commit();
                navigationView.setCheckedItem(R.id.treatments);
                break;


            default:
                break;
        }
    }

    private ArrayList getReminders(){
        ArrayList<String> reminders = new ArrayList<>();
        reminders.add("Ibuprofeno");
        reminders.add("08:00:00");
        reminders.add("Ibuprofeno");
        reminders.add("16:00:00");
        reminders.add("Ibuprofeno");
        reminders.add("24:00:00");
        reminders.add("Topamax");
        reminders.add("08:00:00");
        reminders.add("Topamax");
        reminders.add("12:00:00");
        reminders.add("Topamax");
        reminders.add("16:00:00");
        reminders.add("Topamax");
        reminders.add("20:00:00");
        reminders.add("Topamax");
        reminders.add("24:00:00");
        reminders.add("Enantyum");
        reminders.add("24:00:00");
        reminders.add("Lionel");
        reminders.add("08:00:00");
        return reminders;
    }

    private ArrayList get3NextReminders(){
        Date currentTime = Calendar.getInstance().getTime(); //agafa la hora actual
        //fer funció que agafa els 3 proxims reminders a partir de la hora que és
        ArrayList<String> nextreminders = new ArrayList<>();
        nextreminders.add("Ibuprofeno");
        nextreminders.add("08:00:00");
        nextreminders.add("Topamax");
        nextreminders.add("08:00:00");
        nextreminders.add("Lionel");
        nextreminders.add("08:00:00");
        return nextreminders;
    }

    private ArrayList<HashMap<String, String>> getInventory() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> items = new HashMap<>();
        items.put("Name", "Ibuprofeno");
        items.put("Quantity", "1");
        items.put("Frequency", "8h");
        items.put("End_date", "2020-10-14");
        items.put("CN", "798116.9");
        list.add(items);
        HashMap<String, String> items2 = new HashMap<>();
        items2.put("Name", "Frenadol");
        items2.put("Quantity", "2");
        items2.put("Frequency", "2h");
        items2.put("End_date", "2021-12-15");
        items2.put("CN", "965012.4");
        list.add(items2);
        HashMap<String, String> items3 = new HashMap<>();
        items3.put("Name", "Topamax");
        items3.put("Quantity", "1");
        items3.put("Frequency", "4h");
        items3.put("End_date", "2024-04-02");
        items3.put("CN", "664029.6");
        list.add(items3);
        return list;
    }

}

