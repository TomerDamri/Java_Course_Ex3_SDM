package model.request;

import java.util.UUID;

public class GetUserBalanceRequest extends AccountRequest {

    public GetUserBalanceRequest (UUID userId) {
        super(userId);
    }

    @Override
    public UUID getUserId () {
        return super.getUserId();
    }
}
