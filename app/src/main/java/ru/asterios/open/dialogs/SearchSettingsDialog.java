package ru.asterios.open.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import ru.asterios.open.R;
import ru.asterios.open.constants.Constants;


public class SearchSettingsDialog extends DialogFragment implements Constants {

    CheckBox genderMaleCheckBox, genderFemaleCheckBox, onlineCheckBox, photoCheckBox;

    private int searchGender, searchOnline, searchPhoto;

    /** Declaring the interface, to invoke a callback function in the implementing activity class */
    AlertPositiveListener alertPositiveListener;

    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    public interface AlertPositiveListener {

        public void onCloseSettingsDialog(int searchGender, int searchOnline, int searchPhoto);
    }

    /** This is a callback method executed when this fragment is attached to an activity.
     *  This function ensures that, the hosting activity implements the interface AlertPositiveListener
     * */
    public void onAttach(android.app.Activity activity) {

        super.onAttach(activity);

        try {

            alertPositiveListener = (AlertPositiveListener) activity;

        } catch(ClassCastException e){

            // The hosting activity does not implemented the interface AlertPositiveListener
            throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener");
        }
    }

    /** This is the OK button listener for the alert dialog,
     *  which in turn invokes the method onPositiveClick(position)
     *  of the hosting activity which is supposed to implement it
     */
    OnClickListener positiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            alertPositiveListener.onCloseSettingsDialog(searchGender, searchOnline, searchPhoto);
        }
    };

    /** This is the OK button listener for the alert dialog,
     *  which in turn invokes the method onPositiveClick(position)
     *  of the hosting activity which is supposed to implement it
     */
    OnClickListener negativeListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

//            alertPositiveListener.onCloseStreamTutorial(position, itemPosition);
        }
    };

    /** This is a callback method which will be executed
     *  on creating this fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /** Getting the arguments passed to this fragment */
        Bundle bundle = getArguments();

        searchGender = bundle.getInt("searchGender");
        searchOnline = bundle.getInt("searchOnline");
        searchPhoto = bundle.getInt("searchPhoto");

        /** Creating a builder for the alert dialog window */
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        /** Setting a title for the window */
        b.setTitle(getText(R.string.label_search_settings_dialog_title));

        LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_search_settings, null);

        b.setView(view);

        genderMaleCheckBox = (CheckBox) view.findViewById(R.id.genderMaleCheckBox);
        genderFemaleCheckBox = (CheckBox) view.findViewById(R.id.genderFemaleCheckBox);
        onlineCheckBox = (CheckBox) view.findViewById(R.id.onlineCheckBox);
        photoCheckBox = (CheckBox) view.findViewById(R.id.photoCheckBox);

        setGender(searchGender);
        setOnline(searchOnline);
        setPhoto(searchPhoto);


        /** Setting a positive button and its listener */

        b.setPositiveButton(getText(R.string.action_ok), positiveListener);

        b.setNegativeButton(getText(R.string.action_cancel), negativeListener);


        b.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    return true;
                }

                return true;
            }
        });

        /** Creating the alert dialog window using the builder class */
        final AlertDialog d = b.create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                final DialogInterface dlg = dialog;

                final Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        d.dismiss();
                        alertPositiveListener.onCloseSettingsDialog(getGender(), getOnline(), getPhoto());
                    }
                });

                Button p = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                p.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something

                        d.dismiss();
                    }
                });
            }
        });

        d.setCanceledOnTouchOutside(false);
        d.setCancelable(false);

        /** Return the alert dialog window */
        return d;
    }

    public int getGender() {

        if (genderFemaleCheckBox.isChecked() && genderMaleCheckBox.isChecked()) {

            return -1;
        }

        if (genderMaleCheckBox.isChecked()) {

            return 1;
        }

        if (genderFemaleCheckBox.isChecked()) {

            return 2;
        }

        return -1;
    }

    public void setGender(int gender) {

        switch (gender) {

            case 1: {

                genderMaleCheckBox.setChecked(true);
                genderFemaleCheckBox.setChecked(false);

                break;
            }

            case 2: {

                genderMaleCheckBox.setChecked(false);
                genderFemaleCheckBox.setChecked(true);

                break;
            }

            default: {

                genderMaleCheckBox.setChecked(true);
                genderFemaleCheckBox.setChecked(true);

                break;
            }
        }
    }

    public int getOnline() {

        if (onlineCheckBox.isChecked()) {

            return 0;
        }

        return -1;
    }

    public void setOnline(int online) {

        if (online == -1) {

            onlineCheckBox.setChecked(false);

        } else {

            onlineCheckBox.setChecked(true);
        }
    }

    public int getPhoto() {

        if (photoCheckBox.isChecked()) {

            return 0;
        }

        return -1;
    }

    public void setPhoto(int photo) {

        if (photo == -1) {

            photoCheckBox.setChecked(false);

        } else {

            photoCheckBox.setChecked(true);
        }
    }
}