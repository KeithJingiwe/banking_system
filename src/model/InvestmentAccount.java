package model;

import java.math.BigDecimal;


public class InvestmentAccount extends Account implements InterestBearing{
    public static final double MONTHLY_RATE = 0.05;
    public static final BigDecimal MIN_OPENING_DEPOSIT = new BigDecimal("500.00");

    public InvestmentAccount(String accountNumber, String branch, Customer owner, BigDecimal initialDeposit) {
        super(accountNumber, branch, owner);
        if (initialDeposit == null || initialDeposit.compareTo(MIN_OPENING_DEPOSIT) < 0) {
            throw new IllegalArgumentException(
                    "An Investment account needs an initial deposit of at least BWP " + MIN_OPENING_DEPOSIT);
        }
        deposit(initialDeposit, "Opening deposit");
    }

    @Override
    public boolean withdraw(BigDecimal amount) { return debit(amount); }

    @Override
    public String getAccountType() { return "Investment"; }

    @Override
    public double getMonthlyRate() { return MONTHLY_RATE; }

    @Override
    public BigDecimal calculateInterest() {
        return Money.round(getBalance().multiply(BigDecimal.valueOf(MONTHLY_RATE)));
    }

    @Override
    public Transaction payInterest() {
        BigDecimal interest = calculateInterest();
        if (interest.signum() <= 0) return null;
        return applyCredit(interest, TransactionType.INTEREST, "Monthly interest");
    }
}
