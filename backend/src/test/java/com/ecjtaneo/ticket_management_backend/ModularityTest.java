package com.ecjtaneo.ticket_management_backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularityTest {

    @Test
    public void verify() {
        ApplicationModules.of(Application.class).verify();
    }

}
