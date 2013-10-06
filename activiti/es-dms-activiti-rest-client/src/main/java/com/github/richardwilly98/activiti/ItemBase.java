package com.github.richardwilly98.activiti;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.Objects;

/*
 * DEBUG [com.githug.richardwilly98.ActivityRestDeploymentServiceTest] {"data":[{"id":"20","name":"Demo processes","deploymentTime":"2013-10-02T16:21:15EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/20"},{"id":"2330","name":"FinancialReportProcess.bpmn20.xml","deploymentTime":"2013-10-02T17:09:08EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/2330"},{"id":"604","name":"Demo reports","deploymentTime":"2013-10-02T16:25:52EDT","category":null,"url":"http://localhost:8080/activiti-rest/service/repository/deployments/604"}],"total":3,"start":0,"sort":"id","order":"asc","size":3} 
 */
@JsonInclude(Include.NON_DEFAULT)
public abstract class ItemBase {

	private String id;
	private String url;
	private String name;
	private String description;

	protected ItemBase() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("name", name)
				.add("description", description).add("url", url).toString();
	}

}
