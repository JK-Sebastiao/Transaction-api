package com.payvyne.transaction.api.service;

import com.payvyne.transaction.api.dto.TransactionDTO;
import com.payvyne.transaction.api.model.Transaction;
import com.payvyne.transaction.api.model.enums.Currency;
import com.payvyne.transaction.api.model.enums.TransactionStatus;
import com.payvyne.transaction.api.repository.TransactionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getById(String id){
        return this.transactionRepository.findById(id);
    }
     public List<Transaction> getTransactionsByAmountAndCurrency(BigDecimal amount, Currency currency){
        return transactionRepository.findByAmountAndCurrency(amount, currency);
     }

    public Transaction save(TransactionDTO transactionDTO) {
        Transaction transaction = Transaction.builder()
                .amount(transactionDTO.getAmount())
                .currency(transactionDTO.getCurrency())
                .status(TransactionStatus.NEW)
                .dateTime(LocalDateTime.now())
                .build();
        return transactionRepository.save(transaction);
    }

    public Transaction update(String id, Transaction updatedTransaction){
        Transaction transaction = transactionRepository.getById(id);
        BeanUtils.copyProperties(updatedTransaction,transaction);
        return transactionRepository.save(transaction);
    }

    public void delete(String id){
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsAllOrByNotNull(Optional<Currency> currency, Optional<TransactionStatus> status, Optional<String> dateTime ){
        return getTransactionsAllOrByNotNull(currency.orElse(null), status.orElse(null),
                dateTime.map(date -> LocalDateTime.parse(date)).orElse(null));
    }

    private List<Transaction> getTransactionsAllOrByNotNull(Currency currency, TransactionStatus status, LocalDateTime dateTime){
        if(currency == null && status == null && dateTime != null){
            return transactionRepository.findByDateTime(dateTime);
        }
        if(currency == null && status != null && dateTime == null){
            return transactionRepository.findByStatus(status);
        }
        if(currency == null && status != null && dateTime != null){
            return transactionRepository.findByStatusAndDateTime(status, dateTime);
        }
        if(currency != null && status == null && dateTime == null){
            return transactionRepository.findByCurrency(currency);
        }
        if(currency != null && status == null && dateTime != null){
            return transactionRepository.findByCurrencyAndDateTime(currency, dateTime);
        }
        if(currency != null && status != null && dateTime == null){
            return transactionRepository.findByCurrencyAndStatus(currency, status);
        }
        if(currency != null && status != null && dateTime != null){
            return transactionRepository.findByCurrencyAndStatusAndDateTime(currency,status,dateTime);
        }

        return transactionRepository.findAll();
    }
}
