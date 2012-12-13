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
import java.math.BigDecimal;
import java.util.Date;

/**.
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = -4510613745741158767L;

    private Date    date = new Date();
    private double  totalPayment;
    private double  coreAmount;
    private double  capitalPayment;
    private double  interestPayment;
    private double  totalInterestPayment;
    private double  openingFee;
    private double  monthlyFee;
    private double  monthlyFeePayment;
    private double  interestRate;
    private double  loanCost;
    private long    daysPerMonth;
    private long    days;
    private double  debetIn;
    private double  debetOut;
    private double  netValue;
    private long    duration;
    private double  annuitySum;
    // annuitySum + monthlyFeePayment
    private double  totalInstalmentPayment;
    // percent of single payment and shold be calculated for both total and amortization payments
    private double  openingFeeRate;
    // percent of single payment and shold be calculated for both total and amortization payments
    private double  monthlyFeeRate;
    // Nominal rate
    private double  nominalRate;
    private BigDecimal insurancePayment = BigDecimal.ZERO;

    /**
     * Get loan repayment date.
     * @return loan repayment date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set loan repayment date.
     * @param date - loan repayment date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get total payment.
     * @return rounded to 2 numbers after digital delimiter total payment.
     */
    public double getTotalPayment() {
        return totalPayment;
    }

    /**
     * Set total payment.
     * @param totalPayment - total payment.
     */
    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    /**
     * Get capital payment. (rounded to 2 numbers after digital delimiter).
     * @return  capital payment
     */
    public double getCapitalPayment() {
        return capitalPayment;
    }

    /**
     * Set capital payment. (rounded to 2 numbers after digital delimiter).
     * @param capitalPayment - capital payment
     */
    public void setCapitalPayment(double capitalPayment) {
        this.capitalPayment = capitalPayment;
    }

    /**
     * Get interest payment (rounded to 2 numbers after digital delimiter).
     * @return interest payment
     */
    public double getInterestPayment() {
        return interestPayment;
    }

    /**
     * Set interest payment.
     * @param interestPayment - interest payment
     */
    public void setInterestPayment(double interestPayment) {
        this.interestPayment = interestPayment;
    }

    /**
     * Get opening fee (rounded to 2 numbers after digital delimiter).
     * @return opening fee.
     */
    public double getOpeningFee() {
        return openingFee;
    }

    /**
     * Set opening fee.
     * This is some due for opening client account.
     * @param openingFee  opening fee.
     */
    public void setOpeningFee(double openingFee) {
        this.openingFee = openingFee;
    }

    /**
     * Get monthly fee (rounded to 2 numbers after digital delimiter).
     * @return monthly fee.
     */
    public double getMonthlyFee() {
        return monthlyFee;
    }

    /**
     * Set monthly fee.
     * @param monthlyFee - monthly fee..
     */
    public void setMonthlyFee(double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    /**
     * Get effective interest rate. (without rounding)
     * @return effective interest rate. (without rounding, in percents (1.00% ... 100.00%)
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * Set effective interest rate.
     * @param interestRate effective interest rate (in percents (1.00% ... 100.00%.)
     */
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * Get loan cost.
     * @return loan cost (rounded to 2 numbers after digital delimiter)
     */
    public double getLoanCost() {
        return loanCost;
    }

    /**
     * Set loan cost.
     * @param loanCost - loan cost
     */
    public void setLoanCost(double loanCost) {
        this.loanCost = loanCost;
    }

    /**
     * Get days from previous payment
     * @return number of days from previous payment
     */
    public long getDays() {
        return days;
    }

    /**
     * Set days from previous payment.
     * @param days number of days from previous payment
     */
    public void setDays(long days) {
        this.days = days;
    }

    /**
     * Get Days in current month.
     * @return days in current month
     */
    public long getDaysPerMonth() {
        return daysPerMonth;
    }

    /**
     * St days in current month.
     * @param daysPerMonth - days in current month
     */
    public void setDaysPerMonth(long daysPerMonth) {
        this.daysPerMonth = daysPerMonth;
    }

    /**
     * Get current client depts (client liabilities to be paid).
     * @return current client depts
     */
    public double getDebetIn() {
        return debetIn;
    }

    /**
     * Set current client depts (client liabilities to be paid).
     * @param debetIn - current client depts
     */
    public void setDebetIn(double debetIn) {
        this.debetIn = debetIn;
    }

    /**
     * Get Net current value of payment total.
     * @return Net current value of payment total (Pi = Pi/(1+ getInterestRate/12 )^i)
     */
    public double getNetValue() {
        return netValue;
    }

    /**
     * Set  Net current value of payment total.
     * @param netValue - Net current value of payment total
     */
    public void setNetValue(double netValue) {
        this.netValue = netValue;
    }

    /**
     * Get monthly fee payment.
     * @return monthly fee payment
     */
    public double getMonthlyFeePayment() {
        return monthlyFeePayment;
    }

    /**
     * Set monthly fee payment as payment only for totalt Payment object.
     * Used only for total Payment object unlike MonthlyFee property.
     * @param monthlyFeePayment - monthly fee payment
     */
    public void setMonthlyFeePayment(double monthlyFeePayment) {
        this.monthlyFeePayment = monthlyFeePayment;
    }

    public double getTotalInterestPayment() {
        return totalInterestPayment;
    }

    public void setTotalInterestPayment(double totalInterestPayment) {
        this.totalInterestPayment = totalInterestPayment;
    }

    public double getDebetOut() {
        return debetOut;
    }

    public void setDebetOut(double debetOut) {
        this.debetOut = debetOut;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getAnnuitySum() {
        return annuitySum;
    }

    public void setAnnuitySum(double annuitySum) {
        this.annuitySum = annuitySum;
    }

    public double getTotalInstalmentPayment() {
        return totalInstalmentPayment;
    }

    public void setTotalInstalmentPayment(double totalInstalmentPayment) {
        this.totalInstalmentPayment = totalInstalmentPayment;
    }

    public double getOpeningFeeRate() {
        return openingFeeRate;
    }

    public void setOpeningFeeRate(double openingFeeRate) {
        this.openingFeeRate = openingFeeRate;
    }

    public double getMonthlyFeeRate() {
        return monthlyFeeRate;
    }

    public void setMonthlyFeeRate(double monthlyFeeRate) {
        this.monthlyFeeRate = monthlyFeeRate;
    }

    public double getNominalRate() {
        return nominalRate;
    }

    public void setNominalRate(double nominalRate) {
        this.nominalRate = nominalRate;
    }

    public double getCoreAmount() {
        return coreAmount;
    }

    public void setCoreAmount(double coreAmount) {
        this.coreAmount = coreAmount;
    }

    public BigDecimal getInsurancePayment() {
        return insurancePayment;
    }

    public void setInsurancePayment(BigDecimal insurancePayment) {
        this.insurancePayment = insurancePayment;
    }

    @Override
    public String toString() {
        return "Payment@" + hashCode();
    }
}
