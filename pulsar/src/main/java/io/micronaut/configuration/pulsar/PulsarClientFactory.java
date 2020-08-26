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
package io.micronaut.configuration.pulsar;

import io.micronaut.configuration.pulsar.config.PulsarClientConfiguration;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import javax.inject.Singleton;

@Factory
@Requires(beans = {PulsarClientConfiguration.class})
public final class PulsarClientFactory {

    @Singleton
    public PulsarClient pulsarClient(PulsarClientConfiguration pulsarClientConfiguration) throws PulsarClientException {
        ClientBuilder clientBuilder = PulsarClient.builder();
        pulsarClientConfiguration.getAuthentication().ifPresent(clientBuilder::authentication);
        pulsarClientConfiguration.getServiceUrl().ifPresent(clientBuilder::serviceUrl);
        //NOTE: this will override service url if present
        pulsarClientConfiguration.getServiceUrlProvider().ifPresent(clientBuilder::serviceUrlProvider);
        return clientBuilder.build();
    }
}
