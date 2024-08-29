package com.anf.core.services.exercise1.impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.schedulers.exercise1.ProcessedDateServiceScheduler;
import com.anf.core.services.exercise1.ProcessedDateService;
import com.anf.core.services.system.ResourceResolverService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ModifiableValueMap;

@Component(service = ProcessedDateService.class)
public class ProcessDateServiceImpl implements ProcessedDateService {

	private static final Logger LOG = LoggerFactory.getLogger(ProcessedDateServiceScheduler.class);

	@Reference
	private ResourceResolverService resourceResolverService;

	@Override
	public void processDate() {
		ResourceResolver resourceResolver = resourceResolverService.createWriter();
		if (resourceResolver == null)
			LOG.error("Resolver not found");
		else {
			Map<String, String> queryMap = new HashMap<>();

			queryMap.put("path", "/content/anf-code-challenge");
			queryMap.put("type", "cq:Page");
			queryMap.put("1_property", "jcr:content/cq:lastReplicationAction");
			queryMap.put("1_property.operation", "exists");
			queryMap.put("2_property", "jcr:content/cq:lastReplicationAction");
			queryMap.put("2_property.value", "Activate");
			queryMap.put("p.and", "true");
			queryMap.put("p.limit", "-1"); // No limit on the number of results returned

			QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
			Session session = resourceResolver.adaptTo(Session.class);

			Query query = queryBuilder.createQuery(PredicateGroup.create(queryMap), session);
			SearchResult result = query.getResult();

			// QueryBuilder has a leaking ResourceResolver. This logic will handle the leaking resource resolver 
			//and close it
			ResourceResolver leakingResourceResolver = null;

			try {
				String path;
				for (Hit hit : result.getHits()) {
					if (leakingResourceResolver == null) {
						// Get a reference to Query Builder's leaking ResourceResolver
						leakingResourceResolver = hit.getResource().getResourceResolver();
					}
					path = hit.getPath().concat("/jcr:content");
					Resource resource = resourceResolver.getResource(path);
					if (resource == null)
						LOG.info("Can not find resource");
					else {
						// Adapt the resource to a ModifiableValueMap
						ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);

						if (properties != null) {
							String timeStamp = new SimpleDateFormat("HH.mm.ss").format(new java.util.Date());
							properties.put("processedDate", timeStamp);
							try {
								resourceResolver.commit();
							} catch (PersistenceException e) {
								e.printStackTrace();
							}
						} else {
							System.out.println("Could not adapt resource to ModifiableValueMap.");
						}

					}
				}
			} catch (RepositoryException e) {
				LOG.error("Error collecting search results", e);
			} finally {
				if (leakingResourceResolver != null) {
					// Closing the leaking QueryBuilder resourceResolver.
					leakingResourceResolver.close();
				}
			}
		}

	}

}
