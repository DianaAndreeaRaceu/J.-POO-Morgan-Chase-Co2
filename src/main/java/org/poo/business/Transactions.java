package org.poo.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.account.*;
import org.poo.card.Card;
import org.poo.card.ClassicCard;
import org.poo.card.OneTimeCard;
import org.poo.cashback.NrOfTransactions;
import org.poo.cashback.SpendingTreshold;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.transaction.AccountTransaction;
import org.poo.transaction.CardTransaction;
import org.poo.transaction.SplitTransaction;
import org.poo.transaction.Transaction;
import org.poo.utils.Utils;

import java.text.DecimalFormat;
import java.util.*;

public final class Transactions {
    private Bank bank;
    private Converter currencyConverter;
    private static final int WARNING_DIFFERENCE = 30;
    private ArrayList<SplitTransaction> splitCustomTransactions = new ArrayList<>();
    private static final int FEE_ONE = 100;
    private static final int FEE_TWO = 250;
    private static final int FEE_THREE = 350;


    public Transactions(final Bank bank, final Converter currencyConverter) {
        this.bank = bank;
        this.currencyConverter = currencyConverter;
    }

    /**
     * Adds a new account for a user.
     *
     * @param email       The email of the user to whom the account will be added.
     * @param currency    The currency of the new account.
     * @param accountType The type of the account ("classic" or "savings").
     * @param timestamp   The timestamp for the transaction.
     */
    public void addAccount(final String email, final String currency,
                           final String accountType, final int timestamp,
                           final CommerciantInput[] commerciants, final double interestRate) {
        User user = bank.findUser(email);
        if (user == null) {
            return;
        }
        String iban = Utils.generateIBAN();
        System.out.println("IBAN: " + iban + " pentru " + email);
        Account account;
        if ("classic".equals(accountType)) {
            account = new Classic(iban, currency, accountType, 0, commerciants);
        } else if ("savings".equals(accountType)) {
            account = new Savings(iban, currency, accountType, 0,
                    interestRate, commerciants);
        } else {
            account = new Bussines(iban, currency, accountType, 0,
                    user, currencyConverter);
        }
        account.addTransaction(new AccountTransaction(timestamp,
                "New account created", null,
                null, null, null, null,
                null, null, -1));
        user.addAccount(account);
        System.out.println("Cont creat pentru " + email + " " + iban);
    }

    /**
     * Deletes an account for a user.
     *
     * @param email     The email of the user whose account is being deleted.
     * @param iban      The IBAN of the account to be deleted.
     * @param timestamp The timestamp for the transaction.
     * @param command   The command input object to store the result of the operation.
     */
    public void deleteAccount(final String email, final String iban,
                              final int timestamp, final CommandInput command) {
        User user = bank.findUser(email);
        if (user == null) {
            command.setDescription("User not found");
            return;
        }

        Account account = user.findAccountForCurrentUser(iban);
        if (account == null) {
            command.setDescription("Account not found");
            return;
        }

        if (Objects.equals(account.getAccountType(), "business")
                && !Objects.equals(user.getEmail(), ((Bussines) account).getOwner().getEmail())) {
            account.addTransaction(new AccountTransaction(timestamp,
                    "You are not authorized to make this transaction.",
                    null, null, null, null,
                    null, null, null, -1));
            command.setDescription(
                    "You are not authorized to make this transaction.");
            return;
        }


        if (account.getBalance() != 0) {
            account.addTransaction(new AccountTransaction(timestamp,
                    "Account couldn't be deleted - there are funds remaining",
                    null, null, null, null,
                    null, null, null, -1));
            command.setDescription(
                    "Account couldn't be deleted - see org.poo.transactions for details");
            return;
        }
        account.addTransaction(new AccountTransaction(timestamp, "Account deleted",
                null, null, null, null, null,
                null, null, -1));
        account.destroyCards();
        user.deleteAccount(account);
        command.setDescription("Account deleted");
    }

