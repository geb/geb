/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geb.gradle.saucelabs

import org.gradle.api.internal.file.FileResolver
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.reflect.Instantiator
import org.gradle.listener.ListenerManager
import org.gradle.logging.ProgressLoggerFactory
import org.gradle.logging.StyledTextOutputFactory
import org.gradle.messaging.actor.ActorFactory
import org.gradle.process.internal.WorkerProcessBuilder

import javax.inject.Inject

class SauceTest extends Test {

	@Inject
	SauceTest(ListenerManager listenerManager, StyledTextOutputFactory textOutputFactory, FileResolver fileResolver, org.gradle.internal.Factory<WorkerProcessBuilder> processBuilderFactory, ActorFactory actorFactory, Instantiator instantiator, ProgressLoggerFactory progressLoggerFactory) {
		super(listenerManager, textOutputFactory, fileResolver, processBuilderFactory, actorFactory, instantiator, progressLoggerFactory)
	}
}
