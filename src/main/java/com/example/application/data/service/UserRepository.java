package com.example.application.data.service;

import com.example.application.data.entity.Users;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.function.Function;


public interface UserRepository  extends
        JpaRepository<Users, Long>,
        JpaSpecificationExecutor<Users>,
        CrudRepository<Users,Long> {
    List<Users> findByLastNameStartsWithIgnoreCase(String lastName);
    //public Users createUser(Users user);
    public Users findByEmail(String email);

    public Users findByUsername(String username);

   @Query("select u from Users u where u.username = ?1 and u.password = ?2")
   public Users findByUsernameAndPassword(String username, String password);

   /* @Query(value = "select c from customers c where c.username = ':username' ")
    Users findUserByUsername(@Param("username") String username);

    @Query(value =  "select c from customers c where c.username = ':username' and c.password = ':password' ")
    Users findUserByCredentials(@Param("username") String username,@Param("password") String password);
    */

    @Override
    <S extends Users, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);
}
