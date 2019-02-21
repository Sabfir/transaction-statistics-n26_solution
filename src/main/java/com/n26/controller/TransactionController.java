package com.n26.controller;

import com.n26.dto.TransactionStatisticDto;
import com.n26.dto.TransactionUnitDto;
import com.n26.service.TransactionService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<TransactionStatisticDto> getTransactionStatistic() {
        return new ResponseEntity<>(transactionService.getStatistic(), OK);
    }

    @PostMapping("/transactions")
    public ResponseEntity createTransaction(@RequestBody @Valid TransactionUnitDto transactionUnitDto) {
        transactionService.create(transactionUnitDto);
        return new ResponseEntity(CREATED);
    }

    @DeleteMapping("/transactions")
    public ResponseEntity removeAllTransactions() {
        transactionService.removeAll();
        return new ResponseEntity(NO_CONTENT);
    }
}
