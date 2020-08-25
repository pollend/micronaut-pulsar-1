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
package io.micronaut.configuration.pulsar.annotation;

import io.micronaut.configuration.pulsar.config.AbstractPulsarConfiguration;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.DefaultScope;
import io.micronaut.messaging.annotation.MessageListener;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.RegexSubscriptionMode;
import org.apache.pulsar.client.api.Schema;

import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
@MessageListener
@DefaultScope(Context.class)
public @interface PulsarConsumer {

    /**
     * @return List of topic names in form of (persistent|non-persistent)://tenant-name/namespace/topic
     */
    String[] topics() default {};

    /**
     * @return Topics name in form of tenantName/namespace/topic-name-pattern.
     */
    @Pattern(regexp = AbstractPulsarConfiguration.TOPIC_NAME_VALIDATOR)
    String topicsPattern() default "";

    /**
     * Defaults to {@code byte[]}
     *
     * @return Type of body in puslar message
     */
    Class<?> messageBodyType() default byte[].class;

    /**
     * Defaults to {@link MessageSchema#BYTES} as default value for Puslar {@link Schema} is {@code byte[]}.
     *
     * @return
     */
    MessageSchema schemaType() default MessageSchema.BYTES;

    /**
     * @return Consumer name for more descriptive monitoring
     */
    String consumerName();

    /**
     * Default value {@link RegexSubscriptionMode#AllTopics}
     * <p>
     * Whether to read topics from persistent, or non-persistent storage, or both
     * <p>
     * If topics are set other than {@link this#topicsPattern()} this value will be ignored.
     *
     * @return subscription
     */
    RegexSubscriptionMode subscriptionTopicsMode();

    /**
     * By default not set.
     * <p>
     * Must be convertible to {@link java.time.Duration}
     *
     * @return Topics auto discovery period for new topics when using {@link this#topicsPattern()}.
     */
    String patternAutoDiscoveryPeriod();

    /**
     * By default consumer should subscribe in non-blocking manner using default
     * {@link java.util.concurrent.CompletableFuture} of {@link ConsumerBuilder#subscribeAsync()}
     * <p>
     * If blocking set to false application will block until consumer is successfully subscribed
     *
     * @return Should the consumer subscribe in async manner or blocking
     */
    boolean subscribeAsync() default true;

    /**
     * By default it will use default value of {@link org.apache.pulsar.client.api.ConsumerBuilder} which is disabled
     * and no redelivery happens unless consumer crashed.
     * <p>
     * Must be greater than 1s.
     *
     * @return Allowed time to pass before message is acknowledged.
     * @see org.apache.pulsar.client.api.ConsumerBuilder#ackTimeout
     */
    String ackTimeout() default "";

    /**
     * @return
     */
    int receiverQueueSize() default -1;

    /**
     * By default no priority is set.
     * Use any value less than 0 to disable. Use anything above 0 to set lower priority level.
     *
     * @return priority level for a consumer
     * @see ConsumerBuilder#priorityLevel(int)
     */
    int priorityLevel() default -1;

    enum MessageSchema { BYTES, JSON, AVRO, PROTOBUF, KEY_VALUE }
}
