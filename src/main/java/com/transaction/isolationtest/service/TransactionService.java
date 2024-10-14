package com.transaction.isolationtest.service;

import com.transaction.isolationtest.domain.Account;
import com.transaction.isolationtest.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;

    /**
     * 커밋된 데이터만 읽을 수 있으며, 다른 트랜잭션에서 커밋된 데이터는 즉시 반영
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void testReadCommitted(Long accountId){
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isPresent()){
            Account acc = account.get();
            System.out.println("READ COMMITTED - 초기 balance: " + acc.getBalance());
            acc.setBalance(acc.getBalance() + 100);
            accountRepository.save(acc);
            System.out.println("READ COMMITTED - 변경된 balance: " + acc.getBalance());
        }
    }

    /**
     * 한 트랜잭션 내에서 같은 데이터를 반복 조회하더라도 동일한 결과를 보장한다.
     * 즉, 트랜잭션 중간에 다른 트랜잭션이 데이터를 수정하더라도 반영되지 않는다.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void testRepeatableRead(Long accountId){
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isPresent()){
            Account acc = account.get();
            System.out.println("REPEATABLE READ - 초기 balance: " + acc.getBalance());
            acc.setBalance(acc.getBalance() + 100);
            accountRepository.save(acc);
            System.out.println("REPEATABLE READ - 변경된 balance: " + acc.getBalance());
        }
    }

    /**
     * 가장 높은 격리 수준.
     * 트랜잭션이 마치 순차적으로 실행되는 거처럼 다른 트랜잭션이 접근하지 못하게 한다.
     * 데이터의 일관성을 최대한 보장하지만 성능이 저하될 수 있다.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void testSerializable(Long accountId){
        Optional<Account> account = accountRepository.findById(accountId);
        if(account.isPresent()){
            Account acc = account.get();
            System.out.println("SERIALIZABLE - 초기 balance: " + acc.getBalance());
            acc.setBalance(acc.getBalance() + 100);
            accountRepository.save(acc);
            System.out.println("SERIALIZABLE - 변경된 balance: " + acc.getBalance());
        }
    }
}
