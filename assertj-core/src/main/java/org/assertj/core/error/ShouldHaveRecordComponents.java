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
package org.assertj.core.error;

import java.util.Set;

/**
 * Creates an error message indicating that an assertion that verifies that a class has record components failed.
 *
 * @author Louis Morgan
 */
public class ShouldHaveRecordComponents extends BasicErrorMessageFactory {

  /**
   * Creates a new <code>{@link ShouldHaveRecordComponents}</code>
   *
   * @param actual the actual value in the failed assertion.
   * @param expected expected record components for this class
   * @param missing missing record components for this class
   * @return the created {@code ErrorMessageFactory}.
   */
  public static ErrorMessageFactory shouldHaveRecordComponents(Class<?> actual, Set<String> expected, Set<String> missing) {
    return new ShouldHaveRecordComponents(actual, expected, missing);
  }

  private ShouldHaveRecordComponents(Class<?> actual, Set<String> expected, Set<String> missing) {
    super("%nExpecting%n" +
          "  %s%n" +
          "to have the following record components:%n" +
          "  %s%n" +
          "but it doesn't have:%n" +
          "  %s",
          actual, expected, missing);
  }

}
