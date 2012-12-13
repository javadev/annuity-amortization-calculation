/*
 * $Id$
 *
 * Copyright 2012 Valentyn Kolesnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.amortization;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**.
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public class MonthlyCalendarFiller {
    /**
     * In case if this duration is less than 20 days, first due date is moved to the due date in the next month.
     */
    public static final int MIN_DAYS_FROM_START = 20;

    private int repaymentday                    = 5;
    private Date contractFinancedDate           = new Date();
    private Date contractFinancedDateToPrint    = new Date();
    private Date contractdateofnextworkday      = new Date();
    private Date firstPaymentDate               = new Date();
    private Date endPaymentDate                 = new Date();


    public MonthlyCalendarFiller() {
    }

    public Date getContractFinancedDate() {
        return contractFinancedDate;
    }

    public Date getContractFinancedDateToPrint() {
        return contractFinancedDateToPrint;
    }

    public Date getContractDateOfNextWorkDay() {
        return contractdateofnextworkday;
    }

    public Date getFirstPaymentDate() {
        return firstPaymentDate;
    }

    public Date getEndPaymentDate() {
        return endPaymentDate;
    }

    public int getDueDay() {
        return repaymentday;
    }

    public List<PaymentDate> createDateList(CalculationInputParameters parameters) {
        List<PaymentDate> dates = new ArrayList<PaymentDate>();
        Calendar calendar = Calendar.getInstance();
        int duration = parameters.getDuration();
        int day = parameters.getRepaymentDay();
        repaymentday = day;
        Date start = parameters.getStartDate();
        Date currentDate;
        Date previousDate;
        contractFinancedDateToPrint = start;
        calendar.setTime(start);
        previousDate = calendar.getTime();

        Calendar calendarForNextDay   = Calendar.getInstance();

        calendarForNextDay.setTime(contractFinancedDateToPrint);
        calendarForNextDay.add(Calendar.DATE, 1);

        // it's necessary because we need to move date on first work day after holidays to make payment in bank system
        correctDate(calendarForNextDay, calendarForNextDay.get(Calendar.DATE));
        contractdateofnextworkday = calendarForNextDay.getTime();


        // it's necessary because we need to move date on first work day after holidays to make payment in bank system
        correctDate(calendar, calendar.get(Calendar.DATE));
        contractFinancedDate = calendar.getTime();

        dates.add(new PaymentDate(contractFinancedDate, 0));
        LOG.debug(this, "Set date of financing on calculation start: " + contractFinancedDate);

        calendar.setTime(start);
        correctDate(calendar, day);


        boolean correctDate = true;
        for (int i = 0; i < duration; i++) {
            calendar.add(Calendar.MONTH, 1);
            if (i == 0) {
                firstPaymentDate = calendar.getTime();
            }
            if (i == duration - 1) {
                Calendar calendarEndDate = Calendar.getInstance();
                calendarEndDate.setTime(calendar.getTime());
                calendarEndDate.set(Calendar.DATE, day);
                endPaymentDate = calendarEndDate.getTime();
                LOG.debug(this, "END PAYMENT DATE=" + endPaymentDate);
            }
            currentDate = correctDate(calendar, day);
            PaymentDate paymentDate = new PaymentDate();
            if (correctDate) {
                long days = getDaysBetween(start, calendar.getTime());
                if ((days + 1) < MIN_DAYS_FROM_START) {
                    calendar.add(Calendar.MONTH, 1);
                    currentDate = correctDate(calendar, day);
                }
                // next line added at 23.11.2009 according CFIT-433
                firstPaymentDate = currentDate;
                correctDate = false;
            }
            paymentDate.setDaysPerMonth(calendar.getActualMaximum(Calendar.DATE));
            paymentDate.setDaysBefore(getDaysBetween(previousDate, currentDate));
            paymentDate.setDate(currentDate);
            dates.add(paymentDate);
            previousDate = currentDate;
        }

        return dates;
    }

    /**
     * Correct date according to days in month.
     * If requested day is more than days in month calendar date will be set to
     * end day of month.
     * (For example: if requested day is 31 and current calendar day is February then
     * calendar date will be set to last day of February)
     * @param calendar - calendare object can be corrected
     * @param requestedDay - repayment date
     * @return corrected Date object
     */
    private Date correctDate(Calendar calendar, int requestedDay) {
        int maxDay = calendar.getActualMaximum(Calendar.DATE);
        int day = requestedDay;
        if (calendar.get(Calendar.DATE) != requestedDay) {
            day = requestedDay <= maxDay ? requestedDay : maxDay;
        }
        calendar.set(Calendar.DATE, day);
        return calendar.getTime();
    }

    private long getDaysBetween(Date beginDay, Date endDay) {
        long diff;
        Calendar calendarFrom = Calendar.getInstance();
        Calendar calendarTo = Calendar.getInstance();
        calendarFrom.setTime(beginDay);
        calendarTo.setTime(endDay);
        diff = calendarTo.getTimeInMillis() - calendarFrom.getTimeInMillis();
        return diff / (1000 * 60 * 60 * 24);
    }
}
