/*
 * $Id$
 *
 * Copyright 2013 Valentyn Kolesnikov
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

/**
 * .
 * @author vko
 * @version $Revision$ $Date$
 */
public class Amortization {
    private static final String DURATION = "--duration=";
    private static final String AMOUNT = "--amount=";
    private static final String RATE = "--rate=";
    private static final String OPENINGRATE = "--openingrate=";
    private static final String MONTHLYRATE = "--monthlyrate=";

    static {
        org.apache.log4j.BasicConfigurator.configure();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            LOG.info(null,
                    "The tool to calculate amortization plan\n"
                    + "Usage: java -jar amortization.jar --duration=[1-60] --amount=[300-80000] --rate=[0.01-100] --openingrate=[0-5] --monthlyrate=[0-5]");
            return;
        }
        CalculationInputParameters calculationInputParameters = new CalculationInputParameters();
        calculationInputParameters.setRate(0.01);
        calculationInputParameters.setDuration(12);
        for (String arg : args) {
            if (arg.startsWith(DURATION)) {
                calculationInputParameters.setDuration(Integer.valueOf(arg.substring(DURATION.length())));
            } else if (arg.startsWith(AMOUNT)) {
                calculationInputParameters.setAmount(Double.valueOf(arg.substring(AMOUNT.length())));
            } else if (arg.startsWith(RATE)) {
                calculationInputParameters.setRate(Double.valueOf(arg.substring(RATE.length())));
            } else if (arg.startsWith(OPENINGRATE)) {
                calculationInputParameters.setRate(Double.valueOf(arg.substring(OPENINGRATE.length())));
            } else if (arg.startsWith(MONTHLYRATE)) {
                calculationInputParameters.setRate(Double.valueOf(arg.substring(MONTHLYRATE.length())));
            }
        }

        List<PaymentDate> dates = new MonthlyCalendarFiller().createDateList(calculationInputParameters);
        AmortizationCalculation calculation = new AmortizationCalculation(dates, calculationInputParameters);
        calculation.calc();
        List<Payment> payments = calculation.getPayments();
        int index = 0;
        for (Payment payment : payments) {
            if (index == 0) {
                index++;
                continue;
            }
            LOG.info(null, index + ">>> Date: " + payment.getDate() + " Full Payment: " + payment.getTotalPayment() + " Capital Payment:"
                    + payment.getCapitalPayment() + " Interest Payment:" + payment.getInterestPayment()
                    + " Monthly Payment:" + payment.getMonthlyFee());
            index++;
        }
    }
}
