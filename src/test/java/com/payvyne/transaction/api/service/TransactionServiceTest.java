package com.payvyne.transaction.api.service;

import com.payvyne.transaction.api.model.dto.TransactionDTO;
import com.payvyne.transaction.api.model.Transaction;
import com.payvyne.transaction.api.model.enums.Currency;
import com.payvyne.transaction.api.model.enums.TransactionStatus;
import com.payvyne.transaction.api.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        transaction = transactionService.create(TransactionDTO.builder()
                .amount(BigDecimal.valueOf(25000)).currency(Currency.USD).build());

    }

    @Test
    public void givenTransactionService_whenSaveAndRetreiveTransaction_thenOk(){

        Optional<Transaction> foundTransaction = transactionService.getById(transaction.getId());

        assertTrue(foundTransaction.isPresent());
        assertEquals(transaction.getAmount().doubleValue(), foundTransaction.get().getAmount().doubleValue());
        assertEquals(transaction.getCurrency(), foundTransaction.get().getCurrency());
        assertEquals(transaction.getStatus(), foundTransaction.get().getStatus());

    }

    @Test
    public void givenTransactionService_whenSaveAndUpdateTransaction_thenOk(){


        Optional<Transaction> optionalTransaction = transactionService.getById(transaction.getId());

        Transaction foundTransaction = optionalTransaction.orElse(null);
        assertNotNull(foundTransaction);

        foundTransaction.setCurrency(Currency.EUR);
        foundTransaction.setStatus(TransactionStatus.CLOSED);

        Transaction updateTransaction = transactionService.update(foundTransaction.getId(), foundTransaction);


        assertEquals(transaction.getId(), updateTransaction.getId());
        assertEquals(foundTransaction.getCurrency(),updateTransaction.getCurrency());
        assertEquals(foundTransaction.getStatus(), updateTransaction.getStatus());
        assertEquals(transaction.getAmount().doubleValue(), updateTransaction.getAmount().doubleValue());

    }

    @Test
    public void givenTransactonService_whenSaveAndDeleteTransaction_thenOK(){

        Optional<Transaction> optionalTransaction = transactionService.getById(transaction.getId());
        assertTrue(optionalTransaction.isPresent());

        transactionService.delete(optionalTransaction.get().getId());

        Optional<Transaction> actualOptional = transactionService.getById(optionalTransaction.get().getId());
        assertFalse(actualOptional.isPresent());
    }

    @Test
    public void givenTransactionService_whenSaveAndRetreiveTransactions_thenOk(){
        transactionService.create(TransactionDTO.builder()
                .amount(BigDecimal.valueOf(2000)).currency(Currency.EUR).build());
        transactionService.create(TransactionDTO.builder()
                .amount(BigDecimal.valueOf(500)).currency(Currency.USD).build());
        transactionService.create(TransactionDTO.builder()
                .amount(BigDecimal.valueOf(15000)).currency(Currency.PLN).build());

        verify(Optional.of(Currency.USD), Optional.of(TransactionStatus.NEW), Optional.empty(),2);
        verify(Optional.of(Currency.EUR), Optional.of(TransactionStatus.NEW), Optional.empty(),1);
        verify(Optional.of(Currency.PLN), Optional.of(TransactionStatus.NEW), Optional.empty(),1);
        verify(Optional.of(Currency.USD), Optional.of(TransactionStatus.CLOSED), Optional.empty(),0);
        verify(Optional.of(Currency.USD), Optional.empty(), Optional.empty(),2);
        verify(Optional.empty(), Optional.of(TransactionStatus.NEW), Optional.empty(),4);


    }

    private void verify(Optional<Currency> currency, Optional<TransactionStatus> status, Optional<String> dateTime, int expectedSize){

        List<Transaction> transactions = transactionService.getTransactionsAllOrByNotNull(currency, status, dateTime);

        assertTrue(transactions.stream().allMatch(transaction1 -> checkCurrency(transaction1,currency) && checkStatus(transaction1, status)));
        assertEquals(expectedSize, transactions.size());

    }

    private boolean checkStatus(Transaction transaction,Optional<TransactionStatus> status ){
        if(!status.isPresent()){
            return true;
        }
        return  transaction.getStatus().equals(status.get());
    }

    private boolean checkCurrency(Transaction transaction, Optional<Currency> currency){
        if(!currency.isPresent()){
            return  true;
        }
        return transaction.getCurrency().equals(currency.get());
    }

    @AfterEach
    public void setDown(){
        List<Transaction> transactions = transactionRepository.findAll();
        transactionRepository.deleteAll(transactions);
    }
}