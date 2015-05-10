/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

// JUnit annotations
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// General utilities
// JUnit annotations
// JUnit assertions

/**
 * {@link Epsilon} test suite.
 *
 * <p>
 * Test different epsilon methods which are used in the program.
 *
 * <p>
 *
 */
public class EpsilonTest {

  /**
   * Test if two values are equal.
   */
  @Test
  public void testEqual() {
    assertTrue(Epsilon.equal(3, 3));
    assertTrue(Epsilon.equal(0.05, 0.05));

    assertFalse(Epsilon.equal(11010, 11010.01));
    assertFalse(Epsilon.equal(11010.02, 11010.01));
  }

  /**
   * Test if a value is greater than another .
   */
  @Test
  public void testGreater() {
    assertTrue(Epsilon.greater(3.01, 3));
    assertTrue(Epsilon.greater(11102.99, 11102.9));

    assertFalse(Epsilon.greater(11102.91, 11102.93));
  }

  /**
   * Test if a value is less than another.
   */
  @Test
  public void testLess() {
    assertTrue(Epsilon.less(32766.8, 32766.81));
    assertTrue(Epsilon.less(-32766.81, -32766.80));
    assertTrue(Epsilon.less(-0.01, 0));

    assertFalse(Epsilon.less(0.02, -0.01));
    assertFalse(Epsilon.less(134, 125));
  }



  /**
   * Test if a value is less than or equal to another .
   */
  @Test
  public void testLessOrEqual() {
    assertTrue(Epsilon.lessOrEqual(5.21, 5.21));
    assertTrue(Epsilon.lessOrEqual(4.39, 4.42));

    assertFalse(Epsilon.lessOrEqual(0.02, -0.01));
    assertFalse(Epsilon.lessOrEqual(134.9, 134.7));
  }

  /**
   * Test if a value is greater than or equal to another.
   */
  @Test
  public void testGreaterOrEqual() {
    assertTrue(Epsilon.greaterOrEqual(5.21, 5.21));
    assertTrue(Epsilon.greaterOrEqual(4.42, 4.39));

    assertFalse(Epsilon.greaterOrEqual(-0.011, -0.01));
    assertFalse(Epsilon.greaterOrEqual(125.20, 125.21));
  }






}
