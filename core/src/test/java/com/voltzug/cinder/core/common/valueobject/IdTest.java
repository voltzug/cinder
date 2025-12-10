package com.voltzug.cinder.core.common.valueobject;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IdTest {

  // Example concrete implementation for testing
  static final class TestPrefix implements Id.Prefix {

    private final String value;

    TestPrefix(String value) {
      this.value = value;
    }

    @Override
    public String code() {
      return value;
    }

    @Override
    public String toString() {
      return value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TestPrefix that = (TestPrefix) o;
      return value.equals(that.value);
    }

    @Override
    public int hashCode() {
      return value.hashCode();
    }
  }

  static final class TestId extends Id {

    TestId(String value, TestPrefix prefix) {
      super(value, prefix);
    }
  }

  @Test
  void valueConcatenatesPrefixAndValue() {
    TestPrefix prefix = new TestPrefix("PRE_");
    TestId id = new TestId("12345", prefix);
    assertEquals("PRE_12345", id.toString());
  }

  @Test
  void prefixReturnsPrefixObject() {
    TestPrefix prefix = new TestPrefix("X_");
    TestId id = new TestId("abc", prefix);
    assertSame(prefix, id.prefix());
  }

  @Test
  void valueReturnsString() {
    TestPrefix prefix = new TestPrefix("PFX-");
    TestId id = new TestId("xyz", prefix);
    assertEquals("xyz", id.value());
  }

  @Test
  void differentPrefixesProduceDifferentIds() {
    TestId id1 = new TestId("val", new TestPrefix("A-"));
    TestId id2 = new TestId("val", new TestPrefix("B-"));
    assertNotEquals(id1.toString(), id2.toString());
  }

  @Test
  void differentValuesProduceDifferentIds() {
    TestPrefix prefix = new TestPrefix("P-");
    TestId id1 = new TestId("foo", prefix);
    TestId id2 = new TestId("bar", prefix);
    assertNotEquals(id1.toString(), id2.toString());
  }

  @Test
  void nullPrefixThrowsNPE() {
    assertThrows(IllegalArgumentException.class, () -> new TestId("abc", null));
  }

  @Test
  void nullValueThrowsNPE() {
    assertThrows(IllegalArgumentException.class, () ->
      new TestId(null, new TestPrefix("0"))
    );
  }
}
