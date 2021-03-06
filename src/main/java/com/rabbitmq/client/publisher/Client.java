package com.rabbitmq.client.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.vo.Base;
import com.rabbitmq.client.vo.BeverageType;
import com.rabbitmq.client.vo.Core;
import com.rabbitmq.client.vo.QueueMessage;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {

    @Autowired
    private Exchange exchange;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    AtomicInteger index = new AtomicInteger(0);
    private ObjectMapper objectMapper = new ObjectMapper();

    Random random = new Random();


    @Scheduled(fixedDelay = 1000, initialDelay = 500)
    public void send() throws JsonProcessingException {


        StringBuilder keyBuilder = new StringBuilder("");
        StringBuilder IDbuilder = new StringBuilder("");
        QueueMessage queueMessage;
        IDbuilder.append(this.index.incrementAndGet());


        switch (random.nextInt(5)) {
            case 0:
                queueMessage = new QueueMessage(IDbuilder.toString(),"아아", Base.WATER.toString(), Core.BEAN.toString(), BeverageType.COFFEE,new Date());
                keyBuilder.append("coffee");

                break;
            case 1:
                queueMessage = new QueueMessage(IDbuilder.toString(),"라떼",Base.MILK.toString(),Core.BEAN.toString(),BeverageType.COFFEE,new Date());
                keyBuilder.append("coffee");
                break;
            case 2:
                queueMessage = new QueueMessage(IDbuilder.toString(),"에이드",Base.ADE.toString(),Core.SYRUP.toString(),BeverageType.NORMAL,new Date());
                keyBuilder.append("normal");
                break;
            case 3:
                queueMessage = new QueueMessage(IDbuilder.toString(),"차",Base.WATER.toString(),Core.TEA.toString(),BeverageType.NORMAL,new Date());
                keyBuilder.append("normal");
                break;

            default:
                queueMessage = new QueueMessage(IDbuilder.toString(),"스무디",Base.MILK.toString(),Core.POWDER.toString(),BeverageType.BLENDER,new Date());
                keyBuilder.append("blender");
                break;
        }



        String jsonMessage = objectMapper.writeValueAsString(queueMessage);
        rabbitTemplate.convertAndSend(exchange.getName(), keyBuilder.toString(), jsonMessage,m ->{
            m.getMessageProperties().getHeaders().put("x-death",0);
            m.getMessageProperties().getHeaders().put("x-delay",10);
            return m;
        });
        System.out.println(" [x] Sent to " + exchange.getName() + " " + keyBuilder.toString() + " '" + queueMessage.getId() + "'" + "ordered : "+ queueMessage.getMenu());


    }
}
