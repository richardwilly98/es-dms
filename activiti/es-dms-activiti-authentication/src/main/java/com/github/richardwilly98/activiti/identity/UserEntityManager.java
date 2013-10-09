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
import com.github.richardwilly98.esdms.client.RestAuthenticationService;
import com.github.richardwilly98.esdms.client.RestUserService;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class UserEntityManager extends AbstractManager implements
		UserIdentityManager {

	private static final Logger log = Logger
			.getLogger(UserEntityManager.class);
	private final EsDmsConfigurator configurator;
	private RestUserService restUserClient;
	private RestAuthenticationService restAuthenticationClient;

	public UserEntityManager(EsDmsConfigurator configurator) {
		log.trace("*** constructor ***");
		this.configurator = configurator;
	}

	public User createNewUser(String userId) {
		throw new ActivitiException(
				"My user manager doesn't support creating a new user");
	}

	public void insertUser(User user) {
		throw new ActivitiException(
				"My user manager doesn't support inserting a new user");
	}

	public void updateUser(UserEntity updatedUser) {
		throw new ActivitiException(
				"My user manager doesn't support updating a user");
	}

	public UserEntity findUserById(String userId) {
		log.debug(String.format("findUserById - %s", userId));
		// return getFakeUserEntity(userId);
		try {
			com.github.richardwilly98.esdms.api.User user = getRestUserClient()
					.findUserById(getUserToken(), userId);
			return convertToUserEntity(user);
			// if (user != null) {
			// UserEntity userEntity = new UserEntity(userId);
			// userEntity.setEmail(user.getEmail());
			// String name = user.getName();
			// if (name.contains(" ")) {
			// userEntity
			// .setFirstName(name.substring(0, name.indexOf(" ")));
			// userEntity
			// .setLastName(name.substring(name.indexOf(" ") + 1));
			// } else {
			// userEntity.setFirstName("");
			// userEntity.setLastName(user.getName());
			// }
			// return userEntity;
			// }
		} catch (ServiceException e) {
			log.error("findUserById failed", e);
		}
		return null;
	}

	public void deleteUser(String userId) {
		throw new ActivitiException(
				"My user manager doesn't support deleting a user");
	}

	// TODO: Implement search for all available fields
	public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
		log.debug(String.format("findUserByQueryCriteria - %s - %s",
				dumpUserQueryImpl(query), page));
		UserQueryImpl userQuery = (UserQueryImpl) query;
		try {
			if (!Strings.isNullOrEmpty(userQuery.getId())) {
				// return convertToUserEntityList(getRestUserClient()
				// .findUsersById(getUserToken(), userQuery.getId()));
				List<User> userList = newArrayList();
				UserEntity user = findUserById(userQuery.getId());
				if (user != null) {
					userList.add(user);
				}
				return userList;
			} else if (!Strings.isNullOrEmpty(userQuery.getEmail())) {
				return convertToUserEntityList(getRestUserClient()
						.findUsersByEmail(getUserToken(), userQuery.getEmail()));
			} else if (!Strings.isNullOrEmpty(userQuery.getLastName())) {
				return convertToUserEntityList(getRestUserClient()
						.findUsersByName(getUserToken(),
								userQuery.getLastName()));
			} else if (!Strings.isNullOrEmpty(userQuery.getFirstName())) {
				return convertToUserEntityList(getRestUserClient()
						.findUsersByName(getUserToken(),
								userQuery.getFirstName()));
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
		// TODO: you can add other search criteria that will allow extended
		// support using the Activiti engine API
	}

	private String dumpUserQueryImpl(UserQueryImpl query) {
		if (query != null) {
			return Objects.toStringHelper(query)
					.add("firstResult", query.getFirstResult())
					.add("firstRow", query.getFirstRow())
					.add("lastRow", query.getLastRow())
					.add("maxResults", query.getMaxResults())
					.add("parameter", query.getParameter())
					.add("databaseType", query.getDatabaseType())
					.add("email", query.getEmail())
					.add("emailLike", query.getEmailLike())
					.add("firstName", query.getFirstName())
					.add("firstNameLike", query.getFirstNameLike())
					.add("fullName", query.getFullNameLike())
					.add("groupId", query.getGroupId())
					.add("id", query.getId())
					.add("lastName", query.getLastName())
					.add("lastNameLike", query.getLastNameLike())
					.add("orderBy", query.getOrderBy())
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
		throw new ActivitiException("My user manager doesn't support querying");
	}

	public UserQuery createNewUserQuery() {
		log.trace("createNewUserQuery");
		return new UserQueryImpl(Context.getProcessEngineConfiguration()
				.getCommandExecutorTxRequired());
	}

	public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId,
			String key) {
		throw new ActivitiException("My user manager doesn't support querying");
	}

	public List<String> findUserInfoKeysByUserIdAndType(String userId,
			String type) {
		throw new ActivitiException("My user manager doesn't support querying");
	}

	public Boolean checkPassword(String userId, String password) {
		log.debug(String.format("checkPassword - %s", userId));
		try {
			String token = getRestAuthenticationClient().login(
					new CredentialImpl.Builder().username(userId)
							.password(password.toCharArray()).build());
			return !Strings.isNullOrEmpty(token);
		} catch (ServiceException e) {
			log.error("checkPassword failed", e);
		}
		return false;
	}

	public List<User> findPotentialStarterUsers(String proceDefId) {
		throw new ActivitiException(
				"LDAP user manager doesn't support querying");
	}

	public List<User> findUsersByNativeQuery(Map<String, Object> parameterMap,
			int firstResult, int maxResults) {
		throw new ActivitiException(
				"LDAP user manager doesn't support querying");
	}

	public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
		throw new ActivitiException(
				"LDAP user manager doesn't support querying");
	}

	private RestUserService getRestUserClient() {
		if (restUserClient == null) {
			restUserClient = new RestUserService(configurator.getUrl());
		}
		return restUserClient;
	}

	private RestAuthenticationService getRestAuthenticationClient() {
		if (restAuthenticationClient == null) {
			restAuthenticationClient = new RestAuthenticationService(
					configurator.getUrl());
		}
		return restAuthenticationClient;
	}

	private UserEntity convertToUserEntity(
			com.github.richardwilly98.esdms.api.User user) {
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

	private List<User> convertToUserEntityList(
			Collection<com.github.richardwilly98.esdms.api.User> users) {
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
				new CredentialImpl.Builder().username(configurator.getUserId())
						.password(configurator.getPassword().toCharArray())
						.build());
	}
}
