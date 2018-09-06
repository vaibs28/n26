package com.n26.service;

import java.text.ParseException;
import java.util.List;

import com.n26.exception.OlderTransactionException;
import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.repository.TransactionRepository;

public class TransactionService {
    
    private TransactionRepository repository = TransactionRepository.getInstance();

    public void createTransaction (Transaction transaction) throws OlderTransactionException, ParseException{
        
        long currentTimestamp = System.currentTimeMillis();
        repository.createTransaction(transaction,currentTimestamp);
    }

    public void deleteTransaction() {
        
        repository.deleteTransaction();
    }

    public Statistics getStatistics() {
        return (repository.getStatistics());
        
        
    }
  
}
