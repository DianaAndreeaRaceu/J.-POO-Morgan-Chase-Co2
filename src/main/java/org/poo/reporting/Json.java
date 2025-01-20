package org.poo.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.account.Account;
import org.poo.account.User;
import org.poo.bussines.Bank;
import org.poo.card.Card;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

import java.util.ArrayList;
import java.util.Objects;


public final class Json {
    /**
     * Generates a JSON representation of a user.
     *
     * @param user   The user to represent in JSON.
     * @param mapper The object mapper used for creating JSON objects.
     * @return An ObjectNode representing the user.
     */
    public ObjectNode showUser(final User user, final ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("firstName", user.getFirstName());
        node.put("lastName", user.getLastName());
        node.put("email", user.getEmail());

        ArrayNode accounts = mapper.createArrayNode();
        for (Account account : user.getAccounts()) {
            accounts.add(showAccount(account, mapper));
        }
        node.set("accounts", accounts);
        return node;
    }

    /**
     * Generates a JSON representation of an account.
     *
     * @param account       The account to represent in JSON.
     * @param objectMapper  The object mapper used for creating JSON objects.
     * @return An ObjectNode representing the account.
     */
    private ObjectNode showAccount(final Account account, final ObjectMapper objectMapper) {
        ObjectNode accountNode = objectMapper.createObjectNode();
        accountNode.put("IBAN", account.getIban());
        accountNode.put("balance", account.getBalance());
        accountNode.put("currency", account.getCurrency());
        accountNode.put("type", account.getAccountType());

        ArrayNode cardsArray = objectMapper.createArrayNode();
        for (Card card : account.getCards()) {
            cardsArray.add(showCard(card, objectMapper));
        }
        accountNode.set("cards", cardsArray);
        return accountNode;
    }

