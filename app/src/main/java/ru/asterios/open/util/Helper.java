package ru.asterios.open.util;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.asterios.open.R;

public class Helper extends Application {

    public String getFormatedAmount(int amount){

        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    public static int getGridSpanCount(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth  = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.item_size);
        return Math.round(screenWidth / cellWidth);
    }

    public static int getStickersGridSpanCount(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float screenWidth  = displayMetrics.widthPixels;
        float cellWidth = activity.getResources().getDimension(R.dimen.sticker_item_size);
        return Math.round(screenWidth / cellWidth);
    }

    public boolean isValidEmail(String email) {

    	if (TextUtils.isEmpty(email)) {

    		return false;

    	} else {

    		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    	}
    }
    
    public boolean isValidLogin(String login) {

        String regExpn = "^([a-zA-Z]{4,24})?([a-zA-Z][a-zA-Z0-9_]{4,24})$";
        CharSequence inputStr = login;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {

            return true;

        } else {

            return false;
        }
    }

    public boolean isValidSearchQuery(String query) {

        String regExpn = "^([a-zA-Z]{1,24})?([a-zA-Z][a-zA-Z0-9_]{1,24})$";
        CharSequence inputStr = query;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {

            return true;

        } else {

            return false;
        }
    }
    
    public boolean isValidPassword(String password) {

        String regExpn = "^[a-z0-9_$@$!%*?&]{6,24}$";
        CharSequence inputStr = password;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {

            return true;

        } else {

            return false;
        }
    }
}
