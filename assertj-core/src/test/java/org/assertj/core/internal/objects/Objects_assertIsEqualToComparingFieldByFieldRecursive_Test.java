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
package org.assertj.core.internal.objects;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.configuration.ConfigurationProvider.CONFIGURATION_PROVIDER;
import static org.assertj.core.error.ShouldBeEqualByComparingFieldByFieldRecursively.shouldBeEqualByComparingFieldByFieldRecursive;
import static org.assertj.core.internal.TypeComparators.defaultTypeComparators;
import static org.assertj.core.internal.objects.SymmetricDateComparator.SYMMETRIC_DATE_COMPARATOR;
import static org.assertj.core.testkit.AlwaysEqualComparator.ALWAYS_EQUALS;
import static org.assertj.core.testkit.AlwaysEqualComparator.ALWAYS_EQUALS_TIMESTAMP;
import static org.assertj.core.testkit.NeverEqualComparator.NEVER_EQUALS;
import static org.assertj.core.testkit.TestData.someInfo;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.internal.AtPrecisionComparator;
import org.assertj.core.internal.DeepDifference.Difference;
import org.assertj.core.internal.ObjectsBaseTest;
import org.assertj.core.internal.TypeComparators;
import org.assertj.core.testkit.Patient;
import org.junit.jupiter.api.Test;

public class Objects_assertIsEqualToComparingFieldByFieldRecursive_Test extends ObjectsBaseTest {

  @Test
  void should_be_able_to_compare_objects_recursively() {
    Person actual = new Person();
    actual.name = "John";
    actual.home.address.number = 1;

    Person other = new Person();
    other.name = "John";
    other.home.address.number = 1;

    objects.assertIsEqualToComparingFieldByFieldRecursively(someInfo(), actual, other, noFieldComparators(),
                                                            defaultTypeComparators());
  }

