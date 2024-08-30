package com.anf.core.servlets.exercise3;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import javax.jcr.Session;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
@ExtendWith({MockitoExtension.class, AemContextExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class SearchServletTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Session session;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query query;

    @Mock
    private SearchResult searchResult;

    @Mock
    private Resource pageResource;

    @Mock
    private Resource jcrContent;

    @Mock
    private Resource imageNode;

    @Mock
    private ValueMap valueMap;
	

    private SearchServlet searchServlet;

    
	/*
	 * private MockSlingHttpServletRequest request; private
	 * MockSlingHttpServletResponse response;
	 */
    
    @Mock SlingHttpServletRequest request;
    @Mock SlingHttpServletResponse response;
    
    @BeforeEach
    public void setUp(AemContext context) throws Exception {
    	MockitoAnnotations.initMocks(this);
        searchServlet = new SearchServlet();

        // Use Reflection to set the QueryBuilder field
        java.lang.reflect.Field queryBuilderField = SearchServlet.class.getDeclaredField("queryBuilder");
        queryBuilderField.setAccessible(true);
        queryBuilderField.set(searchServlet, queryBuilder);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        when(queryBuilder.createQuery(any(), any(Session.class))).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
    }

    @Test
    public void testDoGet_SearchTermIsEmpty() throws Exception {
    	when(request.getParameter("searchTerm")).thenReturn("");

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        searchServlet.doGet(request, response);

        verify(response).getWriter();
        assertEquals("[]", responseWriter.toString().trim());
    }

    @Test
    public void testDoGet_NoResultsFound() throws Exception {
        when(request.getParameter("searchTerm")).thenReturn("test");
        when(searchResult.getHits()).thenReturn(new ArrayList<>());

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        searchServlet.doGet(request, response);

        verify(response).getWriter();
        assertEquals("[{\"message\":\"No results found.\"}]", responseWriter.toString().trim());
    }

    @Test
    public void testDoGet_ResultsFound() throws Exception {
        when(request.getParameter("searchTerm")).thenReturn("test");

        List<Hit> hits = new ArrayList<>();
        Hit hitMock = mock(Hit.class);
        hits.add(hitMock);

        when(searchResult.getHits()).thenReturn(hits);
        when(hitMock.getResource()).thenReturn(pageResource);
        when(pageResource.getChild("jcr:content")).thenReturn(jcrContent);
        when(jcrContent.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("jcr:title", "")).thenReturn("Test Title");
        when(valueMap.get("jcr:description", "")).thenReturn("Test Description");
        when(jcrContent.getChild("image")).thenReturn(imageNode);
        when(imageNode.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("fileReference", "")).thenReturn("/content/dam/test.jpg");
        when(valueMap.get("cq:lastModified", "")).thenReturn("2023-04-03T10:26:28.250-04:00");

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        searchServlet.doGet(request, response);

        verify(response).getWriter();
        JSONArray resultsArray = new JSONArray(responseWriter.toString().trim());
        assertEquals(1, resultsArray.length());

        JSONObject result = resultsArray.getJSONObject(0);
        assertEquals("Test Title", result.getString("title"));
        assertEquals("Test Description", result.getString("description"));
        assertEquals("/content/dam/test.jpg", result.getString("image"));
        assertEquals("2023-04-03T10:26:28.250-04:00", result.getString("lastModified"));
    }

}
