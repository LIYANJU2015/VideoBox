package com.util;

import com.commonlibs.util.LogUtils;

/**
 * Created by liyanju on 2017/5/14.
 */

public class YouTubeUtil {

    public static String convertDuration(String duration) {
        duration = duration.substring(2);  // del. PT-symbols
        String H, M, S;
        // Get Hours:
        int indOfH = duration.indexOf("H");  // position of H-symbol
        if (indOfH > -1) {  // there is H-symbol
            H = duration.substring(0,indOfH);      // take number for hours
            duration = duration.substring(indOfH); // del. hours
            duration = duration.replace("H","");   // del. H-symbol
        } else {
            H = "";
        }
        // Get Minutes:
        int indOfM = duration.indexOf("M");  // position of M-symbol
        if (indOfM > -1) {  // there is M-symbol
            M = duration.substring(0,indOfM);      // take number for minutes
            duration = duration.substring(indOfM); // del. minutes
            duration = duration.replace("M","");   // del. M-symbol
            // If there was H-symbol and less than 10 minutes
            // then add left "0" to the minutes
            if (H.length() > 0 && M.length() == 1) {
                M = "0" + M;
            }
        } else {
            // If there was H-symbol then set "00" for the minutes
            // otherwise set "0"
            if (H.length() > 0) {
                M = "00";
            } else {
                M = "0";
            }
        }
        // Get Seconds:
        int indOfS = duration.indexOf("S");  // position of S-symbol
        if (indOfS > -1) {  // there is S-symbol
            S = duration.substring(0,indOfS);      // take number for seconds
            duration = duration.substring(indOfS); // del. seconds
            duration = duration.replace("S","");   // del. S-symbol
            if (S.length() == 1) {
                S = "0" + S;
            }
        } else {
            S = "00";
        }
        if (H.length() > 0) {
            return H + ":" +  M + ":" + S;
        } else {
            return M + ":" + S;
        }
    }
}
