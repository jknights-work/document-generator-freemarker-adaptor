package com.willow.document.generator.freemarker.service;

import com.willow.common.application.ApplicationConfiguration;
import com.willow.common.application.ApplicationConfigurationFactory;
import com.willow.common.service.ServiceException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 */
public class FreeMarkerDocumentGeneratorServiceTest {

  private static final Logger LOG = Logger.getLogger(FreeMarkerDocumentGeneratorServiceTest.class);

  private static final ApplicationConfiguration CONFIG = ApplicationConfigurationFactory.getConfiguration();

  private static final FreeMarkerDocumentGeneratorService SUBJECT = (FreeMarkerDocumentGeneratorService) CONFIG.getObjectById("subject");

  private static final String expectedStringTNSUpdateSession = "{\"sourceOfFunds\":{\"provided\":{\"card\":{\"number\":\"5123456789012346\",\"expiry\":{\"year\":\"17\",\"month\":\"05\"}}},\"type\":\"CARD\"}}";
  private static final String expectedStringTNSCreateTokenSession = "{\"session\":{\"id\":\"123456789\"},\"sourceOfFunds\":{\"type\":\"CARD\"}}";
  private static final String expectedStringTNSCreateTokenRawNumber = "{\"sourceOfFunds\":{\"provided\":{\"card\":{\"number\":\"5123456789012346,\"expiry\":{\"year\":17,\"month\":05}}},\"type\":\"CARD\"}}";
  private static final String expectedSimpleTemplate = "{\"name\":\"omer\"}";
  private static final String expectedSimpleTemplateMap = "{\"person\":{\"name\":\"omer\"}}";

  public FreeMarkerDocumentGeneratorServiceTest() {
    startServices();
  }

  /**
   * Test generate method with TNS UpdateSession template, of class FreeMarkerDocumentGeneratorService.
   */
  @Test
  public void testGenerateTNSUpdateSessionTemplate() {

    LOG.info("Testing updateSessionTemplate");

    InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/updateSessionTemplate.txt");
    //String templatePath = "/dovefiles/freemarker-templates/updateSessionTemplate.txt";
    Map<String, Object> data = new HashMap<>();
    Map<String, Object> sourceOfFunds = new HashMap<>();
    Map<String, Object> provided = new HashMap<>();
    Map<String, Object> card = new HashMap<>();
    Map<String, Object> expiry = new HashMap<>();
    expiry.put("month", "05");
    expiry.put("year", "17");
    card.put("number", "5123456789012346");
    card.put("expiry", expiry);
    provided.put("card", card);
    sourceOfFunds.put("provided", provided);
    sourceOfFunds.put("type", "CARD");
    data.put("sourceOfFunds", sourceOfFunds);

    //byte[] generatedBytes = SUBJECT.generate(templatePath, data);
    byte[] generatedBytes = SUBJECT.generate(in, data);
    String generatedString = new String(generatedBytes);
    assertEquals(expectedStringTNSUpdateSession, generatedString);
  }

  public void testCreateOrUpdateTokenTemplateSession() {

    LOG.info("Testing createOrUpdateTokenTemplate");

    InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/createOrUpdateTokenTemplate.txt");
    //String templatePath = "/dovefiles/freemarker-templates/updateSessionTemplate.txt";
//    Map<String, Object> data = new HashMap<String, Object>();
    Map<String, Object> sourceOfFunds = new HashMap<>();
    Map<String, Object> provided = new HashMap<>();
    Map<String, Object> card = new HashMap<>();
    Map<String, Object> expiry = new HashMap<>();
    expiry.put("month", null);
    expiry.put("year", null);
    card.put("number", null);
    card.put("expiry", expiry);
    provided.put("card", card);
    sourceOfFunds.put("provided", provided);
//    sourceOfFunds.put("type", "CARD");
//    data.put("sourceOfFunds", sourceOfFunds);

    Map<String, Object> data = new HashMap<>();
//    Map<String, Object> sourceOfFunds = new HashMap<String, Object>();
    Map<String, Object> session = new HashMap<>();
    session.put("id", "123456789");
//    sourceOfFunds.put("provided", provided);
    data.put("sourceOfFunds", sourceOfFunds);
    data.put("session", session);

    //byte[] generatedBytes = SUBJECT.generate(templatePath, data);
    byte[] generatedBytes = SUBJECT.generate(in, data);
    String generatedString = new String(generatedBytes);
    LOG.info("Generated String : " + generatedString);
    assertEquals(expectedStringTNSCreateTokenSession, generatedString);
  }

  public void testCreateOrUpdateTokenTemplateRawNumber() {

    LOG.info("Testing createOrUpdateTokenTemplate");

    InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/createOrUpdateTokenTemplate.txt");
    //String templatePath = "/dovefiles/freemarker-templates/updateSessionTemplate.txt";
    Map<String, Object> provided = new HashMap<>();
    Map<String, Object> card = new HashMap<>();
    Map<String, Object> expiry = new HashMap<>();
    expiry.put("month", "05");
    expiry.put("year", "17");
    card.put("number", "5123456789012346");
    card.put("expiry", expiry);
    provided.put("card", card);

    Map<String, Object> data = new HashMap<>();
    Map<String, Object> sourceOfFunds = new HashMap<>();
    Map<String, Object> session = new HashMap<>();
    session.put("id", null);
    sourceOfFunds.put("provided", provided);
    data.put("sourceOfFunds", sourceOfFunds);
    data.put("session", session);

    //byte[] generatedBytes = SUBJECT.generate(templatePath, data);
    byte[] generatedBytes = SUBJECT.generate(in, data);
    String generatedString = new String(generatedBytes);
    LOG.info("Generated String : " + generatedString);
    assertEquals(expectedStringTNSCreateTokenRawNumber, generatedString);
  }

  public void testGenerateSimpleTemplate() {
    LOG.info("Testing SimpleTemplate");

    InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/simpleTemplate.txt");
    //String templatePath = "/dovefiles/freemarker-templates/simpleTemplate.txt";
    Map<String, Object> name = new HashMap<>();
    name.put("name", "omer");

    byte[] generatedBytes = SUBJECT.generate(in, name);
    String generatedString = new String(generatedBytes);
    assertEquals(expectedSimpleTemplate, generatedString);
  }

  public void testGenerateSimpleTemplateMap() {
    LOG.info("Testing SimpleTemplateMap");

    InputStream in = this.getClass().getClassLoader().getResourceAsStream("templates/simpleTemplateMap.txt");
    //String templatePath = "/dovefiles/freemarker-templates/simpleTemplateMap.txt";
    Map<String, Object> person = new HashMap<>();
    Map<String, Object> name = new HashMap<>();
    Map<String, Object> data = new HashMap<>();
    name.put("name", "omer");
    person.put("name", name);
    data.put("person", name);

    //byte[] generatedBytes = SUBJECT.generate(templatePath, data);
    byte[] generatedBytes = SUBJECT.generate(in, data);
    String generatedString = new String(generatedBytes);
    assertEquals(expectedSimpleTemplateMap, generatedString);
  }

  private void startServices() {
    if (!SUBJECT.isRunning()) {
      try {
        SUBJECT.start();
      } catch (ServiceException ex) {
        LOG.error("Failed to start service", ex);
        fail("Failed to start service");
      }
    }
  }

}
