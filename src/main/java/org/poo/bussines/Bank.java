package org.poo.bussines;

import org.poo.account.Account;
import org.poo.account.Bussines;
import org.poo.account.User;
import org.poo.card.Card;
import org.poo.fileio.ExchangeInput;

import java.util.ArrayList;
import java.util.Objects;

public final class Bank {
    private ArrayList<User> users;
    private Converter currencyConverter;
    private Transactions transactions;

    public Bank(final ArrayList<ExchangeInput> rates) {
        this.users = new ArrayList<>();
        this.currencyConverter = new Converter(rates);
        this.transactions = new Transactions(this, currencyConverter);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public Transactions getTransactions() {
        return transactions;
    }

    /**
     * Adds a new user to the bank.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @param email     The email of the user.
     */
    public void addUser(final String firstName,
                        final String lastName, final String email,
                        final  String birthDate, final String occupation) {
        User user = new User(firstName, lastName, email, birthDate, occupation);
        users.add(user);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user to find.
     * @return The User object if found, or {@code null} if no user with the given email exists.
     */
    public User findUser(final String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    public User findUserByIban(final String iban) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if(iban.equals(account.getIban())) {
                    return user;
                }
            }
        }
        return null;
    }

    public Account findAccountByIban(final String iban) {
        User user = findUserByIban(iban);
        if(user == null) {
            return null;
        }
        return user.findAccountForCurrentUser(iban);
    }

    public Account findJobAccount(final String iban, final String email) {
        User user = findUser(email);
        if(user == null) {
            return null;
        }
        for (Account account : user.getBussinessAccounts()) {
            if(Objects.equals(account.getIban(), iban)) {
                return account;
            }
        }
        return null;
    }

    public Account findAccountByCardNumber(final String cardNumber, User user) {
        for (Account account : user.getAccounts()) {
            for (Card card : account.getCards()) {
                if(Objects.equals(cardNumber, card.getCardNumber())) {
                    return account;
                }
            }
        }
        return null;
    }

    public Account findJobAccountByCardNumber(final String cardNumber, User user) {
        for (Card card : user.getBussinessCards()) {
            if(Objects.equals(card.getCardNumber(), cardNumber)) {
                for (User secondUser : users) {
                    for (Account account : secondUser.getAccounts()) {
                        for (Card secondCard : account.getCards()) {
                            if(Objects.equals(secondCard.getCardNumber(), cardNumber)) {
                                return account;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public Account findBussinessAccount(User user, String cardNumber) {
        for (Account account : user.getBussinessAccounts()) {
            for (Card verifCard : account.getCards()) {
                if(Objects.equals(verifCard.getCardNumber(), cardNumber)) {
                    return account;
                }
            }
        }
        return null;
    }

}
