package main;

import java.security.*;

public class TransactionOutput {
    
    public String id;
    public PublicKey recipient;
    public float value;
    public String parentTransactionId;

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
    }

    public boolean validateCoinOwnership(PublicKey publicKey) {
        return (publicKey == recipient);
    }
}