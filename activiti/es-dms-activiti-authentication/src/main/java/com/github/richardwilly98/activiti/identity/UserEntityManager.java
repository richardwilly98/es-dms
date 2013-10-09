package com.github.richardwilly98.activiti.identity;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.apache.log4j.Logger;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.client.RestAuthenticationService;
import com.github.richardwilly98.esdms.client.RestUserService;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.RoleService;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

public class UserEntityManager extends AbstractManager implements UserIdentityManager {

    private static final Logger log = Logger.getLogger(UserEntityManager.class);
    private final EsDmsConfigurator configurator;
    private RestUserService restUserClient;
    private RestAuthenticationService restAuthenticationClient;

    public UserEntityManager(EsDmsConfigurator configurator) {
        this.configurator = configurator;
    }

    private RestUserService getRestUserClient() {
        if (restUserClient == null) {
            restUserClient = new RestUserService(configurator.getUrl());
        }
        return restUserClient;
    }

    private RestAuthenticationService getRestAuthenticationClient() {
        if (restAuthenticationClient == null) {
            restAuthenticationClient = new RestAuthenticationService(configurator.getUrl());
        }
        return restAuthenticationClient;
    }

    public User createNewUser(String userId) {
        throw new ActivitiException("My user manager doesn't support creating a new user");
    }

    public void insertUser(User user) {
        throw new ActivitiException("My user manager doesn't support inserting a new user");
    }

    public void updateUser(UserEntity updatedUser) {
        throw new ActivitiException("My user manager doesn't support updating a user");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.activiti.engine.impl.persistence.entity.UserIdentityManager#findUserById
     * (java.lang.String) Activiti.userId is mapped to user.login in es-dms
     */
    public UserEntity findUserById(String userId) {
        log.debug(String.format("findUserById - %s", userId));
        try {
            com.github.richardwilly98.esdms.api.User user = findUserByLogin(userId);
            if (user != null) {
                return convertToUserEntity(user);
            }
        } catch (ServiceException e) {
            log.error("findUserById failed", e);
        }
        return null;
    }

    public void deleteUser(String userId) {
        throw new ActivitiException("My user manager doesn't support deleting a user");
    }

    // TODO: Implement search for all available fields
    // TODO: How pagination is handle in Activiti?
    public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
        log.debug(String.format("findUserByQueryCriteria - %s - %s", dumpUserQueryImpl(query), page));
        UserQueryImpl userQuery = (UserQueryImpl) query;
        try {
            if (!Strings.isNullOrEmpty(userQuery.getId())) {
                List<User> userList = newArrayList();
                UserEntity user = findUserById(userQuery.getId());
                if (user != null) {
                    userList.add(user);
                }
                return userList;
            } else if (!Strings.isNullOrEmpty(userQuery.getEmail())) {
                return convertToUserEntityList(getRestUserClient().findUsersByEmail(getUserToken(), userQuery.getEmail()));
            } else if (!Strings.isNullOrEmpty(userQuery.getLastName())) {
                return convertToUserEntityList(getRestUserClient().findUsersByName(getUserToken(), userQuery.getLastName()));
            } else if (!Strings.isNullOrEmpty(userQuery.getFirstName())) {
                return convertToUserEntityList(getRestUserClient().findUsersByName(getUserToken(), userQuery.getFirstName()));
            } else if (!Strings.isNullOrEmpty(userQuery.getFullNameLike())) {
                String criteria = userQuery.getFullNameLike();
                if (criteria.startsWith("%")) {
                    criteria = criteria.substring(1);
                }
                if (criteria.endsWith("%")) {
                    criteria = criteria.substring(0, criteria.indexOf("%"));
                }
                return convertToUserEntityList(getRestUserClient().findUsersByName(getUserToken(), criteria));
            } else {
                // TODO: get all users from your identity domain and convert
                // them to
                // List<User>
                return newArrayList();
            }
        } catch (ServiceException ex) {
            log.warn("findUserByQueryCriteria failed", ex);
            return newArrayList();
        }
    }

    private String dumpUserQueryImpl(UserQueryImpl query) {
        if (query != null) {
            return Objects.toStringHelper(query).add("firstResult", query.getFirstResult()).add("firstRow", query.getFirstRow())
                    .add("lastRow", query.getLastRow()).add("maxResults", query.getMaxResults()).add("parameter", query.getParameter())
                    .add("databaseType", query.getDatabaseType()).add("email", query.getEmail()).add("emailLike", query.getEmailLike())
                    .add("firstName", query.getFirstName()).add("firstNameLike", query.getFirstNameLike())
                    .add("fullNameLike", query.getFullNameLike()).add("groupId", query.getGroupId()).add("id", query.getId())
                    .add("lastName", query.getLastName()).add("lastNameLike", query.getLastNameLike()).add("orderBy", query.getOrderBy())
                    .toString();
        }
        return null;
    }

    public long findUserCountByQueryCriteria(UserQueryImpl query) {
        log.debug(String.format("findUserCountByQueryCriteria - %s", query));
        List<User> users = findUserByQueryCriteria(query, null);
        if (users != null) {
            return users.size();
        } else {
            return 0;
        }
    }

