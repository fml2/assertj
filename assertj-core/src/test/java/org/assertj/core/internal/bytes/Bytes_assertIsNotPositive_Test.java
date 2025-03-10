/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2025 the original author or authors.
 */
package org.assertj.core.internal.bytes;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.error.ShouldBeLessOrEqual.shouldBeLessOrEqual;
import static org.assertj.core.testkit.TestData.someHexInfo;
import static org.assertj.core.testkit.TestData.someInfo;
import static org.assertj.core.util.AssertionsUtil.expectAssertionError;

import org.assertj.core.api.AssertionInfo;
import org.assertj.core.internal.BytesBaseTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for <code>{@link Bytes#assertIsNotPositive(AssertionInfo, Bytes))}</code>.
 * 
 * @author Nicolas François
 */
class Bytes_assertIsNotPositive_Test extends BytesBaseTest {

  private AssertionInfo hexInfo = someHexInfo();

  @Test
  void should_succeed_since_actual_is_not_positive() {
    bytes.assertIsNotPositive(someInfo(), (byte) -6);
  }

  @Test
  void should_succeed_since_actual_is_zero() {
    bytes.assertIsNotPositive(someInfo(), (byte) 0);
  }

  @Test
  void should_fail_since_actual_is_positive() {
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> bytes.assertIsNotPositive(someInfo(), (byte) 6));
    // THEN
    then(assertionError).hasMessage(shouldBeLessOrEqual((byte) 6, (byte) 0).create());
  }

  @Test
  void should_fail_since_actual_is_positive_in_hex_representation() {
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> bytes.assertIsNotPositive(hexInfo, (byte) 0x06));
    // THEN
    then(assertionError).hasMessage(shouldBeLessOrEqual((byte) 0x06, (byte) 0x00).create(hexInfo.description(),
                                                                                         hexInfo.representation()));
  }

  @Test
  void should_fail_since_actual_can_be_positive_according_to_custom_comparison_strategy() {
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> bytesWithAbsValueComparisonStrategy.assertIsNotPositive(someInfo(),
                                                                                                                       (byte) -1));
    // THEN
    then(assertionError).hasMessage(shouldBeLessOrEqual((byte) -1, (byte) 0, absValueComparisonStrategy).create());
  }

  @Test
  void should_fail_since_actual_can_be_positive_according_to_custom_comparison_strategy_in_hex_representation() {
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> bytesWithAbsValueComparisonStrategy.assertIsNotPositive(hexInfo,
                                                                                                                       (byte) 0xFF));
    // THEN
    then(assertionError).hasMessage(shouldBeLessOrEqual((byte) 0xFF, (byte) 0x00,
                                                        absValueComparisonStrategy).create(hexInfo.description(),
                                                                                           hexInfo.representation()));
  }

  @Test
  void should_fail_since_actual_is_positive_according_to_custom_comparison_strategy() {
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> bytesWithAbsValueComparisonStrategy.assertIsNotPositive(someInfo(),
                                                                                                                       (byte) 1));
    // THEN
    then(assertionError).hasMessage(shouldBeLessOrEqual((byte) 1, (byte) 0, absValueComparisonStrategy).create());
  }

  @Test
  void should_fail_since_actual_is_positive_according_to_custom_comparison_strategy_in_hex_representation() {
    // WHEN
    AssertionError assertionError = expectAssertionError(() -> bytesWithAbsValueComparisonStrategy.assertIsNotPositive(hexInfo,
                                                                                                                       (byte) 0x01));
    // THEN
    then(assertionError).hasMessage(shouldBeLessOrEqual((byte) 0x01, (byte) 0x00,
                                                        absValueComparisonStrategy).create(hexInfo.description(),
                                                                                           hexInfo.representation()));
  }

}
