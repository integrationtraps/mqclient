package trapshoorter.framewrok.io.mq;

import java.io.IOException;
import java.util.Hashtable;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;

public class MQMessageSenderClient {

    public void MQMessageSenderClient()  {
    }

    public void enviaMensagem(String mensagem) throws MQException, IOException {
        MQQueue outboundMessageQueue = getOutboundMessageQueue();
        String messageId = "AAB1234";
        sendMQMessage(outboundMessageQueue,messageId, mensagem);
    }


    private static MQQueue getOutboundMessageQueue() throws MQException {
        MQEnvironment.hostname = "192.168.1.122";
        MQEnvironment.channel = "SYSTEM.DEF.SVRCONN";
        MQEnvironment.port = 1414;

        String queueName = "FL.TESTE";
        String queueManagerName = "QM_DSV";
        return getQueue(queueName, queueManagerName);
    }

    private static MQQueue getQueue(String queueName, String queueManagerName) throws MQException {
        @SuppressWarnings("unchecked")
        Hashtable<String, String> properties = MQEnvironment.properties;
        properties.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);
        MQQueueManager queueManager = new MQQueueManager(queueManagerName);
        int openOptions = MQConstants.MQOO_INPUT_AS_Q_DEF | MQConstants.MQOO_OUTPUT;
        MQQueue queue = queueManager.accessQueue(queueName, openOptions);
        return queue;
    }


    private static void sendMQMessage(final MQQueue queue, String messageId, String payload) throws IOException, MQException {

        MQMessage putMessage = new MQMessage();

        putMessage.messageId = messageId.getBytes();
        putMessage.correlationId = messageId.getBytes();
        putMessage.characterSet = 1208; //UTF-8
        putMessage.format = CMQC.MQFMT_STRING;
        putMessage.persistence = CMQC.MQPER_PERSISTENCE_AS_Q_DEF; //durable message
        putMessage.expiry = 28800000; //in 1/10th of a second. 48 hours.

        putMessage.replyToQueueName = "REPLYTO.Q";
        putMessage.replyToQueueManagerName = "REPLYTO.QMGR";
        putMessage.writeString(payload);

        queue.put(putMessage); //put the message on to the queue
    }

//    public static void main(String[] args) throws MQException, IOException {
//        MQQueue outboundMessageQueue = getOutboundMessageQueue();
//
//        String messageId = "AAB1234";
//        String messagePaylod = "<message>testing of message sending using Webspeher MQ</message>";
//
//        sendMQMessage(outboundMessageQueue,messageId, messagePaylod);
//    }
}
