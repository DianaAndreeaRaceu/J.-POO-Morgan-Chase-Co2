package org.poo.card;

import org.poo.account.Account;
import org.poo.account.Bussines;
import org.poo.account.User;
import org.poo.bussines.Bank;
import org.poo.bussines.Converter;
import org.poo.fileio.CommandInput;
import org.poo.transaction.CardTransaction;

import java.util.Objects;

public abstract class Card {
    private String cardNumber;
    private boolean active;
    private boolean frozen;
    private boolean warning;
    private boolean isUsed;

    public Card(final String cardNumber) {
        this.cardNumber = cardNumber;
        this.active = true;
        this.frozen = false;
        this.warning = false;
        this.isUsed = false;
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

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
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

    public void pay(User user, final double convertedAmount,
                    final Account account, final CommandInput command,
                    final String email, final String currency,
                    final Converter currencyConverter) {

        double amountInRon = currencyConverter.convert(currency,
                "RON", convertedAmount);

        if(Objects.equals(account.getAccountType(), "business")) {
            user = ((Bussines)account).getOwner();
        }

        if(Objects.equals(user.getServicePlan(), "standard")) {
            if(convertedAmount + (convertedAmount * 0.2)/100 > account.getBalance()) {
                command.setDescription("Insufficient funds");
                account.addTransaction(new CardTransaction(
                        command.getTimestamp(), "Insufficient funds",
                        -1, null, getCardNumber(), email, account.getIban()));
                return;
            }
            account.setBalance(account.getBalance() - (convertedAmount + (convertedAmount * 0.2)/100));
        } else if(Objects.equals(user.getServicePlan(), "silver") && amountInRon >= 500) {
            if(convertedAmount + (convertedAmount * 0.1)/100 > account.getBalance()) {
                command.setDescription("Insufficient funds");
                account.addTransaction(new CardTransaction(
                        command.getTimestamp(), "Insufficient funds",
                        -1, null, getCardNumber(), email, account.getIban()));
                return;
            }
            account.setBalance(account.getBalance() - (convertedAmount + (convertedAmount * 0.1)/100));
            user.setFee(user.getFee() + 1);
            if(user.getFee() == 5) {
                user.setServicePlan("gold");
            }

        } else {
            account.setBalance(account.getBalance() - convertedAmount);
        }
    }


    public void cashWithdrawal(final User user, final double amount, final double amountInRon,
                    final Account account, final CommandInput command) {

        if(user.getServicePlan().equals("standard")) {
            if(account.getBalance() - (amount + (amount * 0.2)/100) < 0) {
                return;
            }
            account.setBalance(account.getBalance() - (amount + (amount * 0.2)/100));
        } else if(user.getServicePlan().equals("silver") && amountInRon >= 500) {
            if(account.getBalance() - (amount + (amount * 0.1)/100) < 0) {
                return;
            }
            account.setBalance(account.getBalance() - (amount+ (amount * 0.1)/100));
            user.setFee(user.getFee() + 1);
            if(user.getFee() == 5) {
                user.setServicePlan("gold");
            }
        } else {
            if(amountInRon >= 300) {
                user.setFee(user.getFee() + 1);
                if(user.getFee() == 5) {
                    user.setServicePlan("gold");
                }
            }
            account.setBalance(account.getBalance() - amount);
        }

        account.addTransaction(new CardTransaction(
                command.getTimestamp(), "Cash withdrawal of " + amountInRon,
                amountInRon, null, null, null, null));
        System.out.println("S-au retras " + amount + " si au ramas " + account.getBalance());
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
                                   final String currency, final User user,
                                   final Converter currencyConverter);
}
