package com.aem.nikecmsassignment.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class FetchHeaderDetailsServletTest {

    AemContext aemContext = new AemContext();

    @InjectMocks
    private FetchHeaderDetailsServlet fetchHeaderDetailsServlet;

    MockSlingHttpServletRequest request;

    MockSlingHttpServletResponse response;


    @BeforeEach
    void setUp() {
    request = aemContext.request();
    response = aemContext.response();
    }

    @Test
    void doGet() {
        fetchHeaderDetailsServlet.doGet(request,response);
    }
}