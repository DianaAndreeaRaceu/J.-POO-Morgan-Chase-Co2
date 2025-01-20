package org.poo.card;

import org.poo.account.Account;
import org.poo.account.Bussines;
import org.poo.account.User;
import org.poo.bussines.Converter;
import org.poo.fileio.CommandInput;
import org.poo.transaction.CardTransaction;

import java.util.Objects;

public final class ClassicCard extends Card{

    public ClassicCard(final String cardNumber) {
        super(cardNumber);
    }

    @Override
    public int payOnline(final Account account, final CommandInput command,
                          final String email, final double convertedAmount,
                          final String currency, final User user,
                           final Converter currencyConverter) {
        int employeePosition = -1;
        int managerPosition = -1;
        if(Objects.equals(account.getAccountType(), "business")) {
            employeePosition = ((Bussines)account).isEmployee(user);
            managerPosition = ((Bussines)account).isManager(user);

            if(employeePosition != -1 && convertedAmount > ((Bussines)account).getSpendingLimit()) {
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
            command.setDescription("Insufficient funds");
            account.addTransaction(new CardTransaction(
                    command.getTimestamp(), "Insufficient funds",
                    -1, null, getCardNumber(), email, account.getIban()));
            return -1;
        }


        pay(user, convertedAmount, account, command, email, currency, currencyConverter);
        if(employeePosition != -1) {
            double amountToAdd = ((Bussines)account).getSpendingEmployees().get(employeePosition) + convertedAmount;
            ((Bussines)account).setSpendingEmployee(employeePosition, amountToAdd);
        } else if(managerPosition != -1) {
            double amountToAdd = ((Bussines)account).getSpendingManagers().get(managerPosition) + convertedAmount;
            ((Bussines)account).setSpendingManager(managerPosition, amountToAdd);
        }
        account.addTransaction(new CardTransaction(command.getTimestamp(), "Card payment",
                convertedAmount, command.getCommerciant(),
                getCardNumber(), email, account.getIban()));
        command.setDescription("Success");
        return 0;
    }
}