    /**
     * Adds funds to an account.
     *
     * @param iban      The IBAN of the account to which funds are added.
     * @param amount    The amount to add.
     *
     */
    public void addFunds(final String iban, final double amount,
                         final String email) {
        int employeePosition = -1;
        int managerPosition = -1;
        for (User user : bank.getUsers()) {
            for (Account account : user.getAccounts()) {
                if (account.getIban().equals(iban)) {
                    if (Objects.equals(account.getAccountType(), "business")) {
                        User secondUser = bank.findUser(email);
                        if (secondUser == null) {
                            return;
                        }
                        if (Objects.equals(secondUser.getEmail(),
                                ((Bussines) account).getOwner().getEmail())) {
                            account.addFunds(amount);
                            return;
                        }
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                                + secondUser.getEmail());
                        employeePosition = ((Bussines) account).isEmployee(secondUser);
                        managerPosition = ((Bussines) account).isManager(secondUser);
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                                + employeePosition + " " + managerPosition);
                        if (employeePosition != -1
                                && amount > ((Bussines) account).getDepositLimit()) {
                            return;
                        }
                        if (employeePosition == -1 && managerPosition == -1
                                && !Objects.equals(secondUser.getEmail(), user.getEmail())) {
                            return;
                        }
                    }
                    account.addFunds(amount);
                    if (Objects.equals(account.getIban(), "RO53POOB7122855990652257")) {
                        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!S-a adaugat "
                                + amount);
                    }
                    if (employeePosition != -1) {
                        double amountToAdd =
                                ((Bussines) account).getDepositEmployees().get(employeePosition)
                                        + amount;
                        ((Bussines) account).setDepositEmployee(employeePosition, amountToAdd);
                    } else if (managerPosition != -1) {
                        double amountToAdd =
                                ((Bussines) account).getDepositManagers().get(managerPosition)
                                        + amount;
                        ((Bussines) account).setDepositManager(managerPosition, amountToAdd);
                    }
                }
            }
        }
    }


    /**
     * Creates a new card for an account.
     *
     * @param iban      The IBAN of the account for which the card is created.
     * @param email     The email of the user who owns the account.
     * @param timestamp The timestamp for the transaction.
     */
    public void createCard(final String iban, final String email,
                           final int timestamp) {

        Account account = bank.findAccountByIban(iban);
        if (account == null) {
            account = bank.findJobAccount(iban, email);
            if (account == null) {
                return;
            }
        }
        Card newCard = new ClassicCard(Utils.generateCardNumber());
        account.addCard(newCard);
        account.addTransaction(new CardTransaction(timestamp,
                "New card created",
                    0, null, newCard.getCardNumber(),
                email, account.getIban()));

        User user = bank.findUser(email);
        if (user == null) {
            return;
        }

        if (Objects.equals(account.getAccountType(), "business")) {
            int employeePosition = ((Bussines) account).isEmployee(user);
            if (employeePosition != -1) {
                user.addBussinessCard(newCard);
            }
        }
        System.out.println("Card creat pentru " + email + " " + newCard.getCardNumber());
    }

    /**
     * Creates a one-time card for an account.
     *
     * @param iban      The IBAN of the account for which the card is created.
     * @param email     The email of the user who owns the account.
     * @param timestamp The timestamp for the transaction.
     */
    public void createOneTimeCard(final String iban, final String email,
                                  final int timestamp) {
        User user = bank.findUser(email);
        if (user == null) {
            return;
        }
        Account account = user.findAccountForCurrentUser(iban);
        if (account == null) {
            account = bank.findJobAccount(iban, email);
            if (account == null) {
                return;
            }
        }
        OneTimeCard newCard = new OneTimeCard(Utils.generateCardNumber());
        account.addCard(newCard);
        account.addTransaction(new CardTransaction(timestamp, "New card created",
                    0, null, newCard.getCardNumber(), email, account.getIban()));

        if (Objects.equals(account.getAccountType(), "business")) {
            int employeePosition = ((Bussines) account).isEmployee(user);
            if (employeePosition != -1) {
                user.addBussinessCard(newCard);
            }
        }
        System.out.println("Card OneTIME creat pentru " + email + " " + newCard.getCardNumber());
    }

    /**
     * Deletes a card from an account.
     *
     * @param cardNumber The number of the card to be deleted.
     * @param timestamp  The timestamp for the transaction.
     */
    public void deleteCard(final String cardNumber, final int timestamp,
                           final String email) {
        User user = bank.findUser(email);
        if (user == null) {
            return;
        }
        Account account = bank.findAccountByCardNumber(cardNumber, user);
        if (account == null) {
            account = bank.findJobAccountByCardNumber(cardNumber, user);
            if (account == null) {
                return;
            }
        }
        Card card = account.findCard(cardNumber);

        if (Objects.equals(account.getAccountType(), "business")) {
            int employeePosition = ((Bussines) account).isEmployee(user);
            if (employeePosition != -1 && !user.hasBussinessCard(cardNumber)) {
                System.out.println("ACCES refuzat");
                return;
            }
        }
        if (card != null) {
            account.addTransaction(new CardTransaction(
                    timestamp, "The card has been destroyed",
                    0, null, cardNumber, user.getEmail(), account.getIban()));
            account.getCards().remove(card);
        }
    }

    /**
     * Handles an online payment using a card.
     *
     * @param cardNumber The card number used for the payment.
     * @param amount     The amount to be paid.
     * @param email      The email of the user initiating the payment.
     * @param currency   The currency in which the payment is made.
     * @param command    The command input object to store the result of the operation.
     */
    public void payOnline(final String cardNumber, final double amount,
                          final String email, final String currency,
                          final CommandInput command, final CommerciantInput[] commerciants) {
        User user = bank.findUser(email);
        if (user == null) {
            command.setDescription("User not found");
            return;
        }

        if (amount == 0) {
            return;
        }
        Account account = bank.findAccountByCardNumber(cardNumber, user);
        if (account == null) {
            account = bank.findBussinessAccount(user, cardNumber);
            if (account == null) {
                command.setDescription("Card not found");
                return;
            }
        }
        Card card = account.findCard(cardNumber);
        if (card == null) {
            command.setDescription("Card not found");
            return;
        }
        double amountInRon = currencyConverter.convert(currency, "RON", amount);
        double convertedAmount = currencyConverter.convert(
                currency, account.getCurrency(), amount);
        int paid = card.payOnline(account, command, email,
                convertedAmount, currency, user, currencyConverter);
        if (paid == -1) {
            return;
        }

        CommerciantInput commerciant =
                Utils.getCommerciantByName(command.getCommerciant(), commerciants);
        if (commerciant == null) {
            return;
        }


        if (Objects.equals(account.getAccountType(), "business")) {
            if (!Objects.equals(((Bussines) account).getOwner().getEmail(), user.getEmail())) {
                Commerciant commerciantResult =
                        ((Bussines) account).findCommerciant(commerciant.getCommerciant());
                if (commerciantResult == null) {
                    commerciantResult = new Commerciant(commerciant.getCommerciant(),
                            commerciant.getType());
                    ((Bussines) account).addCommerciant(commerciantResult);
                }
                commerciantResult.addAmount(convertedAmount);
                if (((Bussines) account).isEmployee(user) != -1) {
                    commerciantResult.addEmployee(user);
                } else if (((Bussines) account).isManager(user) != -1) {
                    commerciantResult.addManager(user);
                }
            }
        }

        if (Objects.equals(commerciant.getCashbackStrategy(), "nrOfTransactions")) {
            if (Objects.equals(account.getAccountType(), "business")) {
                NrOfTransactions.calculateCashback(convertedAmount, ((Bussines) account).getOwner(),
                        account, commerciant.getCommerciant(), commerciant.getType());
            } else {
                NrOfTransactions.calculateCashback(convertedAmount, user, account,
                        commerciant.getCommerciant(), commerciant.getType());
            }

        } else {
            int position = Utils.getPositionForCommerciant(command.getCommerciant(), commerciants);
            if (position != -1) {
                account.addSpendingThreshold(amountInRon);
                if (Objects.equals(account.getAccountType(), "business")) {
                    SpendingTreshold.calculateCashback(convertedAmount,
                            ((Bussines) account).getOwner(),
                            account, account.getSpendingThreshold());
                } else {
                    SpendingTreshold.calculateCashback(convertedAmount,
                            user, account, account.getSpendingThreshold());
                }

            }
        }
    }

    /**
     * Transfers money between accounts.
     *
     * @param account   The IBAN of the sender's account.
     * @param amount    The amount to transfer.
     * @param receiver  The IBAN or alias of the receiver's account.
     * @param command   The command input object to store the result of the operation.
     */
    public void sendMoney(final String account, final double amount,
                          final String receiver, final CommandInput command,
                          final CommerciantInput[] commerciants, final String email) {
        Account senderAccount = null;
        Account receiverAccount = null;
        User senderUser = bank.findUser(email);
        for (User user : bank.getUsers()) {
            Account senderAux = user.findAccountForCurrentUser(account);
            Account receiverAux = user.findAccountForCurrentUser(receiver);
            Account receiverAuxAlias = user.findAccountForCurrentUserAlias(receiver);
            if (senderAux != null) {
                senderAccount = senderAux;
                if (Objects.equals(senderAccount.getAccountType(), "business")
                        && !Objects.equals(((Bussines) senderAccount).getOwner().getEmail(),
                        user.getEmail())) {
                    int employeePosition = ((Bussines) senderAccount).isEmployee(user);
                    int managerPosition = ((Bussines) senderAccount).isManager(user);
                    if (employeePosition == -1 && managerPosition == -1) {
                        return;
                    }
                }
            }
            if (receiverAux != null) {
                receiverAccount = receiverAux;
            }
            if (receiverAuxAlias != null) {
                receiverAccount = receiverAuxAlias;
            }
        }

        if (senderUser == null) {
            command.setDescription("User not found");
            return;
        }

        CommerciantInput commerciant = Utils.getCommerciantByAccount(receiver, commerciants);

        if (receiverAccount == null && commerciant == null) {
            command.setDescription("User not found");
            return;
        }

        if (senderAccount == null) {
            return;
        }

        if (amount > senderAccount.getBalance()) {
            senderAccount.addTransaction(new AccountTransaction(
                    command.getTimestamp(), "Insufficient funds",
                    null, null, null, null,
                    null, null, null, -1));
            return;
        }

        if (receiverAccount != null) {
            double convertedAmount = currencyConverter.convert(senderAccount.getCurrency(),
                    receiverAccount.getCurrency(), amount);

            int result = senderAccount.sendMoney(amount, convertedAmount, receiverAccount,
                    senderUser, command, currencyConverter);

            if (result != -1) {
                senderAccount.addTransaction(new AccountTransaction(
                        command.getTimestamp(), command.getDescription(),
                        amount + " " + senderAccount.getCurrency(),
                        command.getCommerciant(), account, receiver,
                        "sent", null, null, amount));

                receiverAccount.addTransaction(new AccountTransaction(
                        command.getTimestamp(), command.getDescription(),
                        convertedAmount + " " + receiverAccount.getCurrency(),
                        command.getCommerciant(), account, receiver, "received",
                        null, null, convertedAmount));
            }

        } else {
            int result = senderAccount.sendMoney(amount, -1, null,
                    senderUser, command, currencyConverter);
            if (result == -1) {
                return;
            }
            if (Objects.equals(senderAccount.getAccountType(), "business")
                    && ((Bussines) senderAccount).getOwner() != senderUser) {
                Commerciant commerciantResult =
                        ((Bussines) senderAccount).findCommerciant(commerciant.getCommerciant());
                if (commerciantResult == null) {
                    commerciantResult = new Commerciant(commerciant.getCommerciant(),
                            commerciant.getType());
                    ((Bussines) senderAccount).addCommerciant(commerciantResult);
                }

                commerciantResult.addAmount(amount);
                if (((Bussines) senderAccount).isEmployee(senderUser) != -1) {
                    commerciantResult.addEmployee(senderUser);
                } else if (((Bussines) senderAccount).isManager(senderUser) != -1) {
                    commerciantResult.addManager(senderUser);
                }
            }

            senderAccount.addTransaction(new AccountTransaction(
                    command.getTimestamp(), command.getDescription(),
                    amount + " " + senderAccount.getCurrency(),
                    command.getCommerciant(), account, receiver,
                    "sent", null, null, amount));

            if (Objects.equals(commerciant.getCashbackStrategy(), "nrOfTransactions")) {
                if (Objects.equals(senderAccount.getAccountType(), "business")) {
                    NrOfTransactions.calculateCashback(amount,
                            ((Bussines) senderAccount).getOwner(),
                            senderAccount, commerciant.getCommerciant(), commerciant.getType());
                } else {
                    NrOfTransactions.calculateCashback(amount, senderUser,
                            senderAccount, commerciant.getCommerciant(), commerciant.getType());
                }

            } else {
                double convertedAmount = currencyConverter.convert(
                        "RON", senderAccount.getCurrency(), amount);
                double amountInRon = currencyConverter.convert(
                        senderAccount.getCurrency(), "RON", amount);
                senderAccount.addSpendingThreshold(amountInRon);
                if (Objects.equals(senderAccount.getAccountType(), "business")) {
                    SpendingTreshold.calculateCashback(convertedAmount,
                            ((Bussines) senderAccount).getOwner(), senderAccount, amountInRon);
                } else {
                    SpendingTreshold.calculateCashback(convertedAmount,
                            senderUser, senderAccount, amountInRon);
                }

            }
        }

    }

    /**
     * Sets the minimum balance for an account.
     *
     * @param amount The minimum balance amount to set.
     * @param iban   The IBAN of the account.
     */
    public void setMinBalance(final double amount, final String iban) {
        for (User user : bank.getUsers()) {
            Account account = user.findAccountForCurrentUser(iban);
            if (account != null) {
                account.setMinBalance(amount);
            }
        }
    }

    /**
     * Sets an alias for an account.
     *
     * @param iban   The IBAN of the account.
     * @param email  The email of the user who owns the account.
     * @param alias  The alias to set for the account.
     */
    public void setAlias(final String iban, final String email, final String alias) {
        User user = bank.getTransactions().bank.findUser(email);
        if (user != null) {
            Account account = user.findAccountForCurrentUser(iban);
            if (account != null) {
                account.setAlias(alias);
            }
        }
    }

    /**
     * Checks the status of a card and updates its status based on account balance.
     *
     * @param cardNumber The card number to check.
     * @param timestamp  The timestamp for the transaction.
     * @param command    The command input object to store the result of the operation.
     */
    public void checkCardStatus(final String cardNumber, final int timestamp,
                                final CommandInput command) {
        for (User user : bank.getUsers()) {
            for (Account account : user.getAccounts()) {
                Card card = account.findCard(cardNumber);
                if (card != null) {
                    command.setDescription("Card found");
                    double balanceDifference = account.getBalance() - account.getMinBalance();

                    if (account.getBalance() <= account.getMinBalance()) {
                        card.setFrozen(true);
                        card.setActive(false);
                        card.setWarning(false);
                        account.addTransaction(new CardTransaction(timestamp,
                                "You have reached the minimum amount "
                                        + "of funds, the card will be frozen",
                                -1, null, null,
                                null, null));
                    } else if (balanceDifference <= WARNING_DIFFERENCE
                            && account.getMinBalance() != 0) {
                        card.setWarning(true);
                        card.setFrozen(false);
                        card.setActive(false);
                        account.addTransaction(new CardTransaction(timestamp,
                                "Warning",
                                -1, null, null,
                                null, null));
                    }
                    return;
                }
            }
        }
        command.setDescription("Card not found");
    }

    /**
     * Splits a payment equally among multiple accounts.
     *
     * @param accounts   A list of IBANs representing the accounts involved in the split payment.
     * @param amount     The total amount to be split equally among the accounts.
     * @param currency   The currency in which the payment is made.
     * @param timestamp  The timestamp of the split payment transaction.
     */
    public void splitPaymentForEqual(final List<String> accounts, final double amount,
                                     final String currency, final int timestamp) {
        String errorIban = null;
        for (String account : accounts) {
            for (User user : bank.getUsers()) {
                Account userAccount = user.findAccountForCurrentUser(account);
                if (userAccount != null) {
                    double convertedAmount = currencyConverter.convert(currency,
                            userAccount.getCurrency(), amount / accounts.size());
                    if (userAccount.getBalance() - convertedAmount <= userAccount.getMinBalance()) {
                        errorIban = userAccount.getIban();
                        break;
                    }
                }
            }
        }
        for (String account : accounts) {
            for (User user : bank.getUsers()) {
                Account userAccount = user.findAccountForCurrentUser(account);
                if (userAccount != null) {
                    if (errorIban != null) {
                        userAccount.addTransaction(new SplitTransaction(timestamp,
                                "Split payment of " + formatDoubleToTwoDecimals(amount)
                                        + " " + currency, null, amount / accounts.size(),
                                currency, accounts, errorIban, null, null));
                    } else {
                        double convertedAmount = currencyConverter.convert(currency,
                                userAccount.getCurrency(), amount / accounts.size());
                        userAccount.setBalance(userAccount.getBalance() - convertedAmount);
                        userAccount.addTransaction(new SplitTransaction(timestamp,
                                "Split payment of " + formatDoubleToTwoDecimals(amount)
                                        + " " + currency, null, amount / accounts.size(),
                                currency, accounts, null, null, null));
                    }
                }
            }
        }
    }

    /**
     * Splits a payment among multiple accounts.
     *
     * @param accounts The list of account IBANs involved in the split payment.
     * @param amount   The total amount to split.
     * @param currency The currency of the payment.
     * @param command  The command input object to store the result of the operation.
     */
    public void splitPayment(final List<String> accounts, final double amount,
                             final String currency, final CommandInput command) {
        splitCustomTransactions.add(new SplitTransaction(command.getTimestamp(),
                null, null, amount, command.getCurrency(),
                command.getAccounts(), null, command.getAmountForUsers(), null));
        for (String iban : accounts) {
            User user = bank.findUserByIban(iban);
            if (user != null) {
                user.addCommandToAccept(command);
            } else {
                System.out.println("User negasit");
                return;
            }
        }
    }

    /**
     * Finds a split transaction based on the provided timestamp.
     *
     * @param timestamp The timestamp of the transaction to find.
     * @return The SplitTransaction object if found, or {@code null} if not found.
     */
    public SplitTransaction findTransaction(final int timestamp) {
        for (SplitTransaction transaction : splitCustomTransactions) {
            if (timestamp == transaction.getTimestamp()) {
                return transaction;
            }
        }
        return null;
    }

    /**
     * Processes a custom split payment among multiple accounts.
     *
     * @param accounts   The list of IBANs for the involved accounts.
     * @param amounts    The list of amounts to be paid by each account.
     * @param currency   The currency of the transaction.
     * @param timestamp  The timestamp of the transaction.
     * @param amount     The total amount of the split payment.
     */
    public void paymentSplitCustom(final List<String> accounts, final List<Double> amounts,
                                   final String currency, final int timestamp,
                                   final double amount) {
        String errorIban = null;
        int amountPosition = 0;
        for (String iban : accounts) {
            User user = bank.findUserByIban(iban);
            if (user != null) {
                Account account = user.findAccountForCurrentUser(iban);
                if (account != null) {
                    double convertedAmount = currencyConverter.convert(currency,
                            account.getCurrency(), amounts.get(amountPosition));
                    amountPosition++;
                    if (account.getBalance() - convertedAmount <= account.getMinBalance()) {
                        errorIban = account.getIban();
                        System.out.println((account.getBalance() - convertedAmount)
                                + " <= " + account.getMinBalance());
                        break;
                    }
                }
            }
        }
        amountPosition = 0;
        for (String iban : accounts) {
            for (User user : bank.getUsers()) {
                Account account = user.findAccountForCurrentUser(iban);
                if (account != null) {
                    if (errorIban != null) {
                        account.addTransaction(new SplitTransaction(timestamp,
                                "Split payment of " + formatDoubleToTwoDecimals(amount)
                                        + " " + currency, null, -1,
                                currency, accounts, errorIban, amounts, null));
                    } else {
                        double convertedAmount = currencyConverter.convert(currency,
                                account.getCurrency(), amounts.get(amountPosition));
                        account.setBalance(account.getBalance() - convertedAmount);
                        account.addTransaction(new SplitTransaction(timestamp,
                                "Split payment of " + formatDoubleToTwoDecimals(amount)
                                        + " " + currency, null, -1,
                                currency, accounts, null, amounts, null));
                        amountPosition++;
                    }
                }
            }
        }

    }

    /**
     * Accepts a split payment request for a user.
     *
     * @param email   The email of the user accepting the split payment.
     * @param command The command input containing the details of the operation.
     */
    public void acceptSplitPayment(final String email, final CommandInput command) {
        User user = bank.findUser(email);
        if (user != null) {
            if (!user.getTransactionRequest().isEmpty()) {
                int timestamp = user.getTransactionRequest().getFirst().getTimestamp();
                SplitTransaction transaction = findTransaction(timestamp);
                if (transaction != null) {
                    transaction.addResponse(true);
                    if (transaction.isFullyAccepted() && transaction.getAmountForUsers() != null) {
                        System.out.println("VEDE CA s-au dat toate accepturile");
                        paymentSplitCustom(transaction.getInvolvedAccounts(),
                                transaction.getAmountForUsers(),
                                transaction.getCurrency(), transaction.getTimestamp(),
                                transaction.getAmount());
                    } else if (transaction.isFullyAccepted()) {
                        splitPaymentForEqual(transaction.getInvolvedAccounts(),
                                transaction.getAmount(),
                                transaction.getCurrency(), transaction.getTimestamp());
                    }
                } else {
                    System.out.println("Tranzactie negasita pentru accept");
                    return;
                }
                user.removeCommand();
            } else {
                System.out.println("Nu sunt comenzi de autorizat");
            }
        } else {
            command.setDescription("User not found");
            System.out.println("User negasit pentru accept");
        }
    }


    /**
     * Rejects a split payment request for a user.
     *
     * @param email   The email of the user rejecting the split payment.
     * @param command The command input containing the details of the operation.
     */
    public void rejectSplitPayment(final String email, final CommandInput command) {
        User user = bank.findUser(email);
        if (user != null) {
            if (!user.getTransactionRequest().isEmpty()) {
                int timestamp = user.getTransactionRequest().getFirst().getTimestamp();
                SplitTransaction transaction = findTransaction(timestamp);
                if (transaction != null) {
                    transaction.addResponse(false);
                } else {
                    System.out.println("Tranzactie negasita pentru accept");
                    return;
                }
                user.removeCommand();
                for (String iban : transaction.getInvolvedAccounts()) {
                    Account account = bank.findAccountByIban(iban);
                    if (account == null) {
                        return;
                    }
                    account.addTransaction(new SplitTransaction(timestamp,
                            "Split payment of "
                                    + formatDoubleToTwoDecimals(transaction.getAmount())
                                    + " " + transaction.getCurrency(), null, -1,
                            transaction.getCurrency(), transaction.getInvolvedAccounts(),
                            null, transaction.getAmountForUsers(),
                            "One user rejected the payment."));
                }

            } else {
                System.out.println("Nu sunt comenzi de refuzat");
            }
        } else {
            command.setDescription("User not found");
            System.out.println("User negasit pentru refuz");
        }
    }


    /**
     * Formats a double value to two decimal places.
     *
     * @param value The value to format.
     * @return The formatted value as a string.
     */
    public String formatDoubleToTwoDecimals(final double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }

    /**
     * Adds interest to a savings account.
     *
     * @param iban      The IBAN of the savings account.
     * @param timestamp The timestamp for the transaction.
     * @param mapper    The object mapper used for creating JSON objects.
     * @return An ObjectNode containing the result of the operation, or null if successful.
     */
    public ObjectNode addInterest(final String iban, final int timestamp,
                                  final ObjectMapper mapper) {
        for (User user : bank.getUsers()) {
            for (Account account : user.getAccounts()) {
                if (account instanceof Savings && account.getIban().equals(iban)) {
                    double interest = account.getBalance()
                            * ((Savings) account).getInterestRate();
                    System.out.println(account.getBalance() + " * "
                            + ((Savings) account).getInterestRate());
                    account.addFunds(interest);
                    account.addTransaction(new AccountTransaction(
                            timestamp,
                            "Interest rate income", String.valueOf(interest),
                            null, null, null,
                            "interestAdd", null, account.getCurrency(), interest));
                    return null;
                }
            }
        }
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("command", "addInterest");

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("description", "This is not a savings account");
        outputNode.put("timestamp", timestamp);

        errorNode.set("output", outputNode);
        errorNode.put("timestamp", timestamp);
        return errorNode;
    }

    /**
     * Changes the interest rate of a savings account.
     *
     * @param iban      The IBAN of the savings account.
     * @param newRate   The new interest rate to set.
     * @param timestamp The timestamp for the transaction.
     * @param mapper    The object mapper used for creating JSON objects.
     * @return An ObjectNode containing the result of the operation, or null if successful.
     */
    public ObjectNode changeInterestRate(final String iban, final double newRate,
                                         final int timestamp, final ObjectMapper mapper) {
        for (User user : bank.getUsers()) {
            for (Account account : user.getAccounts()) {
                if (account instanceof Savings && account.getIban().equals(iban)) {

                    ((Savings) account).setInterestRate(newRate);
                    account.addTransaction(new AccountTransaction(
                            timestamp,
                            "Interest rate of the account changed to " + newRate,
                            null, null, null, null,
                            "interest", null, account.getCurrency(), newRate));
                    return null;
                }
            }
        }
        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("command", "changeInterestRate");

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("description", "This is not a savings account");
        outputNode.put("timestamp", timestamp);

        errorNode.set("output", outputNode);
        errorNode.put("timestamp", timestamp);
        return errorNode;
    }

    /**
     * Prints all transactions for a user, sorted by timestamp.
     *
     * @param user            The user whose transactions will be printed.
     * @param mapper          The object mapper used for creating JSON objects.
     * @param commandTimestamp The timestamp for the command.
     * @return An ObjectNode containing the list of transactions.
     */
    public ObjectNode printTransactions(final User user, final ObjectMapper mapper,
                                        final int commandTimestamp) {
        ObjectNode transactionsNode = mapper.createObjectNode();
        transactionsNode.put("command", "printTransactions");
        transactionsNode.put("timestamp", commandTimestamp);

        ArrayNode outputArray = mapper.createArrayNode();

        List<Transaction> allTransactions = new ArrayList<>();
        for (Account account : user.getAccounts()) {
            allTransactions.addAll(account.getTransactions());
        }

        allTransactions.sort((t1, t2) -> Integer.compare(t1.getTimestamp(), t2.getTimestamp()));
        for (Transaction transaction : allTransactions) {
            ObjectNode transactionNode = mapper.createObjectNode();
            transaction.showTransaction(transaction, transactionNode, mapper);
            outputArray.add(transactionNode);
        }
        transactionsNode.set("output", outputArray);
        return transactionsNode;
    }

    /**
     * Generates a report of transactions for an account within a given time range.
     *
     * @param firstTimestamp The start timestamp for the report.
     * @param lastTimestamp  The end timestamp for the report.
     * @param iban           The IBAN of the account.
     * @param users          The list of users in the system.
     * @param timestamp      The timestamp for the command.
     * @param mapper         The object mapper used for creating JSON objects.
     * @return An ObjectNode containing the transaction report.
     */
    public ObjectNode report(final int firstTimestamp, final int lastTimestamp,
                             final String iban, final ArrayList<User> users,
                             final int timestamp, final ObjectMapper mapper) {
        ObjectNode transactionsNode = mapper.createObjectNode();
        transactionsNode.put("command", "report");

        for (User user : users) {
            Account account = user.findAccountForCurrentUser(iban);
            if (account != null) {
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("balance", account.getBalance());
                outputNode.put("currency", account.getCurrency());
                outputNode.put("IBAN", iban);

                ArrayNode transactionsArray = mapper.createArrayNode();

                for (Transaction transaction : account.getTransactions()) {
                    if (transaction.getTimestamp() >= firstTimestamp
                            && transaction.getTimestamp() <= lastTimestamp) {
                        ObjectNode transactionNode = mapper.createObjectNode();
                        transaction.showTransaction(transaction, transactionNode, mapper);
                        transactionNode.put("timestamp", transaction.getTimestamp());
                        transactionsArray.add(transactionNode);
                    }
                }
                outputNode.set("transactions", transactionsArray);
                transactionsNode.set("output", outputNode);
                transactionsNode.put("timestamp", timestamp);
                return transactionsNode;
            }
        }
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("description", "Account not found");
        outputNode.put("timestamp", timestamp);
        transactionsNode.set("output", outputNode);
        transactionsNode.put("timestamp", timestamp);
        return transactionsNode;
    }

    /**
     * Generates a spendings report for an account within a given time range.
     *
     * @param firstTimestamp The start timestamp for the report.
     * @param lastTimestamp  The end timestamp for the report.
     * @param iban           The IBAN of the account.
     * @param users          The list of users in the system.
     * @param timestamp      The timestamp for the command.
     * @param mapper         The object mapper used for creating JSON objects.
     * @return An ObjectNode containing the spendings report.
     */
    public ObjectNode spendingsReport(final int firstTimestamp, final int lastTimestamp,
                                      final String iban, final ArrayList<User> users,
                                      final int timestamp, final ObjectMapper mapper) {
        ObjectNode transactionsNode = mapper.createObjectNode();
        transactionsNode.put("command", "spendingsReport");

        for (User user : users) {
            Account account = user.findAccountForCurrentUser(iban);
            if (account != null) {
                return account.spendingsReport(account, firstTimestamp, lastTimestamp,
                        timestamp, transactionsNode, mapper);
            }
        }
        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("description", "Account not found");
        outputNode.put("timestamp", timestamp);
        transactionsNode.set("output", outputNode);
        transactionsNode.put("timestamp", timestamp);
        return transactionsNode;
    }

    /**
     * Processes a savings withdrawal request for a user.
     *
     * @param command    The command input containing details of the withdrawal request.
     * @param account    The IBAN of the savings account from which to withdraw.
     * @param amount     The amount to withdraw from the savings account.
     * @param currency   The currency in which the withdrawal is requested.
     * @param timestamp  The timestamp of the withdrawal transaction.
     */
    public void withdrawSavings(final CommandInput command, final String account,
                                final double amount, final String currency,
                                final  int timestamp) {
        for (User user : bank.getUsers()) {
            Account savingsAccount = user.findAccountForCurrentUser(account);
            if (savingsAccount != null) {
                if (savingsAccount.getAccountType().equals("savings")) {

                    Account classicAccount = user.findTheClassicAccountForCurrency(currency);
                    if (classicAccount != null) {
                        if (user.hasAtLeast21Years()) {
                            double convertedAmount = currencyConverter.convert(currency,
                                    savingsAccount.getCurrency(), amount);
                            if (savingsAccount.getBalance() < convertedAmount) {
                                savingsAccount.addTransaction(new AccountTransaction(timestamp,
                                        "Insufficient funds",
                                        null, null, null,
                                        null, null,
                                        null, null, -1));
                                return;
                            }

                            savingsAccount.withdrawSavings(convertedAmount,
                                    amount, classicAccount, command);

                            savingsAccount.addTransaction(new AccountTransaction(timestamp,
                                    "Savings withdrawal",
                                    null, null, savingsAccount.getIban(),
                                    classicAccount.getIban(), "withdrawal", null,
                                    null, amount));
                            classicAccount.addTransaction(new AccountTransaction(timestamp,
                                    "Savings withdrawal",
                                    null, null, savingsAccount.getIban(),
                                    classicAccount.getIban(), "withdrawal", null,
                                    null, amount));
                        } else {
                            savingsAccount.addTransaction(new AccountTransaction(timestamp,
                                    "You don't have the minimum age required.",
                                    null, null, null, null,
                                    null, null, null, -1));
                        }
                    } else {
                        savingsAccount.addTransaction(new AccountTransaction(timestamp,
                                "You do not have a classic account.",
                                null, null, null, null,
                                null, null, null, -1));
                    }
                } else {
                    savingsAccount.addTransaction(new AccountTransaction(timestamp,
                            "Account is not of type savings",
                            null, null, null, null,
                            null, null, null, -1));
                    System.out.println("not type savings");
                }
            } else {
                command.setDescription("Account not found");
            }
        }
    }

    /**
     * Upgrades the service plan of a user.
     *
     * @param newPlanType The new plan type to upgrade to ("silver" or "gold").
     * @param account     The IBAN of the user's account used for the upgrade.
     * @param timestamp   The timestamp of the upgrade request.
     * @param command     The command input containing the details of the operation.
     */
    public void upgradePlan(final String newPlanType, final String account,
                            final int timestamp, final CommandInput command) {
        User user = bank.findUserByIban(account);
        if (user == null) {
            command.setDescription("Account not found");
            return;
        }
        Account payAccount = user.findAccountForCurrentUser(account);
        if (payAccount == null) {
            command.setDescription("Account not found");
            return;
        }

        if (newPlanType.equals("silver") && (user.getServicePlan().equals("standard")
                || user.getServicePlan().equals("student"))) {

            double convertedAmount = currencyConverter.convert(
                    "RON", payAccount.getCurrency(), FEE_ONE);
            if (payAccount.getBalance() >= convertedAmount) {
                payAccount.removeFunds(convertedAmount);
                user.setServicePlan("silver");
            } else {
                payAccount.addTransaction(new AccountTransaction(timestamp, "Insufficient funds",
                        null, null, null, null,
                        null, null, null, -1));
                return;
            }
        } else if (newPlanType.equals("silver") && user.getServicePlan().equals("silver")) {
            payAccount.addTransaction(new AccountTransaction(timestamp,
                    "The user already has the silver plan.",
                    null, null, null, null,
                    null, null, null, -1));
            return;
        } else if (newPlanType.equals("gold") && user.getServicePlan().equals("silver")) {
            double convertedAmount = currencyConverter.convert(
                    "RON", payAccount.getCurrency(), FEE_TWO);
            if (payAccount.getBalance() >= convertedAmount) {
                payAccount.removeFunds(convertedAmount);
                user.setServicePlan("gold");
            } else {
                payAccount.addTransaction(new AccountTransaction(timestamp,
                        "Insufficient funds",
                        null, null, null, null,
                        null, null, null, -1));
                return;
            }
        } else if (newPlanType.equals("gold") && (user.getServicePlan().equals("standard")
                || user.getServicePlan().equals("student"))) {
            double convertedAmount = currencyConverter.convert(
                    "RON", payAccount.getCurrency(), FEE_THREE);
            if (payAccount.getBalance() >= convertedAmount) {
                payAccount.removeFunds(convertedAmount);
                user.setServicePlan("gold");
            } else {
                payAccount.addTransaction(new AccountTransaction(timestamp, "Insufficient funds",
                        null, null, null, null,
                        null, null, null, -1));
                return;
            }
        }
        payAccount.addTransaction(new AccountTransaction(timestamp, "Upgrade plan",
                null, null, account, null,
                null, newPlanType, null, -1));
    }

    /**
     * Handles a cash withdrawal operation.
     *
     * @param cardNumber The card number used for the withdrawal.
     * @param amount     The amount to withdraw.
     * @param email      The email of the user performing the withdrawal.
     * @param location   The location of the withdrawal.
     * @param command    The command input containing the details of the operation.
     */
    public void cashWithdrawal(final String cardNumber, final double amount,
                               final String email, final String location,
                               final CommandInput command) {
        User user = bank.findUser(email);
        if (user == null) {
            command.setDescription("User not found");
            return;
        }

        Card card = null;
        Account account = null;
        for (Account accountFinder : user.getAccounts()) {
            for (Card cardFinder : accountFinder.getCards()) {
                if (cardFinder.getCardNumber().equals(cardNumber)) {
                    card = cardFinder;
                    account = accountFinder;
                }
            }
        }
        if (card == null) {
            command.setDescription("Card not found");
            return;
        }
        if (card.isFrozen()) {
            command.setDescription("The card is frozen");
            return;
        }

        if (card.isUsed()) {
            command.setDescription("Card has already been used");
            return;
        }

        if (account.getBalance() < amount) {

            account.addTransaction(new CardTransaction(command.getTimestamp(),
                    "Insufficient funds", -1, null,
                    null, null, null));
        } else if (account.getBalance() - amount < account.getMinBalance()) {
            command.setDescription("Cannot perform payment due to a "
                    + "minimum balance being set");
        } else {
            double amountConverted = currencyConverter.convert("RON",
                    account.getCurrency(), amount);
            card.cashWithdrawal(user, amountConverted, amount, account, command);
        }

    }

    /**
     * Adds a new business associate (employee or manager) to a business account.
     *
     * @param iban  The IBAN of the business account.
     * @param role  The role of the associate ("employee" or "manager").
     * @param email The email of the user being added as an associate.
     */
    public void addNewBusinessAssociate(final String iban, final String role,
                                        final String email) {
        Account account = bank.findAccountByIban(iban);
        if (account == null) {
            return;
        }
        if (!Objects.equals(account.getAccountType(), "business")) {
            return;
        }
        User user = bank.findUser(email);
        if (user == null) {
            return;
        }
        user.addBussinesAccount(account);
        if (Objects.equals(role, "employee")) {
            ((Bussines) account).addEmployee(user);
            int position = ((Bussines) account).getEmployees().size() - 1;
            ((Bussines) account).addDepositEmployee(position, 0);
            ((Bussines) account).addSpendingEmployee(position, 0);
        } else {
            ((Bussines) account).addManager(user);
            int position = ((Bussines) account).getManagers().size() - 1;
            ((Bussines) account).addDepositManager(position, 0);
            ((Bussines) account).addSpendingManager(position, 0);
        }
    }

    /**
     * Changes the spending limit for a business account.
     *
     * @param email     The email of the account owner.
     * @param iban      The IBAN of the business account.
     * @param amount    The new spending limit to set.
     * @param command   The command input containing the details of the operation.
     */
    public void changeSpendingLimit(final String email, final String iban,
                                    final double amount, final CommandInput command) {
        User user = bank.findUser(email);
        if (user == null) {
            return;
        }
        Account account = bank.findAccountByIban(iban);
        if (account == null) {
            return;
        }
        if (!Objects.equals(account.getAccountType(), "business")) {
            command.setDescription("This is not a business account");
            return;
        }
        if (!Objects.equals(user.getEmail(), ((Bussines) account).getOwner().getEmail())) {
            command.setDescription("You must be owner in order to change spending limit.");
            return;
        }
        ((Bussines) account).setSpendingLimit(amount);
    }

    /**
     * Changes the deposit limit for a business account.
     *
     * @param email     The email of the account owner.
     * @param iban      The IBAN of the business account.
     * @param amount    The new deposit limit to set.
     * @param command   The command input containing the details of the operation.
     */
    public void changeDepositLimit(final String email, final String iban,
                                   final double amount, final CommandInput command) {
        User user = bank.findUser(email);
        if (user == null) {
            return;
        }
        Account account = bank.findAccountByIban(iban);
        if (account == null) {
            return;
        }
        if (!Objects.equals(account.getAccountType(), "business")) {
            command.setDescription("This is not a business account");
            return;
        }
        if (!Objects.equals(user.getEmail(), ((Bussines) account).getOwner().getEmail())) {
            command.setDescription("You must be owner in order to change deposit limit.");
            return;
        }
        ((Bussines) account).setDepositLimit(amount);
    }

    /**
     * Generates a business report for a specified business account.
     *
     * @param type          The type of report ("transaction" or "commerciant").
     * @param startTimestamp The start timestamp of the report range.
     * @param endTimestamp   The end timestamp of the report range.
     * @param iban          The IBAN of the business account.
     * @param timestamp     The timestamp of the report generation.
     * @param mapper        The object mapper for creating JSON objects.
     * @return An ObjectNode containing the business report.
     */
    public ObjectNode generateBusinessReport(final String type,
                                             final int startTimestamp, final int endTimestamp,
                                             final String iban, final int timestamp,
                                             final ObjectMapper mapper) {
        Account account = bank.findAccountByIban(iban);
        if (account == null) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("command", "businessReport");
            errorNode.put("description", "Account not found");
            errorNode.put("timestamp", timestamp);
            return errorNode;
        }

        if (!(account instanceof Bussines)) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("command", "businessReport");
            errorNode.put("description", "Account is not of type business");
            errorNode.put("timestamp", timestamp);
            return errorNode;
        }

        Bussines businessAccount = (Bussines) account;

        if ("transaction".equals(type)) {
            return generateTransactionReport(businessAccount, startTimestamp,
                    endTimestamp, timestamp, mapper);
        }

        if ("commerciant".equals(type)) {
            return generateCommerciantReport(businessAccount, startTimestamp,
                    endTimestamp, timestamp, mapper);
        }

        ObjectNode errorNode = mapper.createObjectNode();
        errorNode.put("command", "businessReport");
        errorNode.put("description", "Invalid report type");
        errorNode.put("timestamp", timestamp);
        return errorNode;
    }

    /**
     * Generates a transaction-based report for a business account.
     *
     * @param businessAccount The business account for which the report is generated.
     * @param startTimestamp  The start timestamp of the report range.
     * @param endTimestamp    The end timestamp of the report range.
     * @param timestamp       The timestamp of the report generation.
     * @param mapper          The object mapper for creating JSON objects.
     * @return An ObjectNode containing the transaction report.
     */
    public ObjectNode generateTransactionReport(final Bussines businessAccount,
                                                final int startTimestamp,
                                                final int endTimestamp,
                                                final int timestamp, final ObjectMapper mapper) {
        ObjectNode reportNode = mapper.createObjectNode();
        reportNode.put("command", "businessReport");

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("IBAN", businessAccount.getIban());
        outputNode.put("balance", businessAccount.getBalance());
        outputNode.put("currency", businessAccount.getCurrency());
        outputNode.put("spending limit", businessAccount.getSpendingLimit());
        outputNode.put("deposit limit", businessAccount.getDepositLimit());

        ArrayNode managersArray = mapper.createArrayNode();
        ArrayNode employeesArray = mapper.createArrayNode();

        double totalSpent = 0;
        double totalDeposited = 0;

        List<User> sortedManagers = businessAccount.getManagers();
        List<User> sortedEmployees = businessAccount.getEmployees();


        for (int i = 0; i < sortedManagers.size(); i++) {
            User manager = sortedManagers.get(i);
            double spent = businessAccount.getSpendingManagers().get(i);
            double deposited = businessAccount.getDepositManagers().get(i);
            totalSpent += spent;
            totalDeposited += deposited;

            ObjectNode managerNode = mapper.createObjectNode();
            managerNode.put("spent", spent);
            managerNode.put("deposited", deposited);
            managerNode.put("username", manager.getLastName() + " " + manager.getFirstName());
            managersArray.add(managerNode);
        }

        for (int i = 0; i < sortedEmployees.size(); i++) {
            User employee = sortedEmployees.get(i);
            double spent = businessAccount.getSpendingEmployees().get(i);
            double deposited = businessAccount.getDepositEmployees().get(i);
            totalSpent += spent;
            totalDeposited += deposited;

            ObjectNode employeeNode = mapper.createObjectNode();
            employeeNode.put("spent", spent);
            employeeNode.put("deposited", deposited);
            employeeNode.put("username", employee.getLastName() + " " + employee.getFirstName());
            employeesArray.add(employeeNode);
        }

        outputNode.set("managers", managersArray);
        outputNode.set("employees", employeesArray);
        outputNode.put("total spent", totalSpent);
        outputNode.put("total deposited", totalDeposited);
        outputNode.put("statistics type", "transaction");

        reportNode.set("output", outputNode);
        reportNode.put("timestamp", timestamp);

        return reportNode;
    }

    /**
     * Generates a commerciant-based report for a business account.
     *
     * @param businessAccount The business account for which the report is generated.
     * @param startTimestamp  The start timestamp of the report range.
     * @param endTimestamp    The end timestamp of the report range.
     * @param timestamp       The timestamp of the report generation.
     * @param mapper          The object mapper for creating JSON objects.
     * @return An ObjectNode containing the commerciant report.
     */
    public ObjectNode generateCommerciantReport(final Bussines businessAccount,
                                                final int startTimestamp,
                                                final int endTimestamp, final int timestamp,
                                                final ObjectMapper mapper) {
        ObjectNode reportNode = mapper.createObjectNode();
        reportNode.put("command", "businessReport");

        ObjectNode outputNode = mapper.createObjectNode();
        outputNode.put("IBAN", businessAccount.getIban());
        outputNode.put("balance", businessAccount.getBalance());
        outputNode.put("currency", businessAccount.getCurrency());
        outputNode.put("spending limit", businessAccount.getSpendingLimit());
        outputNode.put("statistics type", "commerciant");
        outputNode.put("deposit limit", businessAccount.getDepositLimit());

        ArrayNode commerciantsArray = mapper.createArrayNode();

        List<Commerciant> sortedCommerciants = new ArrayList<>(businessAccount.getCommerciants());
        sortedCommerciants.sort(Comparator.comparing(Commerciant::getName));

        for (Commerciant commerciant : sortedCommerciants) {
            ObjectNode commerciantNode = mapper.createObjectNode();
            commerciantNode.put("commerciant", commerciant.getName());
            commerciantNode.put("total received", commerciant.getFinalAmount());

            ArrayNode managersArray = mapper.createArrayNode();
            ArrayNode employeesArray = mapper.createArrayNode();

            for (User manager : commerciant.getSortedManagers()) {
                managersArray.add(manager.getLastName() + " " + manager.getFirstName());
            }

            for (User employee : commerciant.getSortedEmployees()) {
                employeesArray.add(employee.getLastName() + " " + employee.getFirstName());
            }

            commerciantNode.set("managers", managersArray);
            commerciantNode.set("employees", employeesArray);

            commerciantsArray.add(commerciantNode);
        }

        outputNode.set("commerciants", commerciantsArray);
        reportNode.set("output", outputNode);
        reportNode.put("timestamp", timestamp);

        return reportNode;
    }


}
