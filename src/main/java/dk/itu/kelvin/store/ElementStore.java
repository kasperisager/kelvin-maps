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
  private BoundingBox bounds;

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

  public void zeb() {
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
    this.waysIsDirty = true;
  }

  /**
   * Adds a land element to the associated list.
   *
   * @param l the land element to be added.
   */
  public void addLand(final Way l) {
    this.land.add(l);
    this.landIsDirty = true;
  }

  /**
   * Adds a relations element to the associated collection.
   * @param r the relation element.
   */
  public void add(final Relation r) {
    this.relations.add(r);
    this.relationsIsDirty = true;
  }

  /**
   * Adds bound element to the associated collection.
   * @param b the relation element.
   */
  public void add(final BoundingBox b) {
    this.bounds = b;
  }

  /**
   * Finds elements that meet the criteria.
   *
   * @param q  The criteria object to look up elements based on.
   * @return the list of elements that meet the criteria.
   */
  public List<? extends Element> search(final Query q) {
    if (waysTree == null || waysIsDirty) {
      this.indexWays(this.ways);
    }
    if (relationsTree == null || relationsIsDirty) {
      this.indexRelations(this.relations);
    }
    if (landTree == null || landIsDirty) {
      this.indexLand(this.land);
    }

    List<Element> elementList = new ArrayList<>();

    for(String s : q.types){
      if(s == "way"){
        elementList.addAll(this.waysTree.range(q.bounds));
      }
      else if(s == "land"){
        elementList.addAll(this.landTree.range(q.bounds));
      }
      else if(s == "relation"){

        elementList.addAll(this.relationsTree.range(q.bounds));
      }
    }

    return elementList;
  }

  public List<Element> getElements(List<Element> list){
    List<Element> liste = new ArrayList<>();
    for( Element e : list){
      liste.add(e);
    }
    return liste;
  }

  /**
   * Indexing the way rangeTree.
   *
   * @param ways The collection of ways to be indexed.
   * @return the indexed rectangleTree.
   */
  public SpatialIndex<Way> indexWays(final Collection<Way> ways) {
    if (ways == null || ways.isEmpty()) {
      return null;
    }

    RectangleTree<Way> wayTree = new RectangleTree<>(ways);
    this.waysIsDirty = false;

    return wayTree;
  }

  /**
   * Indexing relations rangeTree.
   *
   * @param relations The collection of relations to be indexed.
   * @return the indexed rectangleTree.
   */
  public SpatialIndex<Relation> indexRelations(final Collection<Relation> relations) {
    if (relations == null || relations.isEmpty()) {
      return null;
    }

    RectangleTree<Relation> relationTree = new RectangleTree<>(relations);
    this.relationsIsDirty = false;

    return relationTree;
  }

  /**
   * Indexing the land rangeTree.
   *
   * @param land The collection of land to be indexed.
   * @return the indexed rectangleTree.
   */
  public SpatialIndex<Way> indexLand(final Collection<Way> land) {
    if (ways == null || ways.isEmpty()) {
      return null;
    }

    RectangleTree<Way> wayTree = new RectangleTree<>(land);
    this.waysIsDirty = false;

    return wayTree;
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

  /**
   * The search query object.
   */
  public static class Query {

    /**
     * Specifies object type to search for.
     */
    private String[] types;

    /**
     * Specifies the tag to search for.
     */
    private String tag;

    /**
     * Bounds to search for.
     */
    private SpatialIndex.Bounds bounds;

    /**
     * Getter for type field.
     */
    public String[] type() {
      return this.types;
    }

    /**
     * Getter for bounds field.
     */
    public SpatialIndex.Bounds bounds() {
      return this.bounds;
    }

    public void setTypes(String... type){
      int N = type.length;
      types = new String[N];

      for(int i = 0; i < N; i++){
        types[i] = type[i];
      }
    }

    /**
     *
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     */
    public void setBounds(float minX, float minY, float maxX, float maxY) {
      SpatialIndex.Bounds b = new SpatialIndex.Bounds(minX, minY, maxX, maxY);
      this.bounds = b;
    }

    /**
     *
     * @param b
     */
    public void setBounds(SpatialIndex.Bounds b) {
      this.bounds = b;
    }
  }

}
