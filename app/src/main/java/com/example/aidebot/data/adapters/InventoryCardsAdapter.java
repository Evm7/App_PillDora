package com.example.aidebot.data.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.aidebot.R;

import java.util.HashMap;

import androidx.cardview.widget.CardView;

public class InventoryCardsAdapter extends CardView {

    private Context mContext;
    private LayoutInflater layoutInfl;

    private TextView productNameTextView;
    private TextView expdateTextView;
    private TextView quantityTextView;
    private Button informationbutton;

    private View view;
    private String CN;


    public InventoryCardsAdapter(View view, LayoutInflater layoutInflater, String CN) {
        super(view.getContext());
        this.mContext = view.getContext();
        this.layoutInfl = layoutInflater;
        this.view = view;
        this.CN = CN;

        // Find individual views that we want to modify in the card item layout
        productNameTextView = this.view.findViewById(R.id.product_name_card);
        expdateTextView = this.view.findViewById(R.id.exp_date_card);
        quantityTextView = this.view.findViewById(R.id.product_quantity_card);
        informationbutton = this.view.findViewById(R.id.info_button_card);


        informationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformation(view);
            }
        });
    }

    public void setData(HashMap<String, String> map) {
        productNameTextView.setText(map.get("Name"));
        expdateTextView.setText(map.get("Exp_date"));
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
                        // Read from isbn input field
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

    public View getView() {
        return this.view;
    }

    private HashMap<String, String> getInformation() {
        //will commute with PresenterCIMA
        HashMap<String, String> info = new HashMap<>();
        info.put("Name", (String) productNameTextView.getText());
        info.put("Quantity", "121");
        info.put("URL", "https://cima.aemps.es/cima/publico/detalle.html?nregistro=" + this.CN);
        return info;
    }

}
