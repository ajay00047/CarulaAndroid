package com.pola.app.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ajay on 04-Sep-17.
 */
public class StringUtil {

    public static boolean isNullOrBlank(String str){
        return (null == str ? true:("".equals(str.trim()) ? true:false));
    }

    public static String convertM2Km(int meter){
        if(meter < 1000)
            return meter+" m";
        return (meter/1000) + " km";
    }

    public static String convertSec2H(int seconds) {
        int minutes=seconds/60;
        if(minutes < 60) {
            return String.format("%d mins", minutes);
        } else if(minutes < 1440) { //1 day = 1440 minutes
            return String.format("%d hrs, %d mins", minutes/60, minutes%60);
        } else {
            return String.format("%d days", minutes / 1440);
        }
    }

    public static Long convertDate2Epoch(String dateTime){
        Long epoch = new Date().getTime()+3000;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy hh:mm a");
        try{
            epoch = sdf.parse(dateTime).getTime()/1000;
        }catch(Exception e){
            e.printStackTrace();
        }
        return epoch;
    }

    public static Long currentEpoch(){
        return new Date().getTime()/1000;
    }
}
