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
import java.math.BigDecimal;

/**
 * .
 * @author vko
 * @version $Revision$ $Date$
 */
public class AmortizationCalculation {
    private static final int INTEREST_DAYS_30 = 30;
    private static final double N_PRECISION = -0.00000000000001;
    private static final double P_PRECISION = 0.00000000000001;
    private List<PaymentDate> calendar;
    private CalculationInputParameters parameters;
    private Payment total;
    private List<Payment> payments;
    private double annuitySum;
    private double monthlyFee;
    private double realMonthlyFee;
    private double monthlyInstallment;
    private double openingFee;
    private double rate;
    /** Date of the end of Grace Period */
    private Date endGracePeriodDate;
    private boolean flagNeedRound = true;

    /**
     * Constructor. If class instance is made by this constructor, annuity sum will not be calculated internally and
     * passed annuity sum by third parameter will be applied.
     *
     * @param calendar
     *            - PaymentDate object list (payment schedule).
     * @param parameters
     *            - input parameters (see CalculationInputParameters class) are needed to calculate amortization plan
     *            for annuity calculation procedure.
     */
    public AmortizationCalculation(List<PaymentDate> calendar, CalculationInputParameters parameters) {
        init(calendar, parameters);
    }

    /**
     * Initialize object newly created.
     *
     * @param calendar
     *            - PaymentDate object list (payment schedule).
     * @param parameters
     *            - input parameters (see CalculationInputParameters class) are needed to calculate amortization plan
     *            for annuity calculation procedure.
     */
    private void init(List<PaymentDate> calendar, CalculationInputParameters parameters) {
        this.calendar = calendar;
        this.parameters = parameters;
        this.rate = parameters.getRate();
        total = new Payment();
        annuitySum = calcAnnuitySums(parameters.getAmount(), parameters.getDuration());
        setPayments(new ArrayList<Payment>());
        realMonthlyFee = parameters.getMonthlyRate() / 100.00 * parameters.getAmount();
        openingFee = parameters.getOpeningRate() / 100.00 * parameters.getAmount();

        monthlyFee = round(realMonthlyFee, 2);
        this.monthlyInstallment = monthlyFee + annuitySum;
        if (flagNeedRound) {
            annuitySum = Math.ceil(annuitySum);
            monthlyInstallment = Math.ceil(monthlyInstallment);
        }
    }

    public Payment getTotal() {
        return total;
    }

    public void calc() {
        payments.clear();
        if (rate < 0.000001 || parameters == null || parameters.getAmount() < 0.000001) {
            return;
        }

        int size = calendar.size();
        boolean isFirstInstallment = true;
        Payment previous = null;
        Calendar totalCalendarDate = Calendar.getInstance();
        for (int index = 0; index < size; index += 1) {
            Payment payment;
            PaymentDate paymentDate = (PaymentDate) calendar.get(index);
            if (paymentDate != null && paymentDate.getDate() != null) {
                totalCalendarDate.setTime(paymentDate.getDate());
            }

            if (index == 0) {
                payment = new Payment();
                payment.setTotalPayment(-parameters.getAmount());
                payment.setDate(paymentDate.getDate());
                payment.setOpeningFee(openingFee);
                payments.add(payment);
                continue;
            }
            payment = index == 1 ? calcFirstInstallment(paymentDate, index) : index + 1 == size
                ? calcLastInstallment(paymentDate, previous, index) : calcInstallment(paymentDate, previous, index);
            previous = payment;
            payment.setMonthlyFeeRate(parameters.getMonthlyRate());
            payment.setOpeningFeeRate(parameters.getOpeningRate());
            payment.setNominalRate(parameters.getRate());
            payment.setTotalInstalmentPayment(monthlyInstallment);
            payment.setCoreAmount(parameters.getCoreAmount());

            add2Total(payment);
            payments.add(payment);
        }
        // round all payments
        completeTotal(totalCalendarDate);

        if ((this.parameters.getEndGracePeriod() != null) && (this.parameters.getEndGracePeriod() > 0)) {
            try {
                this.endGracePeriodDate = this.payments.get(this.parameters.getEndGracePeriod().intValue()).getDate();
                LOG.debug(this, "calc(): EndGracePeriodDate=" + this.endGracePeriodDate);

            } catch (Exception ex) {
                LOG.error(this, "check grace period value, may be is too big: " + ex.getMessage());
            }
        }
    }

    /**
     * set flag for need round values.
     * <ul>
     * <li><b>true</b> - need to round ( for all, but skip last )</li>
     * <li><b>false</b> natural values</li>
     * </ul>
     *
     * @param newRoundValue set need round
     */
    public void setRoundValues(boolean newRoundValue) {
        this.flagNeedRound = newRoundValue;
    }

