package model;

import java.math.BigDecimal;

public interface Transactable {
    boolean deposit(BigDecimal amount);
    boolean withdraw(BigDecimal amount);
}
