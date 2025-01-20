package org.poo.cashback;

import org.poo.account.Account;
import org.poo.account.User;

public interface CashbackStrategy {
    static void calculateCashback(double transactionAmount, User user, Account account, String type) {
    }
}
