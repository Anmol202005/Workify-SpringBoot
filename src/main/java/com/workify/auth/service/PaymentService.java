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
        int amt = Integer.parseInt(data.get("amount").toString());
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


    public String updateOrder(Map<String, Object> data, HttpServletRequest request) {
        Orders orders = orderRepository.findByOrderId(data.get("order_id").toString());
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
            orders.setStatus(data.get("status").toString());
            orders.setPaymentId(data.get("payment_id").toString());
            orderRepository.save(orders);
            if(orders.getStatus().equals("paid")){
                user.get().setMembership(true);
                userRepository.save(user.get());
                return ("Payment successful");
            }
            return ("Payment failed");
    }
    }