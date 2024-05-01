package com.aem.nikecmsassignment.core.services;

import com.day.cq.dam.api.Asset;
import com.google.gson.*;
import org.apache.commons.collections.map.SingletonMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.graphql.api.engine.QueryExecutor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component(service = FetchHeaderDetailsService.class)
public class FetchHeaderDetailsServiceImpl implements FetchHeaderDetailsService {

    private static final Logger log = LoggerFactory.getLogger(FetchHeaderDetailsService.class);

    public static final String QUERY_RESOURCE = "/content/dam/NikeCMSAssignment/queries/headerQuery";
    public static final String GLOBAL_ENDPOINT = "/content/cq:graphql/global/endpoint";

    @Reference
    private QueryExecutor queryExecutor;


    @Override
    public String getHeaderData(ResourceResolver resourceResolver) {
            String query = getQueryData(resourceResolver);
            if (StringUtils.isNotEmpty(query)) {
                Resource resource = resourceResolver.getResource(GLOBAL_ENDPOINT);
                Map<String, Object> executorResponse = queryExecutor.execute(query,new SingletonMap(), resource, new String[0]);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement response =  gson.toJsonTree(executorResponse);

                return addLocalesInfo(response);
            } else {
                throw new ResourceNotFoundException("Get Empty query");
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
                log.warn("Query resource is not found");
            }
        } else {
            log.warn("Resource or asset is null");
        }
        return StringUtils.EMPTY;
    }

    private String addLocalesInfo(JsonElement responseElement) {
        if (responseElement instanceof JsonObject) {
            JsonObject responseObject = responseElement.getAsJsonObject();
            if(responseObject.has("data")){
                JsonObject dataObject = responseObject.getAsJsonObject("data");
                if(dataObject.has("nikeHeaderList")){
                    JsonObject nikeHeaderListObject = dataObject.getAsJsonObject("nikeHeaderList");
                    if(nikeHeaderListObject.has("items")){
                        JsonArray itemsArray = nikeHeaderListObject.getAsJsonArray("items");
                        String locale = extractLocale(itemsArray);
                       if(StringUtils.isNotEmpty(locale)) {

                           String[] localeParts = locale.split("_");
                           if (localeParts.length >= 2) {
                               String isoLanguageCode = localeParts[0];
                               String isoCountryCode = localeParts[1];
                               addLocaleInfoToHeaderFields(itemsArray, locale, isoLanguageCode, isoCountryCode);
                               return responseObject.toString();
                           }
                       }
                    }
                }
            }
        }
           return responseElement.toString();
        }


    private String extractLocale(JsonArray itemsArray) {
        JsonObject pathObject = itemsArray.get(0).getAsJsonObject();
        String path = pathObject.get("_path").getAsString();
        String[] pathParts = path.split("/");
        return pathParts[pathParts.length - 2];
    }

    private void addLocaleInfoToHeaderFields(JsonArray itemsArray, String locale, String isoLanguageCode, String isoCountryCode) {
        for (JsonElement item : itemsArray) {
            JsonObject itemObject = item.getAsJsonObject();
            JsonArray headerFieldsArray = itemObject.getAsJsonArray("headerFields");
            JsonArray newHeaderFieldsArray = new JsonArray();
            JsonObject headerObject = new JsonObject();
            headerObject.addProperty("locale", locale);
            headerObject.addProperty("isoLanguageCode", isoLanguageCode);
            headerObject.addProperty("isoCountryCode", isoCountryCode);
            newHeaderFieldsArray.add(headerObject);
            for (JsonElement headerField : headerFieldsArray) {
                newHeaderFieldsArray.add(headerField);
            }
            itemObject.add("headerFields", newHeaderFieldsArray);
        }
    }

}

