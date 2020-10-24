package model.response;

import java.util.List;

import model.TransactionDTO;

public class GetUserTransactionsResponse {

    List<TransactionDTO> transactions;

    public GetUserTransactionsResponse (List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public List<TransactionDTO> getTransactions () {
        return transactions;
    }
}
