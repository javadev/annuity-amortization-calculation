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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**.
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public class AmortizationPlanTest extends AbstractTarificationTestCase {

    @Before
    public void setUp() {
        org.apache.log4j.BasicConfigurator.configure();
    }

    /**
     * test of Grace Period
     */
    @Test
    public void testGracePeriod() {
        // gracePeriod
        Integer gracePeriod = 6;

        LOG.debug(this, "### testGracePeriod() begin ###");

        List<CalculationInputParameters> calculationInputParameterss = createListFromData("testGracePeriod",
                CalculationInputParameters.class);

        for (CalculationInputParameters calculationInputParameters : calculationInputParameterss) {
            List<PaymentDate> dates = new MonthlyCalendarFiller().createDateList(calculationInputParameters);
            AmortizationCalculation calculation = new AmortizationCalculation(dates, calculationInputParameters);

            calculation.calc();
            Assert.assertTrue(calculation.getPayments() != null);

            List<Payment> payments = calculation.getPayments();
            double totalBody = .00;
            double totalPrc = .00;
            int index = 0;
            for (Payment payment : payments) {
                if (index == 0) {
                    index++;
                    continue;
                }
                LOG.debug(this, index + ">>> Date: " + payment.getDate() + " Full Payment: " + payment.getTotalPayment() + "  Capital Payment:"
                        + payment.getCapitalPayment() + "  Interest Payment:" + payment.getInterestPayment()
                        + " Monthly Payment:" + payment.getMonthlyFee());
                totalBody += payment.getTotalPayment();
                totalPrc += payment.getInterestPayment();
                Assert.assertEquals("Total should be equal capital + interest + monthly", payment.getTotalPayment(),
                        payment.getCapitalPayment() + payment.getInterestPayment() + payment.getMonthlyFee(), 0.01);
                index++;
            }
            double totalCalculationBody = calculation.round(calculation.getTotal().getTotalPayment(), 2);
            LOG.debug(this, ">>> Full Calculation Body: " + totalCalculationBody + "    Calc:" + totalBody);
            Assert.assertTrue("Payments - " + payments.size(), Math.abs(totalCalculationBody
                    - calculation.getTotal().getInterestPayment() - calculation.getTotal().getMonthlyFee()
                    - calculation.getTotal().getCapitalPayment()) < 0.0001);

            // set for GracePeriod
            calculation = new AmortizationCalculation(dates, calculationInputParameters);
            calculationInputParameters.setEndGracePeriod((gracePeriod == null) ? null : (long) gracePeriod);
            calculation.calc();
            Assert.assertTrue(calculation.getPayments() != null);

            payments = calculation.getPayments();
            double graceBody = .00;
            double gracePrc = .00;
            index = 0;
            for (Payment payment : payments) {
                if (index == 0) {
                    index++;
                    continue;
                }
                LOG.debug(this, index + ">>> Date: " + payment.getDate() + " Grace Payment: " + payment.getTotalPayment() + "  Capital Payment:"
                        + payment.getCapitalPayment() + "  Interest Payment:" + payment.getInterestPayment()
                        + " Monthly Payment:" + payment.getMonthlyFee());
                graceBody += payment.getTotalPayment();
                gracePrc += payment.getInterestPayment();
                Assert.assertEquals("Total should be equal capital + interest + monthly", payment.getTotalPayment(),
                        payment.getCapitalPayment() + payment.getInterestPayment() + payment.getMonthlyFee(), 0.01);
                index++;
            }
            double graceCalculationBody = calculation.round(calculation.getTotal().getTotalPayment(), 2);
            LOG.debug(this, ">>> Full Calculation Body: " + graceCalculationBody + "    Calc:" + graceBody);
            LOG.debug(this, index + ">>> Total Payment: " + calculation.getTotal().getTotalPayment() + "  Capital Payment:"
                    + calculation.getTotal().getCapitalPayment() + "  Interest Payment:"
                    + calculation.getTotal().getInterestPayment() + " Monthly Payment:"
                    + calculation.getTotal().getMonthlyFee());
            Assert.assertTrue("Payments - " + payments.size(), Math.abs(graceCalculationBody
                    - calculation.getTotal().getInterestPayment() - calculation.getTotal().getMonthlyFee()
                    - calculation.getTotal().getCapitalPayment()) < 0.0001);
        }

        LOG.debug(this, "### testGracePeriod()  end  ###");
    }
}
