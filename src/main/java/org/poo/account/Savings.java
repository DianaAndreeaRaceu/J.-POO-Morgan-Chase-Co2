package org.poo.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommerciantInput;

public final class Savings extends Account {
    private double interestRate;

    public Savings(final String iban, final String currency,
                   final String accountType, final double minBalance,
                   final double interestRate, final CommerciantInput[] commerciants) {
        super(iban, currency, accountType, minBalance);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(final double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public ObjectNode spendingsReport(final Account account, final int firstTimestamp,
                                      final int lastTimestamp, final int timestamp,
                                      final ObjectNode transactionsNode,
                                      final ObjectMapper mapper) {
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("error", "This kind of report is not supported for a saving account");
        transactionsNode.set("output", outputNode);
        transactionsNode.put("timestamp", timestamp);
        return transactionsNode;
    }
}
