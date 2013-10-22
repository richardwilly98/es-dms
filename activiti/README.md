es-dms Activiti Integration
===========================

Tested with Activiti 5.13

It contains 2 modules:
- es-dms-activiti-authentication to handle SSO within Activiti
- es-dms-activiti-rest-client to access Activiti REST api from es-dms

es-dms-activiti-authentication
------------------------------

2 new 'system' roles have been generated in es-dms:
- process-admin: 'process-admin' users will be able to login to Activiti and they will have admin rights. By default only admin has this role.
- process-user: 'process-user' users will be able to login to Activiti.

A new 'Process' role type has been created. Roles of this type will be converted in Activiti group.

es-dms-activiti-rest-client
---------------------------

- New document action 'Start Process' is available (only for process users). This action will create a new process instance of the first process definition (with category es-dms). The current user will be assigned to the current process task.
- TODO: Add external resource link to access the document.
- TODO: Create comment 'Process auto-generated from es-dms'
- TODO: Build association document with new process created in es-dms

- A sample process definition can be found in install/process-definition/twoTaskProcess.bpmn20.xml. 
It can be imported from Activiti login as Administrator. Manage -> Deployments -> Upload New

- Known issue: explorer seems buggy with sessions. It might be necessary to remove JSESSIONID (/activiti-explorer/) if login fails.
