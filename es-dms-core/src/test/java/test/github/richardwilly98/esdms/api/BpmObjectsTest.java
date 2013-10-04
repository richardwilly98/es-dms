package test.github.richardwilly98.esdms.api;

import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.bpm.ProcessDefinitionImpl;
import com.github.richardwilly98.esdms.bpm.ProcessInstanceImpl;
import com.github.richardwilly98.esdms.bpm.TaskImpl;
import com.github.richardwilly98.esdms.bpm.api.ProcessDefinition;
import com.github.richardwilly98.esdms.bpm.api.ProcessInstance;
import com.github.richardwilly98.esdms.bpm.api.Task;

public class BpmObjectsTest {
    private static Logger log = Logger.getLogger(BpmObjectsTest.class);

    @Test
    public void testProcessDefinition() {
        log.debug("*** testProcessDefinition ***");
        ProcessDefinition definition = new ProcessDefinitionImpl.Builder().owner("richard").creation(new Date())
                .description("My process definition").name("definition1").id("definition1").category("category1").version("1.0.0")
                .status("open").build();
        Assert.assertNotNull(definition);
        Assert.assertEquals(definition.getId(), "definition1");
        Assert.assertEquals(definition.getName(), "definition1");
        Assert.assertEquals(definition.getDescription(), "My process definition");
        Assert.assertEquals(definition.getOwner(), "richard");
        Assert.assertEquals(definition.getCategory(), "category1");
        Assert.assertEquals(definition.getVersion(), "1.0.0");
        Assert.assertEquals(definition.getStatus(), "open");
        Assert.assertNotNull(definition.getCreation());
    }

    @Test
    public void testNewProcessInstance() {
        log.debug("*** testNewProcessInstance ***");
        ProcessInstance task = new ProcessInstanceImpl.Builder().initiator("richard").start(new Date()).description("My process instance")
                .name("instance1").id("instance1").owner("user1").modified(new Date()).processDefinitionId("definition1").status("closed").build();
        Assert.assertNotNull(task);
        Assert.assertEquals(task.getId(), "instance1");
        Assert.assertEquals(task.getName(), "instance1");
        Assert.assertEquals(task.getDescription(), "My process instance");
        Assert.assertEquals(task.getInitiator(), "richard");
        Assert.assertEquals(task.getOwner(), "user1");
        Assert.assertEquals(task.getProcessDefinitionId(), "definition1");
        Assert.assertEquals(task.getStatus(), "closed");
        Assert.assertNotNull(task.getStart());
        Assert.assertNotNull(task.getModified());
        Assert.assertNull(task.getEnd());
    }

    @Test
    public void testNewTask() {
        log.debug("*** testNewTask ***");
        Task task = new TaskImpl.Builder().assignee("richard").creation(new Date()).description("My task").name("task1").id("task1")
                .owner("user1").priority(50).processInstanceId("pi1").status("open").build();
        Assert.assertNotNull(task);
        Assert.assertEquals(task.getId(), "task1");
        Assert.assertEquals(task.getName(), "task1");
        Assert.assertEquals(task.getDescription(), "My task");
        Assert.assertEquals(task.getAssignee(), "richard");
        Assert.assertEquals(task.getOwner(), "user1");
        Assert.assertEquals(task.getPriority(), 50);
        Assert.assertEquals(task.getProcessInstanceId(), "pi1");
        Assert.assertEquals(task.getStatus(), "open");
        Assert.assertNotNull(task.getCreation());
        Assert.assertNull(task.getDue());
    }
}
