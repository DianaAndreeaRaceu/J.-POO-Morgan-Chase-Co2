package org.poo.fileio;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class ObjectInput {
    private UserInput[] users;
    private ExchangeInput[] exchangeRates;
    private CommandInput[] commands;
    private CommerciantInput[] commerciants;

    public CommerciantInput[] getCommerciants() {
        return commerciants;
    }

    public CommandInput[] getCommands() {
        return commands;
    }

    public ExchangeInput[] getExchangeRates() {
        return exchangeRates;
    }

    public UserInput[] getUsers() {
        return users;
    }
}
