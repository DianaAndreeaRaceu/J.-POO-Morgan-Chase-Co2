package org.poo.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Transaction {
    private int timestamp;
    private String description;
    private String commerciant;

    public Transaction(final int timestamp, final String description,
                       final String commerciant) {
        this.timestamp = timestamp;
        this.description = description;
        this.commerciant = commerciant;
    }

    public final int getTimestamp() {
        return timestamp;
    }

    public final String getDescription() {
        return description;
    }

    public final String getCommerciant() {
        return commerciant;
    }

    /**
     * Populates the provided JSON node with details of a transaction.
     *
     * @param transaction     The transaction to be represented in JSON format.
     * @param transactionNode The JSON node to populate with transaction details.
     * @param mapper          The object mapper used for creating JSON objects.
     */
    public abstract void showTransaction(
            Transaction transaction, ObjectNode transactionNode,
            ObjectMapper mapper);

    /**
     * Populates the provided JSON node with details of a transaction for a spending report
     * and adds it to the specified transaction array.
     *
     * @param transaction       The transaction to be included in the spending report.
     * @param transactionNode   The JSON node to populate with transaction details.
     * @param transactionArray  The array to which the transaction node will be added.
     */
    public abstract void showTransactionSpendingReport(
            Transaction transaction, ObjectNode transactionNode,
            ArrayNode transactionArray);
}
