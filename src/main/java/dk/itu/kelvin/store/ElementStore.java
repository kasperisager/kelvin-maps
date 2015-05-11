/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// General utilities
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

// Math
import dk.itu.kelvin.math.Geometry;

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
import dk.itu.kelvin.util.WeightedGraph;

/**
 * Common store for storing all elements in the chart.
 */
public final class ElementStore extends Store<Element, SpatialIndex.Bounds> {
  /**
   * Weighted graph for all carRoads.
   */
  private WeightedGraph carGraph = new WeightedGraph();

  /**
   * Weighted graph for all roads.
   */
  private WeightedGraph bicycleGraph = new WeightedGraph();

  /**
   * A list for all land elements.
   */
  private List<Way> land = new ArrayList<>();

  /**
   * A list for all way elements.
   */
  private List<Way> ways = new ArrayList<>();

  /**
   * A list for all road elements.
   */
  private List<Way> roads = new ArrayList<>();

  /**
   * A list for all cycleways elements.
   */
  private List<Way> cycleways = new ArrayList<>();

  /**
   * A list for all transportWays elements.
   */
  private List<Way> transportWays = new ArrayList<>();

  /**
   * A list for all water elements.
   */
  private List<Relation> relations = new ArrayList<>();

  /**
   * A list for all Points Of Interest.
   */
  private List<Node> pois = new ArrayList<>();

  /**
   * A list for all bounds.
   */
  private BoundingBox bounds;

  /**
   * Point tree for quick search in node elements.
   */
  private SpatialIndex<Node> poiTree;

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
   * Rectangle tree for quick search in road elements.
   */
  private SpatialIndex<Way> roadsTree;

  /**
   * Point tree for quick search in cycleways elements.
   */
  private SpatialIndex<Way> cyclewaysTree;

  /**
   * Point tree for quick search in transportWays elements.
   */
  private RectangleTree<Way> transportWaysTree;

  /**
   * Indicates whether waysTree needs to be indexed or not.
   */
  private boolean waysIsDirty;

  /**
   * Indicates whether roadsTree needs to be indexed or not.
   */
  private boolean roadsIsDirty;

  /**
   * Indicates whether roadsTree needs to be indexed or not.
   */
  private boolean cyclewaysIsDirty;

  /**
   * Indicates whether landsTree needs to be indexed or not.
   */
  private boolean landIsDirty;

  /**
   * Indicates whether relationsTree needs to be indexed or not.
   */
  private boolean relationsIsDirty;

  /**
   * Indicates whether poiTree needs to be indexed or not.
   */
  private boolean poiIsDirty;

  /**
   * Adds a way element to the associated list.
   *
   * @param w The element to be added.
   */
  public void add(final Way w) {
    String highway = w.tag("highway");
    String cycleway = w.tag("cycleway");
    String bicycleRoad = w.tag("bicycle_road");

    if (highway != null) {
      switch (highway) {
        case "motorway":
        case "trunk":
        case "primary":
        case "secondary":
        case "tertiary":
        case "unclassified":
        case "residential":
        case "service":
        case "motorway_link":
        case "trunk_link":
        case "primary_link":
        case "secondary_link":
        case "tertiary_link":
        case "living_street":
        case "road":
          this.roads.add(w);
          this.transportWays.add(w);
          this.addEdge(w);
          this.roadsIsDirty = true;
          break;
        case "cycleway":
          this.cycleways.add(w);
          this.transportWays.add(w);
          this.addEdge(w);
          this.cyclewaysIsDirty = true;
          break;
        default:
          break;
      }
    }
    else if (cycleway != null) {
      switch (cycleway) {
        case "lane":
        case "opposite":
        case "opposite_lane":
        case "track":
        case "opposite_track":
        case "share_busway":
        case "shared_lane":
          this.cycleways.add(w);
          this.transportWays.add(w);
          this.addEdge(w);
          this.cyclewaysIsDirty = true;
          break;
        default:
          break;
      }
    }
    else if (bicycleRoad != null) {
      switch (bicycleRoad) {
        case "yes":
          this.cycleways.add(w);
          this.transportWays.add(w);
          this.addEdge(w);
          this.cyclewaysIsDirty = true;
          break;
        default:
          break;
      }
    }
    else {
      this.ways.add(w);
      this.waysIsDirty = true;
    }
  }

  /**
   * Accessor to carGraph.
   * @return A graph for to shortest path for cars.
   */
  public WeightedGraph carGraph() {
    return this.carGraph;
  }

