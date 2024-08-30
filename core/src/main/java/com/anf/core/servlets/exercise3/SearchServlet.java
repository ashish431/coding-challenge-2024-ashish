package com.anf.core.servlets.excercise3;

import javax.servlet.Servlet;

import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Reference;
import javax.jcr.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "cq:Page", 
	methods = HttpConstants.METHOD_GET, 
	selectors = "search",
	extensions = "json")
@ServiceDescription("Servlet to Search the give text")

public class SearchServlet extends SlingAllMethodsServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Reference
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String searchTerm = request.getParameter("searchTerm");

        if (searchTerm == null || searchTerm.isEmpty()) {
            response.getWriter().write("[]");
            return;
        }

        ResourceResolver resourceResolver = request.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("path", "/content"); // Set your content path here
        queryMap.put("type", "cq:Page");
        queryMap.put("group.p.or", "true"); // Enable OR condition between predicates
        queryMap.put("group.1_property", "jcr:content/jcr:title");
        queryMap.put("group.1_property.operation", "like");
        queryMap.put("group.1_property.value", "%" + searchTerm + "%");
        queryMap.put("group.2_property", "jcr:content/jcr:description");
        queryMap.put("group.2_property.operation", "like");
        queryMap.put("group.2_property.value", "%" + searchTerm + "%");
        queryMap.put("p.limit", "-1");
        Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
        SearchResult searchResult = query.getResult();

        JSONArray resultsArray = new JSONArray();

        try {
            List<Hit> hits = searchResult.getHits();
            for (Hit hit : hits) {
                Resource pageResource = hit.getResource();
                Resource jcrContent = pageResource.getChild("jcr:content");

                if (jcrContent != null) {
                    JSONObject pageDetails = new JSONObject();
                    pageDetails.put("title", jcrContent.getValueMap().get("jcr:title", ""));
                    pageDetails.put("description", jcrContent.getValueMap().get("jcr:description", ""));
                    
                    String imagePath = "";
                    Resource imageNode = jcrContent.getChild("image");
                    if (imageNode != null) {
                        imagePath = imageNode.getValueMap().get("fileReference", "");
                    }
                    pageDetails.put("image", imagePath);
                    pageDetails.put("lastModified", jcrContent.getValueMap().get("cq:lastModified", ""));

                    resultsArray.put(pageDetails);
                }
            }

            if (resultsArray.length()==0) {
                JSONObject noResults = new JSONObject();
                noResults.put("message", "No results found.");
                resultsArray.put(noResults);
            }

        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error while processing the search results.");
        }

        response.setContentType("application/json");
        response.getWriter().write(resultsArray.toString());
    }
    
	/*
	 * private String findChildNode(Resource jcrContent) {
	 * if(jcrContent.hasChildren()) { Resource jcrContent =
	 * pageResource.getChild("jcr:content"); } return null; }
	 */
}