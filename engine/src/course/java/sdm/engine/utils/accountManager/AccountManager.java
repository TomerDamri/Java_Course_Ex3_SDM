package course.java.sdm.engine.utils.accountManager;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import course.java.sdm.engine.mapper.DTOMapper;
import course.java.sdm.engine.model.TransactionType;
import model.TransactionDTO;

public class AccountManager {
    private static AccountManager singletonAccountManager = null;
    Map<UUID, UserAccount> userIdToUserAccount;

    public static AccountManager getAccountManager () {
        if (singletonAccountManager == null) {
            singletonAccountManager = new AccountManager();
        }

        return singletonAccountManager;
    }

    public synchronized void createAccountForUser (UUID userId) {
        validateNoAccountExistForUser(userId);

        UserAccount newAccount = new UserAccount(UUID.randomUUID(), userId);
        userIdToUserAccount.put(userId, newAccount);
    }

    public synchronized UserAccount removeAccountForUser (UUID userId) {
        validateAccountExistForUser(userId);

        return userIdToUserAccount.remove(userId);
    }

    public synchronized Double getUserBalance (UUID userId) {
        validateAccountExistForUser(userId);
        return userIdToUserAccount.get(userId).getBalance();
    }

    public synchronized List<TransactionDTO> getUserTransactions (UUID userId) {
        validateAccountExistForUser(userId);

        DTOMapper dtoMapper = DTOMapper.getDTOMapper();
        List<Transaction> transactions = userIdToUserAccount.get(userId).getTransactions();

        return transactions.stream()
                           .map(tran -> dtoMapper.toTransactionDTO(tran.getOperationType(),
                                                                   tran.getDate(),
                                                                   tran.getTransactionAmount(),
                                                                   tran.getBalanceBeforeTransaction(),
                                                                   tran.getBalanceAfterTransaction()))
                           .collect(Collectors.toList());
    }

    public void deposit (UUID userId, double amount) {
        validateAccountExistForUser(userId);
        updateBalanceForUser(TransactionType.DEPOSIT, amount, userId, LocalDate.now());
    }

    public void transfer (UUID fromUserId, double amount, UUID toUserId, LocalDate transferDate) {
        validateAccountExistForUser(fromUserId);
        validateAccountExistForUser(toUserId);
        updateBalanceForUser(TransactionType.NEGATIVE_TRANSFER, amount * (-1), fromUserId, transferDate);
        updateBalanceForUser(TransactionType.POSITIVE_TRANSFER, amount, toUserId, transferDate);
    }

    private AccountManager () {
        this.userIdToUserAccount = new HashMap<>();
    }

    private synchronized void updateBalanceForUser (TransactionType transactionType, double amount, UUID userId, LocalDate transferDate) {
        // get user account
        UserAccount userAccount = userIdToUserAccount.get(userId);
        // update balance
        double prevBalance = userAccount.getBalance();
        userAccount.setBalance(prevBalance + amount);
        // add transaction for user
        addTransactionToUserAccount(transactionType, amount, prevBalance, userAccount, transferDate);
    }

    private void addTransactionToUserAccount (TransactionType transactionType,
                                              double amount,
                                              double prevBalance,
                                              UserAccount userAccount,
                                              LocalDate transferDate) {
        Transaction newTransaction = new Transaction(transactionType, transferDate, amount, prevBalance, userAccount.getBalance());
        userAccount.getTransactions().add(newTransaction);
    }

    private void validateAccountExistForUser (UUID userId) {
        if (!userIdToUserAccount.containsKey(userId)) {
            throw new RuntimeException(String.format("The user with ID: '%s' don't have user account", userId));
        }
    }

    private void validateNoAccountExistForUser (UUID userId) {
        if (userIdToUserAccount.containsKey(userId)) {
            throw new RuntimeException(String.format("The user with ID: '%s' already has user account", userId));
        }
    }

    private class UserAccount {

        private UUID accountId;
        private UUID userId;
        private double balance;
        private List<Transaction> transactions;

        public UserAccount (UUID accountId, UUID userId) {
            this.accountId = accountId;
            this.userId = userId;
            this.balance = 0;
            this.transactions = new LinkedList<>();
        }

        public UUID getAccountId () {
            return accountId;
        }

        public UUID getUserId () {
            return userId;
        }

        public double getBalance () {
            return balance;
        }

        public List<Transaction> getTransactions () {
            return transactions;
        }

        public void setBalance (double balance) {
            this.balance = balance;
        }
    }

    private class Transaction {

        private TransactionType operationType;
        private LocalDate date;
        private Double transactionAmount;
        private Double balanceBeforeTransaction;
        private Double balanceAfterTransaction;

        public Transaction (TransactionType operationType,
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

        public TransactionType getOperationType () {
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
}
