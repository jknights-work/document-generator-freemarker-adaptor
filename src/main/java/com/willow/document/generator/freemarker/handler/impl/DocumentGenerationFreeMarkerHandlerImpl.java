/*
 *   File      : DocumentGenerationFreeMarkerHandlerImpl.java
 *   Author    : cmartin
 *   Copyright : Martin Technical Consulting Limited Ltd (2018)
 *   Created   : 21-Jan-2018
 *
 *   History
 *     21-Jan-2018 cmartin The initial version.
 */
package com.willow.document.generator.freemarker.handler.impl;

import com.willow.common.document.generator.service.DocumentGenerationHandler;
import com.willow.document.generator.freemarker.service.FreeMarkerDocumentGeneratorService;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * The <tt>DocumentGenerationFreeMarkerHandlerImpl</tt> class provides a handler for the DocumentGeneratorService to
 * delegate the generation process to the FreeMarkerDocumentGenerationService.
 */
public final class DocumentGenerationFreeMarkerHandlerImpl implements DocumentGenerationHandler {

  private Map<String, String> templates;

  /**
   * The FreeMarker document generation service.
   */
  private FreeMarkerDocumentGeneratorService service;

  /**
   * The key used to store the template location.
   */
  private String templateLocationAttributeKey;

  public DocumentGenerationFreeMarkerHandlerImpl() {
    templates = new HashMap<String, String>();
  }

  /**
   * Get the map of templates
   *
   * @return a map or <code>null</code> if not set
   */
  public Map<String, String> getTemplates() {
    return templates;
  }

  /**
   * Set the map of templates
   *
   * @param templates The map
   */
  public void setTemplates(Map<String, String> templates) {
    this.templates = templates;
  }

  /**
   * Get the key used to store the template location.
   *
   * @return a String or null if not set
   */
  public String getTemplateLocationAttributeKey() {
    return templateLocationAttributeKey;
  }

  /**
   * Set the key used to store the template location.
   *
   * @param key string representation of the config param
   */
  public void setTemplateLocationAttributeKey(final String key) {
    this.templateLocationAttributeKey = key;
  }

  /**
   * Get the FreeMarker generation service.
   *
   * @return an instance of FreeMarkerDocumentGeneratorService or null if not set
   */
  public FreeMarkerDocumentGeneratorService getService() {
    return service;
  }

  /**
   * Set the FreeMarker generation service.
   *
   * @param newService an instance of FreeMarkerDocumentGeneratorService
   */
  public void setService(final FreeMarkerDocumentGeneratorService newService) {
    service = newService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] generate(final Map<String, String> generateAttributes, final Map<String, Object> data,
                         final String sessionId) {
    String key = generateAttributes.get(templateLocationAttributeKey);
    String templateName = templates.get(key);
    if (templateName == null) {
      throw new IllegalArgumentException("There was no template defined for key " + key);
    }
    return getService().generate(templateName, data);
  }

}
