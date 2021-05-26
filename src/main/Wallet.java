package main;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Wallet {
    
    public PrivateKey privateKey; // sign the data
    public PublicKey publicKey; // verify the integrity of data

    protected static Map<String, TransactionOutput> unspentTransactionOutputs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random); // 256 byte key
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: Chain.unspentTransactionOutputs.entrySet()) {
            TransactionOutput unspentTransactionOutput = item.getValue();
            if (unspentTransactionOutput.validateCoinOwnership(publicKey)) {
                unspentTransactionOutputs.put(unspentTransactionOutput.id, unspentTransactionOutput);
                total += unspentTransactionOutput.value;
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recipient, float value) {
        if (getBalance() < value) {
            System.out.println("Not enough money in your wallet!");
            return null;
        }

        List<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: unspentTransactionOutputs.entrySet()) {
            TransactionOutput unspentTransactionOutput = item.getValue();
            total += unspentTransactionOutput.value;
            inputs.add(new TransactionInput(unspentTransactionOutput.id));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs) {
            unspentTransactionOutputs.remove(input.transactionOutputId);
        }
        return newTransaction;
    }
}
