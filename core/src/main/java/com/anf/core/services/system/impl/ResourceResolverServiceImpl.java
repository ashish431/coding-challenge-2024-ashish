package com.anf.core.services.system.impl;

import com.anf.core.services.system.ResourceResolverException;
import com.anf.core.services.system.ResourceResolverService;
import java.util.Collections;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import java.util.HashMap;
import java.util.Map;
@Component(service = ResourceResolverService.class)
public class ResourceResolverServiceImpl implements ResourceResolverService {

  static final String READER = "system-user-reader";
  static final String WRITER = "system-user-writer";

  private ResourceResolverFactory resourceResolverFactory;

  @Reference
  public void setResourceResolverFactory(ResourceResolverFactory resourceResolverFactory) {
    this.resourceResolverFactory = resourceResolverFactory;
  }

  @Override
  public ResourceResolver createReader() {
    return create(READER);
  }

  @Override
  public ResourceResolver createWriter() {
    return create(WRITER);
  }

  private ResourceResolver create(String user) {
 
    	 Map<String, Object> param = new HashMap<>();
         param.put(ResourceResolverFactory.SUBSERVICE, user);
         try {
             return resourceResolverFactory.getServiceResourceResolver(param);
         }
         catch (LoginException e) {
        	 throw new ResourceResolverException(String.format("Could not login with user {}.", user), e);
         }
  }
}
