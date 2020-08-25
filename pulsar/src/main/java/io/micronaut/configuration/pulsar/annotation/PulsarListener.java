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

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.DefaultScope;
import io.micronaut.messaging.annotation.MessageListener;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.SubscriptionType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ElementType.TYPE})
@MessageListener
@DefaultScope(Context.class)
public @interface PulsarListener {

    /**
     * If not set UUID will be generated as subscription name to avoid collisions if consumer type is Exclusive.
     *
     * @return Subscription name
     * @see ConsumerBuilder#subscriptionType
     */
    String subscriptionName() default "";

    /**
     * By default Exclusive.
     *
     * @return Type of consumer subscription
     * @see ConsumerBuilder#subscriptionType
     */
    SubscriptionType subscriptionType() default SubscriptionType.Exclusive;

    /**
     * By default it will use PulsarConsumer builder default values.
     *
     * @return Maximum amount of time allowed to pass for message to be acknowledged or else redelivery happens.
     * @see org.apache.pulsar.client.api.ConsumerBuilder#acknowledgmentGroupTime
     */
    String ackGroupTimeout() default "";
}
