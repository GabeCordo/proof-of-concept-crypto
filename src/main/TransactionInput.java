package main;

public class TransactionInput {
    
    public String transactionOutputId; // reference to transactionOutputs
    public TransactionOutput unspentTransactionOutputs;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
