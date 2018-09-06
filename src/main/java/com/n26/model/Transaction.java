package com.n26.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;


@com.fasterxml.jackson.annotation.JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Transaction {
    
    private double amount;
    private String timestamp;
    
    public Transaction() {}
    
    public Transaction(double amount, String timestamp) {
        // TODO Auto-generated constructor stub
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    
}
