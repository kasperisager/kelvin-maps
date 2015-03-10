/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// General utilities
import java.util.HashMap;

// Models
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.Address;

/**
 * A class to store all addresses from the OSM file.
 */
public class AddressStore extends HashMap<Address, Node>{

  /**
   * Sort the map for faster searches.
   */
  public void sort() {
    // sort the map.
  }

  /**
   *  A method that searches all addresses for a given address.
   * @param a the address to find.
   * @return the node related to the found address.
   */
  public Node find(Address a) {
    if (a == null) {
      return null;
    }

    Node node = this.get(a);
    if (node == null) {
      return null;
    }

    System.out.println(node.x() + node.y());
    return node;
  }

}
