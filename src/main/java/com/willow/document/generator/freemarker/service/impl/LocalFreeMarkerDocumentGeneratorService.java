/*
 *   File      : LocalFreeMarkerDocumentGeneratorService.java
 *   Author    : cmartin
 *   Copyright : Martin Technical Consulting Limited Ltd (2018)
 *   Created   : 21-Jan-2018
 *
 *   History
 *     21-Jan-2018 cmartin The initial version.
 */
package com.willow.document.generator.freemarker.service.impl;

/**
 *
 * The <tt>LocalFreeMarkerDocumentGeneratorService</tt> class provides a local concrete implementation of the FreeMarker
 * document generator service.
 */
public final class LocalFreeMarkerDocumentGeneratorService extends AbstractFreeMarkerDocumentGeneratorService {

  /**
   * The parameterised constructor as dictated by AbstractModelService.
   *
   * @param name the name of the service instance
   */
  public LocalFreeMarkerDocumentGeneratorService(final String name) {
    super(name);
  }

  /**
   * The default constructor as dictated by AbstractModelService.
   */
  public LocalFreeMarkerDocumentGeneratorService() {
    super("LocalFreeMarkerDocumentGeneratorService");
  }
}
