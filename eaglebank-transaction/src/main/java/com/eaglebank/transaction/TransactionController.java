package com.eaglebank.transaction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @GetMapping("/hello-transaction")
    public String helloTransaction() {
        return "Hello Transaction";
    }
}
