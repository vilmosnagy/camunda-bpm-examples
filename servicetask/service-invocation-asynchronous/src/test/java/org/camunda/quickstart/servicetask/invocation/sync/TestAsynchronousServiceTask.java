package org.camunda.quickstart.servicetask.invocation.sync;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.quickstart.servicetask.invocation.AsynchronousServiceTask;
import org.camunda.quickstart.servicetask.invocation.BusinessLogic;
import org.camunda.quickstart.servicetask.invocation.MockMessageQueue;
import org.camunda.quickstart.servicetask.invocation.MockMessageQueue.Message;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for demonstrating the asynchronous service invocation.
 *
 */
public class TestAsynchronousServiceTask {

  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRule();

  @Test
  @Deployment(resources = { "asynchronousServiceInvocation.bpmn" })
  public void testServiceInvocationSuccessful() throws InterruptedException {

    final ProcessEngine processEngine = processEngineRule.getProcessEngine();
    final RuntimeService runtimeService = processEngineRule.getRuntimeService();

    Map<String, Object> variables = Collections.<String, Object> singletonMap(BusinessLogic.SHOULD_FAIL_VAR_NAME, false);

    // start the process instance on a new thread
    new Thread(() -> {
      runtimeService.startProcessInstanceByKey("asynchronousServiceInvocation", variables);
    }).start();

    // the message is present in the Queue:
    System.out.println("Waiting for Message, " + System.currentTimeMillis());

    Message message = MockMessageQueue.INSTANCE.getNextMessage();

    System.out.println("Message received, " + System.currentTimeMillis());

    assertNotNull(message);

    // Next, trigger the business logic. This will send the callback to the process engine.
    // When this method call returns, the process instance will be waiting in the next waitstate.
    BusinessLogic.INSTANCE.invoke(message, processEngine);

  }

}
