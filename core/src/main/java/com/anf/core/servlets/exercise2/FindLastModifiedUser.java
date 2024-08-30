package com.anf.core.servlets.exercise2;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.PageManagerFactory;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "cq:Page", 
	methods = HttpConstants.METHOD_GET, 
	selectors = "findLastModifiedUser",
	extensions = {"xml", "json" })
@ServiceDescription("Servlet to find the first and last name of the user who last modified the page")
public class FindLastModifiedUser extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(FindLastModifiedUser.class);

	@Reference
	private PageManagerFactory pageManagerFactory;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		String pagePath = request.getParameter("pagePath");
		if (pagePath == null || pagePath.isEmpty()) {
			response.sendError(SlingHttpServletResponse.SC_BAD_REQUEST, "Page path is required as a parameter.");
			return;
		}

		ResourceResolver resourceResolver = request.getResourceResolver();
		PageManager pageManager = pageManagerFactory.getPageManager(resourceResolver);
		Page page = pageManager.getPage(pagePath);

		if (page == null) {
			response.sendError(SlingHttpServletResponse.SC_NOT_FOUND, "Page not found.");
			return;
		}

		//String lastModifiedBy = page.getProperties().get("cq:lastModifiedBy", String.class);
		String lastModifiedBy = getLastModifiedBy(page);

		if (lastModifiedBy == null || lastModifiedBy.isEmpty()) {
			response.sendError(SlingHttpServletResponse.SC_NOT_FOUND, "Last modified by information is not available.");
			return;
		}

		
		// Fetch user details using the correct UserManager class
		String firstName = "Unknown";
		String lastName = "Unknown";
		try {
			UserManager userManager = resourceResolver.adaptTo(UserManager.class);
			if (userManager != null) {
				Authorizable authorizable = userManager.getAuthorizable(lastModifiedBy);
				if (authorizable != null && !authorizable.isGroup()) {
					User user = (User) authorizable;
					firstName = user.getProperty("profile/givenName") != null
							? user.getProperty("profile/givenName")[0].getString()
							: "Unknown";
					lastName = user.getProperty("profile/familyName") != null
							? user.getProperty("profile/familyName")[0].getString()
							: "Unknown";
				}
			}
		} catch (RepositoryException e) {
			LOG.error("Error fetching user information: ", e);
		}

		// Fetching child pages modified by the same author
		List<String> childPages = getChildPagesModifiedByAuthor(page, lastModifiedBy);

		String extension = request.getRequestPathInfo().getExtension();
		if ("json".equalsIgnoreCase(extension)) {
			response.setContentType("application/json");
			response.getWriter().write(buildJsonResponse(firstName, lastName, childPages).toString());
		} else if ("xml".equalsIgnoreCase(extension)) {
			response.setContentType("application/xml");
			response.getWriter().write(buildXmlResponse(firstName, lastName, childPages));
		} else {
			response.sendError(SlingHttpServletResponse.SC_NOT_ACCEPTABLE, "Unsupported format requested.");
		}
	}

	private List<String> getChildPagesModifiedByAuthor(Page parentPage, String authorId) {
	    List<String> childPages = new ArrayList<>();

	    Iterator<Page> childPagesIterator = parentPage.listChildren();
	    while (childPagesIterator.hasNext()) {
	        Page child = childPagesIterator.next();
	        String childModifiedBy = child.getProperties().get("cq:lastModifiedBy", String.class);
	        
	        if (authorId.equals(childModifiedBy)) {
	            childPages.add(child.getPath());
	        }
	    }
	    return childPages;
	}

	private JSONObject buildJsonResponse(String firstName, String lastName, List<String> childPages) {
		JSONObject jsonResponse = new JSONObject();
		try {
			jsonResponse.put("firstName", firstName);
			jsonResponse.put("lastName", lastName);

			JSONArray jsonChildPages = new JSONArray();
			for (String childPage : childPages) {
				jsonChildPages.put(childPage);
			}
			jsonResponse.put("childPages", jsonChildPages);

			return jsonResponse;
		} catch (JSONException e) {
		}
		return jsonResponse;

	}

	private String buildXmlResponse(String firstName, String lastName, List<String> childPages) {
		StringBuilder xmlResponse = new StringBuilder();
		xmlResponse.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlResponse.append("<author>");
		xmlResponse.append("<firstName>").append(firstName).append("</firstName>");
		xmlResponse.append("<lastName>").append(lastName).append("</lastName>");
		xmlResponse.append("<childPages>");
		for (String childPage : childPages) {
			xmlResponse.append("<page>").append(childPage).append("</page>");
		}
		xmlResponse.append("</childPages>");
		xmlResponse.append("</author>");

		return xmlResponse.toString();
	}
	private String getLastModifiedBy(Page page) {
	    Resource contentResource = page.getContentResource(); // Getting the jcr:content node
	    if (contentResource != null) {
	        ValueMap properties = contentResource.getValueMap();
	        return properties.get("cq:lastModifiedBy", String.class);
	    }
	    return null; // Return null if there's no jcr:content or property is not set
	}
	
}