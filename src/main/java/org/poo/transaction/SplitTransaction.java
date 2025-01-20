package org.poo.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public final class SplitTransaction extends Transaction {
    private double amount;
    private String currency;
    private ArrayList<String> involved;
    private String errorIban;
    private ArrayList<Boolean> responses;
    private List<Double> amountForUsers;
    private boolean isFullyAccepted;
    private String error;

    public SplitTransaction(final int timestamp, final String description,
                            final String commerciant, final double amount,
                            final String currency, final List<String> accounts,
                            final String errorIban, final List<Double> amountForUsers,
                            final String error) {
        super(timestamp, description, commerciant);
        this.amount = amount;
        this.currency = currency;
        this.involved = (ArrayList<String>) accounts;
        this.errorIban = errorIban;
        this.responses = new ArrayList<>();
        this.amountForUsers = amountForUsers;
        this.isFullyAccepted = false;
        this.error = error;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public ArrayList<String> getInvolvedAccounts() {
        return involved;
    }

    public String getErrorIban() {
        return errorIban;
    }

    public boolean isFullyAccepted() {
        return isFullyAccepted;
    }

    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    /**
     * Adds a response to the transaction from a participant.
     *
     * @param response The response from the participant: {@code true} for acceptance,
     *                 {@code false} for rejection.
     */
    public void addResponse(final boolean response) {
        if (response) {
            responses.add(true);
            if (responses.size() == involved.size()) {
               isFullyAccepted = true;
            }
        } else {
            System.out.println("S-a primit un refuz");
        }
    }

    @Override
    public void showTransaction(final Transaction transaction,
                                final ObjectNode transactionNode, final ObjectMapper mapper) {

        if (amountForUsers != null) {
            ArrayNode amountArray = mapper.createArrayNode();
            for (Double amountfinder : amountForUsers) {
                amountArray.add(amountfinder);
            }
            transactionNode.set("amountForUsers", amountArray);
        } else {
            transactionNode.put("amount", ((SplitTransaction) transaction).getAmount());
        }

        transactionNode.put("currency", ((SplitTransaction) transaction).getCurrency());
        transactionNode.put("description", transaction.getDescription());
        if (((SplitTransaction) transaction).getErrorIban() != null) {
            transactionNode.put("error", "Account "
                    + ((SplitTransaction) transaction).getErrorIban()
                    + " has insufficient funds for a split payment.");
        }

        if (error != null) {
            transactionNode.put("error", error);
        }

        ArrayList<String> involvedAccounts = ((SplitTransaction) transaction).getInvolvedAccounts();
        ArrayNode accountsArray = mapper.createArrayNode();
        for (String involvedAccount : involvedAccounts) {
            accountsArray.add(involvedAccount);
        }
        transactionNode.set("involvedAccounts", accountsArray);
        if (amountForUsers != null) {
            transactionNode.put("splitPaymentType", "custom");
        } else {
            transactionNode.put("splitPaymentType", "equal");
        }
        transactionNode.put("timestamp", transaction.getTimestamp());
    }

    @Override
    public void showTransactionSpendingReport(final Transaction transaction,
                                              final ObjectNode transactionNode,
                                              final ArrayNode transactionArray) {
    }
}
