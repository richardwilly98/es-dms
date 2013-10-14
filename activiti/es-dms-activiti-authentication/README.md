Dependencies to be copied in webapps/activiti-explorer/WEB-INF/lib and webapps/activiti-rest/WEB-INF/lib
```
es-dms-activiti-authentication-1.0-SNAPSHOT.jar
es-dms-core-1.0-SNAPSHOT.jar
es-dms-rest-client-1.0-SNAPSHOT.jar
hk2-api-2.2.0-b14.jar
hk2-locator-2.2.0-b14.jar
hk2-utils-2.2.0-b14.jar
cglib-2.2.0-b14.jar
javax.inject-2.2.0-b14.jar
javax.annotation-api-1.2.jar
javax.ws.rs-api-2.0.jar
jersey-client-2.2.jar
jersey-common-2.2.jar
jersey-media-multipart-2.2.jar
jersey-server-2.2.jar
mimepull-1.9.3.jar
jackson-core-2.2.2.jar
jackson-jaxrs-base-2.2.2.jar
jackson-jaxrs-json-provider-2.2.2.jar
jackson-module-jaxb-annotations-2.2.2.jar
hibernate-validator-5.0.1.Final.jar
validation-api-1.1.0.Final.jar
classmate-0.8.0.jar
jboss-logging-3.1.1.GA.jar
javax.el-2.2.4.jar
javax.el-api-2.2.4.jar
```

Update webapps/activiti-explorer/WEB-INF/activiti-standalone-context.xml and webapps/activiti-rest/WEB-INF/classes/activiti-context.xml
```
  <bean id="processEngineConfiguration" class="org.activiti.spring.SpringProcessEngineConfiguration">
  ...
    <property name="configurators">
      <list>
          <bean class="com.github.richardwilly98.activiti.identity.EsDmsConfigurator">
            <property name="url" value="http://localhost:9000/api" />
            <property name="userId" value="admin" />
            <property name="password" value="secret" />
          </bean>
      </list>
    </property>
  ...
  </bean>

  ...
  <!-- Only in activiti-explorer -->
  <bean id="activitiLoginHandler" class="com.github.richardwilly98.activiti.explorer.login.EsDmsLoginHandler">
    <property name="url" value="http://localhost:9000/api" />
    <property name="userId" value="admin" />
    <property name="password" value="secret" />
    <property name="identityService" ref="identityService" />
  </bean>
```

From same xml file configurations remove the demoDataGenerator bean

Restart Tomcat

cUrl Examples
----
```
curl -XGET --user admin:secret http://localhost:18080/activiti-rest/service/repository/process-definitions
```