package com.example.application.data.service;

import com.example.application.data.entity.Cart;
import com.example.application.data.entity.Item;
import com.example.application.data.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface OrderRepository  extends
        JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    //List<Cart> findByItemStartsWithIgnoreCase(String name);
    //List<Cart> findByCartStartsWithIgnoreCase(Cart cart);

}
