package com.n26.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.model.Statistics;
import com.n26.service.TransactionService;

@RestController
public class StatisticsController {

    private TransactionService transactionService = new TransactionService();
    
    @RequestMapping(value = "/statistics" , method = RequestMethod.GET)
    public Statistics getStatistics() {
        return(transactionService.getStatistics());
    }
}
