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

import java.io.Serializable;
import java.util.Date;

/**.
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public class CalculationInputParameters implements Serializable {
    private static final long serialVersionUID = -8460631603752150160L;

    private double      amount;
    private double      coreAmount;
    private double      rate;
    private double      openingRate;
    private double      openingFeePayment;
    private double      monthlyRate;
    private double      annuityExample;
    private int         duration;
    private Date        startDate;
    private int         repaymentDay;
    private double      downPayment;
    private double      installment;
    private double      totalPrice;
    private String      typefirstduedatecal;
    /** grace period, range of the value: [1..48] */
    private Long    endGracePeriod;

    public CalculationInputParameters(double amount, double rate, double openingRate, double monthlyRate, int duration,
            Date startDate, int repaymentDay, double downPayment, double installment) {
        this.amount = amount;
        this.rate = rate;
        this.openingRate = openingRate;
        this.monthlyRate = monthlyRate;
        this.duration = duration;
        this.startDate = startDate;
        this.repaymentDay = repaymentDay;
        this.downPayment = downPayment;
        this.installment = installment;
        this.setTotalPrice(.00);
        this.openingFeePayment = .00;
    }

    public CalculationInputParameters() {
        amount = .00;
        coreAmount = .00;
        rate = .00;
        openingRate = .00;
        monthlyRate = .00;
        duration = 0;
        startDate = new Date();
        repaymentDay = 1;
        installment = .00;
        downPayment = .00;
        this.setTotalPrice(.00);
        this.openingFeePayment = .00;
    }

    public double getInstallment() {
        return this.installment;
    }

    public void setInstallment(double installment) {
        this.installment = installment;
    }

    public double getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(double downPayment) {
        this.downPayment = downPayment;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getOpeningRate() {
        return openingRate;
    }

    public void setOpeningRate(double openingRate) {
        this.openingRate = openingRate;
    }

    public double getMonthlyRate() {
        return monthlyRate;
    }

    public double getAnnuityExample() {
        return annuityExample;
    }

    public void setAnnuityExample(double annuity) {
        this.annuityExample = annuity;
    }

    public void setMonthlyRate(double monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getRepaymentDay() {
        return repaymentDay;
    }

    public void setRepaymentDay(int repaymentDay) {
        this.repaymentDay = repaymentDay;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getOpeningFeePayment() {
        return openingFeePayment;
    }

    public void setOpeningFeePayment(double openingFeePayment) {
        this.openingFeePayment = openingFeePayment;
    }

    public double getCoreAmount() {
        return coreAmount;
    }

    public void setCoreAmount(double coreAmount) {
        this.coreAmount = coreAmount;
    }

    public String getTypefirstduedatecal() {
        return typefirstduedatecal;
    }

    public void setTypefirstduedatecal(String typefirstduedatecal) {
        this.typefirstduedatecal = typefirstduedatecal;
    }

    public Long getEndGracePeriod() {
        return endGracePeriod;
    }

    public void setEndGracePeriod(Long endGracePeriod) {
        this.endGracePeriod = endGracePeriod;
    }

}
