package com.example.aidebot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.aidebot.data.adapters.InventoryCardsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InventoryFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mContainer = inflater.inflate(R.layout.fragment_inventory, container, false);
        return putInventory(mContainer);
    }


    private View putInventory(View view) {
        LinearLayout linearLayout = view.findViewById(R.id.Layout_inventory);
        ArrayList<HashMap<String, String>> list = getInventory();
        Iterator<HashMap<String, String>> iterator = list.iterator();
        View card[] = new View[list.size()];
        int i = 0;
        while (iterator.hasNext()) {
            HashMap<String, String> next = iterator.next();
            String CN = next.get("CN");
            card[i] = getLayoutInflater().inflate(R.layout.drug_item, null);
            card[i].setId(i);
            InventoryCardsAdapter pr = new InventoryCardsAdapter(card[i], getLayoutInflater(), CN);
            pr.setData(next);
            try {
                linearLayout.addView(pr.getView());
            } catch (Exception e) {
                e.printStackTrace();
            }
            i++;
        }

        return view;
    }

    private ArrayList<HashMap<String, String>> getInventory() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> items = new HashMap<>();
        items.put("Name", "Ibuprofeno");
        items.put("Quantity", "50");
        items.put("Exp_date", "2020-10-14");
        items.put("CN", "798116.9");
        list.add(items);
        HashMap<String, String> items2 = new HashMap<>();
        items2.put("Name", "Frenadol");
        items2.put("Quantity", "120");
        items2.put("Exp_date", "2021-12-15");
        items2.put("CN", "965012.4");
        list.add(items2);
        HashMap<String, String> items3 = new HashMap<>();
        items3.put("Name", "Topamax");
        items3.put("Quantity", "30");
        items3.put("Exp_date", "2024-04-02");
        items3.put("CN", "664029.6");
        list.add(items3);
        HashMap<String, String> items4 = new HashMap<>();
        items4.put("Name", "Paracetamol");
        items4.put("Quantity", "50");
        items4.put("Exp_date", "2020-10-14");
        items4.put("CN", "798116.9");
        list.add(items4);
        HashMap<String, String> items7 = new HashMap<>();
        items7.put("Name", "Ibuprofeno");
        items7.put("Quantity", "50");
        items7.put("Exp_date", "2020-10-14");
        items7.put("CN", "798116.9");
        list.add(items7);
        HashMap<String, String> items8 = new HashMap<>();
        items8.put("Name", "Frenadol");
        items8.put("Quantity", "120");
        items8.put("Exp_date", "2021-12-15");
        items8.put("CN", "965012.4");
        list.add(items8);
        HashMap<String, String> items9 = new HashMap<>();
        items9.put("Name", "Topamax");
        items9.put("Quantity", "30");
        items9.put("Exp_date", "2024-04-02");
        items9.put("CN", "664029.6");
        list.add(items9);

        return list;
    }

}
