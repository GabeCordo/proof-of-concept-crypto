package main;

import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;

    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash(); //create a sha256 hash to identify the current block
    }
    
    public String calculateHash() {
        return StringUtil.applySha256(previousHash + Long.toString(timeStamp) + data);
    }

    public void mineBlock(int difficulty) {
        String proof = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0,difficulty).equals(proof)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!! " + hash);
    }
}