package com.n26.controller;

import java.text.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.n26.exception.OlderTransactionException;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;

@RestController
public class TransactionController {

    private TransactionService transactionService = new TransactionService();

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity createTransaction(@RequestBody Transaction transaction) {

        try {
            transactionService.createTransaction(transaction);
            return ResponseEntity.status(HttpStatus.CREATED)
                .build();
        } catch (OlderTransactionException e) {
            return ResponseEntity.status(204)
                .build();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return ResponseEntity.status(422).build();
        }
    }

    @RequestMapping(value = "/transactions" , method = RequestMethod.DELETE)
    public ResponseEntity getTransactions(){
        transactionService.deleteTransaction();
        return ResponseEntity.status(204).build();
    }

}
