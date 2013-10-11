package com.github.richardwilly98.activiti.identity;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.apache.log4j.Logger;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.client.RestAuthenticationService;
import com.github.richardwilly98.esdms.client.RestRoleService;
import com.github.richardwilly98.esdms.client.RestUserService;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.RoleService;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

public class GroupEntityManager extends AbstractManager implements GroupIdentityManager {

    private static final Logger log = Logger.getLogger(GroupEntityManager.class);
    public static final String ADMIN_GROUP_ID = "admin";
    public static final String ADMIN_GROUP_NAME = "admin";
    public static final String ADMIN_GROUP_TYPE = "security-role";
    private final EsDmsConfigurator configurator;
    private RestAuthenticationService restAuthenticationClient;
    private RestRoleService restRoleClient;
    private RestUserService restUserClient;
    private final GroupEntity adminGroup;

    public GroupEntityManager(EsDmsConfigurator configurator) {
        this.configurator = configurator;
        adminGroup = new GroupEntity(ADMIN_GROUP_ID);
        adminGroup.setName(ADMIN_GROUP_NAME);
        adminGroup.setType(ADMIN_GROUP_TYPE);
    }

    private RestAuthenticationService getRestAuthenticationClient() {
        if (restAuthenticationClient == null) {
            restAuthenticationClient = new RestAuthenticationService(configurator.getUrl());
        }
        return restAuthenticationClient;
    }

    private RestRoleService getRestRoleClient() {
        if (restRoleClient == null) {
            restRoleClient = new RestRoleService(configurator.getUrl());
        }
        return restRoleClient;
    }

    private RestUserService getRestUserClient() {
        if (restUserClient == null) {
            restUserClient = new RestUserService(configurator.getUrl());
        }
        return restUserClient;
    }

    public Group createNewGroup(String groupId) {
        throw new ActivitiException("My group manager doesn't support creating a new group");
    }

    public void insertGroup(Group group) {
        throw new ActivitiException("My group manager doesn't support inserting a new group");
    }

    public void updateGroup(GroupEntity updatedGroup) {
        throw new ActivitiException("My group manager doesn't support updating a new group");
    }

    public void deleteGroup(String groupId) {
        throw new ActivitiException("My group manager doesn't support deleting a new group");
    }

    public GroupQuery createNewGroupQuery() {
        log.debug("createNewGroupQuery");
        return new GroupQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
        log.debug(String.format("findGroupByQueryCriteria - %s - %s", dumpGroupQueryImpl(query), page));
        try {
            if (query.getUserId() != null) {
                return findGroupsByUser(query.getUserId());
            } else if (!Strings.isNullOrEmpty(query.getId())) {
                List<Group> groupList = newArrayList();
                Group group = convertToGroupEntity(getRestRoleClient().findRoleById(getUserToken(), query.getId()));
                if (group != null) {
                    groupList.add(group);
                }
                return groupList;
            } else if (!Strings.isNullOrEmpty(query.getName())) {
                return convertToGroupEntityList(getRestRoleClient().findRolesByName(getUserToken(), query.getName()));
            } else {
                return convertToGroupEntityList(getRestRoleClient().findRolesByType(getUserToken(), RoleType.PROCESS));
            }
        } catch (ServiceException ex) {
            log.warn("findGroupByQueryCriteria failed", ex);
            return newArrayList();
        }
        // TODO: you can add other search criteria that will allow extended
        // support using the Activiti engine API
    }

    private String dumpGroupQueryImpl(GroupQueryImpl query) {
        if (query != null) {
            return Objects.toStringHelper(query).add("firstResult", query.getFirstResult()).add("firstRow", query.getFirstRow())
                    .add("lastRow", query.getLastRow()).add("maxResults", query.getMaxResults()).add("parameter", query.getParameter())
                    .add("databaseType", query.getDatabaseType()).add("id", query.getId()).add("name", query.getName())
                    .add("nameLike", query.getNameLike()).add("orderBy", query.getOrderBy()).add("type", query.getType())
                    .add("userId", query.getUserId()).toString();
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
        try {
            com.github.richardwilly98.esdms.api.User user = findUserByLogin(userId);
            List<Group> groups = new ArrayList<Group>();
            if (user.getRoles() != null) {
                Optional<Role> role = Iterables.tryFind(user.getRoles(), new Predicate<Role>() {
                    public boolean apply(Role r) {
                        log.debug("Check role id: " + r.getId());
                        return r.getId().equals(RoleService.DefaultRoles.Constants.PROCESS_ADMINISTRATOR_ROLE_ID);
                    }
                });
                log.info("Found process-admin role? " + role.isPresent());
                if (role.isPresent()) {
                    log.debug(String.format("Add admin group for user %s", userId));
                    groups.add(adminGroup);
                }

                Collection<Role> roles = Collections2.filter(user.getRoles(), new Predicate<Role>() {
                    public boolean apply(Role r) {
                        log.debug("Check role id: " + r.getId());
                        return r.getType() == RoleType.PROCESS;
                    }
                });
                log.info("Found process role? " + roles.iterator().hasNext());
                groups.addAll(convertToGroupEntityList(roles));
            }
            return groups;
        } catch (ServiceException sEx) {
            log.error("findGroupsByUser failed", sEx);
        }
        return null;
    }

    public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        throw new ActivitiException("es-dms group manager doesn't support querying");
    }

    public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
        throw new ActivitiException("es-dms group manager doesn't support querying");
    }

    public static GroupEntity convertToGroupEntity(com.github.richardwilly98.esdms.api.Role role) {
        if (role != null && role.getType() == RoleType.PROCESS) {
            log.trace(String.format("Convert role: %s", role));
            GroupEntity groupEntity = new GroupEntity(role.getId());
            groupEntity.setName(role.getName());
            groupEntity.setType("assignment");
            return groupEntity;
        }
        return null;

    }

    public static List<Group> convertToGroupEntityList(Collection<com.github.richardwilly98.esdms.api.Role> roles) {
        if (roles != null && roles.size() > 0) {
            List<Group> groupEntityList = newArrayList();
            for (com.github.richardwilly98.esdms.api.Role role : roles) {
                if (role.getType() == RoleType.PROCESS) {
                    GroupEntity groupEntity = convertToGroupEntity(role);
                    if (groupEntity != null) {
                        groupEntityList.add(groupEntity);
                    }
                }
            }
            return groupEntityList;
        }
        return newArrayList();
    }

    private com.github.richardwilly98.esdms.api.User findUserByLogin(String login) throws ServiceException {
        return getRestUserClient().findUserByLogin(getUserToken(), login);
    }

    private String getUserToken() throws ServiceException {
        return getRestAuthenticationClient().login(
                new CredentialImpl.Builder().username(configurator.getUserId()).password(configurator.getPassword().toCharArray()).build());
    }
}
