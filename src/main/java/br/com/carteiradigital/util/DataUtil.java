package br.com.carteiradigital.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {

    public static String dateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return sdf.format(date);
    }
}
