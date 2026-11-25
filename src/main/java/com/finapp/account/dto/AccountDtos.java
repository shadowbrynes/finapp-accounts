package com.finapp.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

public class AccountDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateAccountRequest {
        @NotNull(message = "accountNumber is required")
        private Long accountNumber;

        @NotBlank(message = "customerName is required")
        private String customerName;

        @NotBlank(message = "email is required")
        private String email;

        private String mobileNumber;
        private String accountType;
        private String branchAddress;
        private BigDecimal balance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountActionRequest {
        @NotNull(message = "accountNumber is required")
        private Long accountNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountResponse {
        private Long accountNumber;
        private Long customerId;
        private String customerName;
        private String email;
        private String mobileNumber;
        private String accountType;
        private String branchAddress;
        private BigDecimal accountBalance;
        private boolean accountActive;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiMessageResponse {
        private String status;
        private String message;
        private Object data;
    }
}