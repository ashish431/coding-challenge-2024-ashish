package com.anf.core.services.system;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * Service to create service resource resolvers which can read or write. The configuration for the
 * users is in the ServiceUserMapper Amended osgi configuration. Use this only if you need some kind
 * of administrative ResourceResolver and have no chance to get the current resource resolver from
 * the request or resource.
 */
public interface ResourceResolverService {

  /**
   * Creates resource resolver with read rights
   *
   * @return a resource resolver with read rights
   */
  ResourceResolver createReader();

  /**
   * Creates resource resolver with read and write rights
   *
   * @return a resource resolver with read and write rights
   */
  ResourceResolver createWriter();
}