    /**
     * Generates a JSON representation of a card.
     *
     * @param card           The card to represent in JSON.
     * @param objectMapper   The object mapper used for creating JSON objects.
     * @return An ObjectNode representing the card.
     */
    private ObjectNode showCard(final Card card, final ObjectMapper objectMapper) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("cardNumber", card.getCardNumber());
        cardNode.put("status", card.cardStatus());
        return cardNode;
    }

    /**
     * Generates the response for the "printUsers" command.
     *
     * @param users    The list of users to include in the response.
     * @param mapper   The object mapper used for creating JSON objects.
     * @param timestamp The timestamp associated with the command.
     * @return An ObjectNode representing the "printUsers" response.
     */
    public ObjectNode generatePrintUsersResponse(final ArrayList<User> users,
                                                 final ObjectMapper mapper, final int timestamp) {
        ObjectNode printUsersNode = mapper.createObjectNode();
        printUsersNode.put("command", "printUsers");

        ArrayNode usersArray = mapper.createArrayNode();
        for (User user : users) {
            usersArray.add(showUser(user, mapper));
        }

        printUsersNode.set("output", usersArray);
        printUsersNode.put("timestamp", timestamp);
        return printUsersNode;
    }

    /**
     * Generates the response for the "deleteAccount" command.
     *
     * @param email    The email of the user attempting to delete the account.
     * @param account  The IBAN of the account to be deleted.
     * @param timestamp The timestamp associated with the command.
     * @param command  The command input object containing details of the operation.
     * @param bank     The bank object responsible for performing the operation.
     * @param mapper   The object mapper used for creating JSON objects.
     * @return An ObjectNode representing the "deleteAccount" response.
     */
    public ObjectNode generateDeleteAccountResponse(
            final String email, final String account, final int timestamp,
            final CommandInput command, final Bank bank, final ObjectMapper mapper) {

        ObjectNode deleteAccountNode = mapper.createObjectNode();
        deleteAccountNode.put("command", "deleteAccount");

        ObjectNode outputNode = mapper.createObjectNode();
        bank.getTransactions().deleteAccount(email, account, timestamp, command);

        if ("Account deleted".equals(command.getDescription())) {
            outputNode.put("success", command.getDescription());
        } else {
            outputNode.put("error", command.getDescription());
        }
        outputNode.put("timestamp", timestamp);

        deleteAccountNode.set("output", outputNode);
        deleteAccountNode.put("timestamp", timestamp);
        return deleteAccountNode;
    }

    /**
     * Generates the response for the "payOnline" command.
     *
     * @param command  The command input object containing payment details.
     * @param bank     The bank object responsible for performing the operation.
     * @param mapper   The object mapper used for creating JSON objects.
     * @return An ObjectNode representing the "payOnline" response or null if the card is found.
     */
    public ObjectNode generatePayOnlineResponse(final CommandInput command,
                                                final Bank bank, final ObjectMapper mapper,
                                                final CommerciantInput[] commerciants) {
        bank.getTransactions().payOnline(command.getCardNumber(), command.getAmount(),
                command.getEmail(), command.getCurrency(), command, commerciants);
        if ("Card not found".equals(command.getDescription())) {
            ObjectNode payOnlineNode = mapper.createObjectNode();
            payOnlineNode.put("command", "payOnline");

            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description", command.getDescription());
            outputNode.put("timestamp", command.getTimestamp());

            payOnlineNode.set("output", outputNode);
            payOnlineNode.put("timestamp", command.getTimestamp());

            return payOnlineNode;
        }
        return null;
    }

    /**
     * Generates the response for the "printTransactions" command.
     *
     * @param email           The email of the user whose transactions are requested.
     * @param bank            The bank object responsible for retrieving user information.
     * @param mapper          The object mapper used for creating JSON objects.
     * @param commandTimestamp The timestamp associated with the command.
     * @return An ObjectNode representing the "printTransactions" response or an error.
     */
    public ObjectNode generatePrintTransactionsResponse(
            final String email, final Bank bank,
            final ObjectMapper mapper, final int commandTimestamp) {
        User user = bank.findUser(email);
        if (user == null) {
            ObjectNode errorOutput = mapper.createObjectNode();
            errorOutput.put("command", "printTransactions");
            errorOutput.put("error", "User not found");
            errorOutput.put("timestamp", commandTimestamp);
            return errorOutput;
        }
        return bank.getTransactions().printTransactions(user, mapper, commandTimestamp);
    }

    /**
     * Generates the response for the "checkCardStatus" command.
     *
     * @param command  The command input object containing card details.
     * @param bank     The bank object responsible for checking card status.
     * @param mapper   The object mapper used for creating JSON objects.
     * @param output   The array node to which the result will be added.
     */
    public void generateCheckCardStatusResponse(final CommandInput command,
                                                final Bank bank, final ObjectMapper mapper,
                                                final ArrayNode output) {

        bank.getTransactions().checkCardStatus(command.getCardNumber(),
                command.getTimestamp(), command);
        if (command.getDescription().equals("Card not found")) {
            ObjectNode check = mapper.createObjectNode();
            check.put("command", "checkCardStatus");

            ObjectNode outputCheck = mapper.createObjectNode();
            outputCheck.put("description", command.getDescription());
            outputCheck.put("timestamp", command.getTimestamp());
            check.put("output", outputCheck);
            check.put("timestamp", command.getTimestamp());
            output.add(check);
        }
    }

    public void generatecashWithdrawalResponse(final CommandInput command,
                                               final Bank bank, final ObjectMapper mapper,
                                               final ArrayNode output) {
        bank.getTransactions().cashWithdrawal(command.getCardNumber(), command.getAmount(),
                command.getEmail(), command.getLocation(), command);
        if(command.getDescription() != null) {
            ObjectNode check = mapper.createObjectNode();
            check.put("command", "cashWithdrawal");

            ObjectNode outputCheck = mapper.createObjectNode();
            outputCheck.put("description", command.getDescription());
            outputCheck.put("timestamp", command.getTimestamp());
            check.put("output", outputCheck);
            check.put("timestamp", command.getTimestamp());
            output.add(check);
        }
    }

    public void generateSendMoneyResponse(final CommandInput command, final Bank bank,
                                          final ObjectMapper mapper, final ArrayNode output,
                                          final CommerciantInput[] commerciants, final String email) {
        bank.getTransactions().sendMoney(command.getAccount(),
                command.getAmount(), command.getReceiver(), command, commerciants, email);
        if(Objects.equals(command.getDescription(), "User not found")) {
            ObjectNode check = mapper.createObjectNode();
            check.put("command", "sendMoney");

            ObjectNode outputCheck = mapper.createObjectNode();
            outputCheck.put("description", command.getDescription());
            outputCheck.put("timestamp", command.getTimestamp());
            check.put("output", outputCheck);
            check.put("timestamp", command.getTimestamp());
            output.add(check);
        }
    }

    public void generateChangeSpendingLimitResponse(final CommandInput command, final Bank bank,
                                          final ObjectMapper mapper, final ArrayNode output) {
        bank.getTransactions().changeSpendingLimit(command.getEmail(),
                command.getAccount(), command.getAmount(), command);
        if(Objects.equals(command.getDescription(),
                "You must be owner in order to change spending limit.")) {
            ObjectNode check = mapper.createObjectNode();
            check.put("command", "changeSpendingLimit");

            ObjectNode outputCheck = mapper.createObjectNode();
            outputCheck.put("description", command.getDescription());
            outputCheck.put("timestamp", command.getTimestamp());
            check.put("output", outputCheck);
            check.put("timestamp", command.getTimestamp());
            output.add(check);
        }
    }

    public void generateUpgradePlanResponse(final CommandInput command, final Bank bank,
                                                    final ObjectMapper mapper, final ArrayNode output) {
        bank.getTransactions().upgradePlan(command.getNewPlanType(),
                command.getAccount(), command.getTimestamp(), command);
        if(Objects.equals(command.getDescription(),
                "Account not found")) {
            ObjectNode check = mapper.createObjectNode();
            check.put("command", "upgradePlan");

            ObjectNode outputCheck = mapper.createObjectNode();
            outputCheck.put("description", command.getDescription());
            outputCheck.put("timestamp", command.getTimestamp());
            check.put("output", outputCheck);
            check.put("timestamp", command.getTimestamp());
            output.add(check);
        }
    }

    public void generateRejectSplitPaymentResponse(final CommandInput command, final Bank bank,
                                            final ObjectMapper mapper, final ArrayNode output) {
        bank.getTransactions().rejectSplitPayment(command.getEmail(), command);
        if(Objects.equals(command.getDescription(),
                "User not found")) {
            ObjectNode check = mapper.createObjectNode();
            check.put("command", "rejectSplitPayment");

            ObjectNode outputCheck = mapper.createObjectNode();
            outputCheck.put("description", command.getDescription());
            outputCheck.put("timestamp", command.getTimestamp());
            check.put("output", outputCheck);
            check.put("timestamp", command.getTimestamp());
            output.add(check);
        }
    }

    public void generateAcceptSplitPaymentResponse(final CommandInput command, final Bank bank,
                                                   final ObjectMapper mapper, final ArrayNode output) {
        bank.getTransactions().acceptSplitPayment(command.getEmail(), command);
        if(Objects.equals(command.getDescription(),
                "User not found")) {
            ObjectNode check = mapper.createObjectNode();
            check.put("command", "acceptSplitPayment");

            ObjectNode outputCheck = mapper.createObjectNode();
            outputCheck.put("description", command.getDescription());
            outputCheck.put("timestamp", command.getTimestamp());
            check.put("output", outputCheck);
            check.put("timestamp", command.getTimestamp());
            output.add(check);
        }
    }

}
