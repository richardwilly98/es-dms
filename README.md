# es-dms #

Test web app using AngujarJS, Jersey and ElasticSearch

## Build status##
[![Build Status](https://buildhive.cloudbees.com/job/richardwilly98/job/es-dms/badge/icon)](https://buildhive.cloudbees.com/job/richardwilly98/job/es-dms/)

## How to compile and run ##

- Run: mvn clean install
- Run: es-dms-site\mvn clean tomcat7:run
- Open browser: http://localhost:8080/es-dms-site
- Elasticsearch is required with mapper-attachment plugin.

  Run `bin/plugin -install elasticsearch/elasticsearch-mapper-attachments/1.7.0`

## Project structure ##

This project contains 3 modules:

1. es-dms-core

  This module contains the object model and service interfaces

2. es-dms-service

  This module contains the services implemented for Elasticsearch

3. es-dms-site

  This module contains the web application. It is using AngularJS as Javascript MVVM framework. REST services are provided using Jersey.

## Resources ##
* [AngularJS] (http://angularjs.org/)
* [Elasticsearch] (http://www.elasticsearch.org/)
* [mapper-attachments] (https://github.com/elasticsearch/elasticsearch-mapper-attachments)
* [Jersey] (https://jersey.java.net/)
