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
 */
public final class Epsilon {
  /**
   * Upper bound on the relative error of {@link short} arithmetic.
   */
  private static final short SHORT = (short) 1E-2;

  /**
   * Upper bound on the relative error of {@link float} arithmetic.
   */
  private static final float FLOAT = (float) 1E-4;

  /**
   * Upper bound on the relative error of {@link double} arithmetic.
   */
  private static final double DOUBLE = (double) 1E-8;

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
  public static boolean equal(final short a, final short b) {
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
   * Check if a {@link short} is less than or equal to another {@link short}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is less than
   *          or equal to the second number.
   */
  public static boolean lessOrEqual(final short a, final short b) {
    return Epsilon.less(a, b) || Epsilon.equal(a, b);
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
   * Check if a {@link short} is greater than or equal to another {@link short}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is greater
   *          than or equal to the second number.
   */
  public static boolean greaterOrEqual(final short a, final short b) {
    return Epsilon.greater(a, b) || Epsilon.equal(a, b);
  }

  /**
   * Check if two {@link float floats} are equal.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the two numbers are equal.
   */
  public static boolean equal(final float a, final float b) {
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
   * Check if a {@link float} is less than or equal to another {@link float}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is less than
   *          or equal to the second number.
   */
  public static boolean lessOrEqual(final float a, final float b) {
    return Epsilon.less(a, b) || Epsilon.equal(a, b);
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
   * Check if a {@link float} is greater than or equal to another {@link float}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is greater
   *          than or equal to the second number.
   */
  public static boolean greaterOrEqual(final float a, final float b) {
    return Epsilon.greater(a, b) || Epsilon.equal(a, b);
  }

  /**
   * Check if two {@link double doubles} are equal.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the two numbers are equal.
   */
  public static boolean equal(final double a, final double b) {
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
   * Check if a {@link double} is less than or equal to another {@link double}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is less than
   *          or equal to the second number.
   */
  public static boolean lessOrEqual(final double a, final double b) {
    return Epsilon.less(a, b) || Epsilon.equal(a, b);
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

  /**
   * Check if a {@link double} is greater than or equal to another
   * {@link double}.
   *
   * @param a The first number.
   * @param b The second number.
   * @return  A boolean indicating whether or not the first number is greater
   *          than or equal to the second number.
   */
  public static boolean greaterOrEqual(final double a, final double b) {
    return Epsilon.greater(a, b) || Epsilon.equal(a, b);
  }
}
