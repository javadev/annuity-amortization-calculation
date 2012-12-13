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

import java.util.Date;
import java.io.Serializable;

/**.
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public class PaymentDate implements Serializable {

    private static final long serialVersionUID = -9078389073349605262L;
    private Date    date;
    private long    daysBefore;
    private long    daysPerMonth;

    /**
     * Default constructor.
     */
    public PaymentDate() {
        daysBefore = 0;
    }

    /**
     * Constructor.
     * @param date - payment date
     * @param daysBefore - number of days from previous date to this one
     */
    public PaymentDate(Date date, long daysBefore) {
        this.date       = date;
        this.daysBefore = daysBefore;
    }

    /**
     * Get payment date.
     * @return  payment date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set payment date.
     * @param date - payment date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get number of days from previous payment date to this one.
     * @return number of days from previous payment date to this one
     */
    public long getDaysBefore() {
        return daysBefore;
    }

    /**
     * Set number of days from previous payment date to this one.
     * @param daysBefore - number of days from previous payment date to this one
     */
    public void setDaysBefore(long daysBefore) {
        this.daysBefore = daysBefore;
    }

    /**
     * Get number of days in month is defined by date.
     * @return number of days in moth
     */
    public long getDaysPerMonth() {
        return daysPerMonth;
    }

    /**
     * Set number of days in month is defined by date.
     * @param daysPerMonth - number of days in moth
     */
    public void setDaysPerMonth(long daysPerMonth) {
        this.daysPerMonth = daysPerMonth;
    }
}
