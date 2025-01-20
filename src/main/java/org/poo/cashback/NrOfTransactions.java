package org.poo.cashback;

import org.poo.account.Account;
import org.poo.account.User;
import org.poo.business.Commerciant;

import java.util.Objects;

/**
 * Implements a cashback strategy based on the number of transactions per merchant.
 * Cashback is awarded for specific thresholds of transactions in categories like
 * "Food," "Clothes," or "Tech."
 */
public final class NrOfTransactions implements CashbackStrategy {

    private static final int CASHBACK_TWO = 2;
    private static final int CASHBACK_FIVE = 5;
    private static final int CASHBACK_TEN = 10;
    private static final int TOTAL_PERCENT = 100;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private NrOfTransactions() {

    }

    /**
     * Calculates and applies cashback based on the number of transactions
     * a user has made with a specific merchant in a certain category.
     *
     * @param transactionAmount The amount of the transaction.
     * @param user              The {@link User} performing the transaction.
     * @param account           The {@link Account} to which cashback may be applied.
     * @param merchantName      The name of the merchant where the transaction occurred.
     * @param type              The category of the transaction (e.g., "Food," "Clothes," "Tech").
     */
    public static void calculateCashback(final double transactionAmount, final User user,
                                         final Account account, final String merchantName,
                                         final String type) {
        if (Objects.equals(user.getEmail(), "Jeffrey_Gonzales@hotmail.us")) {
            System.out.println("!!!!!!!!!!!!!!!!!!!"
                    + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                    + "!!!Jeffrey se calculeaza daca are cashback " + merchantName);
        }

        if (Objects.equals(type, "Food") && account.canApplyTwoCashBack()) {
            account.setBalance(account.getBalance()
                    + (transactionAmount * CASHBACK_TWO) / TOTAL_PERCENT);
            account.setCashbackReceivedTwo(true);
            if (Objects.equals(user.getEmail(), "Jeffrey_Gonzales@hotmail.us")) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!"
                        + "!!!!!!!!!!!!!!!!!!!!!!!!!!Jeffrey are cashback 2 " + merchantName);
            }
        }
        if (Objects.equals(type, "Clothes") && account.canApplyFiveCashBack()) {
            account.setBalance(account.getBalance()
                    + (transactionAmount * CASHBACK_FIVE) / TOTAL_PERCENT);
            account.setCashbackReceivedFive(true);
            if (Objects.equals(user.getEmail(), "Jeffrey_Gonzales@hotmail.us")) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!"
                        + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Jeffrey are cashback 5 " + merchantName);
            }

        }
        if (Objects.equals(type, "Tech") && account.canApplyTenCashBack()) {
            System.out.println("Transaction Amount " + transactionAmount);
            System.out.println("Comisionul de adaugat : "
                    + ((transactionAmount * CASHBACK_TEN) / TOTAL_PERCENT));
            System.out.println("REZULTA " + (account.getBalance()
                    + (transactionAmount * CASHBACK_TEN) / TOTAL_PERCENT));
            account.setBalance(account.getBalance()
                    + (transactionAmount * CASHBACK_TEN) / TOTAL_PERCENT);
            account.setCashbackReceivedTen(true);
            if (Objects.equals(user.getEmail(), "Jeffrey_Gonzales@hotmail.us")) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                        + "!!!!!!!!!!!Jeffrey are cashback 10 " + merchantName);

            }

        }
        int positionCommerciant = account.findPositionForCommerciant(merchantName);
        if (positionCommerciant == -1) {
            account.addCommerciantNrOfTr(new Commerciant(merchantName, type));
        } else {
            account.setTransactionForCommerciant(positionCommerciant);
        }

    }
}
