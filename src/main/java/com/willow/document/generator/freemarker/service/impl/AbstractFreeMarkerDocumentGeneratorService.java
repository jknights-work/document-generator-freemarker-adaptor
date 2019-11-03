/*
 *   File      : AbstractFreeMarkerDocumentGeneratorService.java
 *   Author    : cmartin
 *   Copyright : Martin Technical Consulting Limited Ltd (2018)
 *   Created   : 21-Jan-2018
 *
 *   History
 *     21-Jan-2018 cmartin The initial version.
 */
package com.willow.document.generator.freemarker.service.impl;

import com.willow.common.document.generator.service.DocumentGenerationException;
import com.willow.common.document.generator.support.DocumentGeneratorStringTool;
import com.willow.common.service.impl.AbstractService;
import com.willow.document.generator.freemarker.service.FreeMarkerDocumentGeneratorService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * The <tt>AbstractFreeMarkerDocumentGeneratorService</tt> class provides a default implementation of a FreeMarker
 * document generator service.
 */
public abstract class AbstractFreeMarkerDocumentGeneratorService
        extends AbstractService implements FreeMarkerDocumentGeneratorService {

  /** The class logger. */
  private static final Logger LOG = Logger.getLogger(AbstractFreeMarkerDocumentGeneratorService.class);

  /**  The default charset for document content. */
  private static final String DEFAULT_DOCUMENT_CHARSET = "utf-8";
  /**  The Document Generator string utility. */
  private DocumentGeneratorStringTool stringUtil;
  /**
   * The base template path *
   */
  private String basePath;

  /**
   * The default constructor as dictated by AbstractModelService.
   */
  public AbstractFreeMarkerDocumentGeneratorService() {
    super("FreeMarkerDocumentGeneratorService");
  }

  /**
   * The parameterised constructor as dictated by AbstractModelService.
   *
   * @param serviceName the name of the service instance
   */
  public AbstractFreeMarkerDocumentGeneratorService(final String serviceName) {
    super(serviceName);
  }

  /**
   * Get the Document Generator string utility.
   *
   * @return an instance of DocumentGeneratorStringUtil or null if not set
   */
  public final DocumentGeneratorStringTool getStringUtil() {
    return stringUtil;
  }

  /**
   * Set the Document Generator string utility.
   *
   * @param util an instance of DocumentGeneratorStringUtil or null if not set
   */
  public final void setStringUtil(final DocumentGeneratorStringTool util) {
    stringUtil = util;
  }

  /**
   * Get the templates base path.
   *
   * @return a String
   */
  public String getBasePath() {
    return basePath;
  }

  /**
   * Set the templates base path.
   *
   * @param newBasePath the base path or null if not set
   */
  public void setBasePath(String newBasePath) {
    basePath = newBasePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean doStart() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean doStop() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void createTemplate(final String templatePath, final String content) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Attempting to create template at location " + templatePath);
    }
    File file = new File(templatePath);
    try {
      Charset charset = Charset.forName(DEFAULT_DOCUMENT_CHARSET);
      Files.write(Paths.get(file.toURI()), content.getBytes(charset), StandardOpenOption.CREATE_NEW);
    } catch (IOException ex) {
      throwCouldNotCreateFileException(file.getPath(), ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getTemplate(final String templatePath) {
    String result = "";
    if (LOG.isDebugEnabled()) {
      LOG.debug("Attempting to get template" + templatePath + " from " + basePath);
    }
    // Consider refactoring to use DocumentStorage service instead
    File basePathFile = new File(basePath);
    File file = new File(basePathFile, templatePath);
    if (file.isFile()) {
      try {
        Charset charset = Charset.forName(DEFAULT_DOCUMENT_CHARSET);
        byte[] readBytes = Files.readAllBytes(file.toPath());
        result = new String(readBytes, charset);
      } catch (IOException ex) {
        throwReadErrorException(file.getPath(), ex);
      }
    } else {
      throwFileNotReadableException();
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void updateTemplate(final String templatePath, final String content) {
    deleteTemplate(templatePath);
    createTemplate(templatePath, content);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void deleteTemplate(final String templatePath) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Attempting to delete template at " + templatePath);
    }
    File file = new File(templatePath);
    if (!file.delete()) {
      throwCouldNotDeleteException(templatePath);
    }
  }

  /**
   * Build a FreeMarker configuration object based on the properties of this instance.
   *
   * @return a FreeMarker Configuration object
   */
  private Configuration buildFreeMarkerConfiguration() throws IOException {
    Configuration result = new Configuration();
    result.setObjectWrapper(ObjectWrapper.DEFAULT_WRAPPER);
    result.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
    
    //ClassTemplateLoader is for reading the freemarker templates that are "included" from 
    //within the main template. This will read both from the classpath (eg jar files) and the file system.
    ClassTemplateLoader ct1 = new ClassTemplateLoader(this.getClass(), "/");
    TemplateLoader[] templateLoaders = new TemplateLoader[]{ct1}; 
    MultiTemplateLoader multiTemplateLoader = new MultiTemplateLoader(templateLoaders);
    result.setTemplateLoader(multiTemplateLoader);
    //end section to read "includes"
    
    return result;
  }

  /**
   * Create a FreeMarkertemplate object.
   *
   * @param documentContent the document content to use as the FreeMarker template
   *
   * @return a FreeMarker Template object
   */
  private Template createFreeMarkerTemplate(final String documentContent) {
    Template result = null;
    try {
      StringReader documentContentReader = new StringReader(documentContent);
      result = new Template("name", documentContentReader, buildFreeMarkerConfiguration());
    } catch (IOException ex) {
      throwTemplateException(ex);
    }
    return result;
  }
  
  private Template createFreeMarkerTemplate(final InputStream documentContent) {
    Template result = null;
    try {
      InputStreamReader inputStreamReader = new InputStreamReader(documentContent);
      result = new Template("name", inputStreamReader, buildFreeMarkerConfiguration());
    } catch (IOException ex) {
      throwTemplateException(ex);
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final byte[] generate(final String templatePath, final Map<String, Object> data) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Attempting to generate template " + templatePath);
    }
    String documentContent = getTemplate(templatePath);
    Writer outWriter = new StringWriter();
    Template t = createFreeMarkerTemplate(documentContent);
    try {
      t.process(data, outWriter);
    } catch (TemplateException ex) {
      throwProcessingException(ex);
    } catch (IOException ex) {
      throwProcessingException(ex);
    }
    byte[] result = getStringUtil().convertStringToBytes(outWriter.toString());
    return result;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public final byte[] generate(final InputStream inputStream, final Map<String, Object> data) {
    Writer outWriter = new StringWriter();
    Template t = createFreeMarkerTemplate(inputStream);
    try {
      t.process(data, outWriter);
    } catch (TemplateException ex) {
      throwProcessingException(ex);
    } catch (IOException ex) {
      throwProcessingException(ex);
    }
    byte[] result = getStringUtil().convertStringToBytes(outWriter.toString());
    return result;
  }

  /**
   * Throw an exception describing failure to create a document file.
   *
   * @param fileName the file that could not be created
   * @param ex an exception thrown by File
   */
  private void throwCouldNotCreateFileException(final String fileName, final Exception ex) {
    String msg = String.format("File %s could not be created", fileName);
    throw new IllegalArgumentException(msg, ex);
  }

  /**
   * Throw an exception describing failure to delete document content.
   *
   * @param fileName the file that couldn't be deleted
   */
  private void throwCouldNotDeleteException(final String fileName) {
    String msg = String.format("File %s could not be deleted", fileName);
    throw new IllegalArgumentException(msg);
  }

  /**
   * Throw an exception describing failure to read a file.
   *
   * @param filePath the file in question
   * @param ex the exception raised by File
   */
  private void throwReadErrorException(final String filePath, final Exception ex) {
    String message = String.format("An error occurred when reading file %s", filePath);
    throw new DocumentGenerationException(message, ex);
  }

  /**
   * Throw an exception describing issues with the template definition raised by FreeMarker.
   *
   * @param ex the exception from FreeMarker
   */
  private void throwTemplateException(final Exception ex) {
    String msg = "An error error when creating the FreeMarker Template object";
    throw new DocumentGenerationException(msg, ex);
  }

  /**
   * Throw an exception describing issues raised by FreeMarker when processing the template.
   *
   * @param ex the exception from FreeMarker
   */
  private void throwProcessingException(final Exception ex) {
    String msg = "An error occurred when processing the FreeMarker template";
    throw new DocumentGenerationException(msg, ex);
  }

  /**
   * Throw an exception describing a failure to read the file.
   */
  private void throwFileNotReadableException() {
    String msg = "An error occurred when attempting to read the file";
    throw new IllegalArgumentException(msg);
  }
}
