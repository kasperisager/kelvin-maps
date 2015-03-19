/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.math;

/**
 * Epsilon class.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Machine_epsilon">
 *      http://en.wikipedia.org/wiki/Machine_epsilon</a>
 *
 * @see <a href="http://stackoverflow.com/q/356807">
 *      http://stackoverflow.com/q/356807</a>
 *
 * @version 1.0.0
 */
public final class Epsilon {
  /**
   * Upper bound on the relative error of {@link short} arithmetic.
   */
  private static final short SHORT = (short) Math.pow(2, -11);

  /**
   * Upper bound on the relative error of {@link float} arithmetic.
   */
  private static final float FLOAT = (float) Math.pow(2, -24);

  /**
   * Upper bound on the relative error of {@link double} arithmetic.
   */
  private static final double DOUBLE = Math.pow(2, -53);

  /**
   * Don't allow instantiation of the class.
   *
   * <p>
   * Since the class only contains static fields and methods, we never want to
   * instantiate the class. We therefore define a private constructor so that
   * noone can create instances of the class other than the class itself.
   *
   * <p>
   * NB: This does not make the class a singleton. In fact, there never exists
   * an instance of the class since not even the class instantiates itself.
   */
  private Epsilon() {
    super();
  }

  /**
   * Check if two {@link short shorts} are equal.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the two numbers are equal.
   */
  public static boolean equals(final short a, final short b) {
    return a == b || Math.abs(a - b) < SHORT;
  }

  /**
   * Check if a {@link short} is less than another {@link short}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is less than
   *          the second number.
   */
  public static boolean less(final short a, final short b) {
    return b - a > SHORT;
  }

  /**
   * Check if a {@link short} is greater than another {@link short}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is greater
   *          than the second number.
   */
  public static boolean greater(final short a, final short b) {
    return a - b > SHORT;
  }

  /**
   * Check if two {@link float floats} are equal.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the two numbers are equal.
   */
  public static boolean equals(final float a, final float b) {
    return a == b || Math.abs(a - b) < FLOAT;
  }

  /**
   * Check if a {@link float} is less than another {@link float}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is less than
   *          the second number.
   */
  public static boolean less(final float a, final float b) {
    return b - a > FLOAT;
  }

  /**
   * Check if a {@link float} is greater than another {@link float}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is greater
   *          than the second number.
   */
  public static boolean greater(final float a, final float b) {
    return a - b > FLOAT;
  }

  /**
   * Check if two {@link double doubles} are equal.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the two numbers are equal.
   */
  public static boolean equals(final double a, final double b) {
    return a == b || Math.abs(a - b) < DOUBLE;
  }

  /**
   * Check if a {@link double} is less than another {@link double}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is less than
   *          the second number.
   */
  public static boolean less(final double a, final double b) {
    return b - a > DOUBLE;
  }

  /**
   * Check if a {@link double} is greater than another {@link double}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is greater
   *          than the second number.
   */
  public static boolean greater(final double a, final double b) {
    return a - b > DOUBLE;
  }
}