package org.poo.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class CardTransaction extends Transaction{
    private String cardNumber;
    private String cardHolder;
    private double amount;
    private String account;

    public CardTransaction(final int timestamp, final String description,
                           final double amount, final String commerciant,
                           final String cardNumber, final String cardHolder,
                           final String account) {
        super(timestamp, description, commerciant);
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.amount = amount;
        this.account = account;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public double getAmount() {
        return amount;
    }

    public String getAccount() {
        return account;
    }

    @Override
    public void showTransaction(
            final Transaction transaction, final ObjectNode transactionNode,
            final ObjectMapper mapper) {
        if (((CardTransaction) transaction).getAmount() > 0) {
            transactionNode.put("amount",
                    ((CardTransaction) transaction).getAmount());
            if(((CardTransaction) transaction).getAccount() != null) {
                transactionNode.put("commerciant", transaction.getCommerciant());
            }
        } else if (((CardTransaction) transaction).getAmount() == 0) {
            transactionNode.put("account", ((CardTransaction) transaction).getAccount());
            transactionNode.put("card", ((CardTransaction) transaction).getCardNumber());
            transactionNode.put("cardHolder", ((CardTransaction) transaction).getCardHolder());
        }
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
    }

    @Override
    public void showTransactionSpendingReport(
            final Transaction transaction, final ObjectNode transactionNode,
            final ArrayNode transactionsArray) {
        transactionNode.put("amount", ((CardTransaction) transaction).getAmount());
        transactionNode.put("commerciant", transaction.getCommerciant());
        transactionNode.put("description", transaction.getDescription());
        transactionNode.put("timestamp", transaction.getTimestamp());
        transactionsArray.add(transactionNode);
    }
}
