package com.transaction.isolationtest;

import com.transaction.isolationtest.domain.Account;
import com.transaction.isolationtest.repository.AccountRepository;
import com.transaction.isolationtest.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class IsolationtestApplicationTests {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void init(){
        Account account1 = new Account();
        account1.setBalance(1000);
        accountRepository.save(account1);

        Account account2 = new Account();
        account2.setBalance(2000);
        accountRepository.save(account2);

        Account account3 = new Account();
        account3.setBalance(3000);
        accountRepository.save(account3);
    }

    @Test
    public void testReadCommitted() throws InterruptedException {
        Long accountId = 1L;

        Thread threadA = new Thread(() -> {
            transactionService.testReadCommitted(accountId);
        });

        Thread threadB = new Thread(() -> {
            try {
                Thread.sleep(500); // 트랜잭션 A가 데이터를 읽은 후 실행
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            transactionService.testReadCommitted(accountId);
        });

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();

        // 트랜잭션 수행 후 상태를 확인
        Account account = accountRepository.findById(accountId).orElseThrow();
        assertEquals(1200, account.getBalance());
    }

    @Test
    public void testRepeatableRead() throws InterruptedException {
        Long accountId = 2L;

        Thread threadA = new Thread(() -> {
            transactionService.testRepeatableRead(accountId);
            try {
                Thread.sleep(1000); // 첫 번째 조회 후 대기, threadA는 아직 커밋되지 않음
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread threadB = new Thread(() -> {
            try {
                Thread.sleep(500); // threadA가 첫 조회를 마친 후 실행
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // threadB가 값을 읽음, threadA가 커밋되기 전의 값이어야 함
            Account account = accountRepository.findById(accountId).orElseThrow();
            System.out.println("REPEATABLE READ - threadB balance: " + account.getBalance());
            assertEquals(2000, account.getBalance()); // 예상: 변경되지 않은 초기 값인 2000
        });

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();

        // 트랜잭션 수행 후 상태를 확인
        Account account = accountRepository.findById(accountId).orElseThrow();
        assertEquals(2100, account.getBalance()); // 예상: threadA의 업데이트만 반영되어 2000 + 100 = 2100
    }

    @Test
    public void testSerializable() throws InterruptedException {
        Long accountId = 3L;

        Thread threadA = new Thread(() -> {
            transactionService.testSerializable(accountId);
        });

        Thread threadB = new Thread(() -> {
            try {
                Thread.sleep(500); // 트랜잭션 A가 먼저 시작되도록 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            transactionService.testSerializable(accountId);
        });

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();

        // 트랜잭션 수행 후 상태를 확인
        Account account = accountRepository.findById(accountId).orElseThrow();
        assertEquals(3200, account.getBalance());
    }


}
