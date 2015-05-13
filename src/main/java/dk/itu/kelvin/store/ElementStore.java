/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// General utilities
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// Utilities
import dk.itu.kelvin.util.Graph;
import dk.itu.kelvin.util.SpatialIndex;
import dk.itu.kelvin.util.PointTree;
import dk.itu.kelvin.util.RectangleTree;
import dk.itu.kelvin.util.WeightedGraph;

// Models
import dk.itu.kelvin.model.Element;
import dk.itu.kelvin.model.Way;
import dk.itu.kelvin.model.Relation;
import dk.itu.kelvin.model.BoundingBox;
import dk.itu.kelvin.model.Node;

/**
 * Common store for storing all elements in the chart.
 */
public final class ElementStore extends Store<Element, SpatialIndex.Bounds> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 3081;

  /**
   * Graph for all carRoads.
   */
  private final Graph<Node, Way> carGraph;

  /**
   * Graph for all roads.
   */
  private final Graph<Node, Way> bicycleGraph;

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
  private transient SpatialIndex<Node> poiTree;

  /**
   * Rectangle tree for quick search in way elements.
   */
  private transient SpatialIndex<Way> waysTree;

  /**
   * Rectangle tree for quick search in land elements.
   */
  private transient SpatialIndex<Way> landTree;

  /**
   * Rectangle tree for quick search in way elements.
   */
  private transient SpatialIndex<Relation> relationsTree;

  /**
   * Rectangle tree for quick search in road elements.
   */
  private transient SpatialIndex<Way> roadsTree;

  /**
   * Point tree for quick search in cycleways elements.
   */
  private transient SpatialIndex<Way> cyclewaysTree;

  /**
   * Point tree for quick search in transportWays elements.
   */
  private transient SpatialIndex<Way> transportWaysTree;

  /**
   * Indicates whether waysTree needs to be indexed or not.
   */
  private transient boolean waysIsDirty;

  /**
   * Indicates whether roadsTree needs to be indexed or not.
   */
  private transient boolean roadsIsDirty;

  /**
   * Indicates whether roadsTree needs to be indexed or not.
   */
  private transient boolean cyclewaysIsDirty;

  /**
   * Indicates whether landsTree needs to be indexed or not.
   */
  private transient boolean landIsDirty;

  /**
   * Indicates whether relationsTree needs to be indexed or not.
   */
  private transient boolean relationsIsDirty;

  /**
   * Indicates whether poiTree needs to be indexed or not.
   */
  private transient boolean poiIsDirty;

  /**
   * Initialize a new element store.
   */
  public ElementStore() {
    Properties carProperties = new Properties();
    carProperties.setProperty("bicycle", "no");

    Properties bicycleProperties = new Properties();
    bicycleProperties.setProperty("bicycle", "yes");

    this.carGraph = new WeightedGraph<>(carProperties);
    this.bicycleGraph = new WeightedGraph<>(bicycleProperties);
  }

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
  public Graph<Node, Way> carGraph() {
    return this.carGraph;
  }

  /**
   * Accessor to bicycleGraph.
   * @return A graph for to shortest path for bicycles.
   */
  public Graph<Node, Way> bycicleGraph() {
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
   * Return the transportWayTree.
   * @return transportWaysTree.
   */
  public SpatialIndex<Way> transportWaysTree() {
    this.index();

    return this.transportWaysTree;
  }

  /**
   * Finds elements that meet the criteria.
   *
   * @param q  The criteria object to look up elements based on.
   * @return the list of elements that meet the criteria.
   */
  private List<Element> search(final Query q) {
    this.index();

    List<Element> elementList = new ArrayList<>();

    for (String s: q.types) {
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
   * (Re-)build all indexes if needed.
   */
  private void index() {
    if (this.waysTree == null || this.waysIsDirty) {
      this.waysTree = new RectangleTree<>(this.ways);
      this.waysIsDirty = false;
    }

    if (this.relationsTree == null || this.relationsIsDirty) {
      this.relationsTree = new RectangleTree<>(this.relations);
      this.relationsIsDirty = false;
    }

    if (this.landTree == null || this.landIsDirty) {
      this.landTree = new RectangleTree<>(this.land);
      this.landIsDirty = false;
    }

    if (this.poiTree == null || this.poiIsDirty) {
      this.poiTree = new PointTree<>(this.pois);
      this.poiIsDirty = false;
    }

    if (this.roadsTree == null || this.roadsIsDirty) {
      this.roadsTree = new RectangleTree<>(this.roads);
      this.roadsIsDirty = false;

      this.transportWaysTree = new RectangleTree<>(this.transportWays);
      this.roadsIsDirty = false;
    }

    if (this.cyclewaysTree == null || this.cyclewaysIsDirty) {
      this.cyclewaysTree = new RectangleTree<>(this.cycleways);
      this.cyclewaysIsDirty = false;

      this.transportWaysTree = new RectangleTree<>(this.transportWays);
      this.roadsIsDirty = false;
    }
  }

  /**
   * Split a way into edges and add them to graph.
   * @param way A way to split into edges.
   */
  private void addEdge(final Way way) {
    if (way == null) {
      return;
    }

    this.carGraph.add(way);
    this.bicycleGraph.add(way);
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
      final float maxY
    ) {
      this.bounds = new SpatialIndex.Bounds(minX, minY, maxX, maxY);

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
