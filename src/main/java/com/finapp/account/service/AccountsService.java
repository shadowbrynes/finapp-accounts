package com.finapp.account.service;

import com.finapp.account.dto.AccountDtos.*;
import com.finapp.account.entity.Account;
import com.finapp.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountsService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest req) {
        if (accountRepository.findByAccountNumber(req.getAccountNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account already exists: " + req.getAccountNumber());
        }
        Account account = Account.builder()
                .accountNumber(req.getAccountNumber())
                .customerId(ThreadLocalRandom.current().nextLong(1000L, 9999L))
                .customerName(req.getCustomerName())
                .email(req.getEmail())
                .mobileNumber(req.getMobileNumber() != null ? req.getMobileNumber() : "08000000000")
                .accountType(req.getAccountType() != null ? req.getAccountType() : "SAVINGS")
                .branchAddress(req.getBranchAddress() != null ? req.getBranchAddress() : "Lagos Head Office")
                .accountBalance(req.getBalance() != null ? req.getBalance() : BigDecimal.valueOf(50000.00))
                .accountActive(true)
                .build();
        return toResponse(accountRepository.save(account));
    }

    public AccountResponse fetchAccount(Long accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountNumber));
        return toResponse(account);
    }

    @Transactional
    public AccountResponse fetchOrCreateFeeAccount(String email) {
        return accountRepository.findFirstByEmail(email)
                .map(this::toResponse)
                .orElseGet(() -> {
                    Account fee = Account.builder()
                            .accountNumber(9988776655L)
                            .customerId(9999L)
                            .customerName("FINAPP REVENUE")
                            .email(email)
                            .mobileNumber("08000000000")
                            .accountType("REVENUE")
                            .branchAddress("FinApp Treasury")
                            .accountBalance(BigDecimal.ZERO)
                            .accountActive(true)
                            .build();
                    return toResponse(accountRepository.save(fee));
                });
    }

    @Transactional
    public void transfer(Long sourceAccount, Long destinationAccount, BigDecimal amount) {
        Account src = accountRepository.findByAccountNumber(sourceAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source account not found: " + sourceAccount));
        Account dest = accountRepository.findByAccountNumber(destinationAccount)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination account not found: " + destinationAccount));

        if (!src.isAccountActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source account is inactive");
        }
        if (src.getAccountBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }
        src.setAccountBalance(src.getAccountBalance().subtract(amount));
        dest.setAccountBalance(dest.getAccountBalance().add(amount));
        accountRepository.save(src);
        accountRepository.save(dest);
    }

    @Transactional
    public AccountResponse setAccountActive(Long accountNumber, boolean active) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountNumber));
        account.setAccountActive(active);
        return toResponse(accountRepository.save(account));
    }

    @Transactional
    public void deleteAccount(Long accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found: " + accountNumber));
        accountRepository.delete(account);
    }

    private AccountResponse toResponse(Account a) {
        return AccountResponse.builder()
                .accountNumber(a.getAccountNumber())
                .customerId(a.getCustomerId())
                .customerName(a.getCustomerName())
                .email(a.getEmail())
                .mobileNumber(a.getMobileNumber())
                .accountType(a.getAccountType())
                .branchAddress(a.getBranchAddress())
                .accountBalance(a.getAccountBalance())
                .accountActive(a.isAccountActive())
                .status(a.isAccountActive() ? "ACTIVE" : "INACTIVE")
                .build();
    }
}