package com.ecjtaneo.ticket_management_backend.user.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecjtaneo.ticket_management_backend.user.model.User;
import com.ecjtaneo.ticket_management_backend.user.model.UserRole;
import com.ecjtaneo.ticket_management_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class DemoAccounts implements ApplicationRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    @Override
    public void run(ApplicationArguments args) {

        if(repository.count() > 0) {
            System.out.println("Demo accounts alread exist. skipping.");
            return;
        }

        var user1Customer = new User();
        user1Customer.setName("IAmCustomerUser");
        user1Customer.setPassword(passwordEncoder.encode("password@12345"));

        var user2Customer = new User();
        user2Customer.setName("IAmCustomerUser2");
        user2Customer.setPassword(passwordEncoder.encode("password@12345"));

        var user2Admin = new User();
        user2Admin.setName("IAmAdminUser");
        user2Admin.setPassword(passwordEncoder.encode("password@12345"));
        user2Admin.setRole(UserRole.ADMIN);

        repository.save(user1Customer);
        repository.save(user2Customer);
        repository.save(user2Admin);
    }
    
}
