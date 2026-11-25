package com.finapp.account.controller;

import com.finapp.account.dto.AccountsContactInfoDto;
import com.finapp.account.dto.AccountDtos.*;
import com.finapp.account.service.AccountsService;
import jakarta.validation.Valid;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@EnableConfigurationProperties(AccountsContactInfoDto.class)
public class AccountsController {

    private final AccountsContactInfoDto contactInfoDto;
    private final AccountsService accountsService;

    public AccountsController(AccountsContactInfoDto contactInfoDto, AccountsService accountsService) {
        this.contactInfoDto = contactInfoDto;
        this.accountsService = accountsService;
    }

    @PostMapping("/create")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountsService.createAccount(request));
    }

    @GetMapping("/fetch")
    public ResponseEntity<AccountResponse> fetchAccountDetails(@RequestParam("accountNumber") Long accountNumber) {
        return ResponseEntity.ok(accountsService.fetchAccount(accountNumber));
    }

    @GetMapping("/fee-account")
    public ResponseEntity<AccountResponse> fetchAccountByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(accountsService.fetchOrCreateFeeAccount(email));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestParam("sourceAccount") Long sourceAccount,
                                         @RequestParam("destinationAccount") Long destinationAccount,
                                         @RequestParam("amount") BigDecimal amount) {
        accountsService.transfer(sourceAccount, destinationAccount, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deactivate")
    public ResponseEntity<AccountResponse> deactivateAccount(@Valid @RequestBody AccountActionRequest request) {
        return ResponseEntity.ok(accountsService.setAccountActive(request.getAccountNumber(), false));
    }

    @PostMapping("/reactivate")
    public ResponseEntity<AccountResponse> reactivateAccount(@Valid @RequestBody AccountActionRequest request) {
        return ResponseEntity.ok(accountsService.setAccountActive(request.getAccountNumber(), true));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiMessageResponse> deleteAccount(@RequestParam("accountNumber") Long accountNumber) {
        accountsService.deleteAccount(accountNumber);
        return ResponseEntity.ok(new ApiMessageResponse("SUCCESS", "Account deleted successfully", null));
    }

    @GetMapping("/contact-info")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo() {
        return ResponseEntity.ok(contactInfoDto);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Accounts service is UP on port 8080");
    }
}