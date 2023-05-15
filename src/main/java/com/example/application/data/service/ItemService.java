package com.example.application.data.service;

import com.example.application.data.entity.Item;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public Optional<Item> get(Long id) {
        return repository.findById(id);
    }

    public Item update(Item entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Item> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

   /* public List<Item> findAllItems(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return repository.findAll();
        } else {
            return repository.search(stringFilter);
        }
    }*/
    public List<Item> findAllItems(){
        return repository.findAll();
    }
    public List<Item> findByNameStartsWithIgnoreCase(String name){
        return repository.findByNameStartsWithIgnoreCase(name);
    }
    public Page<Item> list(Pageable pageable, Specification<Item> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
}

