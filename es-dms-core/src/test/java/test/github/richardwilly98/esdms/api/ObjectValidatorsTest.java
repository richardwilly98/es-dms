package test.github.richardwilly98.esdms.api;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.AuditEntryImpl;
import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.FileImpl;
import com.github.richardwilly98.esdms.ParameterImpl;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.AuditEntry;
import com.github.richardwilly98.esdms.api.AuditEntry.Event;
import com.github.richardwilly98.esdms.api.Credential;
import com.github.richardwilly98.esdms.api.Document;
import com.github.richardwilly98.esdms.api.Document.DocumentStatus;
import com.github.richardwilly98.esdms.api.Document.DocumentSystemAttributes;
import com.github.richardwilly98.esdms.api.File;
import com.github.richardwilly98.esdms.api.Parameter;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.Parameter.ParameterType;
import com.github.richardwilly98.esdms.api.Role.RoleType;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.common.collect.ImmutableSet;

public class ObjectValidatorsTest {
    private static Logger log = Logger.getLogger(ObjectValidatorsTest.class);

    private static Validator validator;

    @BeforeClass
    public void initialize() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testAuditEntryValidation() {
        String id = "entry-" + System.currentTimeMillis();
        Event event = Event.UNDEFINED;
        String user = "user1";
        String itemId = "doc1";
        AuditEntry entry = new AuditEntryImpl.Builder().id(id).name(id).event(event).user(user).date(new Date()).itemId(itemId).build();
        Set<ConstraintViolation<AuditEntry>> constraintViolations = validator.validate(entry);
        Assert.assertEquals(constraintViolations.size(), 0);

        entry.setId(null);
        constraintViolations = validator.validate(entry);
        Assert.assertEquals(constraintViolations.size(), 1);
        log.debug(constraintViolations.iterator().next().getMessage());
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "id is required");

