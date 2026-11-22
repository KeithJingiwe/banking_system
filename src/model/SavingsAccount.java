package model;

import java.math.BigDecimal;

public class SavingsAccount extends Account implements InterestBearing{
 public static final double MONTHLY_RATE = 0.0005;

 public SavingsAccount(String accountNumber, String branch, Customer owner) {
     super(accountNumber, branch, owner);
 }

 @Override
 public boolean withdraw(BigDecimal amount) {
     return reject("Withdrawals are not allowed on a Savings account.");
 }

 @Override
    public String getAccountType() { return "Savings"; }

 @Override
 public double getMonthlyRate() { return MONTHLY_RATE; }

 @Override
 public BigDecimal calculateInterest() {
     return Money.round(getBalance().multiply(BigDecimal.valueOf(MONTHLY_RATE)));
 }
 @Override
 public Transaction payInterest() {
     BigDecimal interest = calculateInterest();
     if (interest.signum() <=0) return null;
     return applyCredit(interest, TransactionType.INTEREST, "Money interest");
 }

}
