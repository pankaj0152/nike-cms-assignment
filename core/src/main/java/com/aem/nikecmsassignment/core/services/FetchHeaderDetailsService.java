package com.aem.nikecmsassignment.core.services;

import org.apache.sling.api.SlingHttpServletRequest;

public interface FetchHeaderDetailsService {
   String getHeaderData(SlingHttpServletRequest request);
}
