package org.poo.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bussines.Converter;
import org.poo.card.Card;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.transaction.AccountTransaction;
import org.poo.transaction.CardTransaction;
import org.poo.transaction.Transaction;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Account {
    private String iban;
    private String alias;
    private String currency;
    private String accountType;
    private double balance;
    private double minBalance;
    private ArrayList<Card> cards;
    private ArrayList<Transaction> transactions;
    private double spendingThreshold;

    public Account(final String iban, final String currency,
                   final String accountType, final double minBalance) {
        this.iban = iban;
        this.currency = currency;
        this.accountType = accountType;
        this.balance = 0;
        this.minBalance = minBalance;
        this.cards = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.spendingThreshold = 0;

    }

    public final String getIban() {
        return iban;
    }

    public final void setIban(final String iban) {
        this.iban = iban;
    }

    public final String getAlias() {
        return alias;
    }

    public final void setAlias(final String alias) {
        this.alias = alias;
    }

    public final String getCurrency() {
        return currency;
    }

    public final void setCurrency(final String currency) {
        this.currency = currency;
    }

    public final String getAccountType() {
        return accountType;
    }

    public final void setAccountType(final String accountType) {
        this.accountType = accountType;
    }

    public final double getBalance() {
        return balance;
    }

    public final void setBalance(final double balance) {
        this.balance = balance;
    }

    public final double getMinBalance() {
        return minBalance;
    }

    public final void setMinBalance(final double minBalance) {
        this.minBalance = minBalance;
    }

    public final ArrayList<Card> getCards() {
        return cards;
    }

    public double getSpendingThreshold() {
        return spendingThreshold;
    }


    public void addSpendingThreshold(double amount) {
        this.spendingThreshold = spendingThreshold + amount;
    }

    /**
     * Adds a transaction to the list of transactions associated with this account.
     *
     * @param transaction The transaction to be added.
     *
     */
    public final void addTransaction(final Transaction transaction) {
        transactions.add(transaction);
    }

    public final ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Adds a card to the list of cards associated with this account.
     *
     * @param card The card to be added.
     *
     */
    public final void addCard(final Card card) {
        this.cards.add(card);
    }

    /**
     * Adds a specified amount of funds to the account balance.
     *
     * @param amount The amount to be added to the account balance.
     *
     */
    public final void addFunds(final double amount) {
        this.balance += amount;
    }

    public final void removeFunds(final double amount) {
        this.balance -= amount;
    }

    /**
     * Removes all cards associated with this account.
     *
     * <p>This method clears the list of cards linked to the account, effectively
     * destroying them.</p>
     */
    public final void destroyCards() {
        cards.clear();
    }

    /**
     * Searches for a card associated with this account by its card number.
     *
     * @param cardNumber The card number to search for.
     * @return The {@link Card} object associated with the given card number,
     *         or {@code null} if no such card is found.
     */
    public final Card findCard(final String cardNumber) {
        for (Card card : cards) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        return null;
    }

    /**
     * Transfers money from this account to another account.
     *
     * <p>The amount is deducted from the balance of this account, and the
     * converted amount is added to the target account's balance.</p>
     *
     * @param amount The amount to be deducted from this account in its own currency.
     * @param convertedAmount The amount to be added to the target account in its currency.
     * @param account The target account to which the money will be transferred.
     */
    public final int sendMoney(final double amount, final double convertedAmount,
                                final Account account, final User user,
                               final CommandInput command, final Converter currencyConverter) {
        double amountInRon = currencyConverter.convert(currency,
                "RON", amount);
        int employeePosition = -1;
        int managerPosition = -1;
        User owner = user;
        if(Objects.equals(getAccountType(), "business")) {
            owner = ((Bussines)this).getOwner();
            employeePosition = ((Bussines)this).isEmployee(user);
            managerPosition = ((Bussines)this).isManager(user);
            if(employeePosition != -1 && ((Bussines)this).getSpendingLimit() < amount) {
                addTransaction(new AccountTransaction(
                        command.getTimestamp(), "LIMIT",
                        null, null, null, null,
                        null, null, null, -1));
                return -1;
            }
        }
        if(Objects.equals(owner.getServicePlan(), "standard")) {
            if(getBalance() - (amount + (amount * 0.2)/100) < 0) {
                addTransaction(new AccountTransaction(
                        command.getTimestamp(), "Insufficient funds",
                        null, null, null, null,
                        null, null, null, -1));
                return -1;
            }
            setBalance(getBalance() - (amount + (amount * 0.2)/100));
        } else if(Objects.equals(owner.getServicePlan(), "silver") && amountInRon >= 500) {
            if(getBalance() - (amount + (amount * 0.1)/100) < 0) {
                addTransaction(new AccountTransaction(
                        command.getTimestamp(), "Insufficient funds",
                        null, null, null, null,
                        null, null, null, -1));
                return -1;
            }
            setBalance(getBalance() - (amount + (amount * 0.1)/100));
            owner.setFee(owner.getFee() + 1);
            if(owner.getFee() == 5) {
                owner.setServicePlan("gold");
            }
        } else {
            setBalance(getBalance() - amount);
            if(amountInRon >= 300) {
                owner.setFee(owner.getFee() + 1);
                if(owner.getFee() == 5) {
                    owner.setServicePlan("gold");
                }
            }
        }
        if(employeePosition != -1) {
            double amountToAdd = ((Bussines)this).getSpendingEmployees().get(employeePosition) + amount;
            ((Bussines)this).setSpendingEmployee(employeePosition, amountToAdd);
        } else if(managerPosition != -1) {
            double amountToAdd = ((Bussines)this).getSpendingManagers().get(managerPosition) + amount;
            ((Bussines)this).setSpendingManager(managerPosition, amountToAdd);
        }
        if(account != null) {
            account.setBalance(account.getBalance() + convertedAmount);
        }
        return 0;
    }


    public final int withdrawSavings(final double amount, final double convertedAmount,
                               final Account account, final CommandInput command) {

        if(getBalance() - amount < 0) {
            addTransaction(new AccountTransaction(
                    command.getTimestamp(), "Insufficient funds",
                    null, null, null, null,
                    null, null, null, -1));
            return -1;
        }
        setBalance(getBalance() - amount);
        account.setBalance(account.getBalance() + convertedAmount);
        return 0;
    }

    /**
     * Generates a spendings report for a specific account within a given timestamp range.
     * This report includes details about transactions that occurred on the account
     * during the specified period and are relevant to spendings.
     *
     * @param account The account for which the spendings report is generated.
     * @param firstTimestamp The starting timestamp of the report's range (inclusive).
     * @param lastTimestamp The ending timestamp of the report's range (inclusive).
     * @param timestamp  The timestamp of the report generation command.
     * @param transactionsNode  The root node where the generated report will be added.
     * @param mapper  The ObjectMapper instance used for creating JSON nodes.
     * @return An ObjectNode containing the spendings report.
     */

    public abstract ObjectNode spendingsReport(Account account, int firstTimestamp,
                                               int lastTimestamp, int timestamp,
                                               ObjectNode transactionsNode, ObjectMapper mapper);

}
