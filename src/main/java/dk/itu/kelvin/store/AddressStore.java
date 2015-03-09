/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// General utilities
import java.util.Map;

// Models
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.Address;

/**
 * A class to store all addresses from the OSM file.
 */
public class AddressStore {

  private Map<Address, Node> addresses;

  /**
   *  Constructor taking in a HashMap with Address and Node objects.
   * @param addresses The list of all addresses.
   */
  public AddressStore(Map<Address, Node> addresses) {
    this.addresses = addresses;
  }

  /**
   * Sort the map for faster searches.
   */
  public void sortAddresses() {
    // sort the map.
  }

  /**
   *  A method that searches all addresses for a given address.
   * @param a the address to find.
   * @return the node related to the found address.
   */
  public Node findAddress(Address a) {
    if (a == null) return null;

    Node node = this.addresses.get(a);

    if (node == null) return null;
    System.out.println(node.x() + node.y());
    return node;
  }

}
