package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final String TransactionID;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final BigDecimal balanceAfter;
    private final String description;

    public Transaction(TransactionType type, BigDecimal amount, BigDecimal balanceAfter, String description) {
        this(IdGenerator.next("TXN"), type, amount, LocalDateTime.now(), balanceAfter, description);
    }

public Transaction(String TransactionID, TransactionType type, BigDecimal amount, LocalDateTime timestamp, BigDecimal balanceAfter, String description) {
    this.TransactionID = TransactionID;
    this.type = type;
    this.amount = Money.round (amount);
    this.timestamp = timestamp;
    this.balanceAfter = Money.round(balanceAfter);
    this.description = description;

}

public String generateReceipt(){
    return String.join("\n",
            "-----------RECEIPT-------------",
            "Transaction : " + TransactionID,
            "Date/Time   : " + timestamp.format(FORMAT),
            "Type        : " + type,
            "Amount      : BWP " + amount,
            "Balance     :BWP " + balanceAfter,
            "Note        : " + description,
            "--------------------------------");
}

public String getTransactionID() {
    return TransactionID;}
    public TransactionType getType() {return type;}
    public BigDecimal getAmount() {return amount;}
    public LocalDateTime getTimestamp() {return timestamp;}
    public BigDecimal getBalanceAfter() {return balanceAfter;}
    public String getDescription() {return description;}


    @Override
    public String toString() {
       return String.format("%s | %-10s | BWP %10s | Balance BWP %10s | %s",
               timestamp.format(FORMAT), type, amount, balanceAfter, description);
    }


}
