package com.n26.repository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.n26.exception.OlderTransactionException;
import com.n26.model.Statistics;
import com.n26.model.Transaction;
import com.n26.model.TransactionStatisticsAggregator;

public class TransactionRepository {

    private static TransactionRepository instance = null;

    private final static int maxTimeMillsToKeep = 6000;

    private final static int timeMillsInterval = 1000;

    private static TransactionStatisticsAggregator[] transactionStatisticsAggregator = new TransactionStatisticsAggregator[maxTimeMillsToKeep / timeMillsInterval];

    private TransactionRepository() {
       
    }

    {
        initAggregator();
    }
    
    public static TransactionRepository getInstance() {
        if (instance == null) {
            return new TransactionRepository();
        }
        return instance;
    }

    public void createTransaction(Transaction transaction, long currentTimestamp) throws OlderTransactionException, ParseException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(tz);
        Date date = formatter.parse(transaction.getTimestamp());
        long transactionTimestamp = date.getTime();

        //if (!isTransactionValid(transactionTimestamp, currentTimestamp))
          //  throw new OlderTransactionException();

        if(transactionTimestamp>currentTimestamp)
            throw new ParseException("error in parsing", 0);
        
        aggregate(transaction, currentTimestamp);

    }

    private void aggregate(Transaction transaction, long currentTimestamp) throws ParseException {
        // getting the transaction index
        int index = getTransactionIndex(transaction);

        TransactionStatisticsAggregator txnStatisticAggregator = transactionStatisticsAggregator[index];

        try {
            txnStatisticAggregator.getLock()
                .writeLock()
                .lock();

            // in case aggregator is empty
            if (txnStatisticAggregator.isEmpty()) {
                txnStatisticAggregator.create(transaction);

            } else {
                // check if existing aggregator is still valid
                boolean b = isTransactionValid(txnStatisticAggregator.getTimestamp(), currentTimestamp);
                if (isTransactionValid(txnStatisticAggregator.getTimestamp(), currentTimestamp)) {
                    txnStatisticAggregator.merge(transaction);
                }
            }

        } finally {
            txnStatisticAggregator.getLock()
                .writeLock()
                .unlock();
        }
    }

    private int getTransactionIndex(Transaction transaction) throws ParseException {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(tz);
        Date date = formatter.parse(transaction.getTimestamp());
        long txnTime = date.getTime();

        return (int) ((txnTime / timeMillsInterval) % transactionStatisticsAggregator.length);
    }

    private boolean isTransactionValid(long txnTimestamp, long currentTimestamp) {
        long diff = (currentTimestamp - txnTimestamp)/100 % 60;
        return (diff <= 60);
    }

    public void deleteTransaction() {
        clear();
    }

    public Statistics getStatistics() {
        List<TransactionStatisticsAggregator> txnStatsAggregator = getStatisticsAggregators();

        Statistics result = new Statistics();

        txnStatsAggregator.forEach(t -> t.mergeToResult(result));

        return result;
    }

    private List<TransactionStatisticsAggregator> getStatisticsAggregators() {
        long currentTime = System.currentTimeMillis();
        return getValidTransactionStatisticsAggregators(currentTime);
    }

    public List<TransactionStatisticsAggregator> getValidTransactionStatisticsAggregators(long currentTimestamp) {

        List<TransactionStatisticsAggregator> transList = new ArrayList<>();
        if (transactionStatisticsAggregator != null) {
            for (int i = 0; i < transactionStatisticsAggregator.length; i++) {
                if (transactionStatisticsAggregator[i].getTimestamp() != 0 && isTransactionValid(transactionStatisticsAggregator[i].getTimestamp(), currentTimestamp))
                    transList.add(transactionStatisticsAggregator[i]);
            }
        }
        return transList;

    }

    private void initAggregator() {
        // fill transactionStatistics with empty statistics aggregators
        for (int x = 0; x < transactionStatisticsAggregator.length; x++) {
            transactionStatisticsAggregator[x] = new TransactionStatisticsAggregator();
        }
    }

    public void clear() {
        initAggregator();
    }

}
