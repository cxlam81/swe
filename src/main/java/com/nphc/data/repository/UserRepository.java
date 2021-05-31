package com.nphc.data.repository;

import com.nphc.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.login = :login")
    Optional<User> findUserByLogin(@Param("login") String login);
}
