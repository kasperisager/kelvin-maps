/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// Models
import dk.itu.kelvin.model.Element;

import dk.itu.kelvin.model.Way;
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
  private List<Way> land = new ArrayList<>();

  /**
   * A list for all land elements.
   */
  private List<Way> ways = new ArrayList<>();

  /**
   * A list for all water elements.
   */
  private List<Relation> relations = new ArrayList<>();

  /**
   * A list for all bounds.
   */
  private List<BoundingBox> bounds = new ArrayList<>();

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
   * Indicates whether waysTree needs to be indexed or not.
   */
  private boolean waysIsDirty;

  /**
   * Indicates whether waysTree needs to be indexed or not.
   */
  private boolean landIsDirty;

  /**
   * Indicates whether waysTree needs to be indexed or not.
   */
  private boolean relationsIsDirty;

  /**
   * Constructor.
   */
  public ElementStore() {
  }

  public void zeb() { // hej palle
    this.waysTree = this.indexWays(this.ways);
    this.relationsTree = this.indexRelations(this.relations);
  }

  /**
   * Adds a way element to the associated list.
   *
   * @param w The element to be added.
   */
  public void add(final Way w) {
    this.ways.add(w);
    System.out.println("add way");
  }

  /**
   * Adds a land element to the associated list.
   *
   * @param l the land element to be added.
   */
  public void addLand(final Way l) {
    this.land.add(l);
    System.out.println("add land");

  }

  /**
   * Adds a relations element to the associated collection.
   * @param r the relation element.
   */
  public void add(final Relation r) {
    this.relations.add(r);
    System.out.println("add relation");

  }

  /**
   * Adds bound element to the associated collection.
   * @param b the relation element.
   */
  public void add(final BoundingBox b) {
    this.bounds.add(b);
    System.out.println("add bbox");
  }

  /**
   * Finds elements that meet the criteria.
   *
   * @param criteria  The criteria to look up elements based on.
   * @return the list of elements that meet the criteria.
   */
  @Override
  public List<Element> search(final SpatialIndex.Bounds criteria) {
    return null;
  }

  /**
   * Removes a specific element from the store.
   *
   * @param element The element to remove from the store.
   */
  @Override
  public void remove(final Element element) {
  }

  /**
   * Indexing the way rangeTree.
   *
   * @param ways The collection of ways to be indexed.
   * @return the indexed rectangleTree.
   */
  public SpatialIndex<Way> indexWays(final Collection<Way> ways) {
    if (ways == null || ways.isEmpty()) {
      System.out.println("is empty");
      return null;
    }

    System.out.println("way added");

    RectangleTree<Way> tree = new RectangleTree<Way>(ways);
    this.waysIsDirty = false;

    return tree;
  }

  /**
   * Indexing relations rangeTree.
   *
   * @param relations The collection of relations to be indexed.
   * @return the indexed rectangeTree.
   */
  public SpatialIndex<Relation> indexRelations(final Collection<Relation> relations) {
    if (relations == null || relations.isEmpty()) {
      System.out.println("is empty");
      return null;
    }

    System.out.println("relation added");

    RectangleTree<Relation> tree = new RectangleTree<Relation>(relations);
    this.relationsIsDirty = false;

    return tree;
  }

  /**
   * If needed indexes the given pointTree.
   *
   * @param nodes the list of nodes to be indexed.
   * @return the indexed point tree.
   */
  public PointTree<? extends Element> index(final Collection<Node> nodes) {
    return null;
  }

}
