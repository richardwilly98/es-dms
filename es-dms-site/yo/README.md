es-dms-yo
=========

Build process
-------------

from the es-dms-site/yo folder open a command line window and type the commands sequence given below.
You might obtain some warnings but make sure there are no errors

Install Node.js [1]  
Install Yo, Grunt and Bower [2]  
```
	npm install -g yo grunt-cli bower
```
Install AngularJS scaffolding  
```
	npm install -g generator-angular
	or
	npm -g install generator-angular generator-karma@0.5.0
	(might need to define the version of Karma for angulajs compatibility)
```
Install all dependencies  
From root project folder run:  
```
	npm install
	bower install
```
Run webapp  
```
	grunt server
```
Resources
---------
[1] - http://nodejs.org/  
[2] - https://github.com/yeoman/yeoman/wiki/Getting-Started
