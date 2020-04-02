package com.example.aidebot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.aidebot.data.adapters.CalendarDayPicker;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NewPrescriptionFragment extends Fragment implements NumberPicker.OnValueChangeListener {

    View mcontainer;
    String CN, frequency, quantity, end_date;
    ArrayList<String> reminders;
    AlertDialog alertDialog;
    Calendar myCalendar;
    LinearLayout horitzontal[] = new LinearLayout[24]; // as a day has 24 hours

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mcontainer = inflater.inflate(R.layout.fragment_new_prescription, container, false);

        //Getting all inputs of the text
        final EditText nameMedicineToFill = mcontainer.findViewById(R.id.nameMedicineToFill);
        final EditText frequencyMedicineToFill = mcontainer.findViewById(R.id.frequencyMedicineToFill);
        final EditText quantityMedicineToFill = mcontainer.findViewById(R.id.quantityMedicineToFill);
        final EditText end_dateMedicineToFill = mcontainer.findViewById(R.id.end_dateMedicineToFill);

        //Getting all buttons to set on Click()
        Button camera_new_prescription_btn = mcontainer.findViewById(R.id.camera_new_prescription_btn);
        Button hour_new_prescription_btn = mcontainer.findViewById(R.id.hour_new_prescription_btn);
        Button quantity_new_prescription_btn = mcontainer.findViewById(R.id.quantity_new_prescription_btn);
        Button calendar_new_prescription_btn = mcontainer.findViewById(R.id.calendar_new_prescription_btn);
        Button intr_prescription_button = mcontainer.findViewById(R.id.intr_prescription_button);
        Button chronic_new_prescription_btn = mcontainer.findViewById(R.id.chronic_new_prescription_btn);


        //initialize calendar for possible end_date use:
        myCalendar = Calendar.getInstance();

        //initialize reminders to be set
        reminders = new ArrayList<>();

        //Assosciate functions to buttons

        //Introduction through Photo
        camera_new_prescription_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(mcontainer.getContext());
            }
        });

        //GetFrequency or get real time for reminders:
        hour_new_prescription_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preciseExactTimeforReminders();
            }
        });

        //Get quantity of Pills:
        quantity_new_prescription_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPicker();
            }
        });

        //Get End Date of the treatment: get Calendar day and displays it
        calendar_new_prescription_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        chronic_new_prescription_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end_date = "CHRONIC";
                end_dateMedicineToFill.setText("CHRONIC");
            }
        });


        //Get all Values function  handlered thourh button press
        intr_prescription_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get all inputs from the EditText
                CN = nameMedicineToFill.getText().toString();
                frequency = frequencyMedicineToFill.getText().toString();
                quantity = quantityMedicineToFill.getText().toString();
                end_date = end_dateMedicineToFill.getText().toString();

                //Check if all inputs are correctly introduced:
                //Verify if CN is possible (due to CheckSum Verification)

                AlertDialog.Builder builder = new AlertDialog.Builder(mcontainer.getContext());

                builder.setView(getLayoutInflater().inflate(R.layout.confirm_new_prescription, null));

                alertDialog = builder.create();
                // To prevent a dialog from closing when the positive button clicked, set onShowListener to
                // the AlertDialog
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialogInterface) {
                        TextView name_drug_text = alertDialog.findViewById(R.id.Name_drug_delete);
                        name_drug_text.setText(CN);

                        TextView quantity_info_text = alertDialog.findViewById(R.id.Quantity_delete);
                        quantity_info_text.setText(quantity);

                        TextView end_date_text = alertDialog.findViewById(R.id.end_date_delete);
                        end_date_text.setText(end_date);

                        TextView frequency_text = alertDialog.findViewById(R.id.frequency_delete);
                        frequency_text.setText(frequency);

                        final Button buttonDelete = alertDialog.findViewById(R.id.YesDelete);
                        Button buttonNotDelete = alertDialog.findViewById(R.id.NotDelete);

                        buttonDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.cancel();
                            }
                        });
                        buttonNotDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.cancel();

                                //IMPORTANT TO CONTACT WITH THE DATABASE THROUGH PRESENTER AND INJECT ALL THIS INFORMATION
                                presentInformation();
                            }

                        });
                    }
                });

                alertDialog.show();


            }
        });
        return mcontainer;

    }

    private void presentInformation() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("CN", CN);
        parameters.put("end_date", end_date);
        parameters.put("frequency", frequency);
        parameters.put("quantity", quantity);
    }

    private void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(getLayoutInflater().inflate(R.layout.picture_dialog, null));

        alertDialog = builder.create();
        // To prevent a dialog from closing when the positive button clicked, set onShowListener to
        // the AlertDialog
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button take_pic = alertDialog.findViewById(R.id.take_pic);
                Button choose_from_gallery = alertDialog.findViewById(R.id.choose_from_gallery);
                final Button close_dialog = alertDialog.findViewById(R.id.close_dialog);


                close_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
                take_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
                    }

                });

                choose_from_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);
                    }

                });
            }
        });

        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        Bitmap imageView = selectedImage;
                        getCNfromImage(imageView);
                        alertDialog.cancel();
                    }

                    break;
                case 1:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Uri selectedImageUri = data.getData();
                            InputStream imageStream = null;
                            try {
                                imageStream = mcontainer.getContext().getContentResolver().openInputStream(selectedImageUri);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap imageView = BitmapFactory.decodeStream(imageStream);
                            getCNfromImage(imageView);
                            alertDialog.cancel();
                        }

                    }
                    break;
            }
        }
    }


    private void getCNfromImage(Bitmap image) {
        //Image selected may well contain CN
        //Use API created for detectionOfCN

        //If CN is found, save it in variable CN and put this CN as String in nameMedicineToFill with real Name get from CIMA.
        Boolean CNfound = true;
        //example1: CN=965012 --> Frenadol
        if (CNfound) {
            CN = "965012";
            //getNameFromCIma method
            String name = "Frenadol";
            EditText nameToFill = mcontainer.findViewById(R.id.nameMedicineToFill);
            nameToFill.setText(CN);
            TextView nameMed = mcontainer.findViewById(R.id.nameMed);
            nameMed.setText("Name of the medicine (CN) --> " + name);
        }
        //If CN not found, return error and go to normal menu as nothing has happended, displaying toast error message.
        else {
            Toast.makeText(mcontainer.getContext(), "Error: CN could not be retrieved correctly.\nTry again",
                    Toast.LENGTH_LONG).show();
        }
    }

    //Handlers time Picking
    //FIRST OPTION JUST CHECKS FREQUENCY --> in order to use this one, choose XML fragment_new_prescription_just_frequency_.xml
    private void chooseFrequency() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(mcontainer.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                frequency = (selectedHour + ":" + selectedMinute);
                EditText frequencyMedicineToFill = mcontainer.findViewById(R.id.frequencyMedicineToFill);
                frequencyMedicineToFill.setText(frequency);

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    //SECOND OPTION STABLISHED EXACT TIME OF THE REMINDERS --> in order to use this one, choose XML fragment_new_prescription.xml
    private void preciseExactTimeforReminders() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mcontainer.getContext());
        builder.setView(getLayoutInflater().inflate(R.layout.new_reminders, null));

        alertDialog = builder.create();
        // To prevent a dialog from closing when the positive button clicked, set onShowListener to
        // the AlertDialog
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                Button close_myself = alertDialog.findViewById(R.id.close_myself);
                close_myself.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                        String text = reminders.toString().replace("[", "").replace("]", "").replace("None, ", "");
                        EditText frequencyMedicineToFill = mcontainer.findViewById(R.id.frequencyMedicineToFill);
                        frequencyMedicineToFill.setText(text);

                    }
                });

                Button add_reminder = alertDialog.findViewById(R.id.add_reminder);
                add_reminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getTime();
                    }

                });
            }
        });

        alertDialog.show();
    }

    private void add_reminder(String text) {
        //Maximum number of Items that can fix in one screen of phone
        final int MAX_DISPLAY = 2;

        //EStablish time reminder:
        final String time_reminder = text;
        //Get Hour Item to display time chosen
        final View hour_item = getLayoutInflater().inflate(R.layout.hour_item, null);

        //Establish time chosen in the Hour Item
        TextView hour_text = hour_item.findViewById(R.id.time_reminder);
        hour_text.setText(time_reminder);


        //LinearLayout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(5, 5, 5, 5);

        //Get Vertical LinearLayout to adjust capability of hour items while adding them
        final LinearLayout vertical_reminders = alertDialog.findViewById(R.id.vertical_reminders);

        //Handles capability of hour items
        if (reminders.size() % MAX_DISPLAY == 0) {
            horitzontal[reminders.size() / MAX_DISPLAY] = new LinearLayout(vertical_reminders.getContext());
            horitzontal[reminders.size() / MAX_DISPLAY].setLayoutParams(params);
            horitzontal[reminders.size() / MAX_DISPLAY].setBaselineAligned(false);
            horitzontal[reminders.size() / MAX_DISPLAY].setHorizontalGravity(Gravity.CENTER);
            horitzontal[reminders.size() / MAX_DISPLAY].setOrientation(LinearLayout.HORIZONTAL);
        }
        horitzontal[reminders.size() / MAX_DISPLAY].addView(hour_item);

        //Handle "not multiple of MAX_DISPLAY item" cases
        if (reminders.size() % MAX_DISPLAY == 0) {
            vertical_reminders.addView(horitzontal[(reminders.size() / MAX_DISPLAY)]);
        }

        //Attempt to adjust clicking on delete item.
        Button delete_myself = hour_item.findViewById(R.id.delete_myself);
        delete_myself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = reminders.lastIndexOf(time_reminder);
                horitzontal[pos / MAX_DISPLAY].removeView(hour_item);
                if (pos > 0) {
                    reminders.set(pos, "None");
                }
            }
        });

        //Introduce hour time in Array of Strings "reminder"
        reminders.add(time_reminder);
    }

    private void getTime() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(mcontainer.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String time_reminder = String.format("%02d:%02d", selectedHour, selectedMinute);
                add_reminder(time_reminder);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    //Handlers Number Picker:
    private void showNumberPicker() {
        final Dialog d = new Dialog(mcontainer.getContext());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.number_selector);
        Button setNumber = (Button) d.findViewById(R.id.setNumber);
        Button closeDialog = (Button) d.findViewById(R.id.closeDialog);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMinValue(0);
        np.setMaxValue(100);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        setNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = String.valueOf(np.getValue());
                EditText quantityMedicineToFill = mcontainer.findViewById(R.id.quantityMedicineToFill);
                quantityMedicineToFill.setText(quantity);
                d.dismiss();
            }
        });
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                Toast.makeText(mcontainer.getContext(), "No quantity choosen.",
                        Toast.LENGTH_LONG).show();
            }
        });
        d.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
    }

    //Handlers Calendar uniq day selection
    private void showDatePickerDialog() {
        CalendarDayPicker newFragment = CalendarDayPicker.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                end_date = day + " / " + (month + 1) + " / " + year;
                EditText end_dateMedicineToFill = mcontainer.findViewById(R.id.end_dateMedicineToFill);
                end_dateMedicineToFill.setText(end_date);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}