  /**
   * Accessor to bicycleGraph.
   * @return A graph for to shortest path for bicycles.
   */
  public WeightedGraph bycicleGraph() {
    return this.bicycleGraph;
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
   *  Adds POI element to the associated collection.
   * @param n The node object which represent a POI.
   */
  public void add(final Node n) {
    this.pois.add(n);
    this.poiIsDirty = true;
  }
  /**
   * Returns new search query.
   * @return the query object.
   */
  public Query find() {
    return new Query();
  }

  /**
   * Finds elements that meet the criteria.
   *
   * @param q  The criteria object to look up elements based on.
   * @return the list of elements that meet the criteria.
   */
  private List<Element> search(final Query q) {
    if (this.waysTree == null || this.waysIsDirty) {
      this.waysTree = this.indexWays(this.ways);
    }
    if (this.relationsTree == null || this.relationsIsDirty) {
      this.relationsTree = this.indexRelations(this.relations);
    }
    if (this.landTree == null || this.landIsDirty) {
      this.landTree = this.indexLand(this.land);
    }
    if (this.poiTree == null || this.poiIsDirty) {
      this.poiTree = this.indexPoi(this.pois);
    }
    if (this.roadsTree == null || this.roadsIsDirty) {
     this.roadsTree = this.indexRoads(this.roads);
      this.transportWaysTree = this.indexTransportWays(this.transportWays);
    }
    if (this.cyclewaysTree == null || this.cyclewaysIsDirty) {
      this.cyclewaysTree = this.indexCycleways(this.cycleways);
      this.transportWaysTree = this.indexTransportWays(this.transportWays);
    }

    List<Element> elementList = new ArrayList<>();

    for (String s : q.types) {
      switch (s) {
        case "transportWay":
          elementList.addAll(this.transportWaysTree.range(q.bounds));
          break;
        case "way":
          elementList.addAll(this.waysTree.range(q.bounds));
          break;
        case "land":
          elementList.addAll(this.landTree.range(q.bounds));
          break;
        case "relation":
          elementList.addAll(this.relationsTree.range(q.bounds));
          break;
        case "poi":
          elementList.addAll(this.poiTree.range(q.bounds, (element) -> {
            return element.tags().containsValue(q.tag);
          }));
          break;
        default:
          break;
      }
    }

    return elementList;
  }

  /**
   * Adds all elements of the param into a new list.
   * @param list the list to be added.
   * @return the resulting list.
   */
  public List<Element> getElements(final List<Element> list) {
    List<Element> liste = new ArrayList<>();
    for (Element e : list) {
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
   * Indexing the road rangeTree.
   *
   * @param ways The collection of roads to be indexed.
   * @return the indexed rectangleTree.
   */
  public SpatialIndex<Way> indexRoads(final Collection<Way> ways) {
    if (ways == null || ways.isEmpty()) {
      return null;
    }

    RectangleTree<Way> roadTree = new RectangleTree<>(ways);
    this.roadsIsDirty = false;

    return roadTree;
  }

  /**
   * Indexing the road rangeTree.
   *
   * @param ways The collection of roads to be indexed.
   * @return the indexed rectangleTree.
   */
  public RectangleTree<Way> indexTransportWays(final Collection<Way> ways) {
    if (ways == null || ways.isEmpty()) {
      return null;
    }

    RectangleTree<Way> transportWaysTree = new RectangleTree<>(ways);
    this.roadsIsDirty = false;

    return transportWaysTree;
  }

  /**
   * Return the transportWayTree.
   * @return transportWaysTree.
   */
  public RectangleTree<Way> transportWaysTree() {
    return this.transportWaysTree;
  }

  /**
   * Indexing the road rangeTree.
   *
   * @param ways The collection of roads to be indexed.
   * @return the indexed rectangleTree.
   */
  public SpatialIndex<Way> indexCycleways(final Collection<Way> ways) {
    if (ways == null || ways.isEmpty()) {
      return null;
    }

    RectangleTree<Way> cyclewaysTree = new RectangleTree<>(ways);
    this.cyclewaysIsDirty = false;

    return cyclewaysTree;
  }

  /**
   * Indexing relations rangeTree.
   *
   * @param relations The collection of relations to be indexed.
   * @return the indexed rectangleTree.
   */
  public SpatialIndex<Relation> indexRelations(
    final Collection<Relation> relations) {
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
    if (this.land == null || this.land.isEmpty()) {
      return null;
    }

    RectangleTree<Way> landTree = new RectangleTree<>(land);
    this.landIsDirty = false;

    return landTree;
  }

  /**
   * If needed indexes the given pointTree.
   *
   * @param nodes the list of nodes to be indexed.
   * @return the indexed point tree.
   */
  public SpatialIndex<Node> indexPoi(final Collection<Node> nodes) {
    if (nodes == null || nodes.isEmpty()) {
      return null;
    }

    PointTree<Node> poiTree = new PointTree<>(nodes);
    this.poiIsDirty = false;

    return poiTree;
  }

  /**
   * Split a way into edges and add them to graph.
   * @param way A way to split into edges.
   */
  public void addEdge(final Way way) {

    double maxSpeed = 0.0;

    boolean oneway = false;

    for (String s : way.tags().keySet()) {
      switch (s) {
        case "maxspeed":
          maxSpeed = Double.parseDouble(way.tags().get("maxspeed"));
          break;
        case "oneway":
          oneway = true;
          break;

        default:
          maxSpeed = 50;
      }
    }

    for (int i = 0; i < way.nodes().size(); i++) {
      if (i + 1 >= way.nodes().size()) {
        break;
      }

      Node from = way.nodes().get(i);
      Node to = way.nodes().get(i + 1);

      float distancePx = (float) Geometry.distance(
        new Geometry.Point(from.x(), from.y()),
        new Geometry.Point(to.x(), to.y())
      );

      float distanceSpeed = (float) (Geometry.distance(
        new Geometry.Point(from.x(), from.y()),
        new Geometry.Point(to.x(), to.y()))
        / maxSpeed);


      // add edge to carGraph
      WeightedGraph.Edge carEdge = this.edge(
        from.x(),
        from.y(),
        to.x(),
        to.y(),
        distanceSpeed);
      this.carGraph.add(carEdge);

      // add edge to bicycleGraph
      WeightedGraph.Edge edgeBicycle = this.edge(
        from.x(),
        from.y(),
        to.x(),
        to.y(),
        distancePx);
      this.bicycleGraph.add(edgeBicycle);

      // add edge to bicycleGraph
      WeightedGraph.Edge edgeBicycle2 =  this.edge(
        to.x(),
        to.y(),
        from.x(),
        from.y(),
        distancePx);
      this.bicycleGraph.add(edgeBicycle2);

      if (!oneway) {
        // add edge to carGraph
        WeightedGraph.Edge carEdge2 = this.edge(
          to.x(),
          to.y(),
          from.x(),
          from.y(),
          distanceSpeed);
        this.carGraph.add(carEdge2);
      }
    }
  }

  /**
   * Initialize a WeightedGraph.Edge.
   * @param fromX X coordinate for from node.
   * @param fromY Y coordinate for from node.
   * @param toX X coordinate for to node.
   * @param toY Y coordinate for to node.
   * @param weight The weight of the edge.
   * @return WeightedGraph.Edge.
   */
  public WeightedGraph.Edge edge(
    final float fromX,
    final float fromY,
    final float toX,
    final float toY,
    final float weight) {

    WeightedGraph.Edge edge = new WeightedGraph.Edge(
      new WeightedGraph.Node(fromX, fromY),
      new WeightedGraph.Node(toX, toY), weight
    );
    return edge;
  }

  /**
   * The search query object.
   */
  public class Query {

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
     * @return list of types.
     */
    public final String[] type() {
      return this.types;
    }

    /**
     * Getter for bounds field.
     *
     * @return the bounds.
     */
    public final SpatialIndex.Bounds bounds() {
      return this.bounds;
    }

    /**
     * Adds strings to a list and adds to field.
     * @param type the different type strings.
     * @return the Query object.
     */
    public final Query types(final String... type) {
      int n = type.length;
      this.types = new String[n];

      for (int i = 0; i < n; i++) {
        this.types[i] = type[i];
      }

      return this;
    }

    /**
     * Set the tag field.
     * @param tag to search for.
     * @return the Query object.
     */
    public final Query tag(final String tag) {
      this.tag = tag;

      return this;
    }

    /**
     * Creates a bounds object and sets it in the field.
     * @param minX minimum x coordinate.
     * @param minY minimum y coordinate.
     * @param maxX maximum x coordinate.
     * @param maxY maximum y coordinate.
     * @return the Query object.
     */
    public final Query bounds(
      final float minX,
      final float minY,
      final float maxX,
      final float maxY) {
      SpatialIndex.Bounds b = new SpatialIndex.Bounds(minX, minY, maxX, maxY);
      this.bounds = b;

      return this;
    }

    /**
     * Set the bounds field.
     * @param b the bounds object to be set.
     * @return the Query object.
     */
    public final Query bounds(final SpatialIndex.Bounds b) {
      this.bounds = b;

      return this;
    }

    /**
     *  Gets the elements searched for.
     * @return list of results.
     */
    public final List<Element> get() {
      return ElementStore.this.search(this);
    }
  }
}
