package org.poo.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.bussines.Bank;
import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.reporting.Json;
import org.poo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        var sortedFiles = Arrays.stream(Objects.requireNonNull(directory.listFiles())).
                sorted(Comparator.comparingInt(Main::fileConsumer))
                .toList();

        for (File file : sortedFiles) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(CheckerConstants.TESTS_PATH + filePath1);
        ObjectInput inputData = objectMapper.readValue(file, ObjectInput.class);

        ArrayNode output = objectMapper.createArrayNode();

        Utils.resetRandom();
        ArrayList<ExchangeInput> exchangeRates =
                new ArrayList<>(Arrays.asList(inputData.getExchangeRates()));
        Bank bank = new Bank(exchangeRates);

        for (int i = 0; i < inputData.getUsers().length; i++) {
            UserInput user = inputData.getUsers()[i];
            bank.addUser(user.getFirstName(), user.getLastName(),
                    user.getEmail(), user.getBirthDate(), user.getOccupation());
        }

        Json json = new Json();

        for (int i = 0; i < inputData.getCommands().length; i++) {
            CommandInput command = inputData.getCommands()[i];
            String stringCommand = command.getCommand();
            switch (stringCommand) {
                case "printUsers":
                    output.add(json.generatePrintUsersResponse(bank.getUsers(),
                            objectMapper, command.getTimestamp()));
                    break;

                case "addAccount":
                    bank.getTransactions().addAccount(command.getEmail(), command.getCurrency(),
                            command.getAccountType(), command.getTimestamp(), inputData.getCommerciants(),
                             command.getInterestRate());
                    break;

                case "deleteAccount":
                    output.add(json.generateDeleteAccountResponse(command.getEmail(),
                            command.getAccount(), command.getTimestamp(),
                            command, bank, objectMapper));
                    break;

                case "addFunds":
                    bank.getTransactions().addFunds(command.getAccount(),
                            command.getAmount(), command.getEmail());
                    break;

                case "createCard":
                    bank.getTransactions().createCard(command.getAccount(),
                            command.getEmail(), command.getTimestamp());
                    break;

                case "createOneTimeCard":
                    bank.getTransactions().createOneTimeCard(command.getAccount(),
                            command.getEmail(), command.getTimestamp());
                    break;

                case "deleteCard":
                    bank.getTransactions().deleteCard(command.getCardNumber(),
                            command.getTimestamp(), command.getEmail());
                    break;

                case "payOnline":
                    ObjectNode payOnlineResponse = json.generatePayOnlineResponse(
                            command, bank, objectMapper, inputData.getCommerciants());
                    if (payOnlineResponse != null) {
                        output.add(payOnlineResponse);
                    }
                    break;

                case "sendMoney":
                    json.generateSendMoneyResponse(command, bank, objectMapper, output,
                            inputData.getCommerciants(), command.getEmail());
                    break;

                case "printTransactions":
                    ObjectNode printTransactionsResponse = json.generatePrintTransactionsResponse(
                            command.getEmail(), bank, objectMapper, command.getTimestamp());
                    output.add(printTransactionsResponse);
                    System.out.println("A printat tranzactiile pentru " + command.getEmail() + " comanda " + command.getTimestamp());
                    break;

                case "setAlias":
                    bank.getTransactions().setAlias(command.getAccount(),
                            command.getEmail(), command.getAlias());
                    break;

                case "setMinimumBalance":
                    bank.getTransactions().setMinBalance(command.getAmount(), command.getAccount());
                    break;

                case "checkCardStatus":
                    json.generateCheckCardStatusResponse(command, bank, objectMapper, output);
                    break;

                case "splitPayment":
                    bank.getTransactions().splitPayment(command.getAccounts(),
                            command.getAmount(), command.getCurrency(), command);
                    break;

                case "addInterest":
                    ObjectNode addInterestResult = bank.getTransactions().addInterest(
                            command.getAccount(), command.getTimestamp(), objectMapper);
                    if (addInterestResult != null) {
                        output.add(addInterestResult);
                    }
                    break;

                case "changeInterestRate":
                    ObjectNode changeRateResult = bank.getTransactions().changeInterestRate(
                            command.getAccount(), command.getInterestRate(),
                            command.getTimestamp(), objectMapper);
                    if (changeRateResult != null) {
                        output.add(changeRateResult);
                    }
                    break;

                case "report":
                    ObjectNode result = bank.getTransactions().report(command.getStartTimestamp(),
                            command.getEndTimestamp(), command.getAccount(),
                            bank.getUsers(), command.getTimestamp(), objectMapper);
                    output.add(result);
                    break;

                case "spendingsReport":
                    ObjectNode spendings = bank.getTransactions().spendingsReport(
                            command.getStartTimestamp(), command.getEndTimestamp(),
                            command.getAccount(), bank.getUsers(),
                            command.getTimestamp(), objectMapper);
                    output.add(spendings);
                    break;

                case "withdrawSavings":
                    bank.getTransactions().withdrawSavings(command, command.getAccount(), command.getAmount(),
                            command.getCurrency(), command.getTimestamp());
                    break;

                case "upgradePlan":
                    json.generateUpgradePlanResponse(command, bank, objectMapper, output);
                    break;

                case "cashWithdrawal":
                    json.generatecashWithdrawalResponse(command, bank, objectMapper, output);
                    break;

                case "acceptSplitPayment":
                    json.generateAcceptSplitPaymentResponse(command, bank, objectMapper, output);
                    break;

                case "rejectSplitPayment":
                    json.generateRejectSplitPaymentResponse(command, bank, objectMapper, output);
                    break;

                case "addNewBusinessAssociate":
                    bank.getTransactions().addNewBusinessAssociate(command.getAccount(),
                            command.getRole(), command.getEmail());
                    break;

                case "changeSpendingLimit":
                    json.generateChangeSpendingLimitResponse(command, bank, objectMapper, output);
                    break;

                case "changeDepositLimit":
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!se apeleaza din main");
                    bank.getTransactions().changeDepositLimit(command.getEmail(),
                            command.getAccount(), command.getAmount());
                    break;

                case "businessReport":
                    ObjectNode businessReport = bank.getTransactions().generateBusinessReport(
                            command.getType(), command.getStartTimestamp(),
                            command.getEndTimestamp(), command.getAccount(),
                            command.getTimestamp(), objectMapper);
                    output.add(businessReport);
                    break;

                default:
                    System.out.println(command.getCommand() + "  ERROR");
                    break;
            }
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), output);
    }

    /**
     * Method used for extracting the test number from the file name.
     *
     * @param file the input file
     * @return the extracted numbers
     */
    public static int fileConsumer(final File file) {
        return Integer.parseInt(
                file.getName()
                        .replaceAll(CheckerConstants.DIGIT_REGEX, CheckerConstants.EMPTY_STR)
        );
    }
}
