package model;

import java.time.LocalDate;

public class TransactionDTO {

    private TransactionTypeDTO operationType;
    private LocalDate date;
    private Double transactionAmount;
    private Double balanceBeforeTransaction;
    private Double balanceAfterTransaction;

    public TransactionDTO (TransactionTypeDTO operationType,
                           LocalDate date,
                           Double transactionAmount,
                           Double balanceBeforeTransaction,
                           Double balanceAfterTransaction) {
        this.operationType = operationType;
        this.date = date;
        this.transactionAmount = transactionAmount;
        this.balanceBeforeTransaction = balanceBeforeTransaction;
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public TransactionTypeDTO getOperationType () {
        return operationType;
    }

    public LocalDate getDate () {
        return date;
    }

    public Double getTransactionAmount () {
        return transactionAmount;
    }

    public Double getBalanceBeforeTransaction () {
        return balanceBeforeTransaction;
    }

    public Double getBalanceAfterTransaction () {
        return balanceAfterTransaction;
    }
}
