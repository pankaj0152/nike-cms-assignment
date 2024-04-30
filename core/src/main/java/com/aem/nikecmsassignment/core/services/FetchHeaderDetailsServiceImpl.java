package com.aem.nikecmsassignment.core.services;

import com.day.cq.dam.api.Asset;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component(service = FetchHeaderDetailsService.class)
public class FetchHeaderDetailsServiceImpl implements FetchHeaderDetailsService {

    private static final Logger log = LoggerFactory.getLogger(FetchHeaderDetailsService.class);

    public static final String QUERY_RESOURCE = "/content/dam/NikeCMSAssignment/queries/headerQuery";
    public static final String ENDPOINT_PATH = "http://localhost:4502/content/_cq_graphql/global/endpoint.json";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BASIC_AUTH = "Basic YWRtaW46YWRtaW4=";

    @Override
    public String getHeaderData(SlingHttpServletRequest request) {
        try (ResourceResolver resourceResolver = request.getResourceResolver()) {
            String query = getQueryData(resourceResolver);
            if (StringUtils.isNotEmpty(query)) {
                return executeHttpRequest(query);
            }
            return StringUtils.EMPTY;
        }
    }
    private String executeHttpRequest(String query) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(ENDPOINT_PATH);
            httpPost.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            httpPost.setHeader(AUTHORIZATION, BASIC_AUTH);
            StringEntity entity = new StringEntity(query);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    String responseString = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                    return addLocalesInfo(responseString);
                } else {
                    log.error("Error in query response: {}", EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8));
                    return "Error in query response: " + statusCode;
                }
            }
        } catch (IOException e) {
            log.error("Error executing HTTP request: {}", e.getMessage());
            return "Error executing HTTP request";
        }
    }

    private String getQueryData(ResourceResolver resourceResolver) {
        Resource resource = resourceResolver.getResource(QUERY_RESOURCE);
        if (!ResourceUtil.isNonExistingResource(resource)) {
            Asset asset = resource.adaptTo(Asset.class);
            Resource original = asset.getOriginal();
            if (original != null) {
                try (InputStream inputStream = original.adaptTo(InputStream.class)) {
                    return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    log.error("Error reading query resource: {}", e.getMessage());
                }
            } else {
                log.error("Query resource is not found");
            }
        } else {
            log.error("Resource or asset is null");
        }
        return StringUtils.EMPTY;
    }

    private String addLocalesInfo(String responseString) {
        try {
            JSONObject responseObject = new JSONObject(responseString);
            JSONObject dataObject = responseObject.getJSONObject("data");
            JSONObject nikeHeaderListObject = dataObject.getJSONObject("nikeHeaderList");
            JSONArray itemsArray = nikeHeaderListObject.getJSONArray("items");

            String locale = extractLocale(itemsArray);
            String isoLanguageCode = locale.split("_")[0];
            String isoCountryCode = locale.split("_")[1];

            addLocaleInfoToHeaderFields(itemsArray, locale, isoLanguageCode, isoCountryCode);

            return responseObject.toString();
        } catch (JSONException e) {
            log.error("Error when adding locales");
            return StringUtils.EMPTY;
        }
    }

    private String extractLocale(JSONArray itemsArray) throws JSONException {
        JSONObject pathObject = itemsArray.getJSONObject(0);
        String path = pathObject.getString("_path");
        String[] pathParts = path.split("/");
        return pathParts[pathParts.length - 2];
    }

    private void addLocaleInfoToHeaderFields(JSONArray itemsArray, String locale, String isoLanguageCode, String isoCountryCode) throws JSONException {
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemObject = itemsArray.getJSONObject(i);
            JSONArray headerFieldsArray = itemObject.getJSONArray("headerFields");
            JSONObject headerObject = new JSONObject();
            headerObject.put("locale", locale);
            headerObject.put("isoLanguageCode", isoLanguageCode);
            headerObject.put("isoCountryCode", isoCountryCode);
            headerFieldsArray.put(0, headerObject);
        }
    }
}