    /**
     * Calculate effective rate according to National Bank of Ukraine rules.
     *
     * @param payments
     *            - payment list (Payment object list)
     * @return - calculated rate with precision E -14.
     */
    private double calcEffectRate() {
        double localRate = .00;
        int size = payments.size();
        double netValue;
        double left;
        double currentRate;
        double offset;
        for (currentRate = 1.00; localRate > N_PRECISION; currentRate += 1.00) {
            localRate = .00;
            for (int index = 0; index < size; index += 1) {
                Payment payment = (Payment) payments.get(index);
                netValue = payment.getTotalPayment() / Math.pow(1 + currentRate / 12, index);
                localRate += netValue;
                if (localRate > N_PRECISION) {
                    break;
                }
            }
            if (localRate < N_PRECISION) {
                break;
            }
        }

        left = currentRate;
        int cnt = 0;
        int precision = BigDecimal.valueOf(P_PRECISION).scale() - 2;
        BigDecimal.valueOf(0.00).precision();
        for (offset = 0.10; offset > P_PRECISION && cnt < precision; offset = offset / 10, cnt++) {
            currentRate = left;
            boolean isLastFor = (offset / 10 < P_PRECISION) || cnt >= precision;
            for (; currentRate > P_PRECISION; currentRate = currentRate - offset) {
                localRate = .00;
                for (int index = 0; index < size; index += 1) {
                    Payment payment = (Payment) payments.get(index);
                    netValue = payment.getTotalPayment() / Math.pow(1 + currentRate / 12, index);
                    localRate += netValue;
                    if (isLastFor) {
                        payment.setNetValue(netValue);
                    }
                    if (localRate > N_PRECISION && !isLastFor) {
                        break;
                    }
                }
                if (localRate > N_PRECISION) {
                    break;
                } else {
                    left = currentRate;
                }
            }
        }
        return currentRate;
    }

    /**
     * add2Total.
     * @param payment for addition to destination
     */
    private void add2Total(Payment payment) {
        total.setTotalPayment(total.getTotalPayment() + payment.getTotalPayment());
        total.setCapitalPayment(total.getCapitalPayment() + payment.getCapitalPayment());
        total.setInterestPayment(total.getInterestPayment() + payment.getInterestPayment());
        total.setTotalInterestPayment(total.getTotalInterestPayment() + payment.getTotalInterestPayment());
        total.setOpeningFee(total.getOpeningFee() + payment.getOpeningFee());
        total.setMonthlyFee(total.getMonthlyFee() + payment.getMonthlyFee());
    }

    private void completeTotal(Calendar cal) {
        total.setMonthlyFeePayment(getMonthlyFee());
        double ef = calcEffectRate();
        ef *= 100.00;
        total.setInterestRate(ef);
        total.setCoreAmount(parameters.getCoreAmount());
        total.setDuration(parameters.getDuration());
        total.setAnnuitySum(annuitySum);
        total.setMonthlyFeeRate(parameters.getMonthlyRate());
        total.setOpeningFeeRate(parameters.getOpeningRate());
        total.setNominalRate(round(parameters.getRate(), 2));
        total.setTotalInstalmentPayment(monthlyInstallment);
        total.setOpeningFee(round(openingFee, 2));

        // round all main values calculated before:
        total.setTotalPayment(round(total.getTotalPayment(), 2));
        total.setCapitalPayment(round(total.getCapitalPayment(), 2));
        total.setInterestPayment(round(total.getInterestPayment(), 2));
        total.setTotalInterestPayment(round(total.getTotalInterestPayment(), 2));
        total.setMonthlyFee(round(total.getMonthlyFee(), 2));

        total.setLoanCost(total.getTotalPayment());

        total.setTotalInterestPayment(total.getTotalPayment() - total.getCapitalPayment());
        cal.add(Calendar.MINUTE, 1);
        total.setDate(cal.getTime());
    }

    private Payment calcFirstInstallment(PaymentDate paymentDate, int period) {
        Payment payment = new Payment();
        double sum = parameters.getAmount();
        long days = getDays((PaymentDate) calendar.get(0), paymentDate);
        payment.setDays(days);
        payment.setDate(paymentDate.getDate());
        // Instalment in part of interests
        double interestByRealDays = calcInterest(sum, rate, Math.min(days, INTEREST_DAYS_30));
        interestByRealDays = round(interestByRealDays, 2);
        double totalInterestPayment = interestByRealDays + getMonthlyFee();
        payment.setInterestPayment(interestByRealDays);
        payment.setTotalInterestPayment(totalInterestPayment);
        if (parameters.getEndGracePeriod() != null && period <= parameters.getEndGracePeriod()) {
            payment.setCapitalPayment(annuitySum - interestByRealDays);
            payment.setMonthlyFee(0D);
            payment.setTotalPayment(annuitySum);
        } else {
            payment.setCapitalPayment(monthlyInstallment - totalInterestPayment);
            payment.setMonthlyFee(getMonthlyFee());
            payment.setTotalPayment(monthlyInstallment);
        }
        payment.setOpeningFee(openingFee);
        payment.setDaysPerMonth(paymentDate.getDaysPerMonth());
        payment.setDebetIn(round(parameters.getAmount(), 2));
        return payment;
    }

