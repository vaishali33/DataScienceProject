package gov.nist.dataeval.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Test {
public static void main(String[] args) {
	
	GregorianCalendar calendar = new GregorianCalendar(1989, 03, 06);
//	calendar.setTime(new Date(1989, 03, 05));
	
	System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
}
}
