package org.poo.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.business.Commerciant;
import org.poo.business.Converter;
import org.poo.card.Card;
import org.poo.fileio.CommandInput;
import org.poo.transaction.AccountTransaction;
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
    private final ArrayList<Card> cards;
    private final ArrayList<Transaction> transactions;
    private double spendingThreshold;

    private final ArrayList<Integer> transactionsPerCommerciant;
    private final ArrayList<Commerciant> commerciants;

    private boolean cashbackReceivedFive;
    private boolean cashbackReceivedTwo;
    private boolean cashbackReceivedTen;

    private static final int CASHBACK_TWO = 2;
    private static final int CASHBACK_FIVE = 5;
    private static final int CASHBACK_TEN = 10;
    private static final int TOTAL_PERCENT = 100;
    private static final int LIMIT_FOR_COMISSION = 500;
    private static final int AMOUNT_FOR_GOLD = 300;
    private static final double COMISSION_ONE = 0.1;
    private static final double COMISSION_TWO = 0.2;
    private static final int MIN_FEE = 5;


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
        this.transactionsPerCommerciant = new ArrayList<>();
        this.commerciants = new ArrayList<>();
        cashbackReceivedTwo = false;
        cashbackReceivedFive = false;
        cashbackReceivedTen = false;

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

    public final double getSpendingThreshold() {
        return spendingThreshold;
    }

    public final void setCashbackReceivedFive(final boolean cashbackReceivedFive) {
        this.cashbackReceivedFive = cashbackReceivedFive;
    }

    public final void setCashbackReceivedTen(final boolean cashbackReceivedTen) {
        this.cashbackReceivedTen = cashbackReceivedTen;
    }

    public final void setCashbackReceivedTwo(final boolean cashbackReceivedTwo) {
        this.cashbackReceivedTwo = cashbackReceivedTwo;
    }

    /**
     * Adds a new commerciant with a single transaction for tracking.
     *
     * @param commerciant The commerciant to add.
     */
    public final void addCommerciantNrOfTr(final Commerciant commerciant) {
        commerciants.add(commerciant);
        transactionsPerCommerciant.add(1);
    }

    /**
     * Updates the transaction count for a specific commerciant by position.
     *
     * @param position The position of the commerciant in the list.
     */
    public final void setTransactionForCommerciant(final int position) {
        transactionsPerCommerciant.set(position,
                transactionsPerCommerciant.get(position) + 1);
    }


    /**
     * Finds the position of a commerciant in the list based on its name.
     *
     * @param commerciant The name of the commerciant to search for.
     * @return The position of the commerciant in the list, or -1 if not found.
     */
    public int findPositionForCommerciant(final String commerciant) {
        int pos = 0;
        for (Commerciant comm : commerciants) {
            if (Objects.equals(comm.getName(), commerciant)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }


    /**
     * Checks if a 2% cashback can be applied based on Food transactions.
     *
     * @return True if cashback can be applied, false otherwise.
     */
    public boolean canApplyTwoCashBack() {
        int pos = 0;
        for (Commerciant commerciant : commerciants) {
            if (Objects.equals(commerciant.getType(), "Food")
                    && transactionsPerCommerciant.get(pos) == CASHBACK_TWO
                    && !cashbackReceivedTwo) {
                return true;
            }
            pos++;
        }
        return false;
    }

    /**
     * Checks if a 5% cashback can be applied based on Clothes transactions.
     *
     * @return True if cashback can be applied, false otherwise.
     */
    public boolean canApplyFiveCashBack() {
        int pos = 0;
        for (Commerciant commerciant : commerciants) {
            if (Objects.equals(commerciant.getType(), "Clothes")
                    && transactionsPerCommerciant.get(pos) == CASHBACK_FIVE
                    && !cashbackReceivedFive) {
                return true;
            }
            pos++;
        }
        return false;
    }


    /**
     * Checks if a 10% cashback can be applied based on Tech transactions.
     *
     * @return True if cashback can be applied, false otherwise.
     */
    public boolean canApplyTenCashBack() {
        int pos = 0;
        System.out.println("Comerciantii " + commerciants);
        System.out.println("Tranzactii " + transactionsPerCommerciant);
        for (Commerciant commerciant : commerciants) {
            if (Objects.equals(commerciant.getType(), "Tech")) {
                System.out.println("VEDE UN COMERCIANT TECH");
                if (transactionsPerCommerciant.get(pos) == CASHBACK_TEN
                        && !cashbackReceivedTen) {
                    System.out.println("RETURNEAZA TRUE");
                    return true;
                }
            }
            pos++;
        }
        return false;
    }

    /**
     * Increments the spending threshold of the account by a specified amount.
     *
     * @param amount The amount to add to the spending threshold.
     */
    public void addSpendingThreshold(final double amount) {
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

    /**
     * Deducts a specified amount from the account balance.
     *
     * @param amount The amount to be deducted from the account balance.
     */
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
        if (Objects.equals(getAccountType(), "business")) {
            owner = ((Bussines) this).getOwner();
            employeePosition = ((Bussines) this).isEmployee(user);
            managerPosition = ((Bussines) this).isManager(user);
            if (employeePosition != -1 && ((Bussines) this).getSpendingLimit() < amount) {
                addTransaction(new AccountTransaction(
                        command.getTimestamp(), "LIMIT",
                        null, null, null, null,
                        null, null, null, -1));
                return -1;
            }
        }
        if (Objects.equals(owner.getServicePlan(), "standard")) {
            if (getBalance() - (amount + (amount * COMISSION_TWO) / TOTAL_PERCENT) < 0) {
                addTransaction(new AccountTransaction(
                        command.getTimestamp(), "Insufficient funds",
                        null, null, null, null,
                        null, null, null, -1));
                return -1;
            }
            setBalance(getBalance() - (amount + (amount * COMISSION_TWO) / TOTAL_PERCENT));
        } else if (Objects.equals(owner.getServicePlan(), "silver")
                && amountInRon >= LIMIT_FOR_COMISSION) {
            if (getBalance() - (amount + (amount * COMISSION_ONE) / TOTAL_PERCENT) < 0) {
                addTransaction(new AccountTransaction(
                        command.getTimestamp(), "Insufficient funds",
                        null, null, null, null,
                        null, null, null, -1));
                return -1;
            }
            setBalance(getBalance() - (amount + (amount * COMISSION_ONE) / TOTAL_PERCENT));
            owner.setFee(owner.getFee() + 1);
            if (owner.getFee() == MIN_FEE && !Objects.equals(user.getServicePlan(), "gold")) {
                owner.setServicePlan("gold");
                System.out.println(owner.getEmail());
                this.addTransaction(new AccountTransaction(command.getTimestamp(),
                        "Upgrade plan",
                        null, null, iban, null,
                        null, "gold", null, -1));
            }
        } else {
            setBalance(getBalance() - amount);
            if (amountInRon >= AMOUNT_FOR_GOLD) {
                owner.setFee(owner.getFee() + 1);
                if (owner.getFee() == MIN_FEE && !Objects.equals(user.getServicePlan(), "gold")) {
                    owner.setServicePlan("gold");
                    this.addTransaction(new AccountTransaction(command.getTimestamp(),
                            "Upgrade plan",
                            null, null, iban, null,
                            null, "gold", null, -1));
                }
            }
        }
        if (employeePosition != -1) {
            double amountToAdd = ((Bussines) this).getSpendingEmployees().get(employeePosition)
                    + amount;
            ((Bussines) this).setSpendingEmployee(employeePosition, amountToAdd);
        } else if (managerPosition != -1) {
            double amountToAdd = ((Bussines) this).getSpendingManagers().get(managerPosition)
                    + amount;
            ((Bussines) this).setSpendingManager(managerPosition, amountToAdd);
        }
        if (account != null) {
            account.setBalance(account.getBalance() + convertedAmount);
        }
        return 0;
    }

    /**
     * Withdraws funds from the account and transfers them to another account.
     *
     * <p>The method checks if the current account has sufficient balance. If yes, the specified
     * amount is deducted from the current account, and the converted amount is added to the
     * target account.</p>
     *
     * @param amount         The amount to be withdrawn from this account.
     * @param convertedAmount The equivalent amount to be added to the target account
     *                        in its currency.
     * @param account        The target account where the funds will be transferred.
     * @param command        The command input object containing transaction details.
     * @return 0 if the transaction is successful, -1 if there are insufficient funds.
     */
    public final int withdrawSavings(final double amount, final double convertedAmount,
                               final Account account, final CommandInput command) {

        if (getBalance() - amount < 0) {
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
