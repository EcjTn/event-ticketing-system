package com.ecjtaneo.ticket_management_backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecjtaneo.ticket_management_backend.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByName(String name);
}
