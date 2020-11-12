/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.kafka.appender;

import org.apache.logging.log4j.categories.Appenders;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.junit.LoggerContextSource;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/**
 * Verifies that restarting the {@link LoggerContext} doesn't cause
 * {@link KafkaManager} to leak threads.
 *
 * @see <a href="https://issues.apache.org/jira/browse/LOG4J2-2916">LOG4J2-2916</a>
 */
@Category(Appenders.Kafka.class)
@LoggerContextSource("KafkaAppenderTest.xml")
class KafkaManagerProducerThreadLeakTest {

    @Test
    void context_restart_shouldnt_leak_producer_threads(final LoggerContext context) {

        // Determine the initial number of threads.
        final int initialThreadCount = kafkaProducerThreadCount();

        // Perform context restarts.
        final int contextRestartCount = 3;
        for (int i = 0; i < contextRestartCount; i++) {
            context.reconfigure();
        }

        // Verify the final thread count.
        final int lastThreadCount = kafkaProducerThreadCount();
        assertEquals(initialThreadCount, lastThreadCount);

    }

    private static int kafkaProducerThreadCount() {
        final long threadCount = Thread
                .getAllStackTraces()
                .keySet()
                .stream()
                .filter(thread -> thread.getName().startsWith("kafka-producer"))
                .count();
        return Math.toIntExact(threadCount);
    }

}
