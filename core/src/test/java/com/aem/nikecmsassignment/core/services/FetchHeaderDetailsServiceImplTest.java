package com.aem.nikecmsassignment.core.services;

import com.day.cq.dam.api.Asset;
import com.google.gson.Gson;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.graphql.api.engine.QueryExecutor;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static junitx.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FetchHeaderDetailsServiceImplTest {

    public static final String QUERY_RESOURCE = "/content/dam/NikeCMSAssignment/queries/headerQuery";
    AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @Spy
    @InjectMocks
    FetchHeaderDetailsServiceImpl fetchHeaderDetailsService;

    ResourceResolver resourceResolver;

    @Mock
    private QueryExecutor queryExecutor;

    @Test
    void getHeaderData() throws IOException, JSONException {
        resourceResolver = aemContext.resourceResolver();
        InputStream queryInputStream = new FileInputStream("src/test/resources/response/query.txt");
        Asset asset = aemContext.create().asset(QUERY_RESOURCE,queryInputStream,"application/graphql");
        aemContext.registerAdapter(Resource.class, Asset.class,asset);
        Resource res = aemContext.create().resource( "/content/cq:graphql/global/endpoint");
        Map<String, Object> executorResponse = new HashMap<>();
        InputStream responseData = new FileInputStream("src/test/resources/response/query-response.json");
        String responseStr = IOUtils.toString(responseData);
        executorResponse = new Gson().fromJson(responseStr,executorResponse.getClass());
        doReturn(executorResponse).when(queryExecutor).execute(any(),any(),any(),any());
        String response = fetchHeaderDetailsService.getHeaderData(resourceResolver);
        JSONObject jsonObject = new JSONObject(response);
        String resLocale  = jsonObject.getJSONObject("data").getJSONObject("nikeHeaderList")
                .getJSONArray("items").getJSONObject(0)
                .getJSONArray("headerFields").getJSONObject(0)
                .getString("locale");

        assertEquals("en_US", resLocale);
    }
}