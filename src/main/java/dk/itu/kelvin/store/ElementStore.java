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
import dk.itu.kelvin.model.Node;


// Utilities
import dk.itu.kelvin.util.SpatialIndex;
import dk.itu.kelvin.util.PointTree;
import dk.itu.kelvin.util.RectangleTree;

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
   * Point tree for quick search in node elements.
   */
  private SpatialIndex<Node> pointTree;

  /**
   * Rectangle tree for quick search in way elements.
   */
  private SpatialIndex<Way> waysTree;

  /**
   * Rectangle tree for quick search in land elements.
   */
  private SpatialIndex<Way> landTree;

  /**
   * Rectangle tree for quick search in way elements.
   */
  private SpatialIndex<Relation> relationsTree;

  /**
   * Adds elements to the associated list.
   * @param element The element to add to the store.
   */
  @Override
  public void add(final Element element) {
    if (element instanceof Way) {
      this.ways.add(element);
    } else if (element instanceof Land) {
      this.land.add(element);
    } else if (element instanceof Relation) {
      this.relations.add(element);
    } else if (element instanceof BoundingBox) {
      this.bounds.add(element);
    }
  }

  /**
   * Adds a collection of elements to the associated list.
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
   * @return the list of elements that meet the criteria.
   */
  @Override
  public List<Element> search(final SpatialIndex.Bounds criteria) {
    return null;
  }

  /**
   * Add a collection of ways to the chart.
   *
   * @param list The collection of ways to add to the chart.
   */
  public RectangleTree indexWays(final Collection<Way> list) {
    if (ways == null || ways.isEmpty()) {
      return null;
    }

    RectangleTree tree = new RectangleTree<Way>(list);

    return tree;
  }

  /**
   * Add a collection of relations to the chart.
   *
   * @param list The relations to add to the chart.
   */
  public RectangleTree relations(final Collection<Relation> list) {
    if (relations == null || relations.isEmpty()) {
      return null;
    }

    RectangleTree tree = new RectangleTree<Relation>(list);

    return tree;
  }

  /**
   * Removes a specific element from the store.
   * @param element The element to remove from the store.
   */
  @Override
  public void remove(final Element element) {
  }

  /**
   * If needed indexes the given pointTree.
   */
  public PointTree<? extends Element> index(final PointTree<? extends Element> p) {
    return null;
  }

  /**
   * If needed indexes the given rectangleTree.
   */
  public RectangleTree<? extends Element> index(final RectangleTree<? extends Element> r) {
    return null;
  }

}
