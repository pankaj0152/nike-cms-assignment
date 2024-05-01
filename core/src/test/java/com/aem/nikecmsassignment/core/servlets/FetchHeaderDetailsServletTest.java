package com.aem.nikecmsassignment.core.servlets;

import com.aem.nikecmsassignment.core.services.FetchHeaderDetailsServiceImpl;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.io.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FetchHeaderDetailsServletTest {

    @InjectMocks
    private FetchHeaderDetailsServlet fetchHeaderDetailsServlet;
    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private SlingHttpServletResponse response;

    @Mock
    private PrintWriter printWriter;

    @Mock
    FetchHeaderDetailsServiceImpl fetchHeaderDetailsService;

    @Mock
    private ResourceResolver resourceResolver;

    @Test
    void doGet() throws IOException {
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(fetchHeaderDetailsService.getHeaderData(resourceResolver)).thenReturn("Header Details");
        when(response.getWriter()).thenReturn(printWriter);
        fetchHeaderDetailsServlet.doGet(request,response);
    }
}