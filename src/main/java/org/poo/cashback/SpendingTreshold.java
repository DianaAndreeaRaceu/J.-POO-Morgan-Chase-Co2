package org.poo.cashback;

import org.poo.account.Account;
import org.poo.account.User;

import java.util.Objects;

public class SpendingTreshold implements CashbackStrategy{

    public static void calculateCashback(double transactionAmount, User user,
                                         Account account, double convertedAmount) {
        if(convertedAmount >= 100 && convertedAmount < 300) {
            if(Objects.equals(user.getServicePlan(), "standard")
                    || Objects.equals(user.getServicePlan(), "student")) {
                account.setBalance(account.getBalance() + (transactionAmount * 0.1)/100);
            } else if(Objects.equals(user.getServicePlan(), "silver")) {
                account.setBalance(account.getBalance() + (transactionAmount * 0.3)/100);
            } else {
                account.setBalance(account.getBalance() + (transactionAmount * 0.5)/100);
            }
        } else if(convertedAmount >= 300  && convertedAmount < 500) {
            if(Objects.equals(user.getServicePlan(), "standard")
                    || Objects.equals(user.getServicePlan(), "student")) {
                account.setBalance(account.getBalance() + (transactionAmount * 0.2)/100);
            } else if(Objects.equals(user.getServicePlan(), "silver")) {
                account.setBalance(account.getBalance() + (transactionAmount * 0.4)/100);
            } else {
                account.setBalance(account.getBalance() + (transactionAmount * 0.55)/100);
            }
        } else if(convertedAmount >= 500) {
            if(Objects.equals(user.getServicePlan(), "standard")
                    || Objects.equals(user.getServicePlan(), "student")) {
                account.setBalance(account.getBalance() + (transactionAmount * 0.25)/100);
            } else if(Objects.equals(user.getServicePlan(), "silver")) {
                account.setBalance(account.getBalance() + (transactionAmount * 0.5)/100);
            } else {
                account.setBalance(account.getBalance() + (transactionAmount * 0.7)/100);
            }
        }
    }
}
