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
    private ArrayList<CommandInput> transactionRequest;
    private ArrayList<Card> bussinessCards;
    private ArrayList<Account> bussinessAccounts;
    private static final int MIN_AGE = 21;

    public User(final String firstName, final String lastName,
                final String email, final String birthDate, final String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = new ArrayList<>();
        this.birthDate = birthDate;
        this.occupation = occupation;
        if (Objects.equals(occupation, "student")) {
            servicePlan = "student";
        } else {
            servicePlan = "standard";
        }
        fee = 0;
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

    public void setServicePlan(final String servicePlan) {
        this.servicePlan = servicePlan;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(final double fee) {
        this.fee = fee;
    }


    public ArrayList<CommandInput> getTransactionRequest() {
        return transactionRequest;
    }

    public ArrayList<Card> getBussinessCards() {
        return bussinessCards;
    }

    /**
     * Adds a business card to the user's list of business cards.
     *
     * @param card The {@link Card} to be added as a business card.
     */
    public void addBussinessCard(final Card card) {
        this.bussinessCards.add(card);
    }

    public ArrayList<Account> getBussinessAccounts() {
        return bussinessAccounts;
    }

    /**
     * Adds a business account to the user's list of business accounts.
     *
     * @param account The {@link Account} to be added as a business account.
     */
    public void addBussinesAccount(final Account account) {
        this.bussinessAccounts.add(account);
    }

    /**
     * Checks if the user has a business card with the specified card number.
     *
     * @param cardNumber The card number to search for.
     * @return {@code true} if the user has a business card with the given number;
     *         {@code false} otherwise.
     */
    public boolean hasBussinessCard(final String cardNumber) {
        for (Card card : bussinessCards) {
            if (Objects.equals(card.getCardNumber(), cardNumber)) {
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

    /**
     * Searches for a classic account in the user's list of accounts by currency.
     *
     * @param currency The currency of the account to search for.
     * @return The {@link Account} with the specified currency and "classic" type,
     *         or {@code null} if no matching account is found.
     */
    public Account findTheClassicAccountForCurrency(final String currency) {
        for (Account account : accounts) {
            if (Objects.equals(account.getAccountType(), "classic")) {
                if (Objects.equals(account.getCurrency(), currency)) {
                    return account;
                }
            }
        }
        return null;
    }


    /**
     * Checks if the user is at least 21 years old based on their birthdate.
     *
     * <p>The method parses the user's birthdate and calculates the age in years.
     * If the user is 21 or older, the method returns {@code true}; otherwise, {@code false}.
     * If the birthdate is invalid or missing, the method also returns {@code false}.</p>
     *
     * @return {@code true} if the user is at least 21 years old; {@code false} otherwise.
     */
    public boolean hasAtLeast21Years() {
        try {
            String birthDateString = getBirthDate();

            if (birthDateString == null || birthDateString.isEmpty()) {
                return false;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate birthdate = LocalDate.parse(getBirthDate(), formatter);

            LocalDate currentDate = LocalDate.now();

            Period age = Period.between(birthdate, currentDate);

            return age.getYears() >= MIN_AGE;
        } catch (DateTimeException e) {
            return false;
        }
    }

    /**
     * Adds a transaction command to the user's list of pending transaction requests.
     *
     * @param command The {@link CommandInput} object representing the transaction to be added.
     */
    public void addCommandToAccept(final CommandInput command) {
        transactionRequest.add(command);
    }

    /**
     * Removes the first transaction command from the user's list of pending transaction requests.
     */
    public void removeCommand() {
        transactionRequest.removeFirst();
    }
}
