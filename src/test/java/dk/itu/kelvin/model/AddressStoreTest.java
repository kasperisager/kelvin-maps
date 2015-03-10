/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.model;

// JUnit annotations
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.*;


public class AddressStoreTest {

  @Test
  public void testEquals() {
    Address a1 = new Address();
    a1.city("Købehavn");
    a1.number("15");
    a1.floor("2");
    a1.door("TH");
    a1.postcode("1667");
    a1.street("Hovedgaden");

    Address a2 = new Address();
    a2.city("Købehavn");
    a2.number("15");
    a2.floor("2");
    a2.door("TH");
    a2.postcode("1667");
    a2.street("Hovedgaden");

    Address a3 = new Address();
    a3.city("Købehavn");
    a3.number("15");
    a3.floor("2");
    a3.door("TH");
    a3.postcode("1000");
    a3.street("Hovedgaden");

    assertTrue(a1.equals(a2));
    assertFalse(a1.equals(a3));
  }

  @Test
  public void testHashCode() {
    Address a1 = new Address();
    a1.city("Købehavn");
    a1.number("15");
    a1.floor("2");
    a1.door("TH");
    a1.postcode("1667");
    a1.street("Hovedgaden");

    Address a2 = new Address();
    a2.city("Købehavn");
    a2.number("15");
    a2.floor("2");
    a2.door("TH");
    a2.postcode("1667");
    a2.street("Hovedgaden");

    Address a3 = new Address();
    a3.city("Købehavn");
    a3.number("15");
    a3.floor("2");
    a3.door("TH");
    a3.postcode("1000");
    a3.street("Hovedgaden");

    assertTrue(a1.hashCode() == a2.hashCode());
    assertFalse(a1.hashCode() == a3.hashCode());
  }



}
