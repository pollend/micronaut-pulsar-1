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

import javax.annotation.Nonnull;
import java.util.Properties;

public abstract class AbstractPulsarConfiguration<K, V> {
    /**
     * The default Apache Pulsar messaging port.
     */
    public static final int DEFAULT_PULSAR_MESSAGING_PORT = 6650;
    /**
     * The default prefix used for Pulsar configuration.
     */
    public static final String PREFIX = "pulsar";
    /**
     * The default server hostname or IP address.
     */
    public static final String DEFAULT_SERVER_HOST_ADDRESS = "localhost";
    /**
     * The default bootstrap server address for messaging.
     */
    public static final String DEFAULT_BOOTSTRAP_SERVER = "pulsar://" + DEFAULT_SERVER_HOST_ADDRESS + ":" + DEFAULT_PULSAR_MESSAGING_PORT;
    /**
     * By default TLS is disabled for Apache Pulsar messaging.
     */
    public static final boolean DEFAULT_PULSAR_USE_TLS = false;
    /**
     * By default Pulsar authentication is empty thus disabled.
     */
    public static final boolean DEFAULT_PULSAR_AUTHENTICATION_IS_ENABLED = false;
    /**
     * By default Pulsar doesn't have any authentication.
     */
    public static final Authentication DEFAULT_PULSAR_AUTHENTICATION = null;
    /**
     * Regex for validating topic name.
     */
    public static final String TOPIC_VALIDATOR = "(persistent|non-persistent)://[a-zA-Z0-9\\-]+\\/[a-zA-Z0-9\\-]+\\/[a-zA-Z0-9\\-]+";
    public static final String TOPIC_NAME_VALIDATOR = "[a-zA-Z0-9\\-]+\\/[a-zA-Z0-9\\-]+\\/\\.+";

    private final Properties config;

    /**
     * Constructs a new instance.
     *
     * @param config The config to use
     */
    protected AbstractPulsarConfiguration(Properties config) {
        this.config = config;
    }

    /**
     * @return The Pulsar configuration
     */
    public @Nonnull
    Properties getConfig() {
        if (config != null) {
            return config;
        }
        return new Properties();
    }
}
