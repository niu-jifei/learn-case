package org.njf.bigdecimal;

import java.math.BigDecimal;

public class BigdecimalTest {
    public static void main(String[] args) {
        BigDecimal bigDecimal1 = new BigDecimal(1);
        BigDecimal bigDecimal2 = new BigDecimal(1.0);

        System.out.println(bigDecimal1 == bigDecimal2);
    }
}
