package com.example.aidebot.data.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aidebot.R;

import java.util.HashMap;

import androidx.cardview.widget.CardView;

public class TreatmentCardsAdapter extends CardView {

    private Context mContext;
    private LayoutInflater layoutInfl;

    private TextView productNameTextView;
    private TextView endDateTextView;
    private TextView quantityTextView;
    private TextView frequencyTextView;
    private Button informationbutton;
    private Button deletebutton;

    private View view;
    private String CN;


    public TreatmentCardsAdapter(View view, LayoutInflater layoutInflater, String CN) {
        super(view.getContext());
        this.mContext = view.getContext();
        this.layoutInfl = layoutInflater;
        this.view = view;
        this.CN = CN;

        // Find individual views that we want to modify in the card item layout
        productNameTextView = this.view.findViewById(R.id.current_product_name_card);
        endDateTextView = this.view.findViewById(R.id.current_end_date_card);
        quantityTextView = this.view.findViewById(R.id.current_product_quantity_card);
        frequencyTextView = this.view.findViewById(R.id.current_frequency_card);
        informationbutton = this.view.findViewById(R.id.current_info_button_card);
        deletebutton = this.view.findViewById(R.id.current_delete_button_card);

        informationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformation(view);
            }
        });

        deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMed(view);
            }
        });
    }

    public void setData(HashMap<String, String> map) {
        productNameTextView.setText(map.get("Name"));
        endDateTextView.setText(map.get("End_date"));
        frequencyTextView.setText(map.get("Frequency"));
        quantityTextView.setText(map.get("Quantity"));
    }

    public void showInformation(View v) {

        //getInformationfromCIMA
        final HashMap<String, String> info = getInformation();

        // Create an AlertDialog.Builder and set the message.
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        // Get the layout inflater
        LayoutInflater inflater = this.layoutInfl;
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_information, null));

        // Create the AlertDialog
        final AlertDialog alertDialog = builder.create();
        // To prevent a dialog from closing when the positive button clicked, set onShowListener to
        // the AlertDialog
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {


                TextView name_drug = alertDialog.findViewById(R.id.Name_drug);
                name_drug.setText(info.get("Name"));

                TextView quantity_info = alertDialog.findViewById(R.id.Quantity_info);
                quantity_info.setText(info.get("Quantity"));

                final Button buttonclose = alertDialog.findViewById(R.id.CloseDialog);
                Button buttonURL = alertDialog.findViewById(R.id.URLprospect);

                buttonclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
                buttonURL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(info.get("URL")); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }

                });
            }
        });

        // Show the AlertDialog
        alertDialog.show();
    }

    public void deleteMed(View v) {
        final View viewToDelete = v;
        //getInformationfromCIMA
        final HashMap<String, String> info = getCurrentMed();

        // Create an AlertDialog.Builder and set the message.
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        // Get the layout inflater
        LayoutInflater inflater = this.layoutInfl;
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_deletedrug, null));

        // Create the AlertDialog
        final AlertDialog alertDialog = builder.create();
        // To prevent a dialog from closing when the positive button clicked, set onShowListener to
        // the AlertDialog
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {

                TextView name_drug = alertDialog.findViewById(R.id.Name_drug_delete);
                name_drug.setText(info.get("Name"));

                TextView quantity_info = alertDialog.findViewById(R.id.Quantity_delete);
                quantity_info.setText(info.get("Quantity"));

                TextView end_date = alertDialog.findViewById(R.id.end_date_delete);
                end_date.setText(info.get("End_date"));

                TextView frequency = alertDialog.findViewById(R.id.frequency_delete);
                frequency.setText(info.get("Frequency"));

                final Button buttonDelete = alertDialog.findViewById(R.id.YesDelete);
                Button buttonNotDelete = alertDialog.findViewById(R.id.NotDelete);

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                        deleteCurrentView(viewToDelete);
                    }
                });
                buttonNotDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }

                });
            }
        });

        // Show the AlertDialog
        alertDialog.show();
    }

    public void deleteCurrentView(View v) {
        LinearLayout linearParent = (LinearLayout) v.getParent().getParent().getParent().getParent().getParent();
        RelativeLayout linearChild = (RelativeLayout) v.getParent().getParent().getParent().getParent();
        linearParent.removeView(linearChild);

        //IMPORTANT NEED TO CONTACT WITH DATABASE
    }

    public View getView() {
        return this.view;
    }

    private HashMap<String, String> getInformation() {
        //will commute with PresenterCIMA
        HashMap<String, String> info = new HashMap<>();
        info.put("Name", (String) productNameTextView.getText());
        info.put("Quantity", "121"); //remember that this quantity is the one which is included in new box --> From CIMA
        info.put("URL", "https://cima.aemps.es/cima/publico/detalle.html?nregistro=" + this.CN);
        return info;
    }

    private HashMap<String, String> getCurrentMed() {

        HashMap<String, String> info = new HashMap<>();
        info.put("Name", (String) productNameTextView.getText());
        info.put("Quantity", (String) quantityTextView.getText());
        info.put("Frequency", (String) frequencyTextView.getText());
        info.put("End_date", (String) endDateTextView.getText());
        return info;
    }


}
