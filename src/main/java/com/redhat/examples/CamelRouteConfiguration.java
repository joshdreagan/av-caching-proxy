/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.examples;

import java.util.concurrent.TimeUnit;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CamelRouteConfiguration extends RouteBuilder {

  private static final Logger log = LoggerFactory.getLogger(CamelRouteConfiguration.class);
  
  @Autowired
  ApplicationConfiguration config;
  
  @Bean
  DefaultCacheManager defaultCacheManager() {
    DefaultCacheManager cacheManager = new DefaultCacheManager();
    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    cacheManager.createCache("OVERVIEW", configurationBuilder.simpleCache(true).build());
    return cacheManager;
  }
  
  @Override
  public void configure() {
    
    from("rest:get:{path}?produces=application/json&bindingMode=off")
      .filter().constant(!config.cache().enabled())
        .to("seda:backend")
        .stop()
      .end()
      .choice()
        .when().simple("${header.function} != ${null}")
          .to("direct:cachingProxy")
        .otherwise()
          .to("seda:backend")
      .end()
      .removeHeaders(".*")
    ;
    
    from("direct:cachingProxy")
      .log(LoggingLevel.INFO, log, "Fetching from cache: symbol='${header.symbol}'")
      .routingSlip().simple("infinispan-embedded:${header.function.toUpperCase()}?cacheContainer=#defaultCacheManager&operation=GET&key=${header.symbol.toUpperCase()}")
      .filter().simple("${body} == ${null}")
        .to("seda:backend")
        .setHeader(InfinispanConstants.KEY).simple("${header.symbol.toUpperCase()}")
        .setHeader(InfinispanConstants.VALUE).body()
        .setHeader(InfinispanConstants.LIFESPAN_TIME).constant(config.cache().ttl())
        .setHeader(InfinispanConstants.LIFESPAN_TIME_UNIT).constant(TimeUnit.MILLISECONDS.toString())
        .wireTap("infinispan-embedded:${header.function.toUpperCase()}?cacheContainer=#defaultCacheManager&operation=PUT")
      .end()
    ;
    
    from("seda:backend?blockWhenFull=true&purgeWhenStopping=true")
      .to("direct:backend")
    ;
    from("direct:backend")
      .log(LoggingLevel.INFO, log, "Fetching from backend: symbol='${header.symbol}'")
      .throttle(config.alphaVantage().throttleRequests()).timePeriodMillis(config.alphaVantage().throttleRequests()).disabled(!config.alphaVantage().throttleEnabled())
      .routingSlip().simple(String.format("%s://%s:%s/${header.path}?followRedirects=true&bridgeEndpoint=true", config.alphaVantage().scheme(), config.alphaVantage().host(), config.alphaVantage().port()))
      .log(LoggingLevel.DEBUG, log, "Backend response: symbol='${header.symbol}', response='${body}'")
      .filter().simple("${body} == ${null}")
        .log(LoggingLevel.WARN, log, "Unable to get backend response: symbol='${header.symbol}', message='Empty/null response returned.'")
        .stop()
      .end()
      .filter().jq("has(\"Error Message\") or has(\"Information\")")
        .log(LoggingLevel.WARN, log, "Unable to get backend response: symbol='${header.symbol}', message='${jq(.[] | .)}'")
        .stop()
      .end()
     ;
  }
}
