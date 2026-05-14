package com.ecjtaneo.ticket_management_backend.user.internal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecjtaneo.ticket_management_backend.user.internal.model.User;

interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByName(String name);
}
