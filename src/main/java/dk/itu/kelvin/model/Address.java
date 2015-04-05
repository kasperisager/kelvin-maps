/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// Regex utilities
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Address class for recognising address patterns and parsing as objects.
 *
 * <p>
 * The class defines a finite state automaton in the form of a set of regular
 * expressions for parsing Danish addresses. The regular expression
 * {@link #REGEX} allows for handling special characters between
 * {@code match groups} as well as additional whitespaces.
 *
 * <p>
 * With {@link #parse(String)} the string parameter representing a full address,
 * will be parsed to an {@link Address} {@code object} using the {@link #REGEX}
 * to create a {@link Pattern} and use said pattern to create a {@link Matcher}
 * with {@link #matchRegex(String, String)}. From the {@link Matcher}
 * {@code object} Strings can be retrieved from the varies
 * {@code matcher groups}.
 *
 * <p>
 * This class has an implementation of {@link #equals(Object)}that compare all
 * {@code non-final fields} returning {@code true} if they match.
 *
 * <p>
 * {@link Address} also has an implementation of {@link #toString()} for giving
 * {@link Address} {@code objects} a {@code String} representation to be used
 * in auto-suggestions.
 */
public final class Address {
  /**
   * Regex for matching a single comma followed by zero or more spaces, or
   * no comma and one or more spaces.
   */
  private static final String COMMA = "(,*\\s*|\\s+)";

  /**
   * Regex for matching a single dot followed by zero or more spaces, or
   * no dot and one or more spaces.
   */
  private static final String DOT = "(\\.*\\s*|\\s+)";

  /**
   * Regex for matching name-like strings consisting of alphabetical characters,
   * dashes, dots, and spaces.
   */
  private static final String NAME = "[\\p{L}\\s\\.\\-]+";

  /**
   * Regex for matching a street name.
   */
  private static final String STREET = "(?<street>" + NAME + ")";

  /**
   * Regex for matching a city name.
   */
  private static final String CITY = "(?<city>" + NAME + ")";

  /**
   * Regex for matching postcodes in the range 1000-9999.
   */
  private static final String POSTCODE = "(?<postcode>[1-9]\\d{3})";

  /**
   * Regex for matching building numbers that may optionally be suffixed by a
   * sequence of letters.
   */
  private static final String NUMBER = "(?<number>\\d+\\p{L}*)";

  /**
   * Regex for matching floor numbers that may optionally be suffixed by a
   * single dot.
   */
  private static final String FLOOR = "(?<floor>"
  +   "\\d+(\\.|,)?|s\\.?t(uen?)?\\.?"
  + ")";

  /**
   * Regex for matching a door number that may optionally be suffixed by a
   * single dot.
   */
  private static final String DOOR_NUMBER = "(?<doorNumber>"
  +   "\\-?[\\p{L}\\d]*\\d[\\p{L}\\d]*\\.?"
  + ")";

  /**
   * Regex for matching a door side that may optionally be suffixed by a single
   * dot.
   */
  private static final String DOOR_SIDE = "(?<doorSide>t\\.?[vh]\\.?)";

  /**
   * Regex for matching door numbers.
   */
  private static final String DOOR = "(?<door>"
  +   "(lejl(ighed|\\.)?|dør)?\\s*"
  +   DOOR_NUMBER + "?\\s*" + DOOR_SIDE + "?"
  + ")";

  /**
   * The combined regex for parsing a Danish address.
   */
  public static final String REGEX = "^"
  +   "(" + STREET + "(\\s*" + COMMA + NUMBER + ")?)?"
  +   "(" + COMMA + "(" + FLOOR + ")?\\s*/?(\\s*" + COMMA + DOOR + ")?)?"
  +   "(" + COMMA + "(" + POSTCODE + ")?(\\s*" + COMMA + CITY + ")?)?"
  + "$";

  /**
   * The x-coordinate of the address.
   */
  private float x;

  /**
   * The y-coordinate of the address.
   */
  private float y;

  /**
   * The street of the address.
   */
  private String street;

  /**
   * The house number of the address.
   */
  private String number;

  /**
   * The apartment floor of the address.
   */
  private String floor;

  /**
   * The door of the apartment floor.
   */
  private String door;

  /**
   * The postcode of the address.
   */
  private String postcode;

  /**
   * The city of the address.
   */
  private String city;

  /**
   * Get the x-coordinate of the address.
   *
   * @return The x-coordinate of the address.
   */
  public float x() {
    return this.x;
  }

  /**
   * Set the x-coordinate of the address.
   *
   * @param x The x-coordinate of the address.
   */
  public void x(final float x) {
    this.x = x;
  }

  /**
   * Get the y-coordinate of the address.
   *
   * @return The y-coordinate of the address.
   */
  public float y() {
    return this.y;
  }

  /**
   * Set the y-coordinate of the address.
   *
   * @param y The y-coordinate of the address.
   */
  public void y(final float y) {
    this.y = y;
  }

  /**
   * Get the street of the address.
   *
   * @return The street of the address.
   */
  public String street() {
    return this.street;
  }

  /**
   * Set the street of the address.
   *
   * @param street  The street of the address.
   * @return        The current {@link Address} instance for chaining.
   */
  public Address street(final String street) {
    this.street = (street != null) ? street.trim().intern() : null;

    return this;
  }

  /**
   * Get the building number of the address.
   *
   * @return The building number of the address.
   */
  public String number() {
    return this.number;
  }

  /**
   * Set the building number of the address.
   *
   * @param number  The building number of the address.
   * @return        The current {@link Address} instance for chaining.
   */
  public Address number(final String number) {
    this.number = (number != null) ? number.trim().intern() : null;

    return this;
  }

  /**
   * Get the apartment floor of the address.
   *
   * @return The apartment floor of the address.
   */
  public String floor() {
    return this.floor;
  }

  /**
   * Set the apartment floor of the address.
   *
   * @param floor The apartment floor of the address.
   * @return      The current {@link Address} instance for chaining.
   */
  public Address floor(final String floor) {
    this.floor = (floor != null) ? floor.trim().intern() : null;

    return this;
  }

  /**
   * Get the door of the apartment floor.
   *
   * @return The door of the apartment floor.
   */
  public String door() {
    return this.door;
  }

  /**
   * Set the door of the apartment floor.
   *
   * @param door  The door of the apartment floor.
   * @return      The current {@link Address} instance for chaining.
   */
  public Address door(final String door) {
    this.door = (door != null) ? door.trim().intern() : null;

    return this;
  }

  /**
   * Get the postcode of the address.
   *
   * @return The postcode of the address.
   */
  public String postcode() {
    return this.postcode;
  }

  /**
   * Set the postcode of the address.
   *
   * @param postcode  The postcode of the address.
   * @return          The current {@link Address} instance for chaining.
   */
  public Address postcode(final String postcode) {
    this.postcode = (postcode != null) ? postcode.trim().intern() : null;

    return this;
  }

  /**
   * Get the city of the address.
   *
   * @return The city of the address.
   */
  public String city() {
    return this.city;
  }

  /**
   * Set the city of the address.
   *
   * @param city  The city of the address.
   * @return      The current {@link Address} instance for chaining.
   */
  public Address city(final String city) {
    this.city = (city != null) ? city.trim().intern() : null;

    return this;
  }

  /**
   * Compile a regex pattern and return a matcher against a specified input.
   *
   * @param regex The regex pattern to match.
   * @param input The input to match the regex against.
   * @return      A {@link Matcher} instance initialized against the input.
   */
  private static Matcher matchRegex(final String regex, final String input) {
    Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    return pattern.matcher(input.trim());
  }

  /**
   * Normalize whitespace of an input string.
   *
   * @param string  The string to normalize.
   * @return        The normalized string.
   */
  private static String normalizeWhitespace(final String string) {
    if (string == null) {
      return null;
    }

    return string.replaceAll("\\s+", " ").trim();
  }

  /**
   * Trim punctuation from a string.
   *
   * @param string  The string to trim.
   * @return        The trimmed string.
   */
  private static String trimPunctuation(final String string) {
    if (string == null) {
      return null;
    }

    return string.replaceAll("\\.|\\-|,", "").trim();
  }

  /**
   * Parse a string representation of an address into an {@link Address} object.
   *
   * @param input The string representation of the address.
   * @return      An {@link Address} object.
   */
  public static Address parse(final String input) {
    if (input == null || input.trim().isEmpty()) {
      return null;
    }

    // Normalize whitespace of the input prior to parsing.
    String normalizedInput = Address.normalizeWhitespace(input);

    Matcher matcher = Address.matchRegex(REGEX, normalizedInput);

    if (!matcher.find()) {
      return null;
    }

    String street = matcher.group("street");
    String number = matcher.group("number");
    String floor = matcher.group("floor");
    String door = matcher.group("door");
    String postcode = matcher.group("postcode");
    String city = matcher.group("city");

    if (postcode == null) {
      if (door != null && door.trim().matches(POSTCODE)) {
        postcode = door;
        door = null;
      }
      else if (floor != null && floor.trim().matches(POSTCODE)) {
        postcode = floor;
        floor = null;
      }
      else if (number != null && number.trim().matches(POSTCODE)) {
        postcode = number;
        number = null;
      }
    }

    if (floor != null) {
      floor = Address.trimPunctuation(floor);
    }

    if (door != null) {
      String doorNumber = Address.trimPunctuation(
        matcher.group("doorNumber")
      );
      String doorSide = Address.trimPunctuation(
        matcher.group("doorSide")
      );

      if (
        doorNumber != null && !doorNumber.isEmpty()
        && doorSide != null && !doorSide.isEmpty()
      ) {
        door = doorNumber + " " + doorSide;
      }
      else if (doorNumber != null && !doorNumber.isEmpty()) {
        door = doorNumber;
      }
      else if (doorSide != null && !doorSide.isEmpty()) {
        door = doorSide;
      }
      else {
        door = null;
      }
    }

    Address address = new Address();

    address
      .street(street)
      .number(number)
      .floor(floor)
      .door(door)
      .postcode(postcode)
      .city(city);

    return address;
  }
}
