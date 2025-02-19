/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.test.integration.usecase;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.sapl.api.interpreter.Val;
import io.sapl.api.pdp.AuthorizationSubscription;
import io.sapl.functions.TemporalFunctionLibrary;
import io.sapl.interpreter.InitializationException;
import io.sapl.test.SaplTestFixture;
import io.sapl.test.integration.SaplIntegrationTestFixture;

class B_StreamingVirtualTimeTest {

	private SaplTestFixture fixture;

	@BeforeEach
	void setUp() throws InitializationException {
		fixture = new SaplIntegrationTestFixture("policiesIT").registerFunctionLibrary(new TemporalFunctionLibrary());
	}

	@Test
	void test() {
		var timestamp0 = Val.of("2021-02-08T16:16:01.000Z");
		var timestamp1 = Val.of("2021-02-08T16:16:02.000Z");
		var timestamp2 = Val.of("2021-02-08T16:16:03.000Z");
		var timestamp3 = Val.of("2021-02-08T16:16:04.000Z");
		var timestamp4 = Val.of("2021-02-08T16:16:05.000Z");
		var timestamp5 = Val.of("2021-02-08T16:16:06.000Z");

		fixture.constructTestCaseWithMocks().withVirtualTime()
				.givenAttribute("time.now", Duration.ofSeconds(1), timestamp0, timestamp1, timestamp2, timestamp3,
						timestamp4, timestamp5)
				.when(AuthorizationSubscription.of("WILLI", "read", "bar")).thenAwait(Duration.ofSeconds(10))
				.expectNextDeny().expectNextDeny().expectNextDeny().expectNextPermit().expectNextPermit()
				.expectNextPermit().expectNoEvent(Duration.ofSeconds(2)).verify();
	}

	@Test
	void test_mockedFunctionAndAttribute_ArrayOfReturnValues() {
		fixture.constructTestCaseWithMocks()
				.givenAttribute("time.now", Val.of("value"), Val.of("doesn't"), Val.of("matter"))
				.givenFunctionOnce("time.secondOf", Val.of(3), Val.of(4), Val.of(5))
				.when(AuthorizationSubscription.of("WILLI", "read", "bar")).expectNextDeny().expectNextPermit()
				.expectNextPermit().verify();
	}

//	@Test
//	void test() {
//		var timestamp0 = Val.of("2021-02-08T16:16:01.000Z");
//		var timestamp1 = Val.of("2021-02-08T16:16:02.000Z");
//		var timestamp2 = Val.of("2021-02-08T16:16:03.000Z");
//		var timestamp3 = Val.of("2021-02-08T16:16:04.000Z");
//		var timestamp4 = Val.of("2021-02-08T16:16:05.000Z");
//		var timestamp5 = Val.of("2021-02-08T16:16:06.000Z");
//
//		fixture.constructTestCaseWithMocks().withVirtualTime()
//				.givenAttribute("time.now", Duration.ofSeconds(5), timestamp0, timestamp1, timestamp2, timestamp3,
//						timestamp4, timestamp5)
//				.when(AuthorizationSubscription.of("WILLI", "read", "bar")).thenAwait(Duration.ofSeconds(5))
//				.expectNextDeny().expectNoEvent(Duration.ofSeconds(20)).expectNextPermit().verify();
//	}
//
//	@Test
//	void test_mockedFunctionAndAttribute_ArrayOfReturnValues() {
//
//		fixture.constructTestCaseWithMocks()
//				.givenAttribute("time.now", Val.of("value"), Val.of("doesn't"), Val.of("matter"))
//				.givenFunctionOnce("time.secondOf", Val.of(3), Val.of(4), Val.of(5))
//				.when(AuthorizationSubscription.of("WILLI", "read", "bar")).expectNextDeny().expectNextPermit()
//				.verify();
//
//	}

}
