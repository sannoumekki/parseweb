package fi.hut.tml.xsmiles.messaging;

import java.util.Hashtable;
import java.net.URL;
import fi.hut.tml.xsmiles.Browser;
import fi.hut.tml.xsmiles.Log;


import fi.hut.tml.xsmiles.EventBroker;
/**
 * Java messaging class. Implements the abstract Messaging interface.
 *
 */
public class JMSMessaging
    implements Messaging,javax.jms.MessageListener
{
        private static  JMSMessaging instance = new JMSMessaging();
        private Browser browser;


        private static final int    MESSAGE_LIFESPAN = 1800000;  // milliseconds (30 minutes)
        private static final int    MESSAGE_WAIT_TIME = 60000;  // milliseconds

        private static final String DEFAULT_PASSWORD = "xsmiles";
        private static final String DEFAULT_UNAME = "xsmiles";

        private static final String DEFAULT_POST_SEND_QUEUE = "SampleQ1";
        private static final String DEFAULT_POST_RECEIVE_QUEUE = "SampleQ2";

    private javax.jms.TopicConnection tConnect = null;
    private javax.jms.TopicSession tSession = null;
    private javax.jms.TopicPublisher publisher = null;
//      private javax.jms.TopicSubscriber subscriber = null;
        private String pubTopicString;

//    private javax.jms.QueueConnection qConnect = null;
//    private javax.jms.QueueSession qSession = null;
//    private javax.jms.QueueSender sender = null;
//      private javax.jms.QueueReceiver receiver = null;


        private String username = DEFAULT_PASSWORD;
        private String password = DEFAULT_UNAME;


        public JMSMessaging()
        {

        }


        public static JMSMessaging getInstance() {
        return instance;
        }


        public void initialize(Browser myBrowser){

                browser = myBrowser;

                String broker = browser.getBrowserConfigurer().getBroker();
                String subTopicString = browser.getBrowserConfigurer().getSubscribeTopic();
                pubTopicString = browser.getBrowserConfigurer().getPublishTopic();



                // Create a topic connection.
                try
                {
                        Log.info("Connecting to JMS broker at "+broker);
                javax.jms.TopicConnectionFactory tFactory;
                tFactory = (new progress.message.jclient.TopicConnectionFactory (broker));
                tConnect = tFactory.createTopicConnection (username, password);
                tSession = tConnect.createTopicSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
                        Log.info("JMS connect ok.");
                }
                catch (javax.jms.JMSException jmse)
                {

                        Log.error(jmse);
                        return;
                }

                // Create a queue connection.
//              try
//        {
//            javax.jms.QueueConnectionFactory qFactory;
//            qFactory = (new progress.message.jclient.QueueConnectionFactory (broker));
//            qConnect = qFactory.createQueueConnection (username, password);
//            qSession = qConnect.createQueueSession(false,javax.jms.Session.AUTO_ACKNOWLEDGE);
//        }
//        catch (javax.jms.JMSException jmse)
//        {
//                      Log.error(jmse);
//        }
//

                // Create Publisher and Subscriber for the topic connection
                try
        {
       //     javax.jms.Topic subTopic = tSession.createTopic (subTopicString);
                        javax.jms.Topic pubTopic = tSession.createTopic (pubTopicString);

                //      subscriber = tSession.createSubscriber(subTopic);
            // subscriber.setMessageListener(this);
            publisher = tSession.createPublisher(pubTopic);
            //Log.info("Connecting to JMS broker at "+broker);
            tConnect.start();


//                      Log.debug("First message");
//              javax.jms.TextMessage m = tSession.createTextMessage();
//              m.setText("Hello, starting");
//              publisher.publish(m);
        }
        catch (javax.jms.JMSException jmse)
        {
                        Log.error(jmse);

        }


        // Create Sender and Receiver queues
//        try
//        {
//            javax.jms.Queue sendQueue = qSession.createQueue (DEFAULT_POST_SEND_QUEUE);
//                      sender = qSession.createSender(sendQueue);
//            javax.jms.Queue receiveQueue = qSession.createQueue (DEFAULT_POST_RECEIVE_QUEUE);
//            receiver = qSession.createReceiver(receiveQueue);
//            // qReceiver.setMessageListener(this);
//            qConnect.start();
//
//
//        javax.jms.TextMessage msg = qSession.createTextMessage();
//        msg.setText( "Hello there\n" );
//        sender.send( msg,javax.jms.DeliveryMode.PERSISTENT,
//                         javax.jms.Message.DEFAULT_PRIORITY,
//                 MESSAGE_LIFESPAN);
//
//        }
//        catch (javax.jms.JMSException jmse)
//        {
//                      Log.error(jmse);
//
//        }





        } // initialize


  public void onMessage( javax.jms.Message aMessage)
    {
                java.net.URL requiredDoc;

        try
        {
            // Cast the message as a text message.
            javax.jms.TextMessage textMessage = (javax.jms.TextMessage) aMessage;

            // This handler reads a single String from the
            // message and prints it to the standard output.
            try
            {
                String string = textMessage.getText();
                System.out.println( string );

                                // If incoming message is just an URL,
                                // we'll try to open that
                                if (isURL(string))
                                {
                                        Log.info("JMS message received, request for URL:" + string);

                                        try
                                        {
                                                requiredDoc = new java.net.URL(string);
                                                EventBroker.getInstance().issueXMLDocRequiredEvent(requiredDoc, true);
                                        } catch (Exception e)
                                {
                                        Log.error(e);
                                }
                                }

                        }
            catch (javax.jms.JMSException jmse)
            {
                jmse.printStackTrace();
            }
        }
        catch (java.lang.RuntimeException rte)
        {
           Log.error(rte);
        }
    } // onMessage


//      public void publishMessage(String message)
//      {
//
//       try
//     {
//
//              javax.jms.TextMessage msg = session.createTextMessage();
//        msg.setText( message );
//        publisher.publish( msg );
//        }
//      catch ( javax.jms.JMSException jmse )
//      {
//            Log.error(jmse);
//      }
//
//
//      } // publishMessage


        private boolean isURL(String message)
        {
                return true;
        }


        public String post(URL dest,String data,Hashtable properties)
        {

                try
                {




                    javax.jms.TemporaryTopic tempTopic;
                        javax.jms.TopicSubscriber subscriber;
                        tempTopic = tSession.createTemporaryTopic();
                        subscriber = tSession.createSubscriber(tempTopic);



                        Log.debug("Posting (using JMS) to topic:"+browser.getBrowserConfigurer().getPublishTopic());
                        //Log.debug("Using Topic"+dest.toString());
                        // JMS post is always syncronous
                        // Open connection to the URL
//                      javax.jms.Topic topic = tSession.createTopic (dest.toString());
//                      javax.jms.TopicPublisher tempPublisher = tSession.createPublisher(topic);
                        progress.message.jclient.XMLMessage msg =
                                ((progress.message.jclient.Session) tSession).createXMLMessage();
                        //javax.jms.TextMessage msg = tSession.createTextMessage();
                msg.setText( data );
//                      publisher.publish( msg,javax.jms.DeliveryMode.PERSISTENT,
//                               javax.jms.Message.DEFAULT_PRIORITY,
//                               MESSAGE_LIFESPAN);



                        msg.setJMSReplyTo(tempTopic);
                        publisher.publish(msg);
                        Log.debug("Message sent, waiting for response");
                        javax.jms.Message receivedMessage = subscriber.receive(MESSAGE_WAIT_TIME);


                        javax.jms.TextMessage receivedTextMessage = (javax.jms.TextMessage) receivedMessage;
                        String string = receivedTextMessage.getText();
                        Log.debug(string);

                        return string;


                } catch (javax.jms.JMSException e)
                {
                        Log.error(e);
                        return null;
                }
        }









}
