package njdsoftware.app_functional.UniversalUtilities;

import android.util.DisplayMetrics;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import njdsoftware.app_functional.MainActivity;

/**
 * Created by Nathan on 11/09/2016.
 */
public class UsefulFunctions {
    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = MainActivity.getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = MainActivity.getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
    public static float round (float value, int numDp) {
        return new BigDecimal(value).setScale(numDp, BigDecimal.ROUND_HALF_UP).floatValue();
    }
    public static String secondsToTime(long seconds){
        int hours =(int) (seconds / 3600);
        int minutes = (int)((seconds % 3600) / 60);
        int secs = (int) (seconds % 60);

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
    public static boolean areDecimalsEqual(float f1, float f2, float accuracy){
        float difference = f1 - f2;
        float positiveDifference;
        if (difference > 0){
            positiveDifference = difference;
        }else{
            positiveDifference = -difference;
        }
        if (positiveDifference <= accuracy){
            return true;
        }else{
            return false;
        }
    }

    public static long daysBetween(Date startDate, Date endDate) {
        return Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
    }
    public static boolean isThisYear(Date date) {
        if ((new LocalDate(date)).getYear() == (new LocalDate()).getYear()){
            return true;
        }else{
            return false;
        }
    }
}
