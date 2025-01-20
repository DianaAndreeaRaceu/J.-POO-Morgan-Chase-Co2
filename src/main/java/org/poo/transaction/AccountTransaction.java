package org.poo.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

public final class AccountTransaction extends Transaction{
    private String senderIban;
    private String receiverIban;
    private String transferType;
    private String amount;
    private String planType;
    private String currency;
    private double amountDoubleType;

    public AccountTransaction(final int timestamp, final String description,
                              final String amount, final String commerciant,
                              final String iban, final String receiverIban,
                              final String transferType, final String planType,
                              final String currency, final double amountDoubleType) {
        super(timestamp, description, commerciant);
        this.senderIban = iban;
        this.receiverIban = receiverIban;
        this.transferType = transferType;
        this.amount = amount;
        this.planType = planType;
        this.currency = currency;
        this.amountDoubleType = amountDoubleType;

    }

    public String getSenderIban() {
        return senderIban;
    }

    public String getReceiverIban() {
        return receiverIban;
    }

    public String getTransferType() {
        return transferType;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public void showTransaction(
            final Transaction transaction, final ObjectNode transactionNode,
            final ObjectMapper mapper) {
        if(planType == null) {
            if (((AccountTransaction) transaction).getReceiverIban() != null
                    && ((AccountTransaction) transaction).getSenderIban() != null
                    && !Objects.equals(transferType, "withdrawal")) {
                transactionNode.put("senderIBAN", ((AccountTransaction) transaction).getSenderIban());
                transactionNode.put("receiverIBAN",
                        ((AccountTransaction) transaction).getReceiverIban());
                transactionNode.put("amount", amount);
                transactionNode.put("transferType",
                        ((AccountTransaction) transaction).getTransferType());
            }
            if(Objects.equals(transferType, "interestAdd")) {
                transactionNode.put("amount", amountDoubleType);
                transactionNode.put("currency", currency);
            } else if(Objects.equals(transferType, "withdrawal")) {
                transactionNode.put("amount", amountDoubleType);
                transactionNode.put("classicAccountIBAN", receiverIban);
            }
            transactionNode.put("description", transaction.getDescription());
            if(Objects.equals(transferType, "withdrawal")) {
                transactionNode.put("savingsAccountIBAN", senderIban);
            }
        } else {
            transactionNode.put("accountIBAN", senderIban);
            transactionNode.put("description", transaction.getDescription());
            transactionNode.put("newPlanType", planType);
        }
        transactionNode.put("timestamp", transaction.getTimestamp());
    }

    @Override
    public void showTransactionSpendingReport(
            final Transaction transaction, final ObjectNode transactionNode,
            final ArrayNode transactionArray) {
    }
}
