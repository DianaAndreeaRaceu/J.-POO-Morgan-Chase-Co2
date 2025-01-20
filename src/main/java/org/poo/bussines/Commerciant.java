package org.poo.bussines;

import org.poo.account.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Commerciant {
    private String name;
    private double finalAmount;
    private List<User> managers;
    private List<User> employees;

    public Commerciant(final String name) {
        this.name = name;
        this.finalAmount = 0;
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
    }

    public List<User> getSortedManagers() {
        List<User> sortedManagers = new ArrayList<>(managers);
        sortedManagers.sort(Comparator.comparing(User::getFirstName));
        return sortedManagers;
    }

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

    public void addEmployee(User user) {
        employees.add(user);
    }

    public void addManager(User user) {
        managers.add(user);
    }

    public void addAmount(double amount) {
        finalAmount += amount;
    }
}
