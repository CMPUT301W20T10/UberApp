package com.cmput301w20t10.uberapp;

import android.util.Log;

import com.cmput301w20t10.uberapp.util.TransactionHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionHelperTest {

    private String jsonString;

    private TransactionHelperTest() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("sender", "sender_username");
            obj.put("recipient", "recipient_username");
            obj.put("value", 10.12F);

            jsonString = obj.toString();
            System.out.println(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testParseTransaction() {

        assertThrows(JSONException.class, () -> {
            TransactionHelper.Transaction trans = TransactionHelper.parseTransaction("not a json string");
        });

        TransactionHelper.Transaction transaction = null;
        try {
            transaction = TransactionHelper.parseTransaction(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assertNotNull(transaction);

        assertEquals("sender_username", transaction.senderUsername);
        assertEquals("recipient_username", transaction.recipientUsername);
        assertEquals(10.12F, transaction.transactionAmount);
    }


}
