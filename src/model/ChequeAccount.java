package model;

import java.math.BigDecimal;

public class ChequeAccount extends Account {
  private final Employer employer;

  public ChequeAccount(String accountNumber, String branch, Customer owner, Employer employer) {
      super(accountNumber, branch, owner);
      boolean employed = owner instanceof IndividualCustomer ind && ind.isEmployed();
      if (!employed || employer == null) {
          throw new IllegalStateException("A cheque account can only be opened for an employed customer with employer details.");
      }
      this.employer = employer;

  }

  @Override
  public boolean withdraw(BigDecimal amount) { return debit(amount); }
    @Override
    public String getAccountType() { return "Cheque"; }

    public Employer getEmployer() { return employer; }
}