        entry.setId(id);
        entry.setName(null);
        constraintViolations = validator.validate(entry);
        Assert.assertEquals(constraintViolations.size(), 1);
        log.debug(constraintViolations.iterator().next().getMessage());
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "name is required");

        entry = new AuditEntryImpl.Builder().id(id).name(id).event(event).date(new Date()).itemId(itemId).build();
        constraintViolations = validator.validate(entry);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "user is required");

        entry = new AuditEntryImpl.Builder().id(id).name(id).user(user).date(new Date()).itemId(itemId).build();
        constraintViolations = validator.validate(entry);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "event is required");

        entry = new AuditEntryImpl.Builder().id(id).name(id).user(user).event(event).itemId(itemId).build();
        constraintViolations = validator.validate(entry);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "date is required");

        entry = new AuditEntryImpl.Builder().id(id).name(id).user(user).event(event).date(new Date()).build();
        constraintViolations = validator.validate(entry);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "itemId is required");
    }

    @Test
    public void testCredentialValidation() {
        String username = "user-" + System.currentTimeMillis();
        char[] password = new char[] { 10, 11, 12, 13, 14 };
        Credential credential = new CredentialImpl.Builder().username(username).password(password).build();
        Set<ConstraintViolation<Credential>> constraintViolations = validator.validate(credential);
        Assert.assertEquals(constraintViolations.size(), 0);

        credential = new CredentialImpl.Builder().password(password).build();
        constraintViolations = validator.validate(credential);
        Assert.assertEquals(constraintViolations.size(), 1);
        log.debug(constraintViolations.iterator().next().getMessage());
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "username is required");

        credential = new CredentialImpl.Builder().username(username).build();
        constraintViolations = validator.validate(credential);
        Assert.assertEquals(constraintViolations.size(), 1);
        log.debug(constraintViolations.iterator().next().getMessage());
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "password is required");
    }

    @Test
    public void testDocumentValidation() {
        String id = "id-" + System.currentTimeMillis();
        String name = "name-" + System.currentTimeMillis();
        Map<String, Object> attributes = newHashMap();
        attributes.put(DocumentSystemAttributes.STATUS.getKey(), DocumentStatus.AVAILABLE.getStatusCode());
        Document document = new DocumentTest(new DocumentImpl.Builder().id(id).name(name).attributes(attributes).roles(null));
        log.debug(document);
        Set<ConstraintViolation<Document>> constraintViolations = validator.validate(document);
        Assert.assertEquals(constraintViolations.size(), 0);

        attributes = newHashMap();
        document = new DocumentTest(new DocumentImpl.Builder().id(id).name(name).attributes(attributes).roles(null));
        log.debug(document);
        constraintViolations = validator.validate(document);
        Assert.assertEquals(constraintViolations.size(), 1);
    }

    @Test
    public void testFileValidation() {
        byte[] content = "<html><body><h1>Hello World</h1></body></html>".getBytes();
        File file = new FileImpl.Builder().content(content).name("test.html").contentType("text/html").build();
        log.debug(file);
        Set<ConstraintViolation<File>> constraintViolations = validator.validate(file);
        Assert.assertEquals(constraintViolations.size(), 0);

         file = new
         FileImpl.Builder().name("test.html").contentType("text/html").build();
         log.debug(file);
         constraintViolations = validator.validate(file);
         Assert.assertEquals(constraintViolations.size(), 1);
         Assert.assertEquals(constraintViolations.iterator().next().getMessage(),
         "content is required");

        file = new FileImpl.Builder().name("test.html").content(content).build();
        constraintViolations = validator.validate(file);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "content-type is required");

        file = new FileImpl.Builder().content(content).contentType("text/html").build();
        constraintViolations = validator.validate(file);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "name is required");
    }

    @Test
    public void testRoleValidation() {
        String id = "role-" + System.currentTimeMillis();
        RoleType type = RoleType.USER_DEFINED;
        Role role = new RoleImpl.Builder().id(id).name(id).type(type).build();
        Set<ConstraintViolation<Role>> constraintViolations = validator.validate(role);
        Assert.assertEquals(constraintViolations.size(), 0);

        role.setId(null);
        constraintViolations = validator.validate(role);
        Assert.assertEquals(constraintViolations.size(), 1);
        log.debug(constraintViolations.iterator().next().getMessage());
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "id is required");

        role.setId(id);
        role.setName(null);
        constraintViolations = validator.validate(role);
        Assert.assertEquals(constraintViolations.size(), 1);
        log.debug(constraintViolations.iterator().next().getMessage());
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "name is required");

        role.setName(id);
        role.setType(null);
        constraintViolations = validator.validate(role);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "type is required");

    }

    @Test
    public void testUserValidation() {
        String id = "user-" + System.currentTimeMillis();
        String name = id;
        String email = id + "@gmail.com";
        char[] password = "xxx".toCharArray();
        Role role = new RoleImpl.Builder().id("my-role").name("My role").build();
        Set<Role> roles = newHashSet(ImmutableSet.of(role));
        User user = new UserImpl.Builder().password(password).id(id).name(name).email(email).login(email).roles(roles).build();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 0);

        user.setId(null);
        constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 1);
        log.debug(constraintViolations.iterator().next().getMessage());
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "id is required");

        user.setId(id);
        user.setName(null);
        constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 1);
        log.debug(constraintViolations.iterator().next().getMessage());
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "name is required");

        user.setName(name);
        user.setEmail("notvalidemail");
        constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "email is not well-formed");

        user.setEmail(email);
        user.setLogin(null);
        constraintViolations = validator.validate(user);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "login is required");

        user = UserService.DefaultUsers.ADMINISTRATOR.getUser();
        constraintViolations = validator.validate(user);
        for (ConstraintViolation<User> cv : constraintViolations) {
            log.info(cv.getMessage());
        }
        Assert.assertEquals(constraintViolations.size(), 0);
    }

    @Test
    public void testParameterValidation() {
        String id = "user-" + System.currentTimeMillis();
        String name = id;
        Parameter parameter1 = new ParameterImpl.Builder().id(id).name(name).type(ParameterType.USER).build();
        Set<ConstraintViolation<Parameter>> constraintViolations = validator.validate(parameter1);
        Assert.assertEquals(constraintViolations.size(), 0);
        
        parameter1 = new ParameterImpl.Builder().id(id).name(name).build();
        constraintViolations = validator.validate(parameter1);
        Assert.assertEquals(constraintViolations.size(), 1);
        Assert.assertEquals(constraintViolations.iterator().next().getMessage(), "type is required");
    }
}