  @Test
  void should_be_able_to_compare_objects_of_different_types_recursively() {
    Person actual = new Person();
    actual.name = "John";
    actual.home.address.number = 1;

    Human other = new Human();
    other.name = "John";
    other.home.address.number = 1;

    objects.assertIsEqualToComparingFieldByFieldRecursively(someInfo(), actual, other, noFieldComparators(),
                                                            defaultTypeComparators());
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_be_able_to_compare_objects_recursively_using_some_precision_for_numerical_types() {
    Giant goliath = new Giant();
    goliath.name = "Goliath";
    goliath.height = 3.0;

    Giant goliathTwin = new Giant();
    goliathTwin.name = "Goliath";
    goliathTwin.height = 3.1;

    assertThat(goliath).usingComparatorForType(new AtPrecisionComparator<>(0.2), Double.class)
                       .isEqualToComparingFieldByFieldRecursively(goliathTwin);
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_be_able_to_compare_objects_recursively_using_given_comparator_for_specified_field() {
    Giant goliath = new Giant();
    goliath.name = "Goliath";
    goliath.height = 3.0;

    Giant goliathTwin = new Giant();
    goliathTwin.name = "Goliath";
    goliathTwin.height = 3.1;

    assertThat(goliath).usingComparatorForFields(new AtPrecisionComparator<>(0.2), "height")
                       .isEqualToComparingFieldByFieldRecursively(goliathTwin);
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_be_able_to_compare_objects_recursively_using_given_comparator_for_specified_nested_field() {
    Giant goliath = new Giant();
    goliath.name = "Goliath";
    goliath.height = 3.0;
    goliath.home.address.number = 1;

    Giant goliathTwin = new Giant();
    goliathTwin.name = "Goliath";
    goliathTwin.height = 3.1;
    goliathTwin.home.address.number = 5;

    assertThat(goliath).usingComparatorForFields(new AtPrecisionComparator<>(0.2), "height")
                       .usingComparatorForFields(new AtPrecisionComparator<>(10), "home.address.number")
                       .isEqualToComparingFieldByFieldRecursively(goliathTwin);
  }

  @Test
  void should_be_able_to_compare_objects_with_cycles_recursively() {
    FriendlyPerson actual = new FriendlyPerson();
    actual.name = "John";
    actual.home.address.number = 1;

    FriendlyPerson other = new FriendlyPerson();
    other.name = "John";
    other.home.address.number = 1;

    // neighbour
    other.neighbour = actual;
    actual.neighbour = other;

    // friends
    FriendlyPerson sherlock = new FriendlyPerson();
    sherlock.name = "Sherlock";
    sherlock.home.address.number = 221;
    actual.friends.add(sherlock);
    actual.friends.add(other);
    other.friends.add(sherlock);
    other.friends.add(actual);

    objects.assertIsEqualToComparingFieldByFieldRecursively(someInfo(), actual, other, noFieldComparators(),
                                                            defaultTypeComparators());
  }

  @Test
  void should_fail_when_fields_differ() {
    AssertionInfo info = someInfo();

    Person actual = new Person();
    actual.name = "John";

    Person other = new Person();
    other.name = "Jack";

    Throwable error = catchThrowable(() -> objects.assertIsEqualToComparingFieldByFieldRecursively(info, actual, other,
                                                                                                   noFieldComparators(),
                                                                                                   defaultTypeComparators()));

    assertThat(error).isInstanceOf(AssertionError.class);
    verify(failures).failure(info, shouldBeEqualByComparingFieldByFieldRecursive(actual, other,
                                                                                 asList(new Difference(asList("name"),
                                                                                                       "John",
                                                                                                       "Jack")),
                                                                                 CONFIGURATION_PROVIDER.representation()));
  }

  @Test
  void should_fail_when_fields_of_child_objects_differ() {
    AssertionInfo info = someInfo();

    Person actual = new Person();
    actual.name = "John";
    actual.home.address.number = 1;

    Person other = new Person();
    other.name = "John";
    other.home.address.number = 2;

    Throwable error = catchThrowable(() -> objects.assertIsEqualToComparingFieldByFieldRecursively(info, actual, other,
                                                                                                   noFieldComparators(),
                                                                                                   defaultTypeComparators()));

    assertThat(error).isInstanceOf(AssertionError.class);
    verify(failures).failure(info,
                             shouldBeEqualByComparingFieldByFieldRecursive(actual, other,
                                                                           asList(new Difference(asList("home.address.number"),
                                                                                                 1,
                                                                                                 2)),
                                                                           CONFIGURATION_PROVIDER.representation()));
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_have_error_message_with_differences_and_path_to_differences() {
    Person actual = new Person();
    actual.name = "Jack";
    actual.home.address.number = 1;

    Person other = new Person();
    other.name = "John";
    other.home.address.number = 2;

    assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).isEqualToComparingFieldByFieldRecursively(other))
                                                   .withMessage(format("%nExpecting actual:%n  %s%nto be equal to:%n  %s%n"
                                                                       +
                                                                       "when recursively comparing field by field, but found the following difference(s):%n%n"
                                                                       +
                                                                       "Path to difference: <home.address.number>%n" +
                                                                       "- actual  : 1%n" +
                                                                       "- expected: 2%n%n" +
                                                                       "Path to difference: <name>%n" +
                                                                       "- actual  : \"Jack\"%n" +
                                                                       "- expected: \"John\"", actual, other));
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_have_error_message_with_path_to_difference_when_difference_is_in_collection() {
    FriendlyPerson actual = new FriendlyPerson();
    FriendlyPerson friendOfActual = new FriendlyPerson();
    friendOfActual.home.address.number = 99;
    actual.friends = Arrays.asList(friendOfActual);

    FriendlyPerson other = new FriendlyPerson();
    FriendlyPerson friendOfOther = new FriendlyPerson();
    friendOfOther.home.address.number = 10;
    other.friends = Arrays.asList(friendOfOther);

    assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).isEqualToComparingFieldByFieldRecursively(other))
                                                   .withMessage(format("%nExpecting actual:%n  %s%nto be equal to:%n  %s%n"
                                                                       +
                                                                       "when recursively comparing field by field, but found the following difference(s):%n%n"
                                                                       +
                                                                       "Path to difference: <friends.home.address.number>%n"
                                                                       +
                                                                       "- actual  : 99%n" +
                                                                       "- expected: 10", actual, other));
  }

  @Test
  void should_not_use_equal_implementation_of_objects_to_compare() {
    AssertionInfo info = someInfo();

    EqualPerson actual = new EqualPerson();
    actual.name = "John";
    actual.home.address.number = 1;

    EqualPerson other = new EqualPerson();
    other.name = "John";
    other.home.address.number = 2;

    Throwable error = catchThrowable(() -> objects.assertIsEqualToComparingFieldByFieldRecursively(info, actual, other,
                                                                                                   noFieldComparators(),
                                                                                                   defaultTypeComparators()));

    assertThat(error).isInstanceOf(AssertionError.class);
    verify(failures).failure(info,
                             shouldBeEqualByComparingFieldByFieldRecursive(actual, other,
                                                                           asList(new Difference(asList("home.address.number"),
                                                                                                 1, 2)),
                                                                           CONFIGURATION_PROVIDER.representation()));
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_fail_when_comparing_unsorted_with_sorted_set() {
    WithCollection<String> actual = new WithCollection<>(new LinkedHashSet<String>());
    actual.collection.add("bar");
    actual.collection.add("foo");
    WithCollection<String> expected = new WithCollection<>(new TreeSet<String>());
    expected.collection.add("bar");
    expected.collection.add("foo");

    Throwable error = catchThrowable(() -> assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected));

    assertThat(error).isInstanceOf(AssertionError.class)
                     .hasMessageContaining("Path to difference: <collection>%n".formatted())
                     .hasMessageContaining("- actual  : [\"bar\", \"foo\"] (LinkedHashSet@".formatted())
                     .hasMessageContaining("- expected: [\"bar\", \"foo\"] (TreeSet@".formatted());
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_fail_when_comparing_sorted_with_unsorted_set() {
    WithCollection<String> actual = new WithCollection<>(new TreeSet<String>());
    actual.collection.add("bar");
    actual.collection.add("foo");
    WithCollection<String> expected = new WithCollection<>(new LinkedHashSet<String>());
    expected.collection.add("bar");
    expected.collection.add("foo");

    Throwable error = catchThrowable(() -> assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected));

    assertThat(error).isInstanceOf(AssertionError.class)
                     .hasMessageContaining("Path to difference: <collection>%n".formatted())
                     .hasMessageContaining("- actual  : [\"bar\", \"foo\"] (TreeSet@".formatted())
                     .hasMessageContaining("- expected: [\"bar\", \"foo\"] (LinkedHashSet@".formatted());
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_fail_when_comparing_unsorted_with_sorted_map() {
    WithMap<Long, Boolean> actual = new WithMap<>(new LinkedHashMap<>());
    actual.map.put(1L, true);
    actual.map.put(2L, false);
    WithMap<Long, Boolean> expected = new WithMap<>(new TreeMap<>());
    expected.map.put(2L, false);
    expected.map.put(1L, true);

    Throwable error = catchThrowable(() -> assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected));

    assertThat(error).isInstanceOf(AssertionError.class)
                     .hasMessageContaining("Path to difference: <map>%n".formatted())
                     .hasMessageContaining("- actual  : {1L=true, 2L=false} (LinkedHashMap@".formatted())
                     .hasMessageContaining("- expected: {1L=true, 2L=false} (TreeMap@".formatted());
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_fail_when_comparing_sorted_with_unsorted_map() {
    WithMap<Long, Boolean> actual = new WithMap<>(new TreeMap<Long, Boolean>());
    actual.map.put(1L, true);
    actual.map.put(2L, false);
    WithMap<Long, Boolean> expected = new WithMap<>(new LinkedHashMap<Long, Boolean>());
    expected.map.put(2L, false);
    expected.map.put(1L, true);

    Throwable error = catchThrowable(() -> assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected));

    assertThat(error).isInstanceOf(AssertionError.class)
                     .hasMessageContaining("Path to difference: <map>%n".formatted())
                     .hasMessageContaining("- actual  : {1L=true, 2L=false} (TreeMap@".formatted())
                     .hasMessageContaining("- expected: {1L=true, 2L=false} (LinkedHashMap@".formatted());
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_handle_null_field_with_field_comparator() {
    // GIVEN
    Patient adam = new Patient(null);
    Patient eve = new Patient(new Timestamp(3L));
    // THEN
    assertThat(adam).usingComparatorForFields(ALWAYS_EQUALS, "dateOfBirth", "health")
                    .isEqualToComparingFieldByFieldRecursively(eve);
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_handle_null_field_with_type_comparator() {
    // GIVEN
    Patient adam = new Patient(null);
    Patient eve = new Patient(new Timestamp(3L));
    // THEN
    assertThat(adam).usingComparatorForType(ALWAYS_EQUALS_TIMESTAMP, Timestamp.class)
                    .isEqualToComparingFieldByFieldRecursively(eve);
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_not_bother_with_comparators_when_fields_are_the_same() {
    // GIVEN
    Timestamp dateOfBirth = new Timestamp(3L);
    Patient adam = new Patient(dateOfBirth);
    Patient eve = new Patient(dateOfBirth);
    // THEN
    assertThat(adam).usingComparatorForFields(NEVER_EQUALS, "dateOfBirth")
                    .isEqualToComparingFieldByFieldRecursively(eve);
  }

  @Test
  void should_treat_date_as_equal_to_timestamp() {
    Person actual = new Person();
    actual.name = "Fred";
    actual.dateOfBirth = new Date(1000L);

    Person other = new Person();
    other.name = "Fred";
    other.dateOfBirth = new Timestamp(1000L);

    objects.assertIsEqualToComparingFieldByFieldRecursively(someInfo(), actual, other, noFieldComparators(),
                                                            defaultTypeComparators());
  }

  @Test
  void should_treat_timestamp_as_equal_to_date_when_registering_a_Date_symmetric_comparator() {
    Person actual = new Person();
    actual.name = "Fred";
    actual.dateOfBirth = new Timestamp(1000L);

    Person other = new Person();
    other.name = "Fred";
    other.dateOfBirth = new Date(1000L);

    TypeComparators typeComparators = new TypeComparators();
    typeComparators.registerComparator(Timestamp.class, SYMMETRIC_DATE_COMPARATOR);

    objects.assertIsEqualToComparingFieldByFieldRecursively(someInfo(), actual, other, noFieldComparators(),
                                                            typeComparators);
    objects.assertIsEqualToComparingFieldByFieldRecursively(someInfo(), other, actual, noFieldComparators(),
                                                            typeComparators);
  }

  @Test
  void should_treat_timestamp_as_equal_to_date_when_registering_a_Date_symmetric_comparator_for_field() {
    Person actual = new Person();
    actual.name = "Fred";
    actual.dateOfBirth = new Timestamp(1000L);

    Person other = new Person();
    other.name = "Fred";
    other.dateOfBirth = new Date(1000L);

    Map<String, Comparator<?>> fieldComparators = new HashMap<>();
    fieldComparators.put("dateOfBirth", SYMMETRIC_DATE_COMPARATOR);
    objects.assertIsEqualToComparingFieldByFieldRecursively(someInfo(), actual, other, fieldComparators,
                                                            defaultTypeComparators());
  }

  @Test
  void should_be_able_to_compare_objects_with_percentages() {
    Person actual = new Person();
    actual.name = "foo";

    Person other = new Person();
    other.name = "%foo";

    Throwable error = catchThrowable(() -> objects.assertIsEqualToComparingFieldByFieldRecursively(someInfo(), actual, other,
                                                                                                   noFieldComparators(),
                                                                                                   defaultTypeComparators()));

    assertThat(error).isInstanceOf(AssertionError.class)
                     .hasMessageContaining("Path to difference: <name>")
                     .hasMessageContaining("- expected: \"%foo\"")
                     .hasMessageContaining("- actual  : \"foo\"");
  }

  @Test
  @SuppressWarnings("deprecation") // test for deprecated method
  void should_report_missing_property() {
    // GIVEN
    Human joe = new Human();
    joe.name = "joe";
    Giant goliath = new Giant();
    goliath.name = "joe";
    goliath.height = 3.0;
    // WHEN
    Throwable error = catchThrowable(() -> assertThat(goliath).isEqualToComparingFieldByFieldRecursively(joe));
    // THEN
    assertThat(error).hasMessageContaining("Human does not declare all Giant fields")
                     .hasMessageContaining("[height]");
  }

  public static class WithMap<K, V> {
    public Map<K, V> map;

    public WithMap(Map<K, V> map) {
      this.map = map;
    }

    @Override
    public String toString() {
      return "WithMap [map=%s]".formatted(map);
    }

  }

  public static class WithCollection<E> {
    public Collection<E> collection;

    public WithCollection(Collection<E> collection) {
      this.collection = collection;
    }

    @Override
    public String toString() {
      return "WithCollection [collection=%s]".formatted(collection);
    }

  }

  public static class Person {
    public Date dateOfBirth;
    public String name;
    public Home home = new Home();
    public Person neighbour;

    @Override
    public String toString() {
      return "Person [name=" + name + ", home=" + home + "]";
    }
  }

  public static class Home {
    public Address address = new Address();

    @Override
    public String toString() {
      return "Home [address=" + address + "]";
    }
  }

  public static class Address {
    public int number = 1;

    @Override
    public String toString() {
      return "Address [number=" + number + "]";
    }
  }

  public static class Human extends Person {
  }

  public static class Giant extends Person {
    public double height = 3.0;

    @Override
    public String toString() {
      return "Giant [name=" + name + ", home=" + home + ", " + "height " + height + "]";
    }
  }

  public static class EqualPerson extends Person {

    @Override
    public boolean equals(Object o) {
      return true;
    }
  }

  public static class FriendlyPerson extends Person {
    public List<FriendlyPerson> friends = new ArrayList<>();
  }
}
