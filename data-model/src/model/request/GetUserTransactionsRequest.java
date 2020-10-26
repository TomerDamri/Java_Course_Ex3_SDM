package model.request;

import java.util.UUID;

public class GetUserTransactionsRequest extends AccountRequest{
    
    public GetUserTransactionsRequest(UUID userId) {
        super(userId);
    }

    @Override
    public UUID getUserId() {
        return super.getUserId();
    }
}
