/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// General utilities
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

// Fast utils
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

// Utilities
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
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 3085;

  /**
   * The addresses contained within the store.
   */
  private List<Address> addresses = new ArrayList<>();

  /**
   * Search index of the addresses.
   */
  private transient PrefixTree<List<Address>> addressIndex;

  /**
   * Keep track of the dirty status of the address index.
   *
   * <p>
   * Whenever the address index becomes dirty a re-index is required.
   */
  private transient boolean addressIndexIsDirty;

  /**
   * Add an address to the store.
   *
   * @param address The address to add to the store.
   */
  public void add(final Address address) {
    if (address == null) {
      return;
    }

    this.addresses.add(address);
    this.addressIndexIsDirty = true;
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

    this.addresses.remove(address);
    this.addressIndexIsDirty = true;
  }

  /**
   * Search the store for addresses matching the specified prefix.
   *
   * @param prefix  The prefix to search for.
   * @return        A list of addresses matching the specified prefix.
   */
  public List<Address> search(final String prefix) {
    if (this.addressIndex == null || this.addressIndexIsDirty) {
      this.createAddressIndex();
    }

    List<Address> results = new ArrayList<>();

    if (prefix == null) {
      return results;
    }

    Address address = Address.parse(prefix.toLowerCase());

    if (address == null) {
      return results;
    }

    Map<String, List<Address>> entries = this.addressIndex.search(
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

    Collections.sort(results, (a, b) -> {
      if (a == b) {
        return 0;
      }

      if (a == null) {
        return -1;
      }

      if (b == null) {
        return 1;
      }

      if (a.equals(b)) {
        return 0;
      }

      if (
        !a.street().equals(b.street())
        && a.street() != null
        && b.street() != null
      ) {
        return a.street().compareTo(b.street());
      }
      if (
        address.number() == null
        && a.postcode() != null
        && b.postcode() != null
      ) {
        return a.postcode().compareTo(b.postcode());
      }

      if (a.number() != null && b.number() != null) {
        if (a.number().length() < b.number().length()) {
          return -1;
        }
        if (a.number().length() == b.number().length()) {
          return a.number().compareTo(b.number());
        }
        if (a.number().length() > b.number().length()) {
          return 1;
        }
      }

      return 0;
    });

    return results;
  }

  /**
   * Construct the search index of the addresses in the store.
   */
  private void createAddressIndex() {
    Map<String, List<Address>> entries = new Object2ObjectOpenHashMap<>();

    for (Address address: this.addresses) {
      String key = this.key(address);

      List<Address> addresses = entries.get(key);

      if (addresses == null) {
        addresses = new ArrayList<>();

        entries.put(key, addresses);
      }

      addresses.add(address);
    }

    this.addressIndex = new TernarySearchTree<>(entries);
    this.addressIndexIsDirty = false;
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

    return key.toString().toLowerCase();
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

    if (al.isEmpty() || bl.isEmpty()) {
      return true;
    }

    return al.startsWith(bl) || bl.startsWith(al);
  }
}
