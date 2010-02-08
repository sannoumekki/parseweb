package examples.ejb.ejb20.basic.transaction;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.*;

import java.util.Hashtable;

public class QueueSender {
  private String url = "t3://localhost:7001";
  private Queue queue = null;
  private QueueConnection m_queueConnection;
  private QueueSession session = null;
  private javax.jms.QueueSender sender = null;

  private static final boolean VERBOSE = true;
  private void log(String s) {
    if (VERBOSE) System.out.println("[QueuSnd] " + s);
  }

  public QueueSender(String url, String connection_factory_jndi, String queue_name, boolean auto_create) throws Exception {
    this.url = url;
    Context ctx = null;

    try {
      ctx = getInitialContext();

      // Create the connection and start it
      QueueConnectionFactory cf =
        (QueueConnectionFactory) ctx.lookup(connection_factory_jndi);
      m_queueConnection = cf.createQueueConnection();
      m_queueConnection.start();

      log("Creating a queue session");
      session = m_queueConnection.createQueueSession(false,   // non transacted
                                                     javax.jms.Session.AUTO_ACKNOWLEDGE);

      queue = (Queue) ctx.lookup(queue_name);

      sender = session.createSender(queue);
    } catch (NamingException ex) {
      if (auto_create) {
        queue = session.createQueue(queue_name);
        ctx.bind(queue_name, queue);
      }
    }
  }

  public void send(String message) throws JMSException {
    log("Send a message to JMS server");
    TextMessage tm = session.createTextMessage();
    tm.setText(message);
    sender.send(tm);
  }

  private Context getInitialContext() throws NamingException {
    try {
      // Get an InitialContext
      Hashtable<String,String> h = new Hashtable<String,String>();
      h.put(Context.INITIAL_CONTEXT_FACTORY,
        "weblogic.jndi.WLInitialContextFactory");
      h.put(Context.PROVIDER_URL, url);
      return new InitialContext(h);
    } catch (NamingException ne) {
      log("We were unable to get a connection to the WebLogic server at " + url);
      log("Please make sure that the server is running.");
      throw ne;
    }
  }

}
