package com.payvyne.transaction.api.resource;

import com.payvyne.transaction.api.dto.TransactionDTO;
import com.payvyne.transaction.api.exception.TransactionAPIError;
import com.payvyne.transaction.api.model.Transaction;
import com.payvyne.transaction.api.model.enums.Currency;
import com.payvyne.transaction.api.model.enums.TransactionStatus;
import com.payvyne.transaction.api.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionResource {

    private TransactionService transactionService;

    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(@RequestParam Optional<Currency> currency, @RequestParam Optional<TransactionStatus> status, @RequestParam Optional<String> dateTime ){
        List<Transaction> transactions = transactionService.getTransactionsAllOrByNotNull(currency, status, dateTime);
        if(transactions.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String id){
        Optional<Transaction> optionalTransaction = transactionService.getById(id);
        if(!optionalTransaction.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(optionalTransaction.get());
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody TransactionDTO transactionDTO){
        Transaction transaction = transactionService.save(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String id, @RequestBody Transaction transaction){
        Transaction updateTransaction = transactionService.update(id, transaction);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updateTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTransaction(@PathVariable String id){
        transactionService.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<TransactionAPIError> handlerException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(TransactionAPIError.builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Internal Server Error: "+exception.getMessage())
                .build());
    }
}
