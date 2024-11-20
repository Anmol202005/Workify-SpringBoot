package com.workify.auth.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.workify.auth.models.Orders;
import com.workify.auth.models.User;
import com.workify.auth.repository.OrderRepository;
import com.workify.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    @Autowired
    public PaymentService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }
    public String createOrder(Map<String, Object> data, HttpServletRequest request) throws RazorpayException {
        int amt = 10;
        RazorpayClient client = new RazorpayClient("rzp_test_gIpzxBwR3tbaq3", "YRolKarPEQE9leckNKSZv2pz");

        JSONObject ob = new JSONObject();
        ob.put("amount", amt * 100);
        ob.put("currency", "INR");
        ob.put("receipt", "txn_123456");

        Order order = client.Orders.create(ob);

        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        Orders newOrder = new Orders();
        newOrder.setOrderId(order.get("id"));
        newOrder.setAmount(order.get("amount"));
        newOrder.setStatus(order.get("status"));
        newOrder.setUser(user.get());
        newOrder.setReceipt(order.get("receipt"));

        orderRepository.save(newOrder);
        return order.toString();
    }


}