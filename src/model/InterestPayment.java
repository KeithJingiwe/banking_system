package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InterestPayment {
    private  final String paymentId = IdGenerator.next("INT" );
    private LocalDate runDate;
    private PaymentStatus status = PaymentStatus.SCHEDULED;
    private int accountProcessed;
    private BigDecimal totalPaid = Money.ZERO;
    private String failureReason;
    private final List<InterestBearing> pending = new ArrayList<>();

    public void execute( List<InterestBearing> accounts){
        if (status != PaymentStatus.SCHEDULED) {
            throw new IllegalStateException("Only a scheduled run can be executed (status: " + status + ").");
        }
        runDate = LocalDate.now();
        pending.addAll(accounts);
        process();

    }

    public void retry() {
        if (status != PaymentStatus.FAILED) {
            throw new IllegalStateException("Only a failed run can be retried (status: " + status + ").");
        }
        process();
    }

    public void cancel() {

        if (status == PaymentStatus.COMPLETED || status == PaymentStatus.CANCELLED) {
            throw new IllegalStateException("A " + status + " run cannot be cancelled.");
        }
        status = PaymentStatus.CANCELLED;
    }

    private void process() {
        status = PaymentStatus.RUNNING;
        Iterator<InterestBearing> it = pending.iterator();
        while (it.hasNext()) {
            try {
                Transaction t = it.next().payInterest();
                if (t != null) {
                    accountProcessed++;
                    totalPaid = totalPaid.add(t.getAmount());
                }
                it.remove();
            } catch (RuntimeException e) {
                failureReason = e.getMessage();
                status = PaymentStatus.FAILED;
                return;
            }
        }
        status = PaymentStatus.COMPLETED;
    }

    public String getPaymentId() { return paymentId; }
    public LocalDate getRunDate() { return runDate; }
    public PaymentStatus getStatus() { return status; }
    public int getAccountsProcessed() { return accountProcessed; }
    public BigDecimal getTotalPaid() { return totalPaid; }
    public String getFailureReason() { return failureReason; }

    @Override
    public String toString() {
        return String.format("InterestPayment %s | %s | accounts paid: %d | total: BWP %s",
                paymentId, status, accountProcessed, totalPaid);
    }
}

