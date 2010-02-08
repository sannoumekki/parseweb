package com.customware.client;

import java.rmi.*;
import java.util.*;

import javax.jms.*;
import javax.naming.*;

/**
 * EmailClient is responsible for sending an email mapmessage to the queue
 */
public class EmailClient {
    private static String QUEUE_NAME         = "EmailQueue";
    private static String CONNECTION_FACTORY = "javax.jms.QueueConnectionFactory";
    private static String PROVIDER_URL       = "t3://localhost:7001";

        private QueueSender  sender;
        private QueueSession session;

        /**
         * Constructor: Setup JMS for publishing
         */
    public EmailClient() {
        try {
            Context ctx = getInitialContext();

                        // Lookup a JMS connection factory
                        QueueConnectionFactory conFactory =
                                (QueueConnectionFactory) ctx.lookup(CONNECTION_FACTORY);

                        // Create a JMS connection
                        QueueConnection connection = conFactory.createQueueConnection();

                        // Create a JMS session object
                        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

                        // Lookup a JMS queue
                        Queue chatQueue = (Queue) ctx.lookup(QUEUE_NAME);

                        // Create a JMS sender
                        sender = session.createSender(chatQueue);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


        /**
         * Method: sendmail(Map mail)
         */
    public void sendmail(Map mail) throws Exception {
        MapMessage message = session.createMapMessage();

                Set keys = mail.keySet();
                Iterator i = keys.iterator();
                while (i.hasNext()) {
                        String key = (String) i.next();
                        String val = (String) mail.get(key);

                        message.setString(key, val);
                }
                // send the mapmessage to the Queue
        sender.send(message);
    }


        /**
         * Method: getInitialContext()
         *
         * Login to JNDI
         *
         * @return Context The initial context
         */
    private Context getInitialContext() throws NamingException {
        Properties env = new Properties();
        env.put("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
        env.put("java.naming.provider.url", PROVIDER_URL);
        env.put("java.naming.security.principal", "system");
                env.put("java.naming.security.credentials", "weblogic");

        return new InitialContext(env);
    }


        /**
         * Static Method: java com.customware.client.EmailClient to_addr [from_addr] [subject] [body]
         */
    public static void main(String args[]) throws Exception {
                if (args.length == 0) {
                        System.out.println("Usage: java com.customware.client.EmailClient to_addr [from_addr] [subject] [body]");
                        System.exit(-1);
                }

                System.out.println("\nBeginning EmailClient\n");
                EmailClient client = new EmailClient();

                Hashtable mail = new Hashtable();
                mail.put("to",      args[0]);
                if (args.length > 1) mail.put("from",    args[1]);
                if (args.length > 2) mail.put("subject", args[2]);
                if (args.length > 3) mail.put("body",    args[3]);

                System.out.println("Sending mail message");
                client.sendmail(mail);
    }
}
