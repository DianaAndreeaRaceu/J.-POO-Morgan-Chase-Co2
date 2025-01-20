package org.poo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommerciantInput;
import org.poo.transaction.CardTransaction;
import org.poo.transaction.Transaction;

import java.util.*;

public final class Utils {
    private Utils() {
        // Checkstyle error free constructor
    }

    private static final int IBAN_SEED = 1;
    private static final int CARD_SEED = 2;
    private static final int DIGIT_BOUND = 10;
    private static final int DIGIT_GENERATION = 16;
    private static final String RO_STR = "RO";
    private static final String POO_STR = "POOB";


    private static Random ibanRandom = new Random(IBAN_SEED);
    private static Random cardRandom = new Random(CARD_SEED);

    /**
     * Utility method for generating an IBAN code.
     *
     * @return the IBAN as String
     */
    public static String generateIBAN() {
        StringBuilder sb = new StringBuilder(RO_STR);
        for (int i = 0; i < RO_STR.length(); i++) {
            sb.append(ibanRandom.nextInt(DIGIT_BOUND));
        }

        sb.append(POO_STR);
        for (int i = 0; i < DIGIT_GENERATION; i++) {
            sb.append(ibanRandom.nextInt(DIGIT_BOUND));
        }

        return sb.toString();
    }

    /**
     * Utility method for generating a card number.
     *
     * @return the card number as String
     */
    public static String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < DIGIT_GENERATION; i++) {
            sb.append(cardRandom.nextInt(DIGIT_BOUND));
        }

        return sb.toString();
    }

    /**
     * Resets the seeds between runs.
     */
    public static void resetRandom() {
        ibanRandom = new Random(IBAN_SEED);
        cardRandom = new Random(CARD_SEED);
    }

    /**
     * Generates a sorted list of merchants and their total transaction amounts
     * from a given list of transactions within a specified timestamp range.
     *
     * @param transactions   The list of transactions to process.
     * @param firstTimestamp The start of the timestamp range (inclusive).
     * @param lastTimestamp  The end of the timestamp range (inclusive).
     * @param mapper         The object mapper used for creating JSON objects.
     * @return An ArrayNode containing merchant data, sorted alphabetically by merchant name.
     */
    public static ArrayNode getSortedCommerciants(final List<Transaction> transactions,
                                                  final int firstTimestamp, final int lastTimestamp,
                                                  final ObjectMapper mapper) {
        Map<String, Double> commerciantsMap = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction.getTimestamp() >= firstTimestamp
                    && transaction.getTimestamp() <= lastTimestamp) {
                if (transaction instanceof CardTransaction
                        && transaction.getCommerciant() != null) {
                    String commerciant = transaction.getCommerciant();
                    double amount = ((CardTransaction) transaction).getAmount();

                    commerciantsMap.put(commerciant, commerciantsMap.getOrDefault(
                            commerciant, 0.0) + amount);
                }
            }
        }

        List<ObjectNode> commerciantsList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : commerciantsMap.entrySet()) {
            ObjectNode commerciantNode = mapper.createObjectNode();
            commerciantNode.put("commerciant", entry.getKey());
            commerciantNode.put("total", entry.getValue());
            commerciantsList.add(commerciantNode);
        }

        commerciantsList.sort((o1, o2)
                -> o1.get("commerciant").asText().compareTo(o2.get("commerciant").asText()));

        ArrayNode commerciantsArray = mapper.createArrayNode();
        commerciantsArray.addAll(commerciantsList);
        return commerciantsArray;
    }

    /**
     * Retrieves a commerciant based on the given IBAN.
     *
     * @param iban          The IBAN of the commerciant's account.
     * @param commerciants  An array of commerciants to search.
     * @return The {@link CommerciantInput} object matching the IBAN,
     * or {@code null} if no match is found.
     */
    public static CommerciantInput getCommerciantByAccount(final String iban,
                                                           final CommerciantInput[] commerciants) {
        for (CommerciantInput commerciant : commerciants) {
            if (Objects.equals(commerciant.getAccount(), iban)) {
                return commerciant;
            }
        }
        return null;
    }

    /**
     * Retrieves a commerciant based on the given name.
     *
     * @param name          The name of the commerciant.
     * @param commerciants  An array of commerciants to search.
     * @return The {@link CommerciantInput} object matching the name,
     * or {@code null} if no match is found.
     */
    public static CommerciantInput getCommerciantByName(final String name,
                                                        final CommerciantInput[] commerciants) {
        for (CommerciantInput commerciant : commerciants) {
            if (Objects.equals(commerciant.getCommerciant(), name)) {
                return commerciant;
            }
        }
        return null;
    }

    /**
     * Finds the position of a commerciant in the array based on its name.
     *
     * @param name          The name of the commerciant.
     * @param commerciants  An array of commerciants to search.
     * @return The position of the commerciant in the array, or {@code -1} if not found.
     */
    public static int getPositionForCommerciant(final String name,
                                                final CommerciantInput[] commerciants) {
        int position = 0;
        for (CommerciantInput commerciant : commerciants) {
            if (Objects.equals(commerciant.getCommerciant(), name)) {
                return position;
            }
            position++;
        }
        return -1;
    }

}
