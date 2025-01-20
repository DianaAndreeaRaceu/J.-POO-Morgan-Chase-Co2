package org.poo.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommerciantInput;
import org.poo.transaction.Transaction;
import org.poo.utils.Utils;

public final class Classic extends Account {
    public Classic(final String iban, final String currency,
                   final String accountType, final double minBalance,
                   final CommerciantInput[] commerciants) {
        super(iban, currency, accountType, minBalance);
    }

    @Override
    public ObjectNode spendingsReport(final Account account, final int firstTimestamp,
                                      final int lastTimestamp, final int timestamp,
                                      final ObjectNode transactionsNode,
                                      final ObjectMapper mapper) {
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("balance", account.getBalance());

        ArrayNode commerciantsArray = Utils.getSortedCommerciants(account.getTransactions(),
                firstTimestamp, lastTimestamp, mapper);

        ArrayNode transactionsArray = mapper.createArrayNode();
        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getTimestamp() >= firstTimestamp
                    && transaction.getTimestamp() <= lastTimestamp) {
                ObjectNode transactionNode = mapper.createObjectNode();
                if (transaction.getCommerciant() != null) {
                    transaction.showTransactionSpendingReport(transaction,
                            transactionNode, transactionsArray);
                }
            }
        }
        outputNode.set("commerciants", commerciantsArray);
        outputNode.put("currency", account.getCurrency());
        outputNode.put("IBAN", getIban());
        outputNode.put("transactions", transactionsArray);
        transactionsNode.set("output", outputNode);
        transactionsNode.put("timestamp", timestamp);
        return transactionsNode;
    }
}
