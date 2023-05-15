package com.example.application.data.service;

import com.example.application.data.entity.Users;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<Users> get(Long id) {
        return repository.findById(id);
    }

    public Users update(Users entity) {
        return repository.save(entity);
    }
    public Users create(Users entity) {
        return repository.save(entity);
    }

    public boolean EmailsMatch(Users entity) {
        Users match = repository.findByEmail(entity.getEmail());
        if (match != null)
        {
            System.out.println(match);
            return true;
        }
        return false;
    }
    public void delete(Long id) {
        repository.deleteById(id);
    }
    public Users findByUsername(String username){
        return repository.findByUsername(username);
}


  public Users findByCredentials(String username,String password)
    {
        return repository.findByUsernameAndPassword(username,password);
    }
    public Page<Users> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Users> list(Pageable pageable, Specification<Users> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
}
