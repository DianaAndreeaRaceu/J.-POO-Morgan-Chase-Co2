package org.poo.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.business.Commerciant;
import org.poo.business.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Bussines extends Account {
    private List<User> employees;
    private List<User> managers;
    private List<Double> spendingEmployees;
    private List<Double> depositEmployees;
    private List<Double> spendingManagers;
    private List<Double> depositManagers;
    private List<Commerciant> commerciants;
    private double spendingLimit;
    private double depositLimit;
    private User owner;
    private static final int INITIAL_LIMIT = 500;

    public Bussines(final String iban, final String currency,
                    final String accountType, final double minBalance,
                    final User user, final Converter converter) {
        super(iban, currency, accountType, minBalance);
        this.owner = user;
        this.spendingLimit = converter.convert("RON", currency, INITIAL_LIMIT);
        this.depositLimit = converter.convert("RON", currency, INITIAL_LIMIT);
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.spendingEmployees = new ArrayList<>();
        this.depositEmployees = new ArrayList<>();
        this.spendingManagers = new ArrayList<>();
        this.depositManagers = new ArrayList<>();
        this.commerciants = new ArrayList<>();
    }

    public User getOwner() {
        return owner;
    }

    /**
     * Adds an employee to the business account.
     *
     * @param user The {@link User} to be added as an employee.
     */
    public void addEmployee(final User user) {
        employees.add(user);
    }

    /**
     * Adds a manager to the business account.
     *
     * @param user The {@link User} to be added as a manager.
     */
    public void addManager(final User user) {
        managers.add(user);
    }

    /**
     * Adds spending data for a specific employee at the given position.
     *
     * @param position The position of the employee in the list.
     * @param amount   The amount of spending to record.
     */
    public void addSpendingEmployee(final int position, final double amount) {
        spendingEmployees.add(position, amount);
    }

    /**
     * Adds deposit data for a specific employee at the given position.
     *
     * @param position The position of the employee in the list.
     * @param amount   The amount of deposit to record.
     */
    public void addDepositEmployee(final int position, final double amount) {
        depositEmployees.add(position, amount);
    }

    /**
     * Adds spending data for a specific manager at the given position.
     *
     * @param position The position of the manager in the list.
     * @param amount   The amount of spending to record.
     */
    public void addSpendingManager(final int position, final double amount) {
        spendingManagers.add(position, amount);
    }

    /**
     * Adds deposit data for a specific manager at the given position.
     *
     * @param position The position of the manager in the list.
     * @param amount   The amount of deposit to record.
     */
    public void addDepositManager(final int position, final double amount) {
        depositManagers.add(position, amount);
    }

    /**
     * Updates the spending amount for an employee at the given position.
     *
     * @param position The position of the employee in the list.
     * @param amount   The new spending amount to set.
     */
    public void setSpendingEmployee(final int position, final double amount) {
        spendingEmployees.set(position, amount);
    }

    /**
     * Updates the deposit amount for an employee at the given position.
     *
     * @param position The position of the employee in the list.
     * @param amount   The new deposit amount to set.
     */
    public void setDepositEmployee(final int position, final double amount) {
        depositEmployees.set(position, amount);
    }

    /**
     * Updates the spending amount for a manager at the given position.
     *
     * @param position The position of the manager in the list.
     * @param amount   The new spending amount to set.
     */
    public void setSpendingManager(final int position, final double amount) {
        spendingManagers.set(position, amount);
    }

    /**
     * Updates the deposit amount for a manager at the given position.
     *
     * @param position The position of the manager in the list.
     * @param amount   The new deposit amount to set.
     */
    public void setDepositManager(final int position, final double amount) {
        depositManagers.set(position, amount);
    }

    public List<Double> getDepositEmployees() {
        return depositEmployees;
    }

    public List<Double> getDepositManagers() {
        return depositManagers;
    }

    public List<Double> getSpendingEmployees() {
        return spendingEmployees;
    }

    public List<Double> getSpendingManagers() {
        return spendingManagers;
    }

    public List<User> getEmployees() {
        return employees;
    }

    public List<User> getManagers() {
        return managers;
    }

    public double getSpendingLimit() {
        return spendingLimit;
    }

    /**
     * Sets the spending limit for the business account.
     *
     * @param spendingLimit The new spending limit to set.
     */
    public void setSpendingLimit(final double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public double getDepositLimit() {
        return depositLimit;
    }

    /**
     * Sets the deposit limit for the business account.
     *
     * @param depositLimit The new deposit limit to set.
     */
    public void setDepositLimit(final double depositLimit) {
        this.depositLimit = depositLimit;
    }

    /**
     * Adds a commerciant to the list of commerciants associated with the business account.
     *
     * @param commerciant The {@link Commerciant} to be added.
     */
    public void addCommerciant(final Commerciant commerciant) {
        commerciants.add(commerciant);
    }

    public List<Commerciant> getCommerciants() {
        return commerciants;
    }

    @Override
    public ObjectNode spendingsReport(final Account account, final int firstTimestamp,
                                      final int lastTimestamp, final int timestamp,
                                      final ObjectNode transactionsNode,
                                      final ObjectMapper mapper) {
        return null;
    }

    /**
     * Checks if a user is an employee of the business account.
     *
     * @param user The {@link User} to check.
     * @return The position of the employee in the list, or -1 if not found.
     */
    public int isEmployee(final User user) {
        int position = 0;
        for (User secondUser : employees) {
            if (Objects.equals(user.getEmail(), secondUser.getEmail())) {
                return position;
            }
            position++;
        }
        return -1;
    }

    /**
     * Checks if a user is a manager of the business account.
     *
     * @param user The {@link User} to check.
     * @return The position of the manager in the list, or -1 if not found.
     */
    public int isManager(final User user) {
        int position = 0;
        for (User secondUser : managers) {
            if (Objects.equals(user.getEmail(), secondUser.getEmail())) {
                return position;
            }
            position++;
        }
        return -1;
    }

    /**
     * Finds a commerciant by name in the list of commerciants.
     *
     * @param name The name of the commerciant to find.
     * @return The {@link Commerciant} object if found, or {@code null} if not.
     */
    public Commerciant findCommerciant(final String name) {
        for (Commerciant commerciant : commerciants) {
            if (Objects.equals(commerciant.getName(), name)) {
                return commerciant;
            }
        } return null;
    }


}
