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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.testkit.TestData.someHexInfo;
import static org.assertj.core.testkit.TestData.someInfo;

import org.assertj.core.internal.BytesBaseTest;
import org.junit.jupiter.api.Test;

/**
 * Tests for <code>{@link Bytes#assertIsNotNegative(AssertionInfo, Bytes))}</code>.
 *
 * @author Nicolas François
 */
class Bytes_assertIsNotNegative_Test extends BytesBaseTest {

  @Test
  void should_succeed_since_actual_is_not_negative() {
    bytes.assertIsNotNegative(someInfo(), (byte) 6);
  }

  @Test
  void should_succeed_since_actual_is_zero() {
    bytes.assertIsNotNegative(someInfo(), (byte) 0);
  }

  @Test
  void should_fail_since_actual_is_negative() {
    assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytes.assertIsNotNegative(someInfo(), (byte) -6))
                                                   .withMessage("%nExpecting actual:%n  -6%nto be greater than or equal to:%n  0%n".formatted());
  }

  @Test
  void should_fail_since_actual_is_negative_in_hex_representation() {
    assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> bytes.assertIsNotNegative(someHexInfo(), (byte) 0xFA))
                                                   .withMessage("%nExpecting actual:%n  0xFA%nto be greater than or equal to:%n  0x00%n".formatted());
  }

  @Test
  void should_succeed_since_actual_is_not_negative_according_to_custom_comparison_strategy() {
    bytesWithAbsValueComparisonStrategy.assertIsNotNegative(someInfo(), (byte) -1);
  }

  @Test
  void should_succeed_since_actual_positive_is_not_negative_according_to_custom_comparison_strategy() {
    bytesWithAbsValueComparisonStrategy.assertIsNotNegative(someInfo(), (byte) 1);
  }

}
