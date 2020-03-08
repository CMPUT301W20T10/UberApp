package com.cmput301w20t10.uberapp.util;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is a class that contains helper functions to make processing transactions
 * easier and more consistent.
 *
 * @author Joshua Mayer
 * @version 1.0.0
 */
public class TransactionHelper {


    /**
     * Parses a string in the form of JSON {@link org.json.JSONObject} into a transaction {@link Transaction}
     * for ease of use. If the string passed is not of a JSON format or does not contain the
     * necessary fields to be a transaction a JSONException is thrown
     *
     * @param json - The String in the JSON format to be parsed
     * @return - A Transaction parsed from the input string
     * @throws JSONException - Thrown if either the string is not of JSON format or does not
     *                          contain the necessary data for a transaction
     */
    @NotNull
    public static Transaction parseTransaction(@NotNull String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        Transaction result = new Transaction();

        result.senderUsername = obj.getString("sender");
        result.recipientUsername = obj.getString("recipient");
        result.transactionAmount = obj.getDouble("value");

        return result;
    }


    /**
     * A simple data structure to represent a transaction
     * @author Joshua Mayer
     * @version 1.0.0
     */
    public static class Transaction {
    public String senderUsername;
    public String recipientUsername;
    public double transactionAmount;
}


}
