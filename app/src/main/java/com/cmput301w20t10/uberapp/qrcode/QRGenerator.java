package com.cmput301w20t10.uberapp.qrcode;

import android.graphics.Bitmap;

import com.cmput301w20t10.uberapp.models.Transaction;
import com.cmput301w20t10.uberapp.models.User;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGenerator {

    /**
     * Generates a QR code for the payment process. The QR contains a String in JSON format
     * which can easily be parsed using JSONObject. The data contained in the QR code is
     * the sender's username, recipient's username and the dollar value for the transaction
     *
     * @param sender - The user who is sending the payment
     * @param recipient - The user who is receiving the payment
     * @param value - The value of the payment
     * @return - The QR code for the transaction in the form of a bitmap
     */
    public static Bitmap generateTransactionQR(User sender, User recipient, float value) {
        JSONObject data = new JSONObject();
        try {
            data.put("sender", sender.getUsername());
            data.put("recipient", recipient.getUsername());
            data.put("value", value);

            // Todo(Joshua): Determine what the appropriate value for dimension
            QRGEncoder encoder = new QRGEncoder(data.toString(), null, QRGContents.Type.TEXT, 800);

            return encoder.encodeAsBitmap();
        } catch (JSONException | WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap generateTransactionQR(Transaction transaction) {
        return generateTransactionQR(transaction.getSender(), transaction.getRecipient(), transaction.getValue());
    }
}
