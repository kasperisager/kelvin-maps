/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// Utilities
import dk.itu.kelvin.util.HashTable;

// Models
import dk.itu.kelvin.model.Element;

/**
 * <h2>HashTable store of elements.</h2>
 * <p>
 * Used to store all Node, Way and Relation as HashTables.
 *
 * <p>
 * Elements are stored with an element id as a {@code Long} for {@code key},
 * the element itself is then stored as {@code E} for {@code value}.
 *
 * <p>
 * Used by ChartParser as the OSM is getting parsed.
 *
 * @param <E> The type of elements contained within the store.
 */
public final class ElementStore<E extends Element> extends HashTable<Long, E> {
}
