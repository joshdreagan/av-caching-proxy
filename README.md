# Alpha Vantage - Caching Proxy

This application can be used proxy calls to the Alpha Vantage API, and cache the responses.

## Prerequisites

none

## Running locally

You can override any of the application configurations by creating an `application.yml` file in the `$PROJECT_ROOT/config` directory. This directory is ignored by Git to prevent checking in personal/user keys or other secrets.

To run the application, you can use the following commands:

```
cd $PROJECT_ROOT
mvn clean package spring-boot:run
```

## Running on OpenShift

Create the `deployment.yml`, and `configmap.yml` files from the available templates, and update them with your values. These files are ignored by Git to prevent checking in personal/user keys or other secrets.

```
cd $PROJECT_ROOT
cp src/main/jkube/deployment.yml.template src/main/jkube/deployment.yml
cp src/main/jkube/configmap.yml.template src/main/jkube/configmap.yml
```

Build and deploy to OpenShift. _Make sure you're logged in to OpenShift and are currently in the namespace you want to deploy to._

```
cd $PROJECT_ROOT
mvn -P openshift clean package oc:deploy
```

## Application Properties

| Property | Default | Description |
| :------- | :------ | :---------- |
| `application.cache.enabled` | `true` | Enable caching of API responses.
| `application.cache.ttl` | 15000 | The time (in milliseconds) before cached responses expire.
| `application.alpha-vantage.scheme` | "https" | The scheme for the Alpha Vantage API. Valid values are "http" or "https".
| `application.alpha-vantage.host` | "www.alphavantage.co" | The host name for the Alpha Vantage API.
| `application.alpha-vantage.port` | `443` | The port for the Alpha Vantage API.
| `application.alpha-vantage.throttle-enabled` | `true` | Should requests to the Alpha Vantage API be throttled.
| `application.alpha-vantage.throttle-requests` | `1` | The number of requests per-period allowed to the Alpha Vantage API.
| `application.alpha-vantage.throttle-period` | `1000` | The period (in milliseconds) for which to throttle requests to the Alpha Vantage API.



