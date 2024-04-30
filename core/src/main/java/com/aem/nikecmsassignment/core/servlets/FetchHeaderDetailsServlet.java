package com.aem.nikecmsassignment.core.servlets;


import com.aem.nikecmsassignment.core.services.FetchHeaderDetailsService;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Component(service = Servlet.class,
        property = {"Sling.servlet.paths="+"/bin/fetchHeaderDetails",
                    "Sling.servlet.methods="+ HttpConstants.METHOD_GET})
public class FetchHeaderDetailsServlet extends SlingSafeMethodsServlet {

    @Reference
    private FetchHeaderDetailsService fetchHeaderDetailsService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            response.getWriter().write(fetchHeaderDetailsService.getHeaderData(request));
        } catch (IOException e) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
        }
    }

}
