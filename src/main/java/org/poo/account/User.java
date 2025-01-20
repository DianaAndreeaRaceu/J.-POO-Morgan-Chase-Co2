package org.poo.account;

import org.poo.card.Card;
import org.poo.fileio.CommandInput;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;
    private String occupation;
    private ArrayList<Account> accounts;
    private String servicePlan;
    private double fee;
    private int nrOfTransactions;
    private boolean cashbackReceivedFive;
    private boolean cashbackReceivedTwo;
    private boolean isCashbackReceivedTen;
    private ArrayList<CommandInput> transactionRequest;
    private ArrayList<Card> bussinessCards;
    private ArrayList<Account> bussinessAccounts;

    public User(final String firstName, final String lastName,
                final String email, final String birthDate, final String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = new ArrayList<>();
        this.birthDate = birthDate;
        this.occupation = occupation;
        if(Objects.equals(occupation, "student")) {
            servicePlan = "student";
        } else {
            servicePlan = "standard";
        }
        fee = 0;
        nrOfTransactions = 0;
        cashbackReceivedTwo = false;
        cashbackReceivedFive = false;
        isCashbackReceivedTen = false;
        transactionRequest = new ArrayList<>();
        bussinessCards = new ArrayList<>();
        bussinessAccounts = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getServicePlan() {
        return servicePlan;
    }

    public void setServicePlan(String servicePlan) {
        this.servicePlan = servicePlan;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getNrOfTransactions() {
        return nrOfTransactions;
    }

    public void setNrOfTransactions(int nrOfTransactions) {
        this.nrOfTransactions = nrOfTransactions;
    }

    public boolean isCashbackReceivedFive() {
        return cashbackReceivedFive;
    }

    public boolean isCashbackReceivedTwo() {
        return cashbackReceivedTwo;
    }

    public boolean isCashbackReceivedTen() {
        return isCashbackReceivedTen;
    }

    public void setCashbackReceivedFive(boolean cashbackReceivedFive) {
        this.cashbackReceivedFive = cashbackReceivedFive;
    }

    public void setCashbackReceivedTen(boolean cashbackReceivedTen) {
        isCashbackReceivedTen = cashbackReceivedTen;
    }

    public void setCashbackReceivedTwo(boolean cashbackReceivedTwo) {
        this.cashbackReceivedTwo = cashbackReceivedTwo;
    }

    public ArrayList<CommandInput> getTransactionRequest() {
        return transactionRequest;
    }

    public ArrayList<Card> getBussinessCards() {
        return bussinessCards;
    }

    public void addBussinessCard(Card card) {
        this.bussinessCards.add(card);
    }

    public ArrayList<Account> getBussinessAccounts() {
        return bussinessAccounts;
    }

    public void addBussinesAccount(Account account) {
        this.bussinessAccounts.add(account);
    }

    public boolean hasBussinessCard(String cardNumber) {
        for (Card card : bussinessCards) {
            if(Objects.equals(card.getCardNumber(), cardNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an account to the user's list of accounts.
     *
     * @param account The {@link Account} to be added. Must not be null.
     */
    public void addAccount(final Account account) {
        this.accounts.add(account);
    }

    /**
     * Removes an account from the user's list of accounts.
     *
     * @param account The {@link Account} to be removed.
     */
    public void deleteAccount(final Account account) {
        accounts.remove(account);
    }

    /**
     * Searches for an account in the user's list of accounts by its IBAN.
     *
     * @param iban The IBAN of the account to search for.
     * @return The {@link Account} with the specified IBAN, or {@code null} if no matching
     *         account is found.
     */
    public Account findAccountForCurrentUser(final String iban) {
        for (Account account : accounts) {
            if (account.getIban().equals(iban)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Searches for an account in the user's list of accounts by its alias.
     *
     * @param alias The alias of the account to search for.
     * @return The {@link Account} with the specified alias, or {@code null} if no matching
     *         account is found.
     */
    public Account findAccountForCurrentUserAlias(final String alias) {
        for (Account account : accounts) {
            if (account.getAlias() != null
                    && account.getAlias().equals(alias)) {
                return account;
            }
        }
        return null;
    }

    public Account findTheClassicAccountForCurrency(String currency) {
        for (Account account : accounts) {
            if(Objects.equals(account.getAccountType(), "classic")) {
                if(Objects.equals(account.getCurrency(), currency)) {
                    return account;
                }
            }
        }
        return null;
    }

    public boolean hasAtLeast21Years() {
        try {
            String birthDateString = getBirthDate();

            if (birthDateString == null || birthDateString.isEmpty()) {
                return false;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate birthDate = LocalDate.parse(getBirthDate(), formatter);

            LocalDate currentDate = LocalDate.now();

            Period age = Period.between(birthDate, currentDate);

            if (age.getYears() >= 21) {
                return true;
            } else {
                return false;
            }
        } catch (DateTimeException e) {
            return false;
        }
    }

    public void addCommandToAccept(CommandInput command) {
        transactionRequest.add(command);
    }

    public void removeCommand() {
        transactionRequest.removeFirst();
    }
}
