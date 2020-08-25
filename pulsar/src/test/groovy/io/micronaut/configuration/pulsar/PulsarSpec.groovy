/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.configuration.pulsar

import io.micronaut.configuration.pulsar.annotation.PulsarConsumer
import io.micronaut.configuration.pulsar.annotation.PulsarListener
import io.micronaut.configuration.pulsar.config.PulsarClientConfiguration
import io.micronaut.configuration.pulsar.events.ConsumerSubscribedEvent
import io.micronaut.configuration.pulsar.processor.PulsarConsumerProcessor
import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.CollectionUtils
import io.micronaut.core.util.StringUtils
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.EmbeddedServer
import org.apache.pulsar.client.admin.PulsarAdmin
import org.apache.pulsar.client.api.*
import org.apache.pulsar.common.policies.data.TenantInfo
import org.testcontainers.containers.PulsarContainer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.PollingConditions

import javax.inject.Singleton

@Stepwise
class PulsarSpec extends Specification {

    @Shared
    @AutoCleanup
    PulsarContainer pulsarContainer = new PulsarContainer("2.6.0")
    @Shared
    @AutoCleanup
    ApplicationContext context
    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer

    def setupSpec() {
        pulsarContainer.start()
        embeddedServer = ApplicationContext.run(EmbeddedServer,
                CollectionUtils.mapOf("pulsar.service-url", pulsarContainer.pulsarBrokerUrl),
                StringUtils.EMPTY_STRING_ARRAY
        )
        context = embeddedServer.applicationContext
        PulsarAdmin admin = PulsarAdmin.builder().serviceHttpUrl(pulsarContainer.httpServiceUrl).build()
        TenantInfo tenant = new TenantInfo([].toSet() as Set<String>, admin.clusters().getClusters().toSet())
        admin.tenants().createTenant("test", tenant)
        admin.namespaces().createNamespace("test/second")
        admin.topics().createNonPartitionedTopic("test/second/test")
    }

    void "test load configuration"() {
        expect:
        context.isRunning()
        context.containsBean(PulsarClientConfiguration)
        context.containsBean(PulsarClient)
        context.containsBean(PulsarConsumerProcessor)
        context.containsBean(PulsarConsumerTopicListTester)
        pulsarContainer.pulsarBrokerUrl == context.getBean(PulsarClientConfiguration).serviceUrl.get()
    }

    void "test consumer read"() {
        when:
        def topic = "public/default/test"
        def consumerTester = context.getBean(PulsarConsumerTopicListTester)
        def producer = context.getBean(PulsarClient).newProducer().topic(topic).create()
        //simple consumer with topic list and blocking
        def message = "This should be received"
        def messageId = producer.send(message.bytes)
        def list = context.getBean(PulsarConsumerRegistry).getConsumers()
        PollingConditions conditions = new PollingConditions(timeout: 30, delay: 1)

        then:
        2 == list.size()
        conditions.eventually {
            message == consumerTester.latestMessage
            messageId == consumerTester.latestMessageId
        }

        cleanup:
        producer.close()
    }

    void "test pattern consumer read with async"() {
        when:
        def topic = "public/default/test"
        def producer = context.getBean(PulsarClient).newProducer().topic(topic).create()
        def consumerPatternTester = context.getBean(PulsarConsumerTopicPatternTester)
        //consumer using pattern subscription and async mode and different order of method args
        EventInterceptor interceptor = context.getBean(EventInterceptor)
        def message = "This should be received"
        MessageId messageId = producer.send(message.bytes)

        then:
        new PollingConditions(timeout: 60, delay: 1).eventually {
            interceptor.hasEvent()
            message == consumerPatternTester.latestMessage
            messageId == consumerPatternTester.latestMessageId
        }

        cleanup:
        producer.close()
    }

    @Singleton
    static class EventInterceptor {
        private ConsumerSubscribedEvent event

        @EventListener
        void listener(ConsumerSubscribedEvent event) {
            this.event = event
        }

        boolean hasEvent() { return null != event }

        ConsumerSubscribedEvent getEvent() { return event }
    }

    @PulsarListener()
    static class PulsarConsumerTopicListTester {
        String latestMessage
        MessageId latestMessageId
        Consumer<byte[]> latestConsumer

        PulsarConsumerTopicListTester() {
        }

        //testing reverse order to ensure processor will do correct call
        @PulsarConsumer(topics = ["public/default/test"], subscribeAsync = false)
        def topicListener(Message<byte[]> message, Consumer<byte[]> consumer) {
            latestMessageId = message.messageId
            latestMessage = new String(message.getValue())
            latestConsumer = consumer
        }
    }

    @PulsarListener
    static class PulsarConsumerTopicPatternTester {
        String latestMessage
        Consumer<byte[]> latestConsumer
        MessageId latestMessageId

        //testing default order
        @PulsarConsumer(topicsPattern = "public/default/tst2", subscriptionTopicsMode = RegexSubscriptionMode.AllTopics)
        void topicListener(Consumer<byte[]> consumer, Message<byte[]> message) {
            latestMessage = new String(message.getValue())
            latestConsumer = consumer
            latestMessageId = message.messageId
        }
    }
}