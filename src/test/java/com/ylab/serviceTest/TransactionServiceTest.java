//package com.ylab.serviceTest;
//
//import com.ylab.entity.Transaction;
//import com.ylab.entity.TransactionType;
//import com.ylab.entity.User;
//import com.ylab.repository.TransactionRepository;
//import com.ylab.service.TransactionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class TransactionServiceTest {
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @InjectMocks
//    private TransactionService transactionService;
//
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        user = new User("test@example.com", "testUser", "password123", false);
//    }
//
//    @Test
//    void testCreateTransactionSuccess() {
//        transactionService.createTransaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, user.getEmail());
//        verify(transactionRepository).save(any(Transaction.class));
//    }
//
//    @Test
//    void testCreateTransactionInvalidAmount() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () ->
//                transactionService.createTransaction(-100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, user.getEmail()));
//        assertEquals("Сумма транзакций должна быть положительной", exception.getMessage());
//    }
//
//    @Test
//    void testDeleteTransactionSuccess() {
//        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, user.getEmail());
//        when(transactionRepository.findById(1)).thenReturn(transaction);
//        transactionService.deleteTransaction(1, user);
//        verify(transactionRepository).deleteById(1);
//    }
//
//    @Test
//    void testEditTransactionSuccess() {
//        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, user.getEmail());
//        when(transactionRepository.findById(1)).thenReturn(transaction);
//        transactionService.editTransaction(1, user, 200.0, "Bonus", "Extra", LocalDate.of(2023, 1, 2), TransactionType.EXPENSE);
//        verify(transactionRepository).save(transaction);
//        assertEquals(200.0, transaction.getAmount());
//        assertEquals("Bonus", transaction.getDescription());
//    }
//
//    @Test
//    void testGetUserTransactions() {
//        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, user.getEmail());
//        when(transactionRepository.findByUser(user)).thenReturn(Collections.singletonList(transaction));
//        List<Transaction> transactions = transactionService.getUserTransaction(user, user, null, null, null, null);
//        assertEquals(1, transactions.size());
//        assertEquals(transaction, transactions.get(0));
//    }
//}