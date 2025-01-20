package org.poo.cashback;

import org.poo.account.Account;
import org.poo.account.User;

/**
 * Defines the contract for cashback strategies.
 *
 * <p>This interface should be implemented by classes that provide specific cashback
 * calculation mechanisms based on different criteria (e.g., number of transactions,
 * spending thresholds).</p>
 */
public interface CashbackStrategy {

    /**
     * Calculates and applies cashback for a transaction based on the strategy's rules.
     *
     * @param transactionAmount The amount of the transaction for which cashback is calculated.
     * @param user              The {@link User} performing the transaction.
     * @param account           The {@link Account} associated with the transaction.
     * @param type              The category or type of the transaction (e.g., "Food," "Clothes").
     */
    static void calculateCashback(double transactionAmount,
                                  User user, Account account, String type) {
    }
}
