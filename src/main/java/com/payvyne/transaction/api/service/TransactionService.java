package com.payvyne.transaction.api.service;

import com.payvyne.transaction.api.model.dto.TransactionDTO;
import com.payvyne.transaction.api.model.Transaction;
import com.payvyne.transaction.api.model.enums.Currency;
import com.payvyne.transaction.api.model.enums.TransactionStatus;
import com.payvyne.transaction.api.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionService {

    private TransactionRepository transactionRepository;

    public Optional<Transaction> getById(long id){
        return this.transactionRepository.findById(id);
    }

    @Transactional
    public Transaction create(TransactionDTO transactionDTO) {
        Transaction transaction = Transaction.builder()
                .amount(transactionDTO.getAmount())
                .currency(transactionDTO.getCurrency())
                .status(TransactionStatus.NEW)
                .dateTime(LocalDateTime.now())
                .description(transactionDTO.getDescription())
                .build();
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction update(long id, Transaction updatedTransaction){
        updatedTransaction.setId(id);
        return transactionRepository.save(updatedTransaction);
    }

    public void delete(long id){
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsAllOrByNotNull(Optional<Currency> currency, Optional<TransactionStatus> status, Optional<String> dateTime){
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
