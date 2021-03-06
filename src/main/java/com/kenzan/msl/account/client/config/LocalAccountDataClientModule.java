package com.kenzan.msl.account.client.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.kenzan.msl.account.client.services.AccountDataClientService;
import com.kenzan.msl.account.client.services.AccountDataClientServiceImpl;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * @author Kenzan
 */
public class LocalAccountDataClientModule extends AbstractModule {

  private static final String DEFAULT_MSL_KEYSPACE = "msl";
  private static final String DEFAULT_MSL_REGION = "us-west-2";
  private static final String DEFAULT_CLUSTER = "127.0.0.1";

  private final static Logger LOGGER = LoggerFactory.getLogger(AccountDataClientModule.class);

  private DynamicStringProperty keyspace = DynamicPropertyFactory.getInstance().getStringProperty("keyspace", DEFAULT_MSL_KEYSPACE);
  private DynamicStringProperty domain = DynamicPropertyFactory.getInstance().getStringProperty("domain", DEFAULT_CLUSTER);
  private DynamicStringProperty region = DynamicPropertyFactory.getInstance().getStringProperty("region", DEFAULT_MSL_REGION);

  @Override
  protected void configure() {
    bind(AccountDataClientService.class).to(AccountDataClientServiceImpl.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  public MappingManager getMappingManager() {
    configureArchaius();
    Cluster.Builder builder = Cluster.builder();
    String domainValue = domain.getValue();
    if (StringUtils.isNotEmpty(domainValue)) {
      String[] clusterNodes = StringUtils.split(domainValue, ",");
      for (String node : clusterNodes) {
        builder.addContactPoint(node);
      }
    }

    Cluster cluster = builder.build();
    Session session = cluster.connect(keyspace.getValue());
    LOGGER.debug(String.format("Keyspace: {%s}, domain: {%s}, region: {%s}", keyspace.getValue(), domainValue, region.getValue()));

    return new MappingManager(session);
  }

  private void configureArchaius() {
    Properties props = System.getProperties();
    String ENV = props.getProperty("env");
    if (StringUtils.isEmpty(ENV) || ENV.toLowerCase().contains("local")) {
      String configUrl = "file://" + System.getProperty("user.dir") + "/../msl-account-data-client-config/data-client-config.properties";
      File f = new File(configUrl);
      if(f.exists() && !f.isDirectory()) {
        System.setProperty("archaius.configurationSource.additionalUrls", configUrl);
      }
    }
  }
}
