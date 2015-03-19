/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// Utilities
import dk.itu.kelvin.util.HashTable;

// Models
import dk.itu.kelvin.model.Element;

/**
 * Element store class.
 *
 * @param <E> The type of elements contained within the store.
 */
public final class ElementStore<E extends Element> extends HashTable<Long, E> {
}
