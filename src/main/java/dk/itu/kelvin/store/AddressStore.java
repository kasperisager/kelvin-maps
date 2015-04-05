/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// Utilities
import dk.itu.kelvin.util.ArrayList;
import dk.itu.kelvin.util.List;
import dk.itu.kelvin.util.Map;
import dk.itu.kelvin.util.PrefixTree;
import dk.itu.kelvin.util.TernarySearchTree;

// Models
import dk.itu.kelvin.model.Address;

/**
 * HashTable store for all addresses from the OSM file.
 *
 * <p>
 * Addresses are stored with an {@link Address} as {@code key} and {@link Node}
 * as {@code value}.
 *
 * <p>
 * The {@link #find(Address)} operation takes constant time complexity.
 */
public final class AddressStore extends Store<Address, String> {
  /**
   * The entries contained within the address store.
   */
  private PrefixTree<List<Address>> entries;

  /**
   * Add an address to the store.
   *
   * @param address The address to add to the store.
   */
  public void add(final Address address) {
    if (address == null) {
      return;
    }

    if (this.entries == null) {
      this.entries = new TernarySearchTree<>();
    }

    String key = this.key(address);

    List<Address> addresses = this.entries.get(key);

    if (addresses == null) {
      addresses = new ArrayList<>();

      this.entries.put(key, addresses);
    }

    addresses.add(address);
  }

  /**
   * Remove an address from the store.
   *
   * @param address The address to remove.
   */
  public void remove(final Address address) {
    if (address == null) {
      return;
    }

    throw new UnsupportedOperationException();
  }

  /**
   * Search the store for addresses matching the specified prefix.
   *
   * @param prefix  The prefix to search for.
   * @return        A list of addresses matching the specified prefix.
   */
  public List<Address> search(final String prefix) {
    List<Address> results = new ArrayList<>();

    if (prefix == null) {
      return results;
    }

    Address address = Address.parse(prefix);

    if (address == null) {
      return results;
    }

    Map<String, List<Address>> entries = this.entries.search(
      this.key(address)
    );

    if (entries != null && !entries.isEmpty()) {
      for (List<Address> addresses: entries.values()) {
        for (Address found: addresses) {
          if (
            this.matches(address.number(), found.number())
            && this.matches(address.postcode(), found.postcode())
            && this.matches(address.city(), found.city())
          ) {
            results.add(found);
          }
        }
      }
    }

    return results;
  }

  /**
   * Convert an address to a key for use in the prefix tree of the store.
   *
   * @param address The address to convert to a key.
   * @return        The converted key.
   */
  private String key(final Address address) {
    StringBuilder key = new StringBuilder();

    if (address.street() != null) {
      key.append(address.street());
    }

    if (address.postcode() != null) {
      key.append(address.postcode());
    }

    return key.toString();
  }

  /**
   * Check if two strings are considered "matching".
   *
   * @param a The first string.
   * @param b The second string.
   * @return  A boolean indicating whether or not the two strings are considered
   *          matching.
   */
  private boolean matches(final String a, final String b) {
    if (a == null || b == null) {
      return true;
    }

    String al = a.trim().toLowerCase();
    String bl = b.trim().toLowerCase();

    if (al.isEmpty() || b.isEmpty()) {
      return true;
    }

    return a.startsWith(b) || b.startsWith(a);
  }
}
