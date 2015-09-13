package com.music.personal.myapplication.utils;

import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * Created by hrajagopal on 9/11/15.
 */
public class Utils {
    public static String getSearchableData(String data) {
        if (data != null) {
            try{
                data = URLDecoder.decode(data, "UTF-8");
                data = data.replaceAll(Pattern.quote(" "), "");
                data = data.toLowerCase();

                return data.trim();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return null;
    }
}
