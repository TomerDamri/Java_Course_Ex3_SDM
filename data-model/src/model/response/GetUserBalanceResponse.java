package model.response;

import java.util.UUID;

public class GetUserBalanceResponse {

    private UUID userId;
    private double balance;

    public GetUserBalanceResponse (UUID userId, double balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public UUID getUserId () {
        return userId;
    }

    public double getBalance () {
        return balance;
    }
}
