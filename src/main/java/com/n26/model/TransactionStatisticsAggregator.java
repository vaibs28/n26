package com.n26.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TransactionStatisticsAggregator {

    private ReadWriteLock lock;

    private Statistics transactionStatistics;


    private long timestamp;

    public TransactionStatisticsAggregator() {
        transactionStatistics = new Statistics();
        this.lock = new ReentrantReadWriteLock();
    }

    public ReadWriteLock getLock() {
        return lock;
    }

    public void create(Transaction transaction) {

        BigDecimal amt = new BigDecimal(transaction.getAmount());
        BigDecimal scaledValue = amt.setScale(2, RoundingMode.HALF_UP);
        
        transactionStatistics.setMin(scaledValue.doubleValue());
        transactionStatistics.setMax(scaledValue.doubleValue());
        transactionStatistics.setCount(1);
        transactionStatistics.setAvg(scaledValue.doubleValue());
        transactionStatistics.setSum(scaledValue.doubleValue());

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        formatter.setTimeZone(tz);
        Date date = null;
        try {
            date = formatter.parse(transaction.getTimestamp());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        timestamp = date.getTime();
        setTransactionStatistics(transactionStatistics);
    }

  
    public void mergeToResult(Statistics result) {
        try {
            getLock().readLock()
                .lock();
            
            
            result.setSum(result.getSum() + getTransactionStatistics().getSum());
            result.setCount(result.getCount() + getTransactionStatistics().getCount());
            result.setAvg(result.getSum() / result.getCount());

            if (result.getMin() > getTransactionStatistics().getMin()) {
                result.setMin(getTransactionStatistics().getMin());
            }
            if (result.getMax() < getTransactionStatistics().getMax()) {
                result.setMax(getTransactionStatistics().getMax());
            }
        } finally {
            getLock().readLock()
                .unlock();
        }
    }

    
    public void merge(Transaction transaction) {
        BigDecimal amt = new BigDecimal(transaction.getAmount());
        BigDecimal scaledValue = amt.setScale(2, RoundingMode.HALF_UP);
        
        transactionStatistics.setSum(transactionStatistics.getSum() + scaledValue.doubleValue());
        transactionStatistics.setCount(transactionStatistics.getCount() + 1);
        transactionStatistics.setAvg(transactionStatistics.getSum() / transactionStatistics.getCount());

        if (transactionStatistics.getMin() > transaction.getAmount()) {
            transactionStatistics.setMin(scaledValue.doubleValue());
        }
        if (transactionStatistics.getMax() < transaction.getAmount()) {
            transactionStatistics.setMax(scaledValue.doubleValue());
        }

    }

    public boolean isEmpty() {
        return transactionStatistics.getCount() == 0;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Statistics getTransactionStatistics() {
        return transactionStatistics;
    }

    public void setTransactionStatistics(Statistics transactionStatistics) {
        this.transactionStatistics = transactionStatistics;
    }

    public void reset() {
        transactionStatistics.reset();
        timestamp = 0;
    }

}