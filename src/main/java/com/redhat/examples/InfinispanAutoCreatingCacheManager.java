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

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import javax.security.auth.Subject;
import org.infinispan.Cache;
import org.infinispan.commons.configuration.ClassAllowList;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.factories.GlobalComponentRegistry;
import org.infinispan.health.Health;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.CacheManagerInfo;
import org.infinispan.manager.ClusterExecutor;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.manager.EmbeddedCacheManagerAdmin;
import org.infinispan.remoting.transport.Address;
import org.infinispan.remoting.transport.Transport;
import org.infinispan.stats.CacheContainerStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfinispanAutoCreatingCacheManager implements EmbeddedCacheManager {

  private static final Logger log = LoggerFactory.getLogger(InfinispanAutoCreatingCacheManager.class);
  
  private final EmbeddedCacheManager delegate;
  private final Configuration defaultConfiguration;

  public InfinispanAutoCreatingCacheManager(EmbeddedCacheManager delegate, Configuration defaultConfiguration) {
    this.delegate = delegate;
    this.defaultConfiguration = defaultConfiguration;
  }

  public EmbeddedCacheManager getDelegate() {
    return delegate;
  }

  @Override
  public Configuration defineConfiguration(String cacheName, Configuration configuration) {
    return delegate.defineConfiguration(cacheName, configuration);
  }

  @Override
  public Configuration defineConfiguration(String cacheName, String templateCacheName, Configuration configurationOverride) {
    return delegate.defineConfiguration(cacheName, templateCacheName, configurationOverride);
  }

  @Override
  public void undefineConfiguration(String configurationName) {
    delegate.undefineConfiguration(configurationName);
  }

  @Override
  public String getClusterName() {
    return delegate.getClusterName();
  }

  @Override
  public List<Address> getMembers() {
    return delegate.getMembers();
  }

  @Override
  public Address getAddress() {
    return delegate.getAddress();
  }

  @Override
  public Address getCoordinator() {
    return delegate.getCoordinator();
  }

  @Override
  public boolean isCoordinator() {
    return delegate.isCoordinator();
  }

  @Override
  public ComponentStatus getStatus() {
    return delegate.getStatus();
  }

  @Override
  public GlobalConfiguration getCacheManagerConfiguration() {
    return delegate.getCacheManagerConfiguration();
  }

  @Override
  public Configuration getCacheConfiguration(String name) {
    return delegate.getCacheConfiguration(name);
  }

  @Override
  public Configuration getDefaultCacheConfiguration() {
    return delegate.getDefaultCacheConfiguration();
  }

  @Override
  public Set<String> getCacheConfigurationNames() {
    return delegate.getCacheConfigurationNames();
  }

  @Override
  public Set<String> getAccessibleCacheNames() {
    return delegate.getAccessibleCacheNames();
  }

  @Override
  public boolean isRunning(String cacheName) {
    return delegate.isRunning(cacheName);
  }

  @Override
  public boolean isDefaultRunning() {
    return delegate.isDefaultRunning();
  }

  @Override
  public boolean cacheExists(String cacheName) {
    return delegate.cacheExists(cacheName);
  }

  @Override
  public <K, V> Cache<K, V> getCache() {
    return delegate.getCache();
  }

  @Override
  public <K, V> Cache<K, V> getCache(String cacheName) {
    if (!this.cacheExists(cacheName)) {
      log.debug("Auto-creating cache with default configuration: cache='{}'", cacheName);
      return this.createCache(cacheName, this.defaultConfiguration);
    }
    return delegate.getCache(cacheName);
  }

  @Override
  public <K, V> Cache<K, V> createCache(String name, Configuration configuration) {
    return delegate.createCache(name, configuration);
  }

  @Override
  public <K, V> Cache<K, V> getCache(String cacheName, boolean createIfAbsent) {
    return delegate.getCache(cacheName, createIfAbsent);
  }

  @Override
  public EmbeddedCacheManager startCaches(String... cacheNames) {
    return delegate.startCaches(cacheNames);
  }

  @Override
  @Deprecated
  public void removeCache(String cacheName) {
    delegate.removeCache(cacheName);
  }

  @Override
  @Deprecated
  public Transport getTransport() {
    return delegate.getTransport();
  }

  @Override
  @Deprecated
  public GlobalComponentRegistry getGlobalComponentRegistry() {
    return delegate.getGlobalComponentRegistry();
  }

  @Override
  public void addCacheDependency(String from, String to) {
    delegate.addCacheDependency(from, to);
  }

  @Override
  @Deprecated
  public CacheContainerStats getStats() {
    return delegate.getStats();
  }

  @Override
  public ClusterExecutor executor() {
    return delegate.executor();
  }

  @Override
  public Health getHealth() {
    return delegate.getHealth();
  }

  @Override
  public CacheManagerInfo getCacheManagerInfo() {
    return delegate.getCacheManagerInfo();
  }

  @Override
  public EmbeddedCacheManagerAdmin administration() {
    return delegate.administration();
  }

  @Override
  @Deprecated
  public ClassAllowList getClassWhiteList() {
    return delegate.getClassWhiteList();
  }

  @Override
  public ClassAllowList getClassAllowList() {
    return delegate.getClassAllowList();
  }

  @Override
  public Subject getSubject() {
    return delegate.getSubject();
  }

  @Override
  public EmbeddedCacheManager withSubject(Subject subject) {
    return delegate.withSubject(subject);
  }

  @Override
  public void close() throws IOException {
    delegate.close();
  }

  @Override
  public Set<String> getCacheNames() {
    return delegate.getCacheNames();
  }

  @Override
  public void start() {
    delegate.start();
  }

  @Override
  public void stop() {
    delegate.stop();
  }

  @Override
  public void addListener(Object listener) {
    delegate.addListener(listener);
  }

  @Override
  public CompletionStage<Void> addListenerAsync(Object listener) {
    return delegate.addListenerAsync(listener);
  }

  @Override
  public void removeListener(Object listener) {
    delegate.removeListener(listener);
  }

  @Override
  public CompletionStage<Void> removeListenerAsync(Object listener) {
    return delegate.removeListenerAsync(listener);
  }

  @Override
  @Deprecated
  public Set<Object> getListeners() {
    return delegate.getListeners();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
