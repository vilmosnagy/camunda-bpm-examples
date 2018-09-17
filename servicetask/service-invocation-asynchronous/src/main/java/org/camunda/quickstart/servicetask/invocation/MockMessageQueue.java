package org.camunda.quickstart.servicetask.invocation;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * <p>This class is supposed to mock the queuing infrastructure between the process engine
 * and the service implementation. In a real life scenario, we would use reliable queuing
 * middleware such as a transactional messaging system (typically JMS).</p>
 *
 */
public class MockMessageQueue {

  /** a Message with payload */
  public static class Message {

    protected final Map<String, Object> payload;

    public Message(Map<String, Object> payload) {
      this.payload = payload;
    }

    public Map<String, Object> getPayload() {
      return payload;
    }

  }

  protected BlockingDeque<Message> queue = new LinkedBlockingDeque<>();

  public final static MockMessageQueue INSTANCE = new MockMessageQueue();

  public void send(Message m) {
    queue.add(m);
  }

  public Message getNextMessage() {
    try {
      return queue.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
