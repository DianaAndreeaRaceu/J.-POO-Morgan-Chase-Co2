package org.poo.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bussines.Commerciant;
import org.poo.bussines.Converter;
import org.poo.fileio.CommerciantInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Bussines extends Account{
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
    public Bussines(final String iban, final String currency,
                    final String accountType, final double minBalance,
                    final User user, final Converter converter) {
        super(iban, currency, accountType, minBalance);
        this.owner = user;
        this.spendingLimit = converter.convert("RON", currency, 500);
        this.depositLimit = converter.convert("RON", currency, 500);
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

    public void addEmployee(User user){
        employees.add(user);
    }

    public void addManager(User user) {
        managers.add(user);
    }

    public void addSpendingEmployee(int position, double amount) {
        spendingEmployees.add(position, amount);
    }

    public void addDepositEmployee(int position, double amount) {
        depositEmployees.add(position, amount);
    }

    public void addSpendingManager(int position, double amount) {
        spendingManagers.add(position, amount);
    }

    public void addDepositManager(int position, double amount) {
        depositManagers.add(position, amount);
    }

    public void setSpendingEmployee(int position, double amount) {
        spendingEmployees.set(position, amount);
    }

    public void setDepositEmployee(int position, double amount) {
        depositEmployees.set(position, amount);
    }

    public void setSpendingManager(int position, double amount) {
        spendingManagers.set(position, amount);
    }

    public void setDepositManager(int position, double amount) {
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

    public void setSpendingLimit(double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public double getDepositLimit() {
        return depositLimit;
    }

    public void setDepositLimit(double depositLimit) {
        this.depositLimit = depositLimit;
    }

    public void addCommerciant(Commerciant comerciant) {
        commerciants.add(comerciant);
    }

    public List<Commerciant> getCommerciants() {
        return commerciants;
    }

    @Override
    public ObjectNode spendingsReport(Account account, int firstTimestamp, int lastTimestamp, int timestamp, ObjectNode transactionsNode, ObjectMapper mapper) {
        return null;
    }

    public int isEmployee(User user) {
        int position = 0;
        for (User secondUser : employees) {
            if(Objects.equals(user.getEmail(), secondUser.getEmail())) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public int isManager(User user) {
        int position = 0;
        for (User secondUser : managers) {
            if(Objects.equals(user.getEmail(), secondUser.getEmail())) {
                return position;
            }
            position++;
        }
        return -1;
    }

    public Commerciant findCommerciant(String name) {
        for (Commerciant commerciant : commerciants) {
            if(Objects.equals(commerciant.getName(), name)) {
                return commerciant;
            }
        } return null;
    }


}
