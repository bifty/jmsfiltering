package com.bharath.jms.claimmanagement;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.api.core.client.SendAcknowledgementHandler;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ClaimManagement {

    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext initialContext = new InitialContext();
        Queue requestQueue = (Queue) initialContext.lookup("queue/claimQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
             JMSContext jmsContext = cf.createContext()) {
            JMSProducer producer = jmsContext.createProducer();
            JMSConsumer consumer = jmsContext.createConsumer(requestQueue, "doctorType IN ('neuro','psych') OR JMSPriority BETWEEN 5 AND 9");

            ObjectMessage objectMessage = jmsContext.createObjectMessage();
            //objectMessage.setIntProperty("hospitalId", 1);
            //objectMessage.setDoubleProperty("claimAmount", 1000);
            //objectMessage.setStringProperty("doctorName", "John");
            objectMessage.setStringProperty("doctorType", "gyna");
            Claim claim = new Claim();
            claim.setHospitalId(1);
            claim.setClaimAmount(1000);
            claim.setDoctorName("John");
            claim.setDoctorType("gyna");
            claim.setInsuranceProvider("blue cross");
            objectMessage.setObject(claim);

            producer.send(requestQueue, objectMessage);

            Claim receiveBody = consumer.receiveBody(Claim.class);
            System.out.println(receiveBody.getClaimAmount());
        }
    }

}
