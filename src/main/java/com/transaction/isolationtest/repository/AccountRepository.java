package com.transaction.isolationtest.repository;

import com.transaction.isolationtest.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
