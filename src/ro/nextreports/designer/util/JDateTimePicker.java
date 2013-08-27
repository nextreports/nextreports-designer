/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.designer.util;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;

/**
 * User: mihai.panaitescu
 * Date: 14-Dec-2009
 * Time: 17:38:13
 */
public class JDateTimePicker extends JPanel {

    private JXDatePicker datePicker;
    JSpinner minSpinner;
    JSpinner hourSpinner;

    private static final String mins[] = createTimeString(60);
    private static final String hours[] = createTimeString(24);

    public static final String uiClassID = "DateTimePickerUI";

    static DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public JDateTimePicker() {
    	super();
    	createPanel();
    }

    private void createPanel() {

        setLayout(new GridBagLayout());

        datePicker =new JXDatePicker();
        datePicker.addPropertyChangeListener(new PropertyChangeListener() {
   	     public void propertyChange(PropertyChangeEvent e) {
	         if ("date".equals(e.getPropertyName())) {
	        	 onChange();
	         }
	     }
	 });

        SpinnerListModel hourModel = new SpinnerListModel(createTimeString(24));
        hourSpinner = new JSpinner(hourModel);
        hourSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                changeDate();
            }
        });

        SpinnerListModel minModel = new SpinnerListModel(createTimeString(60));
        minSpinner = new JSpinner(minModel);
        minSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                changeDate();
            }
        });

        add(datePicker, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(hourSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(5, 0, 5, 2), 0, 0));
        add(new JLabel(":"), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
        add(minSpinner, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(5, 2, 5, 5), 0, 0));
        add(new JLabel(""), new GridBagConstraints(4, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));

    }

    private static String[] createTimeString(int time) {
        String[] retString = new String[time];
        for (int i = 0; i < retString.length; i++) {
            String timeS = "";

            if (i < 10)
                timeS = "0";
            timeS += Integer.toString(i);
            retString[i] = timeS;
        }
        return retString;
    }

    private void changeDate() {
    	// do not change the date with every spinner because this fires many events
    	// better use an apply or ok button

        Date date = getDate();
        if (date != null) {
            datePicker.setDate(date);
            int hours = Integer.parseInt((String) hourSpinner.getValue());
            int min = Integer.parseInt((String) minSpinner.getValue());

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            // use set instead of add to set the time values
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date newDate = calendar.getTime();            
            setDate(newDate);
            onChange();
        }
    }

    public Date getDate() {
    	Date date = datePicker.getDate();
    	if (date != null) {
    		int hours = Integer.parseInt((String) hourSpinner.getValue());
    		int min = Integer.parseInt((String) minSpinner.getValue());
    		GregorianCalendar calendar = new GregorianCalendar();
    		calendar.setTime(date);
    		// use set instead of add to set the time values
    		calendar.set(Calendar.HOUR_OF_DAY, hours);
    		calendar.set(Calendar.MINUTE, min);
    		calendar.set(Calendar.SECOND, 0);
    		calendar.set(Calendar.MILLISECOND, 0);

    		date = calendar.getTime();
    	}
    	return date;
    }

    public void setDate(Date date) {
    	if (date != null) {
            datePicker.setDate(date);
            GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int min = calendar.get(Calendar.MINUTE);
			System.out.println("Set the date, hours: "+hour+"("+hours[hour]+"), mins: "+min+"("+mins[min]+")");
			try {
		    	hourSpinner.setValue(hours[hour]);
		    	minSpinner.setValue(mins[min]);
			}catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }

    public JXDatePicker getDatePicker() {
        return datePicker;
    }
    
    protected void onChange() {    	
    }


}
