package com.example.application.data.service;

import com.example.application.data.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ItemRepository  extends
        JpaRepository<Item, Long>,
        JpaSpecificationExecutor<Item> {
    @Query("select i from Item i " +
            "where lower(i.name) like lower(concat('%', :searchTerm, '%')) ")
    List<Item> findByNameStartsWithIgnoreCase(@Param("searchTerm") String searchTerm);

}
