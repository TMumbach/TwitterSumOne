package br.com.sumone.sumonetwitter.utils;

import android.content.res.Resources;

/**
 * Created by Tiago on 04/08/2015.
 */
public class DisplayUtils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
