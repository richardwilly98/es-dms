package com.github.richardwilly98.activiti.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.apache.log4j.Logger;

import com.google.common.base.Objects;

public class GroupEntityManager extends AbstractManager implements
		GroupIdentityManager {

	private static final Logger log = Logger
			.getLogger(GroupEntityManager.class);
	private final EsDmsConfigurator configurator;

	public GroupEntityManager(EsDmsConfigurator configurator) {
		log.trace("MyGroupEntityManager - constructor");
		this.configurator = configurator;
	}

	public Group createNewGroup(String groupId) {
		throw new ActivitiException(
				"My group manager doesn't support creating a new group");
	}

	public void insertGroup(Group group) {
		throw new ActivitiException(
				"My group manager doesn't support inserting a new group");
	}

	public void updateGroup(GroupEntity updatedGroup) {
		throw new ActivitiException(
				"My group manager doesn't support updating a new group");
	}

	public void deleteGroup(String groupId) {
		throw new ActivitiException(
				"My group manager doesn't support deleting a new group");
	}

	public GroupQuery createNewGroupQuery() {
		log.debug("createNewGroupQuery");
		return new GroupQueryImpl(Context.getProcessEngineConfiguration()
				.getCommandExecutorTxRequired());
	}

	public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
		log.debug(String.format("findGroupByQueryCriteria - %s - %s", dumpGroupQueryImpl(query),
				page));
		// Only support for groupMember() at the moment
		if (query.getUserId() != null) {
			return findGroupsByUser(query.getUserId());
		} else {
			// throw new ActivitiIllegalArgumentException(
			// "This query is not supported by the LDAPGroupManager");
			return findGroupsByUser("*");
		}
		// TODO: you can add other search criteria that will allow extended
		// support using the Activiti engine API
	}

	private String dumpGroupQueryImpl(GroupQueryImpl query) {
		if (query != null) {
			return Objects.toStringHelper(query)
					.add("firstResult", query.getFirstResult())
					.add("firstRow", query.getFirstRow())
					.add("lastRow", query.getLastRow())
					.add("maxResults", query.getMaxResults())
					.add("parameter", query.getParameter())
					.add("databaseType", query.getDatabaseType())
					.add("id", query.getId())
					.add("name", query.getName())
					.add("nameLike", query.getNameLike())
					.add("orderBy", query.getOrderBy())
					.add("type", query.getType())
					.add("userId", query.getUserId())
					.toString();
		}
		return null;
	}

	public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
		log.debug(String.format("findGroupCountByQueryCriteria - %s", query));
		List<Group> groups = findGroupByQueryCriteria(query, null);
		if (groups != null) {
			return groups.size();
		} else {
			return 0;
		}
	}

	public List<Group> findGroupsByUser(String userId) {
		log.debug(String.format("findGroupsByUser - %s", userId));
		List<Group> groups = new ArrayList<Group>();
		GroupEntity group;
		if ("kermit".equalsIgnoreCase(userId)) {
			group = new GroupEntity("admin");
			group.setName("admin");
			group.setType("security-role");
			groups.add(group);
		}
		group = new GroupEntity("group1");
		group.setName("group1");
		groups.add(group);
		return groups;
	}

	public List<Group> findGroupsByNativeQuery(
			Map<String, Object> parameterMap, int firstResult, int maxResults) {
		throw new ActivitiException(
				"LDAP group manager doesn't support querying");
	}

	public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
		throw new ActivitiException(
				"LDAP group manager doesn't support querying");
	}

}
