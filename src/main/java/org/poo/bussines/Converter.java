package org.poo.bussines;

import org.poo.fileio.ExchangeInput;

import java.util.ArrayList;

public final class Converter {
    private ArrayList<ExchangeInput> rates;

    public Converter(final ArrayList<ExchangeInput> rates) {
        this.rates = rates;
    }

    /**
     * Converts an amount from one currency to another.
     *
     * @param from   The source currency code.
     * @param to     The target currency code.
     * @param amount The amount to be converted.
     * @return The converted amount in the target currency,
     * or {@code -1} if no conversion is possible.
     */
    public double convert(final String from, final String to,
                          final double amount) {
        if (from.equals(to)) {
            return amount;
        }

        double directRate = findDirectRate(from, to);
        if (directRate != -1) {
            return amount * directRate;
        }

        double reverseRate = findDirectRate(to, from);
        if (reverseRate != -1) {
            return amount / reverseRate;
        }

        for (ExchangeInput rate : rates) {
            if (rate.getFrom().equals(from)) {
                double intermediateAmount = amount * rate.getRate();
                double finalAmount = convert(rate.getTo(), to, intermediateAmount);
                if (finalAmount != -1) {
                    return finalAmount;
                }
            } else if (rate.getTo().equals(from)) {
                double intermediateAmount = amount / rate.getRate();
                double finalAmount = convert(rate.getFrom(), to, intermediateAmount);
                if (finalAmount != -1) {
                    return finalAmount;
                }
            }
        }
        return -1;
    }

    private double findDirectRate(final String from, final String to) {
        for (ExchangeInput rate : rates) {
            if (rate.getFrom().equals(from) && rate.getTo().equals(to)) {
                return rate.getRate();
            }
        }
        return -1;
    }
}
