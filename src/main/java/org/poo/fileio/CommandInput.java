package org.poo.fileio;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public final class CommandInput {
    private String command;
    private String email;
    private String account;
    private String newPlanType;
    private String role;
    private String currency;
    private String target;
    private String description;
    private String cardNumber;
    private String commerciant;
    private String receiver;
    private String alias;
    private String accountType;
    private String splitPaymentType;
    private String type;
    private String location;
    private int timestamp;
    private int startTimestamp;
    private int endTimestamp;
    private double interestRate;
    private double spendingLimit;
    private double depositLimit;
    private double amount;
    private double minBalance;
    private List<String> accounts;
    private List<Double> amountForUsers;

    public String getCommand() {
        return command;
    }

    public String getEmail() {
        return email;
    }

    public String getAccount() {
        return account;
    }

    public String getNewPlanType() {
        return newPlanType;
    }

    public String getCommerciant() {
        return commerciant;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getSplitPaymentType() {
        return splitPaymentType;
    }

    public String getLocation() {
        return location;
    }

    public String getRole() {
        return role;
    }

    public String getAccountType() {
        return accountType;
    }

    public List<Double> getAmountForUsers() {
        return amountForUsers;
    }

    public double getSpendingLimit() {
        return spendingLimit;
    }

    public double getDepositLimit() {
        return depositLimit;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public int getEndTimestamp() {
        return endTimestamp;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public String getDescription() {
        return description;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getTarget() {
        return target;
    }

    public List<String> getAccounts() {
        return accounts;
    }

    public double getMinBalance() {
        return minBalance;
    }

    public String getAlias() {
        return alias;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
