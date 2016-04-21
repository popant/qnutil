package com.channelsoft.qnutil.util;
 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
 
public class CalendarTest {
 
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
 
    public static void main(String[] args) {
        CalendarTest test = new CalendarTest();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, 1);
        List<Date> dates = test.getDates(start, end);
 
        test.printDate("Start\t", start.getTime());
        for (Date date : dates) {
            test.printDate("-->\t", date);
        }
        test.printDate("End\t", end.getTime());
    }
 
    // Print the date with the format.
    public void printDate(String p_msg, Date p_date) {
        System.out.println(p_msg + sdf.format(p_date));
    }
 
    /**
     * Get the Dates between Start Date and End Date.
     * @param p_start   Start Date
     * @param p_end     End Date
     * @return Dates List
     */
    public List<Date> getDates(Calendar p_start, Calendar p_end) {
        List<Date> result = new ArrayList<Date>();
        Calendar temp = p_start.getInstance();
        temp.add(Calendar.DAY_OF_YEAR, 1);
        while (temp.before(p_end)) {
            result.add(temp.getTime());
            temp.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }
}