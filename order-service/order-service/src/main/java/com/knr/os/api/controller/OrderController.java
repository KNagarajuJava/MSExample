package com.knr.os.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knr.os.api.common.TransactionRequest;
import com.knr.os.api.common.TransactionResponse;
import com.knr.os.api.service.OrderService;
import com.knt.os.api.common.Payment;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService service;
    
    @PostMapping("/bookOrder")
    public TransactionResponse bookOrder(@RequestBody TransactionRequest request) throws Exception {
        return service.saveOrder(request);
    }
    @GetMapping("/{orderId}")
    public String findOrderHistoryByOrderId(@PathVariable int orderId) throws Exception {
        return service.findOrderHistoryByOrderId(orderId) ;
    }
}
