package org.poo.card;

import org.poo.account.Account;
import org.poo.account.Bussines;
import org.poo.account.User;
import org.poo.business.Converter;
import org.poo.fileio.CommandInput;
import org.poo.transaction.CardTransaction;

import java.util.Objects;

public abstract class Card {
    private String cardNumber;
    private boolean active;
    private boolean frozen;
    private boolean warning;
    private boolean isUsed;
    private static final int CASHBACK_TWO = 2;
    private static final int CASHBACK_FIVE = 5;
    private static final int CASHBACK_TEN = 10;
    private static final int TOTAL_PERCENT = 100;
    private static final int LIMIT_FOR_COMISSION = 500;
    private static final int AMOUNT_FOR_GOLD = 300;
    private static final double COMISSION_ONE = 0.1;
    private static final double COMISSION_TWO = 0.2;
    private static final int MIN_FEE = 5;

    public Card(final String cardNumber) {
        this.cardNumber = cardNumber;
        this.active = true;
        this.frozen = false;
        this.warning = false;
    }

    public final String getCardNumber() {
        return cardNumber;
    }

    public final void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public final boolean isActive() {
        return active;
    }

    public final void setActive(final boolean active) {
        this.active = active;
    }

    public final boolean isFrozen() {
        return frozen;
    }

    public final void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }

    public final void setWarning(final boolean warning) {
        this.warning = warning;
    }

    /**
     * Checks if the card has been used for any transactions.
     *
     * @return {@code true} if the card has been used, {@code false} otherwise.
     */
    public boolean isUsed() {
        return isUsed;
    }


    /**
     * Retrieves the current status of the card.
     *
     * @return A string representing the status of the card:
     *         - "active" if the card is active,
     *         - "frozen" if the card is frozen,
     *         - "warning" if the card is in a warning state.
     */
    public String cardStatus() {
        if (active) {
            return "active";
        } else if (frozen) {
            return "frozen";
        } else {
            return "warning";
        }
    }

    /**
     * Handles the logic for making a payment using the card.
     *
     * @param user             The user making the payment.
     * @param convertedAmount  The amount to be paid, converted to the account's currency.
     * @param account          The account associated with the card.
     * @param command          The command input containing details of the payment.
     * @param email            The email of the user making the payment.
     * @param currency         The currency in which the payment is made.
     * @param currencyConverter The converter to handle currency conversion.
     * @return 1 if the user's service plan is upgraded to "gold", 0 otherwise.
     */
    public int pay(final User user, final double convertedAmount,
                    final Account account, final CommandInput command,
                    final String email, final String currency,
                    final Converter currencyConverter) {
        System.out.println("DIN SUMA " + account.getBalance());

        double amountInRon = currencyConverter.convert(currency,
                "RON", convertedAmount);

        User userFinal = user;
        if (Objects.equals(account.getAccountType(), "business")) {
            userFinal = ((Bussines) account).getOwner();
        }

        if (Objects.equals(userFinal.getServicePlan(), "standard")) {
            if (convertedAmount
                    + (convertedAmount * COMISSION_TWO) / TOTAL_PERCENT > account.getBalance()) {
                command.setDescription("Insufficient funds");
                account.addTransaction(new CardTransaction(
                        command.getTimestamp(), "Insufficient funds",
                        -1, null, getCardNumber(), email, account.getIban()));
                return 0;
            }
            account.setBalance(account.getBalance() - (convertedAmount
                    + (convertedAmount * COMISSION_TWO) / TOTAL_PERCENT));
            System.out.println("S-a platit " + (convertedAmount
                    + (convertedAmount * COMISSION_TWO) / TOTAL_PERCENT)
                    + " si au ramas " + account.getBalance() + "COM 2");
        } else if (Objects.equals(userFinal.getServicePlan(), "silver")
                && amountInRon >= LIMIT_FOR_COMISSION) {
            if (convertedAmount
                    + (convertedAmount * COMISSION_ONE) / TOTAL_PERCENT > account.getBalance()) {
                command.setDescription("Insufficient funds");
                account.addTransaction(new CardTransaction(
                        command.getTimestamp(), "Insufficient funds",
                        -1, null, getCardNumber(), email, account.getIban()));
                return 0;
            }
            account.setBalance(account.getBalance() - (convertedAmount
                    + (convertedAmount * COMISSION_ONE) / TOTAL_PERCENT));
            userFinal.setFee(userFinal.getFee() + 1);
            if (userFinal.getFee() == MIN_FEE
                    && !Objects.equals(userFinal.getServicePlan(), "gold")) {
                userFinal.setServicePlan("gold");
                return 1;
            }

        } else {
            account.setBalance(account.getBalance() - convertedAmount);
            if (amountInRon >= AMOUNT_FOR_GOLD) {
                userFinal.setFee(userFinal.getFee() + 1);
                if (userFinal.getFee() == MIN_FEE
                        && !Objects.equals(userFinal.getServicePlan(), "gold")) {
                    userFinal.setServicePlan("gold");
                    return 1;
                }
            }
        }
        return 0;
    }


    /**
     * Handles the logic for cash withdrawal using the card.
     *
     * @param user          The user withdrawing cash.
     * @param amount        The amount to be withdrawn.
     * @param amountInRon   The equivalent amount in RON.
     * @param account       The account from which cash is withdrawn.
     * @param command       The command input containing details of the withdrawal.
     */
    public void cashWithdrawal(final User user, final double amount, final double amountInRon,
                    final Account account, final CommandInput command) {

        if (user.getServicePlan().equals("standard")) {
            if (account.getBalance() - (amount
                    + (amount * COMISSION_TWO) / TOTAL_PERCENT) < 0) {
                account.addTransaction(new CardTransaction(command.getTimestamp(),
                        "Insufficient funds", -1, null,
                        null, null, null));
                return;
            }
            account.setBalance(account.getBalance() - (amount
                    + (amount * COMISSION_TWO) / TOTAL_PERCENT));
        } else if (user.getServicePlan().equals("silver")
                && amountInRon >= LIMIT_FOR_COMISSION) {
            if (account.getBalance() - (amount
                    + (amount * COMISSION_ONE) / TOTAL_PERCENT) < 0) {
                account.addTransaction(new CardTransaction(command.getTimestamp(),
                        "Insufficient funds", -1, null,
                        null, null, null));
                return;
            }
            account.setBalance(account.getBalance() - (amount
                    + (amount * COMISSION_ONE) / TOTAL_PERCENT));
            user.setFee(user.getFee() + 1);
            if (user.getFee() == MIN_FEE) {
                user.setServicePlan("gold");
            }
        } else {
            if (amountInRon >= AMOUNT_FOR_GOLD) {
                user.setFee(user.getFee() + 1);
                if (user.getFee() == MIN_FEE) {
                    user.setServicePlan("gold");
                }
            }
            account.setBalance(account.getBalance() - amount);
        }

        account.addTransaction(new CardTransaction(
                command.getTimestamp(), "Cash withdrawal of " + amountInRon,
                amountInRon, null, null, null, null));
    }

    /**
     * Handles the logic for processing an online payment using an account.
     *
     * @param account          The account from which the payment is being made.
     * @param command          The command input object containing payment details.
     * @param email            The email of the user making the payment.
     * @param convertedAmount  The amount to be paid, converted to the account's currency.
     */
    public abstract int payOnline(Account account, CommandInput command,
                                   String email, double convertedAmount,
                                   String currency, User user,
                                   Converter currencyConverter);
}
