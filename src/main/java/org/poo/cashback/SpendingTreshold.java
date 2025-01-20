package org.poo.cashback;

import org.poo.account.Account;
import org.poo.account.User;

import java.util.Objects;

/**
 * Implements the cashback strategy based on spending thresholds.
 * Provides cashback rewards depending on the transaction amount
 * and the user's service plan.
 */
public final class SpendingTreshold implements CashbackStrategy {
    private static final double COMISSION_ONE = 0.1;
    private static final double COMISSION_THREE = 0.3;
    private static final double COMISSION_FIVE = 0.5;
    private static final double COMISSION_TWO = 0.2;
    private static final double COMISSION_FOR = 0.4;
    private static final double COMISSION_SEVEN = 0.7;
    private static final double COMISSION_FIFTYFIVE = 0.55;
    private static final double COMISSION_TWENTYFIVE = 0.25;
    private static final double TOTAL_PERCENT = 100;
    private static final double FIRST_STEP = 100;
    private static final double SECOND_STEP = 300;
    private static final double THIRD_STEP = 500;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private SpendingTreshold() {
    }

    /**
     * Calculates and applies cashback to the user's account based on the
     * transaction amount and the user's service plan.
     *
     * @param transactionAmount The amount of the transaction in the user's account currency.
     * @param user              The {@link User} performing the transaction.
     * @param account           The {@link Account} from which the transaction is made.
     * @param convertedAmount   The transaction amount converted into a common currency (e.g., RON).
     */
    public static void calculateCashback(final double transactionAmount, final User user,
                                         final Account account, final double convertedAmount) {
        if (convertedAmount >= FIRST_STEP && convertedAmount < SECOND_STEP) {
            if (Objects.equals(user.getServicePlan(), "standard")
                    || Objects.equals(user.getServicePlan(), "student")) {
                account.setBalance(account.getBalance()
                        + (transactionAmount * COMISSION_ONE) / TOTAL_PERCENT);
            } else if (Objects.equals(user.getServicePlan(), "silver")) {
                account.setBalance(account.getBalance()
                        + (transactionAmount * COMISSION_THREE) / TOTAL_PERCENT);
            } else {
                account.setBalance(account.getBalance()
                        + (transactionAmount * COMISSION_FIVE) / TOTAL_PERCENT);
            }
        } else if (convertedAmount >= SECOND_STEP  && convertedAmount < THIRD_STEP) {
            if (Objects.equals(user.getServicePlan(), "standard")
                    || Objects.equals(user.getServicePlan(), "student")) {
                account.setBalance(account.getBalance()
                        + (transactionAmount * COMISSION_TWO) / TOTAL_PERCENT);
            } else if (Objects.equals(user.getServicePlan(), "silver")) {
                account.setBalance(account.getBalance()
                        + (transactionAmount * COMISSION_FOR) / TOTAL_PERCENT);
            } else {
                account.setBalance(account.getBalance()
                        + (transactionAmount * COMISSION_FIFTYFIVE) / TOTAL_PERCENT);
            }
        } else if (convertedAmount >= THIRD_STEP) {
            if (Objects.equals(user.getServicePlan(), "standard")
                    || Objects.equals(user.getServicePlan(), "student")) {
                account.setBalance(account.getBalance()
                        + (transactionAmount * COMISSION_TWENTYFIVE) / TOTAL_PERCENT);
            } else if (Objects.equals(user.getServicePlan(), "silver")) {
                account.setBalance(account.getBalance()
                        + (transactionAmount * COMISSION_FIVE) / TOTAL_PERCENT);
            } else {
                account.setBalance(account.getBalance()
                        + (transactionAmount * COMISSION_SEVEN) / TOTAL_PERCENT);
            }
        }
    }
}
