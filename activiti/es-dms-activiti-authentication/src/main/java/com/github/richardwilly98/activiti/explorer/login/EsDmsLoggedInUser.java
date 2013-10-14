package com.github.richardwilly98.activiti.explorer.login;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.explorer.Constants;
import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.identity.LoggedInUser;

import com.github.richardwilly98.activiti.identity.UserEntityManager;

public class EsDmsLoggedInUser implements LoggedInUser {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private UserEntity userEntity;
    private transient final IdentityService identityService;
    private final List<Group> securityRoles = newArrayList();
    private final List<Group> groups = newArrayList();
    private boolean isAdmin = false;
    private boolean isUser = false;

    public EsDmsLoggedInUser(final IdentityService identityService) {
        this.identityService = identityService;
    }
    
    public void convert(final com.github.richardwilly98.esdms.api.User user) {
        userEntity = UserEntityManager.convertToUserEntity(user);
        List<Group> groups = identityService.createGroupQuery().groupMember(user.getLogin()).list();
        for (Group group : groups) {
            if (Constants.SECURITY_ROLE.equals(group.getType())) {
                this.securityRoles.add(group);
                if (Constants.SECURITY_ROLE_USER.equals(group.getId())) {
                    isUser = true;
                }
                if (Constants.SECURITY_ROLE_ADMIN.equals(group.getId())) {
                    isAdmin = true;
                }
            } else if (ExplorerApp.get().getAdminGroups() != null && ExplorerApp.get().getAdminGroups().contains(group.getId())) {
                this.securityRoles.add(group);
                isAdmin = true;
            } else if (ExplorerApp.get().getUserGroups() != null && ExplorerApp.get().getUserGroups().contains(group.getId())) {
                this.securityRoles.add(group);
                isUser = true;
            } else {
                this.groups.add(group);
            }

        }
    }

    @Override
    public String getId() {
        return userEntity.getId();
    }

    @Override
    public String getFirstName() {
        return userEntity.getFirstName();
    }

    @Override
    public String getLastName() {
        return userEntity.getLastName();
    }

    @Override
    public String getFullName() {
        return userEntity.getFirstName() + " " + userEntity.getLastName();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public boolean isUser() {
        return isUser;
    }

    @Override
    public List<Group> getSecurityRoles() {
        return securityRoles;
    }

    @Override
    public List<Group> getGroups() {
        return groups;
    }

}
