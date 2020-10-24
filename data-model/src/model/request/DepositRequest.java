package model.request;

import java.util.UUID;

public class DepositRequest extends AccountRequest {

    private double amount;

    public DepositRequest (UUID userId, double amount) {
        super(userId);
        this.amount = amount;
    }

    @Override
    public UUID getUserId () {
        return super.getUserId();
    }

    public double getAmount () {
        return amount;
    }
}
