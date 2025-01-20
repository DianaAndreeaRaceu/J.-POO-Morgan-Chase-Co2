package org.poo.business;

import org.poo.account.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Commerciant {
    private String name;
    private double finalAmount;
    private List<User> managers;
    private List<User> employees;
    private String type;

    public Commerciant(final String name, final String type) {
        this.name = name;
        this.finalAmount = 0;
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.type = type;
    }

    /**
     * Retrieves a sorted list of managers by their first names.
     *
     * @return A {@link List} of {@link User} objects
     * representing the sorted managers.
     */
    public List<User> getSortedManagers() {
        List<User> sortedManagers = new ArrayList<>(managers);
        sortedManagers.sort(Comparator.comparing(User::getFirstName));
        return sortedManagers;
    }

    /**
     * Retrieves a sorted list of employees by their first names.
     *
     * @return A {@link List} of {@link User} objects representing
     * the sorted employees.
     */
    public List<User> getSortedEmployees() {
        List<User> sortedEmployees = new ArrayList<>(employees);
        sortedEmployees.sort(Comparator.comparing(User::getFirstName));
        return sortedEmployees;
    }

    public String getName() {
        return name;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    /**
     * Adds an employee to the commerciant's employee list.
     *
     * @param user The {@link User} to be added as an employee.
     */
    public void addEmployee(final User user) {
        employees.add(user);
    }

    /**
     * Adds a manager to the commerciant's manager list.
     *
     * @param user The {@link User} to be added as a manager.
     */
    public void addManager(final User user) {
        managers.add(user);
    }

    /**
     * Increments the total amount associated with the commerciant.
     *
     * @param amount The {@code double} value to add to the total amount.
     */
    public void addAmount(final double amount) {
        finalAmount += amount;
    }

    public String getType() {
        return type;
    }
}
