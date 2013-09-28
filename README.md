# es-dms #

Test web app using AngujarJS, Jersey and ElasticSearch

## Build status##
[![Build Status](https://drone.io/github.com/richardwilly98/es-dms/status.png)](https://drone.io/github.com/richardwilly98/es-dms/latest)

## How to compile and run ##

you will need to compile and run this section first. Once this part is complete, please follow instructions given in the es-dms / es-dms-site / yo / README.md file in order to install, compile and run the yo framework and the grunt server that hosts the site.

- Run: mvn clean install
- Run: es-dms-site\mvn clean tomcat7:run
- Open browser: http://localhost:8080/es-dms-site
- Elasticsearch is required with mapper-attachment plugin.

  Run `bin/plugin -install elasticsearch/elasticsearch-mapper-attachments/1.8.0`
  Run bin/plugin -install mobz/elasticsearch-head              -- this will install a web based administration interface for elasticsearch
  
- follow instructions at:  https://github.com/richardwilly98/es-dms/blob/master/es-dms-site/yo/README.md
  to complete installation and run site. 

## Project structure ##

This project contains 3 modules:

1. es-dms-core

  This module contains the object model and service interfaces

2. es-dms-service

  This module contains the services implemented for Elasticsearch

3. es-dms-site

  This module contains the web application. It is using AngularJS as Javascript MVVM framework. REST services are provided using Jersey.

# License #
```
Copyright 2012-2013 Richard Louapre

This file is part of ES-DMS.

The current version of ES-DMS is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

ES-DMS is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program.  If not, see
<http://www.gnu.org/licenses/gpl-3.0.html>.
```

## Resources ##
* [AngularJS] (http://angularjs.org/)
* [Elasticsearch] (http://www.elasticsearch.org/)
* [mapper-attachments] (https://github.com/elasticsearch/elasticsearch-mapper-attachments)
* [Jersey] (https://jersey.java.net/)
