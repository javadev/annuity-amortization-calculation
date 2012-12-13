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

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

/**.
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public class AmortizationCalculationTest extends AbstractTarificationTestCase {
    private AmortizationCalculation amortizationCalculation;

    @Before
    public void setUp() {
       org.apache.log4j.BasicConfigurator.configure();
    }

    /**
     * Test method for calcAnnuitySums(double, int).
     */
    @Test
    public void testCalcAnnuitySums() {
        CalculationInputParameters params = createObjectFromData("testCalcAnnuitySums",
                CalculationInputParameters.class);
        amortizationCalculation = new AmortizationCalculation(Collections.<PaymentDate>emptyList(), params);
        double annuity = amortizationCalculation.calcAnnuitySums(1000D, 10);
        assertEquals("Should be equal", 104.64D, annuity, 2);
    }

    /**
     * Test method for calcAnnuitySums(double, int).
     */
    @Test
    public void testCalcAnnuitySumsWithMonthlyFee() {
        CalculationInputParameters params = createObjectFromData("testCalcAnnuitySumsWithMonthlyFee",
                CalculationInputParameters.class);
        amortizationCalculation = new AmortizationCalculation(Collections.<PaymentDate>emptyList(), params);
        assertEquals("Should be equal", 104.64D, amortizationCalculation.getAnnuitySum(), 2);
        assertEquals("Should be equal", 139.64D, amortizationCalculation.getMonthlyInstallment(), 2);
    }

    /**
     * Test method for calcAnnuitySums(double, int).
     */
    @Test
    public void testCalcAnnuitySumsWithMonthlyFee2() {
        CalculationInputParameters params = createObjectFromData("testCalcAnnuitySumsWithMonthlyFee2",
                CalculationInputParameters.class);
        amortizationCalculation = new AmortizationCalculation(Collections.<PaymentDate>emptyList(), params);
        assertEquals("Should be equal", 1012.94D, amortizationCalculation.getAnnuitySum(), 2);
        assertEquals("Should be equal", 1148.9D, amortizationCalculation.getMonthlyInstallment(), 2);
    }
}
