/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JUnit annotations
import org.junit.Test;
import org.junit.Ignore;

// JUnit assertions
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Address parser unit tests.
 *
 * Valid addresses consists of:
 *
 * 1. A street name (one or more words separated by spaces)
 * 2. A building number (one or more digits optionally followed by a sequence
 *    of letters)
 * 3. A floor number (one or more digits optionally followed by a dot (.))
 * 4. A door number (one or more digits optionally follow by a dot (.) and
 *    optionally suffixed with th/tv, in which any combination of dots (.) can
 *    be used to separate the letters)
 * 5. A postcode (number from 1000 - 9999)
 * 6. A city (one or more words separated by spaces)
 *
 * All parts of the address are optional.
 *
 * Notice that either one or more spaces or a comma (,) optionally followed by
 * spaces must be used in between street name + building number and the floor
 * number + door number as well as the postcode + city:
 */
public class AddressTest {
  /**
   * The Address parser should parse an address with a street name (can be
   * multiple words) consisting of letters (a-z as well as Danish letters).
   */
  @Test
  public void testStreetParsing() {
    // Test valid street name composed of a single word.
    Address a1 = Address.parse("Foo");
    assertNotNull(a1);
    assertEquals("Foo", a1.street());

    // Test valid street name composed of multiple words.
    Address a2 = Address.parse("Foo Avenue");
    assertNotNull(a2);
    assertEquals("Foo Avenue", a2.street());

    // Test valid use of Danish letters.
    Address a3 = Address.parse("ÆØÅ æøå");
    assertNotNull(a3);
    assertEquals("ÆØÅ æøå", a3.street());

    // Test valid use of German umlauts.
    Address a4 = Address.parse("ÜüÖöÄä");
    assertNotNull(a4);
    assertEquals("ÜüÖöÄä", a4.street());

    // Test valid use of accented letters.
    Address a5 = Address.parse("ÁáÉéÍíÓóÚú");
    assertNotNull(a5);
    assertEquals("ÁáÉéÍíÓóÚú", a5.street());

    // Test valid use of punctuation.
    Address a6 = Address.parse("A.B Foo-Bar");
    assertNotNull(a6);
    assertEquals("A.B Foo-Bar", a6.street());
  }

  /**
   * The Address parser should parse an address with a street name and a number
   * which can be suffixed with letters (a-z only).
   */
  @Test
  public void testNumberParsing() {
    // Test valid simple street name and number.
    Address a1 = Address.parse("Foo 12");
    assertNotNull(a1);
    assertEquals("Foo", a1.street());
    assertEquals("12", a1.number());

    // Test valid street number using letter suffix.
    Address a2 = Address.parse("Foo 14AB");
    assertNotNull(a2);
    assertEquals("Foo", a2.street());
    assertEquals("14AB", a2.number());

    // Test street numer/postcode mixup. This should be parsed as a postcode.
    Address a3 = Address.parse("Foo 1234");
    assertNotNull(a2);
    assertNull(a3.number());
  }

  /**
   * The Address parser should parse an address with a street name, a building
   * number, and an apartment floor number optionally followed by a single dot.
   *
   * A comma may be used to delimit the street name + building number and the
   * apartment floor.
   */
  @Test
  public void testFloorParsing() {
    // Test parsing of floor numbers using different combinations of commas and
    // dots.
    Address[] addresses = new Address[] {
      Address.parse("Foo 14 12"),
      Address.parse("Foo 14, 12"),
      Address.parse("Foo 14 12."),
      Address.parse("Foo 14, 12.")
    };

    for (Address a: addresses) {
      assertNotNull(a);
      assertEquals("Foo", a.street());
      assertEquals("14", a.number());
      assertEquals("12", a.floor());
    }

    // Test postcode/floor number mixup. In this case, "1234" should be parsed
    // as a postcode and not a floor number.
    Address a1 = Address.parse("Foo 14, 1234");
    assertNotNull(a1);
    assertNull(a1.floor());

    // Test postcode/floor number mixup. In this case, "1234" should be parsed
    // as a floor number as it is suffixed with a dot.
    Address a2 = Address.parse("Foo 14, 1234.");
    assertNotNull(a2);
    assertEquals("1234", a2.floor());

    // Test parsing of named ground floor (longhand).
    Address a3 = Address.parse("Foo 24, stuen");
    assertNotNull(a3);
    assertEquals("stuen", a3.floor());

    Address a4 = Address.parse("Foo 24, stue");
    assertNotNull(a4);
    assertEquals("stue", a4.floor());

    // Test parsing of named ground floor (shorthand).
    Address a5 = Address.parse("Foo 24, st");
    assertNotNull(a5);
    assertEquals("st", a5.floor());

    Address a6 = Address.parse("Foo 24, st.");
    assertNotNull(a6);
    assertEquals("st", a6.floor());

    Address a7 = Address.parse("Foo 24, s.t.");
    assertNotNull(a7);
    assertEquals("st", a7.floor());
  }

