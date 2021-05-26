package main;

import java.security.*;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    
    public String transactionId; // hash of the transcription
    public PublicKey sender; // senders public key
    public PublicKey reciepient; // receivers public key
    public static float value;
    public byte[] signature; // proof of wallet/funds 
    
    // references to previous transaction outputs
    public List<TransactionInput> inputs = new ArrayList<TransactionInput>();
    // reference to a coin sent to your wallet
    // => your balance is the sum of all unspent transaction outputs addressed to you
    public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
        this.sender = from;
        this.reciepient = to;
        this.value = value;
        this.inputs = inputs;
        this.transactionId = calculateHash();
    }

    public String calculateHash() {
        sequence++; // incremement the sequence pointer to ensure there are no two identical transactions
        return StringUtil.applySha256(
                            StringUtil.getStringFromKey(sender) +
                            StringUtil.getStringFromKey(reciepient) +
                            Float.toString(value) +
                            sequence
                            );          
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.unspentTransactionOutputs == null) continue;
            total += i.unspentTransactionOutputs.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o: outputs) {
            total += o.value;
        }
        return total;
    }

    public boolean processTransaction() {
         if (verifySignature() == false) {
             System.out.println("Signature Mismatch - Verification Failed");
             return false;
         }

         for (TransactionInput i : inputs) {
             i.unspentTransactionOutputs = Chain.unspentTransactionOutputs.get(i.transactionOutputId);
         }

         if (getInputsValue() < Chain.MINIMUM_TRANSACTION) {
             System.out.println("Transaction size too small");
             return false;
         }

         float leftOver = getInputsValue() - value;
         transactionId = calculateHash(); // the remaining value of the wallet

         outputs.add(new TransactionOutput(this.reciepient, value, transactionId)); // send amount to recipient
         // a transaction output can only be used once as an input, so we need to send ourselves a remainder as
         // the new transaction output
         outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

         for (TransactionOutput o : outputs) {
             Chain.unspentTransactionOutputs.put(o.id, o);
         }

         for (TransactionInput i : inputs) {
             if (i.unspentTransactionOutputs == null) continue;
             Chain.unspentTransactionOutputs.remove(i.unspentTransactionOutputs.id);
         }
         return true;
    }
}
