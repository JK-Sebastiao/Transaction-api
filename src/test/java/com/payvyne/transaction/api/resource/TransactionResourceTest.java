package com.payvyne.transaction.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payvyne.transaction.api.TransactionApiApplication;
import com.payvyne.transaction.api.model.dto.TransactionDTO;
import com.payvyne.transaction.api.model.Transaction;
import com.payvyne.transaction.api.model.enums.Currency;
import com.payvyne.transaction.api.model.enums.TransactionStatus;
import com.payvyne.transaction.api.repository.TransactionRepository;
import com.payvyne.transaction.api.service.TransactionService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@SpringBootTest(classes = TransactionApiApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionResourceTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    private ObjectMapper mapper;

    @BeforeAll
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        transactionService.create(TransactionDTO.builder()
                .amount(BigDecimal.valueOf(25000)).currency(Currency.USD).build());
        transactionService.create(TransactionDTO.builder()
                .amount(BigDecimal.valueOf(2000)).currency(Currency.EUR).build());
        transactionService.create(TransactionDTO.builder()
                .amount(BigDecimal.valueOf(500)).currency(Currency.USD).build());
        transactionService.create(TransactionDTO.builder()
                .amount(BigDecimal.valueOf(15000)).currency(Currency.PLN).build());

    }

    @Test
    public void givenTransactionResource_whenGetTransactions_thenStatus200() throws Exception {
        mvc.perform(get("/transactions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void givenTransactionResource_whenGetTransactionsWithParam_thenStatus200() throws Exception {
        mvc.perform(get("/transactions?currency=USD&status=NEW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].currency", is("USD")))
                .andExpect(jsonPath("$[0].status", is("NEW")))
                .andExpect(jsonPath("$[1].currency", is("USD")))
                .andExpect(jsonPath("$[1].status", is("NEW")))
        ;
    }

    @Test
    public void givenTransactionResource_whenPostTransactions_thenStatus201() throws Exception {
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .amount(BigDecimal.valueOf(1000))
                .currency(Currency.GBP)
                .build();
        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status", is("NEW")))
                .andExpect(jsonPath("$.currency", is("GBP")))
                .andExpect(jsonPath("$.amount", is(1000)));
    }

    @Test
    public void givenTransactionResource_whenPutTransactions_thenStatus202() throws Exception {
        Transaction  transaction = transactionService.getById(1).get();
        transaction.setAmount(BigDecimal.valueOf(9000));
        transaction.setStatus(TransactionStatus.CLOSED);
        mvc.perform(put("/transactions/"+transaction.getId())
                .contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CLOSED")))
                .andExpect(jsonPath("$.currency", is("USD")))
                .andExpect(jsonPath("$.amount", is(9000)));
    }

    @AfterAll
    public void setDown(){
        List<Transaction> transactions = transactionRepository.findAll();
        transactionRepository.deleteAll(transactions);
    }


}
