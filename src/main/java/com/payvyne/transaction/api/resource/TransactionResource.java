package com.payvyne.transaction.api.resource;

import com.payvyne.transaction.api.authentication.model.Role;
import com.payvyne.transaction.api.exception.NotFoundException;
import com.payvyne.transaction.api.model.Transaction;
import com.payvyne.transaction.api.model.dto.TransactionDTO;
import com.payvyne.transaction.api.model.enums.Currency;
import com.payvyne.transaction.api.model.enums.TransactionStatus;
import com.payvyne.transaction.api.service.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;

@Tag(name = "Transaction")
@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionResource {

    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(@RequestParam Optional<Currency> currency, @RequestParam Optional<TransactionStatus> status, @RequestParam Optional<String> dateTime ) {
        List<Transaction> transactions = transactionService.getTransactionsAllOrByNotNull(currency, status, dateTime);
        if(transactions.isEmpty()){
            throw new NotFoundException("There are no transactions");
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable long id) {
        Optional<Transaction> optionalTransaction = transactionService.getById(id);
        if(!optionalTransaction.isPresent()){
            throw new NotFoundException(String.format("There is no transaction with id: %s", id));
        }
        return ResponseEntity.ok(optionalTransaction.get());
    }

    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody TransactionDTO transactionDTO){
        Transaction transaction = transactionService.create(transactionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    @PutMapping("/{id}")
    @RolesAllowed({Role.ADMIN,Role.MANAGER})
    public ResponseEntity<Transaction> update(@PathVariable long id, @RequestBody Transaction transaction){
        Transaction updateTransaction = transactionService.update(id, transaction);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updateTransaction);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({Role.ADMIN})
    public ResponseEntity delete(@PathVariable long id){
        transactionService.delete(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
