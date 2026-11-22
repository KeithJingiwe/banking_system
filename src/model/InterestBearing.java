package model;

import java.math.BigDecimal;

public interface InterestBearing {
    double getMonthlyRate();
    BigDecimal calculateInterest();
    Transaction payInterest();
}
