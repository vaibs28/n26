package com.n26.model;

public class Statistics {

    double sum;
    double avg;
    double max;
    double min;
    long count;

    public Statistics() {
        min = 0;
        max = 0;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void reset() {
        this.sum = 0;
        this.avg = 0;
        this.max = Long.MIN_VALUE;
        this.min = Long.MAX_VALUE;
        this.count = 0;
    }

}
