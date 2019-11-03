/*
 *   File      : FreeMarkerDocumentGeneratorService.java
 *   Author    : cmartin
 *   Copyright : Martin Technical Consulting Limited Ltd (2018)
 *   Created   : 21-Jan-2018
 *
 *   History
 *     21-Jan-2018 cmartin The initial version.
 */

package com.willow.document.generator.freemarker.service;

import com.willow.common.service.Service;
import java.io.InputStream;
import java.util.Map;

/**
 * The FreeMarkerDocumentGeneratorService interface describes a service that generates documents usingFreeMarker as the
 * underlying generation engine.
 */
public interface FreeMarkerDocumentGeneratorService extends Service {

  /**
   * Create a template at the named location.
   *
   * @param templatePath the destination for saving the template
   * @param content the template content
   */
  void createTemplate(String templatePath, String content);

  /**
   * Return the contents of a named template.
   *
   * @param templatePath the location of the template
   * @return the template content
   */
  String getTemplate(String templatePath);

  /**
   * Update template contents.
   *
   * @param templatePath the path to the template
   * @param content the new template content
   */
  void updateTemplate(String templatePath, String content);

  /**
   * Delete a template.
   *
   * @param templatePath the path to the template
   */
  void deleteTemplate(String templatePath);

  /**
   * Generate a document.
   *
   * @param templatePath the path to the document template
   * @param data the template data
   * @return the generated content
   */
  byte[] generate(String templatePath, Map<String, Object> data);
  
  /**
   * Generate a document from an input stream
   *
   * @param template the template as an input stream
   * @param data the template data
   * @return the generated content
   */
  byte[] generate(InputStream template, Map<String, Object> data);
}
