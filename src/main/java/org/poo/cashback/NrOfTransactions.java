package org.poo.cashback;

import org.poo.account.Account;
import org.poo.account.User;

import java.util.Objects;

public class NrOfTransactions implements CashbackStrategy{

    public static void calculateCashback(double transactionAmount, User user, Account account, String type) {

        if(user.getNrOfTransactions() > 2 && Objects.equals(type, "Food")
                && !user.isCashbackReceivedTwo()) {
            account.setBalance(account.getBalance() + (transactionAmount * 2)/100);
            user.setCashbackReceivedTwo(true);
        }
        if(user.getNrOfTransactions() > 5 && Objects.equals(type, "Clothes")
                && !user.isCashbackReceivedFive()) {
            account.setBalance(account.getBalance() + (transactionAmount * 5)/100);
            user.setCashbackReceivedFive(true);
        }
        if(user.getNrOfTransactions() > 10 && Objects.equals(type, "Tech")
                && !user.isCashbackReceivedTen()) {
            account.setBalance(account.getBalance() + (transactionAmount * 10)/100);
            user.setCashbackReceivedTen(true);
        }
        user.setNrOfTransactions(user.getNrOfTransactions() + 1);
    }
}