  /**
   * The Address parser should parse an address with a street name, a building
   * number, an apartment floor number, and a door number optionally followed by
   * a single dot.
   */
  @Test
  public void testDoorParsing() {
    // Test parsing of door numbers using different combinations of punctuation.
    Address[] addresses1 = new Address[] {
      Address.parse("Foo 27 12 34"),
      Address.parse("Foo 27, 12 34"),
      Address.parse("Foo 27 12. 34"),
      Address.parse("Foo 27, 12. 34"),
      Address.parse("Foo 27 12 34."),
      Address.parse("Foo 27, 12 34."),
      Address.parse("Foo 27 12. 34."),
      Address.parse("Foo 27, 12. 34."),
      Address.parse("Foo 27 12, 34"),
      Address.parse("Foo 27, 12, 34"),
      Address.parse("Foo 27 12, -34"),
      Address.parse("Foo 27, 12, -34"),
      Address.parse("Foo 27 12, -34."),
      Address.parse("Foo 27, 12, -34.")
    };

    for (Address a: addresses1) {
      assertNotNull(a);
      assertEquals("Foo", a.street());
      assertEquals("27", a.number());
      assertEquals("12", a.floor());
      assertEquals("34", a.door());
    }

    // Test parsing of extended door numbers using different combinations of
    // dots. The door number extensions should be normalized.
    Address[] addresses2 = new Address[] {
      Address.parse("Foo 24, 12. 34. th"),
      Address.parse("Foo 24, 12. 34. th."),
      Address.parse("Foo 24, 12. 34. t.h"),
      Address.parse("Foo 24, 12. 34. t.h.")
    };

    // Test parsing of extended door numbers with only the door extension (th).
    Address a1 = Address.parse("Foo 24, 12. th");
    assertNotNull(a1);
    assertEquals("th", a1.door());

    for (Address a: addresses2) {
      assertNotNull(a);
      assertEquals("34 th", a.door());
    }

    // Test parsing of extended door numbers using different combinations of
    // dots. The door number extensions should be normalized.
    Address[] addresses3 = new Address[] {
      Address.parse("Foo 24, 12. 34. tv"),
      Address.parse("Foo 24, 12. 34. tv."),
      Address.parse("Foo 24, 12. 34. t.v"),
      Address.parse("Foo 24, 12. 34. t.v.")
    };

    for (Address a: addresses3) {
      assertNotNull(a);
      assertEquals("34 tv", a.door());
    }

    // Test parsing of extended door numbers with only the door extension (tv).
    Address a2 = Address.parse("Foo 24, 12. tv");
    assertNotNull(a2);
    assertEquals("tv", a2.door());

    // Test parsing of named ground floor in conjunction with door.
    Address a3 = Address.parse("Foo 24, stuen 1. tv");
    assertNotNull(a3);
    assertEquals("stuen", a3.floor());
    assertEquals("1 tv", a3.door());

    // Test parsing of named door prefix.
    Address[] addresses4 = new Address[] {
      Address.parse("Foo 27, 12. lejlighed 1. tv"),
      Address.parse("Foo 27, 12. dør 1. tv"),
      Address.parse("Foo 27, 12. lejl 1. tv"),
      Address.parse("Foo 27, 12. lejl. 1. tv")
    };

    for (Address a: addresses4) {
      assertNotNull(a);
      assertEquals("1 tv", a.door());
    }
  }

