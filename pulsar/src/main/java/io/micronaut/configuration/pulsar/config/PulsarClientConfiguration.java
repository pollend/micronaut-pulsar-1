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
package io.micronaut.configuration.pulsar.config;

import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.ServiceUrlProvider;

import java.util.Optional;

public interface PulsarClientConfiguration {
    String getServiceUrl();

    default Optional<ServiceUrlProvider> getServiceUrlProvider() {
        return Optional.empty();
    }

    Optional<Authentication> getAuthentication();

    default boolean requiresAuthentication() {
        return getAuthentication().isPresent();
    }
}
