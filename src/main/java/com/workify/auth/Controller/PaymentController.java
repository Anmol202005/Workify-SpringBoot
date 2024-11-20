package com.workify.auth.Controller;

import com.razorpay.RazorpayException;
import com.workify.auth.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public String createOrder(@RequestBody Map<String, Object> data, HttpServletRequest request) throws RazorpayException {
        return paymentService.createOrder(data, request);
    }
}