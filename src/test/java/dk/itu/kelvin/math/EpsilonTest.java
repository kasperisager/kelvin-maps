/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * {@link Epsilon} test suite.
 *
 * <p>
 * Test different epsilon methods which are used in the program.
 * <p>
 *
 */
public final class EpsilonTest {

  /**
   * Test if two values are equal.
   */
  @Test
  public void testEqual() {
    // test float && double
    assertTrue(Epsilon.equal(113.2, 113.2));
    assertTrue(Epsilon.equal(0.05, 0.05));

    assertFalse(Epsilon.equal(11010, 11010.01));
    assertFalse(Epsilon.equal(11010.02, 11010.01));

    // test short
    Short s1 =  2;
    Short s2 = 1;
    Short s3 = 2;
    assertTrue(Epsilon.equal(s1, s3));
    assertFalse(Epsilon.equal(s2, s1));


    // test Math.abs(a - b) < E^-8 (double)
    assertTrue(Epsilon.equal(1.0, 1.00000001));
    assertFalse(Epsilon.equal(1.0, 1.0000001));

    // test Math.abs(a - b) < E^-4 (float) - virker kun på E^-5
    //assertTrue(Epsilon.equal(1.0, 1.0001));
    //assertFalse(Epsilon.equal(1.0f, 1.001f));
  }

  /**
   * Test if a value is greater than another .
   */
  @Test
  public void testGreater() {
    // test float && double
    assertTrue(Epsilon.greater(3.03, 3.02));
    assertTrue(Epsilon.greater(11102.99f, 11102.9f));

    assertFalse(Epsilon.greater(11102.91f, 11102.93f));
    assertFalse(Epsilon.greater(4.03, 4.04));

    // test short
    Short s1 =  2;
    Short s2 = 1;
    assertTrue(Epsilon.greater(s1, s2));
    assertFalse(Epsilon.greater(s2, s1));
  }

  /**
   * Test if a value is less than another.
   */
  @Test
  public void testLess() {
    // test float && double
    assertTrue(Epsilon.less(0.80, 0.81));
    assertFalse(Epsilon.less(0.10, 0.09));

    // test short
    Short s1 =  1;
    Short s2 = 21;
    assertTrue(Epsilon.less(s1, s2));
    assertFalse(Epsilon.less(s2, s1));
  }

  /**
   * Test if a value is less than or equal to another .
   */
  @Test
  public void testLessOrEqual() {
    // test float && double
    assertTrue(Epsilon.lessOrEqual(-32766.81, -32766.80));
    assertTrue(Epsilon.lessOrEqual(-0.01, 0));
    assertTrue(Epsilon.lessOrEqual(-32766.8102, -32766.8102));
    assertTrue(Epsilon.lessOrEqual(0.80f, 0.81f));
    assertFalse(Epsilon.lessOrEqual(0.10f, 0.09f));

    assertFalse(Epsilon.lessOrEqual(0.020, -0.019));
    assertFalse(Epsilon.lessOrEqual(134.9, 134.7));

    // test short
    Short s1 =  2;
    Short s2 = 1;
    Short s3 = 2;
    assertTrue(Epsilon.lessOrEqual(s1, s3));
    assertTrue(Epsilon.lessOrEqual(s2, s3));
    assertFalse(Epsilon.lessOrEqual(s1, s2));
  }

  /**
   * Test if a value is greater than or equal to another.
   */
  @Test
  public void testGreaterOrEqual() {
    // test float && double
    assertTrue(Epsilon.greaterOrEqual(5.21, 5.21));
    assertTrue(Epsilon.greaterOrEqual(65.21f, 55.21f));
    assertTrue(Epsilon.greaterOrEqual(54.42f, 34.39f));


    assertFalse(Epsilon.greaterOrEqual(-0.011, -0.01));
    assertFalse(Epsilon.greaterOrEqual(125.20, 125.21));
    assertFalse(Epsilon.greaterOrEqual(44.42f, 54.42f));

    // test short
    Short s1 =  2;
    Short s2 = 1;
    Short s3 = 2;
    assertTrue(Epsilon.greaterOrEqual(s1, s3));
    assertTrue(Epsilon.greaterOrEqual(s3, s2));
    assertFalse(Epsilon.greaterOrEqual(s2, s1));

  }
}
