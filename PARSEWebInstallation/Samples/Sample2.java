import java.util.*;
import javax.jms.*;
import javax.naming.*;

public class RemoteJMSQueueSender {

        // Set up objects for use by JMS.
        private Context _context = null;
        private QueueConnection _connection = null;
        private QueueSession _session = null;
        private TextMessage _message = null;
        private QueueSender _sender = null;


        public static void main(String [] args) {
                System.setSecurityManager(new java.rmi.RMISecurityManager());

                RemoteJMSQueueSender jqs = new RemoteJMSQueueSender();
                if(args.length >0 )
                        jqs.sendMessage(args[0]);
                else
                        jqs.sendMessage("Hello from Macromedia JRun");
        }

        public void sendMessage(String msg){
                try {
                        java.util.Properties properties = new java.util.Properties();
                        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,"allaire.ejipt.ContextFactory");
                        properties.setProperty(Context.PROVIDER_URL,"ejipt://10.1.216.151:3324");
                        _context = new InitialContext(properties);

                        // Get QueueConnectionFactory from JNDI.
                        final QueueConnectionFactory factory =(QueueConnectionFactory)_context.lookup("java:comp/env/jms/QueueConnectionFactory");

                        // Use factory to create QueueConnection (anonymous)
                        _connection = factory.createQueueConnection();

                        // Use QueueConnection to create QueueSession
                        _session = _connection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);

                        // Uses QueueSession to create QueueSender
                        // lookup will fail with NamingException defaultQueue Not found
                        //_sender = _session.createSender((Queue)_context.lookup("java:comp/env/jms/defaultQueue"));
                        // use createQueue method instead
                        _sender = _session.createSender(_session.createQueue("defaultQueue"));

                        // Create TextMessage object
                        _message = _session.createTextMessage();
                }
                catch(NamingException e) {
                        System.out.println("NamingException: " + e.getMessage());
                        return;
                }
                catch(JMSException e) {
                        System.out.println("JMSException: " + e.getMessage());
                        return;
                }

                int priority = Message.DEFAULT_PRIORITY;
                int delivery = Message.DEFAULT_DELIVERY_MODE;

                try {
                        _message.setText(msg);
                        // Send to the queue. The message will last for 5 minutes.
                        _sender.send(_message, delivery, priority, 5 * 60 * 1000);
                }
                catch(JMSException e) {
                        System.out.println("JMS Exception: " + e.getMessage());
                }

                // Display send confirmation.
                System.out.println("Message sent successfully : " + msg);

                try {
                        if (_sender != null ) _sender.close();
                        if(_session != null) _session.close();
                        if(_connection != null) _connection.close();
                        if (_context != null) _context.close();
                }
                catch(Exception e) {
                        // ignore
                }
                return;
        } // End sendMessage
}
