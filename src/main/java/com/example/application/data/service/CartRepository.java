package com.example.application.data.service;

import com.example.application.data.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface CartRepository  extends
        JpaRepository<Cart, Long>,
        JpaSpecificationExecutor<Cart> {
    //List<Cart> findByUserStartsWithIgnoreCase(String name);
}
