package org.poo.card;

import org.poo.account.Account;
import org.poo.account.Bussines;
import org.poo.account.User;
import org.poo.business.Converter;
import org.poo.fileio.CommandInput;
import org.poo.transaction.CardTransaction;
import org.poo.utils.Utils;

import java.util.Objects;

public final class OneTimeCard extends Card {
    public OneTimeCard(final String cardNumber) {
        super(cardNumber);
    }

    @Override
    public int payOnline(final Account account, final CommandInput command,
                          final String email, final double convertedAmount,
                          final String currency, final User user,
                          final Converter currencyConverter) {
        System.out.println("Vede ca are de platit cu un card ONETIME");
        int employeePosition = -1;
        int managerPosition = -1;
        if (Objects.equals(account.getAccountType(), "business")
                && !Objects.equals(((Bussines) account).getOwner().getEmail(), user.getEmail())) {
            employeePosition = ((Bussines) account).isEmployee(user);
            managerPosition = ((Bussines) account).isManager(user);

            if (employeePosition != -1
                    && convertedAmount > ((Bussines) account).getSpendingLimit()) {
                return -1;
            }
        }
        if (isFrozen()) {
            account.addTransaction(new CardTransaction(
                    command.getTimestamp(), "The card is frozen",
                    -1, null, getCardNumber(), email, account.getIban()));
            return -1;
        }
        if (convertedAmount > account.getBalance()) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!NU ARE BANI DESTUI");
            System.out.println("Soldul ramane " + account.getBalance());
            command.setDescription("Insufficient funds");
            account.addTransaction(new CardTransaction(
                    command.getTimestamp(), "Insufficient funds",
                    -1, null, getCardNumber(), email, account.getIban()));
            return -1;
        }

        System.out.println("INCEPE plata");
        pay(user, convertedAmount, account, command, email, currency, currencyConverter);
        if (employeePosition != -1) {
            double amountToAdd = ((Bussines) account).getSpendingEmployees().get(employeePosition)
                    + convertedAmount;
            ((Bussines) account).setSpendingEmployee(employeePosition, amountToAdd);
        } else if (managerPosition != -1) {
            double amountToAdd = ((Bussines) account).getSpendingManagers().get(managerPosition)
                    + convertedAmount;
            ((Bussines) account).setSpendingManager(managerPosition, amountToAdd);
        }

        account.addTransaction(new CardTransaction(command.getTimestamp(), "Card payment",
                convertedAmount, command.getCommerciant(),
                getCardNumber(), email, account.getIban()));
        command.setDescription("Success");

        account.addTransaction(new CardTransaction(
                command.getTimestamp(), "The card has been destroyed",
                0, null, getCardNumber(), email, account.getIban()));
        setCardNumber(Utils.generateCardNumber());
        account.addTransaction(new CardTransaction(
                command.getTimestamp(), "New card created",
                0, null, getCardNumber(), email, account.getIban()));
        return 0;
    }
}
