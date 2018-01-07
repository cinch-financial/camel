/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Envelope;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

public class RabbitConsumerTest {
    private RabbitMQConsumer consumer = Mockito.mock(RabbitMQConsumer.class);
    private Processor processor = Mockito.mock(Processor.class);
    private RabbitMQEndpoint endpoint = Mockito.mock(RabbitMQEndpoint.class);
    private RabbitMQMessageConverter messageConverter = Mockito.mock(RabbitMQMessageConverter.class);
    private Connection conn = Mockito.mock(Connection.class);
    private Channel channel = Mockito.mock(Channel.class);
    private Exchange exchange = Mockito.mock(Exchange.class);
    private String consumerTag = "tag";
    private Envelope envelope = Mockito.mock(Envelope.class);
    private AMQP.BasicProperties properties = Mockito.mock(AMQP.BasicProperties.class);

    @Test(timeout = 5000)
    public void testHandleDeliveryShouldNotHangForeverIfChannelWasClosed() throws Exception {
        Mockito.when(consumer.getEndpoint()).thenReturn(endpoint);
        Mockito.when(consumer.getProcessor()).thenReturn(processor);
        Mockito.when(endpoint.getMessageConverter()).thenReturn(messageConverter);
        Mockito.when(endpoint.createRabbitExchange(any(Envelope.class), any(AMQP.BasicProperties.class), any())).thenReturn(exchange);
        Mockito.when(consumer.getConnection()).thenReturn(conn);
        Mockito.when(conn.createChannel()).thenReturn(channel);
        Mockito.when(channel.isOpen()).thenReturn(false).thenReturn(true);

        RabbitConsumer rabbitConsumer = new RabbitConsumer(consumer);

        rabbitConsumer.handleDelivery(consumerTag, envelope, properties, "body".getBytes());
        rabbitConsumer.handleDelivery(consumerTag, envelope, properties, "body".getBytes());
        rabbitConsumer.stop();
    }
}