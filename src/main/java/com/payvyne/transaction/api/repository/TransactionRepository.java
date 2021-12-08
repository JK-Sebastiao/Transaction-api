package com.payvyne.transaction.api.repository;

import com.payvyne.transaction.api.model.enums.Currency;
import com.payvyne.transaction.api.model.Transaction;
import com.payvyne.transaction.api.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAmountAndCurrency(BigDecimal amount, Currency currency);
    List<Transaction> findByCurrency(Currency currency);
    List<Transaction> findByCurrencyAndDateTime(Currency currency, LocalDateTime dateTime);
    List<Transaction> findByCurrencyAndStatus(Currency currency, TransactionStatus status);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByStatusAndDateTime(TransactionStatus status, LocalDateTime dateTime);
    List<Transaction> findByDateTime(LocalDateTime dateTime);
    List<Transaction> findByCurrencyAndStatusAndDateTime( Currency currency, TransactionStatus status, LocalDateTime dateTime);

}
