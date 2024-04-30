package com.aem.nikecmsassignment.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class FetchHeaderDetailsServletTest {

    AemContext aemContext = new AemContext();

    @BeforeEach
    void setUp() {

    }

    @Test
    void doGet() {
    }
}