package model.request;

import java.time.LocalDate;
import java.util.UUID;

public class DepositRequest extends AccountRequest {

    private double amount;
    private LocalDate date;

    public LocalDate getDate () {
        return date;
    }

    public DepositRequest (UUID userId, double amount, LocalDate date) {
        super(userId);
        this.amount = amount;
        this.date = date;
    }

    @Override
    public UUID getUserId () {
        return super.getUserId();
    }

    public double getAmount () {
        return amount;
    }
}
