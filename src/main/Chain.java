package main;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.GsonBuilder;
import java.security.*;

public class Chain {
    
    protected static List<Block> blockChain = new ArrayList<>();

    // track unspent outputs to avoid having to iterate through the entire blockchain
    protected static Map<String, TransactionOutput> unspentTransactionOutputs = new HashMap<>();

    public static final int DIFFICULTY = 5;
    public static final float MINIMUM_TRANSACTION = 0.1f;

    public static Wallet walletA;
    public static Wallet walletB;

    public static Boolean isChainValid() {
         
        Block currentBlock;
        Block previousBlock;

        // iterate through each block to verify the previous hash
        for (int i=1; i<blockChain.size(); i++) {
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i-1);
            // see if the current block's hash value has changed (data has been manipulated)
            if(!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current hashes not equal!");
                return false;
            }
            // see if the previous block's hash has been changed (data has been manipulated)
            if(!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous hash not equal!");
                return false;
            }
        }
        return true;
    }

    public static void main(String args[]) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        
        walletA = new Wallet();
        walletB = new Wallet();

        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);

        System.out.println("Verification: " + transaction.verifySignature());
    }
}
