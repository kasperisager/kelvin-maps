/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// General utilities
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

// Koloboke collections
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;

// Models
import dk.itu.kelvin.model.Element;

/**
 * Element store class.
 *
 * @version 1.0.0
 */
public final class ElementStore<T extends Element> implements Iterable<T> {
  /**
   * Internal element storage.
   *
   * This uses the Koloboke primitive hash map implementation to minimize memory
   * footprint.
   *
   * @see <a href="http://openhft.github.io/Koloboke/api/0.6/java8">
   *      http://openhft.github.io/Koloboke/api/0.6/java8</a>
   */
  private Map<Long, T> store = HashLongObjMaps.newMutableMap();

  /**
   * Get the size of the element store.
   *
   * @return The size of the element store.
   */
  public int size() {
    return this.store.size();
  }

  /**
   * Check if the element store is empty.
   */
  public boolean empty() {
    return this.store.isEmpty();
  }

  /**
   * Get an element by ID from the store.
   *
   * @return The element if found, otherwise null.
   */
  public T get(final long id) {
    return this.store.get(id);
  }

  /**
   * Check if an element with the given ID exists within the store.
   *
   * @return  A boolean indicating whether or not the element store contains
   *          the element with the specified ID.
   */
  public boolean contains(final long id) {
    return this.store.containsKey(id);
  }

  /**
   * Check if the given element exists within the store.
   *
   * @return  A boolean indicating whether or not the element store contains
   *          the specified element.
   */
  public boolean contains(final T element) {
    return this.store.containsValue(element);
  }

  /**
   * Put an element in the store.
   *
   * @param id      The ID of the element.
   * @param element The element.
   */
  public void put(final long id, final T element) {
    if (element == null) {
      return;
    }

    this.store.put(id, element);
  }

  /**
   * Remove an element by ID from the store.
   *
   * @param id The ID of the element to remove.
   */
  public void remove(final long id) {
    this.store.remove(id);
  }

  /**
   * Iterate over the elements of the store.
   *
   * @return An iterator over the elements of the store.
   */
  public Iterator iterator() {
    return this.store.values().iterator();
  }
}