  /**
   * The Address parser should parse an address with a street name, a building
   * number, and a postcode (number between 1000 and 9999).
   */
  @Test
  public void testPostcodeParsing() {
    // Test parsing of postcode. If an address contains nothing but a postcode,
    // the postcode should be parsed correctly.
    Address a1 = Address.parse("1234");
    assertNotNull(a1);
    assertEquals("1234", a1.postcode());

    // Test parsing of postcodes using different combinations of commas. When
    // used in conjunction with a street, a comma can be used to delimit the
    // two.
    Address[] addresses = new Address[] {
      Address.parse("Foo 14 1234"),
      Address.parse("Foo 14, 1234")
    };

    for (Address a: addresses) {
      assertNotNull(a);
      assertEquals("Foo", a.street());
      assertEquals("14", a.number());
      assertEquals("1234", a.postcode());
    }

    // Test lower postcode boundary - 1. This should not parse as a postcode.
    Address a2 = Address.parse("Foo 14 999");
    assertNotNull(a2);
    assertNull(a2.postcode());

    // Test lower postcode boundary - 1 prefixed with a zero. This should not
    // parse as a postcode.
    Address a3 = Address.parse("Foo 14 0999");
    assertNotNull(a3);
    assertNull(a3.postcode());

    // Test lower postcode boundary.
    Address a4 = Address.parse("Foo 14 1000");
    assertNotNull(a4);
    assertEquals("1000", a4.postcode());

    // Test upper postcode boundary.
    Address a5 = Address.parse("Foo 14 9999");
    assertNotNull(a5);
    assertEquals("9999", a5.postcode());

    // Test upper postcode boundary + 1. This should not parse as a postcode.
    Address a6 = Address.parse("Foo 14 10000");
    assertNotNull(a6);
    assertNull(a6.postcode());
  }

  /**
   * The Address parser should parse an address with a street name, a building
   * number, and a city name.
   */
  @Test
  public void testCityParsing() {
    // Test standalone postcode and city parsing with a single word.
    Address a1 = Address.parse("1234 Foo");
    assertNotNull(a1);
    assertEquals("1234", a1.postcode());
    assertEquals("Foo", a1.city());

    // Test standalone postcode and city parsing with multiple words.
    Address a2 = Address.parse("1234 Foo Bar");
    assertNotNull(a2);
    assertEquals("1234", a2.postcode());
    assertEquals("Foo Bar", a2.city());

    // Test parsing of city in conjunction with a street. A comma is used to
    // delimit the street and city in this simple case.
    Address a3 = Address.parse("Foo Road, Foo City");
    assertNotNull(a3);
    assertEquals("Foo Road", a3.street());
    assertEquals("Foo City", a3.city());

    // Test parsing of city in conjunction with a street and building number.
    Address a4 = Address.parse("Foo Road 14 Foo City");
    assertNotNull(a4);
    assertEquals("Foo Road", a4.street());
    assertEquals("14", a4.number());
    assertEquals("Foo City", a4.city());

    // Test parsing of postcode and city in conjunction with a street. A comma
    // can optionally be used to separate the street from the postcode and
    // city.
    Address a5 = Address.parse("Foo 1234 Bar");
    assertNotNull(a5);
    assertEquals("Foo", a5.street());
    assertEquals("1234", a5.postcode());
    assertEquals("Bar", a5.city());

    Address a6 = Address.parse("Foo, 1234 Bar");
    assertNotNull(a6);
    assertEquals("Foo", a6.street());
    assertEquals("1234", a6.postcode());
    assertEquals("Bar", a6.city());
  }

  /**
   * The Address parser should be lenient in parsing addresses with excess
   * whitespace between sections.
   *
   * All whitespace should be normalized; 2 or more instances of whitespace
   * characters should be collapsed to a single space ( ).
   */
  @Test
  public void testWhitespaceLeniency() {
    // Test different combinations of whitespace characters.
    Address[] addresses = new Address[] {
      Address.parse("Foo Road 14, 25. 3. tv, 1300 Bar"),
      Address.parse("Foo Road14,25.3.tv,1300Bar"),
      Address.parse("\tFoo\tRoad\t14,\t25.\t3.\ttv,\t1300\tBar\t"),
      Address.parse("  Foo   Road  14,    25.  3.     tv,   1300   Bar   "),
      Address.parse("Foo \t Road \t 14, \t 25. \t 3. \t tv, \t 1300 \t Bar")
    };

    for (Address a: addresses) {
      assertNotNull(a);
      assertEquals("Foo Road", a.street());
      assertEquals("14", a.number());
      assertEquals("25", a.floor());
      assertEquals("3 tv", a.door());
      assertEquals("1300", a.postcode());
      assertEquals("Bar", a.city());
    }
  }

  /**
   * The Address parser should be lenient when parsing an address with excess
   * use of punctuation between sections.
   */
  @Test
  public void testPunctuationLeniency() {
    // TODO
  }
}
