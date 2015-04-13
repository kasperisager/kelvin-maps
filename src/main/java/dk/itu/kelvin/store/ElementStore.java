/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// Models
import dk.itu.kelvin.model.Element;

import dk.itu.kelvin.model.Way;
import dk.itu.kelvin.model.Land;
import dk.itu.kelvin.model.Relation;
import dk.itu.kelvin.model.BoundingBox;


// Utilities
import dk.itu.kelvin.util.RectangleTree;
import dk.itu.kelvin.util.SpatialIndex;

// General utilities
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Common store for storing all elements in the chart.
 */
public final class ElementStore extends Store<Element, SpatialIndex.Bounds> {

  /**
   * A list for all land elements.
   */
  private List<Element> land = new ArrayList<>();

  /**
   * A list for all land elements.
   */
  private List<Element> ways = new ArrayList<>();;

  /**
   * A list for all water elements.
   */
  private List<Element> relations = new ArrayList<>();

  /**
   * A list for all bounds.
   */
  private List<Element> bounds = new ArrayList<>();

  /**
   * Constructor does nothing.
   */
  public ElementStore() {
    // Do nothing.
  }

  /**
   * Adds elements to the associated list.
   * @param element The element to add to the store.
   */
  @Override
  public void add(final Element element) {
    if (element instanceof Way) {
      ways.add(element);
    } else if (element instanceof Land) {
      land.add(element);
    } else if (element instanceof Relation) {
      relations.add(element);
    } else if (element instanceof BoundingBox) {
      bounds.add(element);
    }
  }

  /**
   *  Adds a collection of elements to the associated list.
   * @param elements The collection to be added.
   */
  public void add(final Collection<Element> elements) {
    for (Element e : elements) {
      this.add(e);
    }
  }

  /**
   * Finds elements that meet the criteria.
   * @param criteria  The criteria to look up elements based on.
   * @return
   */
  @Override
  public List<Element> search(SpatialIndex.Bounds criteria) {
    return null;
  }

  /**
   * Removes a specific element from the store.
   * @param element The element to remove from the store.
   */
  @Override
  public void remove(Element element) {

  }

}