    public List<Group> findGroupsByUser(String userId) {
        log.debug(String.format("findGroupsByUser - %s", userId));
        try {
            com.github.richardwilly98.esdms.api.User user = findUserByLogin(userId);
            List<Group> groups = GroupEntityManager.convertToGroupEntityList(user.getRoles());
            if (log.isDebugEnabled()) {
                for (Group group : groups) {
                    log.debug(String.format("find group %s", group.getId()));
                }
            }
            return groups;
        } catch (ServiceException e) {
            log.error("findGroupsByUser failed", e);
        }
        return null;
    }

    public UserQuery createNewUserQuery() {
        log.trace("createNewUserQuery");
        return new UserQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutorTxRequired());
    }

    public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId, String key) {
        log.debug(String.format("findUserInfoByUserIdAndKey - %s - %s", userId, key));
        throw new ActivitiException("My user manager doesn't support querying");
    }

    public List<String> findUserInfoKeysByUserIdAndType(String userId, String type) {
        log.debug(String.format("findUserInfoKeysByUserIdAndType - %s - %s", userId, type));
        throw new ActivitiException("My user manager doesn't support querying");
    }

    public Boolean checkPassword(String userId, String password) {
        log.debug(String.format("checkPassword - %s", userId));
        try {
            com.github.richardwilly98.esdms.api.User user = findUserByLogin(userId);
            log.debug(String.format("findUserByLogin: %s", user));
            if (user != null) {
                Optional<Role> role = Iterables.tryFind(user.getRoles(), new Predicate<Role>() {
                    public boolean apply(Role r) {
                        log.debug("Check role id: " + r.getId());
                        return r.getId().equals(RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole().getId())
                                || r.getId().equals(RoleService.DefaultRoles.PROCESS_USER.getRole().getId());
                    }
                });
                log.debug(String.format("Is %s a process user?", userId, role.isPresent()));
                if (role.isPresent()) {
                    String token = getRestAuthenticationClient().login(
                            new CredentialImpl.Builder().username(userId).password(password.toCharArray()).build());
                    return !Strings.isNullOrEmpty(token);
                    // }
                    // if
                    // (user.getRoles().contains(RoleService.DefaultRoles.PROCESS_USER.getRole())
                    // ||
                    // user.getRoles().contains(RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole()))
                    // {
                    // log.debug(String.format("User %s contains %s or %s roles",
                    // user.getLogin(),
                    // RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole().getId(),
                    // RoleService.DefaultRoles.PROCESS_USER
                    // .getRole().getId()));
                    // String token = getRestAuthenticationClient().login(
                    // new
                    // CredentialImpl.Builder().username(userId).password(password.toCharArray()).build());
                    // return !Strings.isNullOrEmpty(token);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Find user % but do not belong to %s or %s roles", user.getLogin(),
                                RoleService.DefaultRoles.PROCESS_ADMINISTRATOR.getRole().getId(), RoleService.DefaultRoles.PROCESS_USER
                                        .getRole().getId()));
                    }
                }
            } else {
                log.info(String.format("Could find user %s with findUserByLogin.", userId));
            }
        } catch (ServiceException e) {
            log.error("checkPassword failed", e);
        }
        return false;
    }

    public List<User> findPotentialStarterUsers(String proceDefId) {
        log.debug(String.format("findPotentialStarterUsers - %s", proceDefId));
        throw new ActivitiException("LDAP user manager doesn't support querying");
    }

    public List<User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
        log.debug(String.format("findUsersByNativeQuery - %s - %s - %s", parameterMap, firstResult, maxResults));
        throw new ActivitiException("LDAP user manager doesn't support querying");
    }

    public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
        log.debug(String.format("findUserCountByNativeQuery - %s", parameterMap));
        throw new ActivitiException("LDAP user manager doesn't support querying");
    }

    private com.github.richardwilly98.esdms.api.User findUserByLogin(String login) throws ServiceException {
        return getRestUserClient().findUserByLogin(getUserToken(), login);
    }

    public static UserEntity convertToUserEntity(com.github.richardwilly98.esdms.api.User user) {
        if (user != null) {
            log.trace(String.format("Convert user: %s", user));
            UserEntity userEntity = new UserEntity(user.getLogin());
            userEntity.setEmail(user.getEmail());
            String name = user.getName();
            if (name.contains(" ")) {
                userEntity.setFirstName(name.substring(0, name.indexOf(" ")));
                userEntity.setLastName(name.substring(name.indexOf(" ") + 1));
            } else {
                userEntity.setFirstName("");
                userEntity.setLastName(user.getName());
            }
            return userEntity;
        }
        return null;

    }

    public static List<User> convertToUserEntityList(Collection<com.github.richardwilly98.esdms.api.User> users) {
        if (users != null && users.size() > 0) {
            List<User> userEntityList = newArrayList();
            for (com.github.richardwilly98.esdms.api.User user : users) {
                UserEntity userEntity = convertToUserEntity(user);
                if (userEntity != null) {
                    userEntityList.add(userEntity);
                }
            }
            return userEntityList;
        }
        return newArrayList();
    }

    private String getUserToken() throws ServiceException {
        return getRestAuthenticationClient().login(
                new CredentialImpl.Builder().username(configurator.getUserId()).password(configurator.getPassword().toCharArray()).build());
    }
}
