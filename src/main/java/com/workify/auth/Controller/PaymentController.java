package com.workify.auth.Controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;


    @RestController
    @RequestMapping("/api/payment")
    public class PaymentController {
        //private final PaymentService paymentService;


        @PostMapping("/create-order")
        public String createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
            int amt = 10;
            var client = new RazorpayClient("rzp_test_gIpzxBwR3tbaq3", "YRolKarPEQE9leckNKSZv2pz");

            JSONObject ob = new JSONObject();
            ob.put("amount", amt * 100);
            ob.put("currency", "INR");
            ob.put("receipt", "txn_123456");


            Order order = client.Orders.create(ob);
            return ("done");
        }
    }

