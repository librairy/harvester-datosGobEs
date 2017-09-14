package org.librairy.harvester.datosgobes.model;

import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class TimeExpression {


    String year;

    String month;

    String day;

    // mié, 26 nov 2014 23:00:00 GMT+0000
    public TimeExpression(String value){

        String[] values = StringUtils.substringAfter(value,",").trim().split(" ");

        year    = values[2];
        month   = getMonthNumber(values[1]);
        day     = values[0];

    }

    public String getISO8601(){
        return year+month+day;
    }

    private String getMonthNumber(String text){
        switch (text.toLowerCase()){
            case "ene" : return "00";
            case "feb" : return "01";
            case "mar" : return "02";
            case "abr" : return "03";
            case "may" : return "04";
            case "jun" : return "05";
            case "jul" : return "06";
            case "ago" : return "07";
            case "sep" : return "08";
            case "oct" : return "09";
            case "nov" : return "10";
            case "dic" : return "11";
            default: throw new RuntimeException("Unknown month: " + text);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new TimeExpression("mié, 26 nov 2014 23:00:00 GMT+0000").getISO8601());
    }
}