    private Payment calcLastInstallment(PaymentDate paymentDate, Payment previous, int period) {
        Payment payment = createPayment(paymentDate, period);
        payment.setOpeningFee(openingFee);
        payment.setMonthlyFee(getMonthlyFee());
        double rest = previous.getDebetIn() - previous.getCapitalPayment();
        payment.setDebetIn(rest);
        previous.setDebetOut(rest);
        payment.setCapitalPayment(rest);
        double interestByRealDays = round(calcInterest(rest, rate, INTEREST_DAYS_30), 2);

        payment.setInterestPayment(interestByRealDays);
        double totalInterest = interestByRealDays + getMonthlyFee();
        payment.setTotalInterestPayment(totalInterest);
        double lastMonthlyPayment = rest + totalInterest;
        payment.setTotalPayment(lastMonthlyPayment);
        payment.setInterestPayment(totalInterest - getMonthlyFee());
        return payment;
    }

    private Payment createPayment(PaymentDate paymentDate, int period) {
        Payment payment = new Payment();
        payment.setOpeningFee(openingFee);
        payment.setMonthlyFee(getMonthlyFee());
        payment.setDaysPerMonth(paymentDate.getDaysPerMonth());
        long days = paymentDate.getDaysBefore();
        payment.setDays(days);
        payment.setDate(paymentDate.getDate());
        if (parameters.getEndGracePeriod() != null && period <= parameters.getEndGracePeriod()) {
            payment.setTotalPayment(annuitySum);
            payment.setMonthlyFee(0D);
        } else {
            payment.setTotalPayment(monthlyInstallment);
            payment.setMonthlyFee(monthlyFee);
        }
        payment.setOpeningFee(openingFee);
        return payment;
    }

    private Payment calcInstallment(PaymentDate paymentDate, Payment previous, int period) {
        Payment payment = createPayment(paymentDate, period);
        double rest = round(previous.getDebetIn() - previous.getCapitalPayment(), 2);
        payment.setDebetIn(rest);
        previous.setDebetOut(rest);
        double interestByRealDays = calcInterest(rest, rate, INTEREST_DAYS_30);
        interestByRealDays = round(interestByRealDays, 2);
        payment.setInterestPayment(interestByRealDays);
        double totalInterestPayment = interestByRealDays + getMonthlyFee();
        payment.setTotalInterestPayment(totalInterestPayment);
        double instPartOfCapital = monthlyInstallment - interestByRealDays - getMonthlyFee();
        if (parameters.getEndGracePeriod() != null && period <= parameters.getEndGracePeriod()) {
            instPartOfCapital = annuitySum - interestByRealDays;
        }
        payment.setCapitalPayment(round(instPartOfCapital, 2));
        return payment;
    }

    /**
     * Calculate annuity payment sum.
     *
     * @param ammount
     *            -
     * @param duration
     *            -
     * @return the annuity sum
     */
    public double calcAnnuitySums(double ammount, int duration) {
        double sum;
        sum = ((Math.pow(1 + (rate / 12 / 100.00), duration) * (rate / 12 / 100.00)) / (Math.pow(
                1 + (rate / 12 / 100.00), duration) - 1))
                * ammount;
        return round(sum, 2);
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public double getAnnuitySum() {
        return annuitySum;
    }

    /**
     * Round value to defined number of signs after digit delimiter.
     *
     * @param value
     *            - value to be rounded
     * @param number
     *            - number of signs after digit delimiter for rounding to
     * @return rounded value.
     */
    public double round(double value, int number) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(number, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Get monthly installment as rounded sum of monthly fee (some rate to be paid each month) and opening fee (some
     * rate to be paid once when loan is granted) round(monthlyFee + annuitySum, 2).
     *
     * @return full monthly payment
     */
    public double getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    private long getDays(PaymentDate fromDate, PaymentDate toDate) {
        return getDays(fromDate.getDate(), toDate.getDate());
    }

    private long getDays(Date fromDate, Date toDate) {
        Calendar fromDateCalendar = Calendar.getInstance();
        fromDateCalendar.setTime(fromDate);
        Calendar toDateCalendar = Calendar.getInstance();
        toDateCalendar.setTime(toDate);

        long monthes = (toDateCalendar.get(Calendar.YEAR) * 12 + toDateCalendar.get(Calendar.MONTH))
                - (fromDateCalendar.get(Calendar.YEAR) * 12 + fromDateCalendar.get(Calendar.MONTH));

        long startDay = fromDateCalendar.get(Calendar.DATE);
        long endDay = toDateCalendar.get(Calendar.DATE);
        long days = monthes * 30 + (endDay - startDay);
        return days;
    }

    private double calcInterest(double sum, double rate, double days) {
        return sum * rate * days / (100.00 * 360);
    }

    /**
     * Date of the end of grace period.
     * @return  the end grace period date
     */
    public Date getEndGracePeriodDate() {
        return endGracePeriodDate;
    }

    /**
     * Date of the end of grace period.
     * @param endGracePeriodDate the end grace period date
     */
    public void setEndGracePeriodDate(Date endGracePeriodDate) {
        this.endGracePeriodDate = endGracePeriodDate;
    }
}
