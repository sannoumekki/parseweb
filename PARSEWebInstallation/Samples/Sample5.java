package jms.queue.test;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;

public class JmsQueueSender {
  public static void main (String[] args) throws Exception{

    // Få fat i en JNDI initial context
    InitialContext initialContext = new InitialContext ();

    // Slå den kø op, som beskederne skal sendes på
    Queue testQueue = (Queue)initialContext.lookup ("queue/testQueue");

    // Slå et factory op, der kan lave connections til JMS-serveren (= JDBC DataSource)
    QueueConnectionFactory queueFactory = (QueueConnectionFactory)initialContext.lookup ("ConnectionFactory");;

    // Brug factory'et til at lave en connection til JMS-serveren (= JDBC Connection)
    QueueConnection connection = queueFactory.createQueueConnection ();

    // Lav en session på forbindelsen (= JDBC Statement)
    boolean transacted = false;
    int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
    QueueSession session = connection.createQueueSession (transacted, acknowledgeMode);

    // Lav en sender
    QueueSender queueSender = session.createSender (testQueue);

    for (int n = 1; n <= 1000; n++) {
      // Lav den JMS-besked, der skal sendes
      ObjectMessage objectMessage = session.createObjectMessage ();

      // Sæt beskedens indhold
      objectMessage.setObject (new Integer(n));

      // Send beskeden
      queueSender.send (objectMessage);

      if ((n % 100) == 0) {
        System.out.println ("Has now sent " + n + " messages!");
      }
    }

    // Luk forbindelsen til JMS-serveren
    connection.close ();
  }
}
