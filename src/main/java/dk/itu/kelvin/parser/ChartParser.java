/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.parser;

// Net utilities
import java.net.URL;

// SAX utilities
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

// SAX helpers
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

// Math
import dk.itu.kelvin.math.Projection;
import dk.itu.kelvin.math.MercatorProjection;

// Storage
import dk.itu.kelvin.store.AddressStore;
import dk.itu.kelvin.store.ElementStore;

// Models
import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.model.BoundingBox;
import dk.itu.kelvin.model.Element;
import dk.itu.kelvin.model.Land;
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.Relation;
import dk.itu.kelvin.model.Way;

/**
 * Parser class.
 *
 * @version 1.0.0
 */
public final class ChartParser {
  /**
   * Projection to use for the parsed coordinates.
   */
  private Projection projection = new MercatorProjection();

  /**
   * Bounding box.
   */
  private BoundingBox bounds;

  /**
   * Land element.
   */
  private Land land;

  /**
   * Store nodes.
   */
  private ElementStore<Node> nodes = new ElementStore<>();

  /**
   * Store ways.
   */
  private ElementStore<Way> ways = new ElementStore<>();

  /**
   * Store relations.
   */
  private ElementStore<Relation> relations = new ElementStore<>();

  /**
   * Store addresses.
   */
  private AddressStore addresses = new AddressStore();

  /**
   * The currently active element.
   */
  private Element element;

  /**
   * The currently active element ID.
   */
  private long elementId;

  /**
   * The currently active address object.
   */
  private Address address;

  /**
   * Get the parsed bounds.
   *
   * @return The parsed bounds.
   */
  public BoundingBox bounds() {
    return this.bounds;
  }

  /**
   * Get the parsed land polygons.
   *
   * @return The parsed land polygons.
   */
  public Land land() {
    return this.land;
  }

  /**
   * Get the parsed node elements.
   *
   * @return The parsed node elements.
   */
  public ElementStore<Node> nodes() {
    return this.nodes;
  }

  /**
   * Get the parsed way elements.
   *
   * @return The parsed way elements.
   */
  public ElementStore<Way> ways() {
    return this.ways;
  }

  /**
   * Get the parsed relation elements.
   *
   * @return The parsed relation elements.
   */
  public ElementStore<Relation> relations() {
    return this.relations;
  }

  /**
   *  Get the addresses field.
   * @return the addresses field.
   */
  public AddressStore addresses() {
    return this.addresses;
  }

  /**
   * Read and parse an OSM XML file.
   *
   * @param file The path to the file to parse.
   *
   * @throws Exception In case of an error. Duh.
   */
  public void read(final String file) throws Exception {
    URL url = ChartParser.class.getResource(file);

    XMLReader reader = XMLReaderFactory.createXMLReader();

    reader.setContentHandler(new ContentHandler());
    reader.parse(url.toExternalForm());
  }

  /**
   * Given an attributes object look up a value by name and return it as a
   * String.
   *
   * @param attributes  The attributes object to look through.
   * @param value       The name of the value to look for.
   * @return            The value if found, otherwise null.
   */
  private String getString(final Attributes attributes, final String value) {
    return attributes.getValue(value).intern();
  }

  /**
   * Given an attributes object look up a value by name and return it as an
   * Integer.
   *
   * @param attributes  The attributes object to look through.
   * @param value       The name of the value to look for.
   * @return            The value if found, otherwise null.
   */
  private Integer getInteger(final Attributes attributes, final String value) {
    return Integer.parseInt(attributes.getValue(value));
  }

  /**
   * Given an attributes object look up a value by name and return it as a
   * Long.
   *
   * @param attributes  The attributes object to look through.
   * @param value       The name of the value to look for.
   * @return            The value if found, otherwise null.
   */
  private Long getLong(final Attributes attributes, final String value) {
    return Long.parseLong(attributes.getValue(value));
  }

  /**
   * Given an attributes object look up a value by name and return it as a
   * Float.
   *
   * @param attributes  The attributes object to look through.
   * @param value       The name of the value to look for.
   * @return            The value if found, otherwise null.
   */
  private Float getFloat(final Attributes attributes, final String value) {
    return Float.parseFloat(attributes.getValue(value));
  }

  /**
   * Given an attributes object look up a value by name and return it as a
   * Double.
   *
   * @param attributes  The attributes object to look through.
   * @param value       The name of the value to look for.
   * @return            The value if found, otherwise null.
   */
  private Double getDouble(final Attributes attributes, final String value) {
    return Double.parseDouble(attributes.getValue(value));
  }

  /**
   * Clean up after ending an element.
   */
  private void clear() {
    this.element = null;
    this.elementId = 0L;
    this.address = null;
  }

  /**
   * Start a bounds element.
   *
   * @param attributes Element attributes.
   */
  private void startBounds(final Attributes attributes) {
    this.bounds = new BoundingBox(
      this.projection.lonToX(this.getFloat(attributes, "minlon")),
      this.projection.latToY(this.getFloat(attributes, "minlat")),
      this.projection.lonToX(this.getFloat(attributes, "maxlon")),
      this.projection.latToY(this.getFloat(attributes, "maxlat"))
    );

    this.land = new Land(this.bounds);
  }

  /**
   * Start a node element.
   *
   * @param attributes Element attributes.
   */
  private void startNode(final Attributes attributes) {
    this.element = new Node(
      this.projection.lonToX(this.getFloat(attributes, "lon")),
      this.projection.latToY(this.getFloat(attributes, "lat"))
    );

    this.elementId = this.getLong(attributes, "id");
  }

  /**
   * End a node element.
   */
  private void endNode() {
    if (this.element == null) {
      return;
    }

    Node node = (Node) this.element;

    this.nodes.put(this.elementId, node);

    if (this.address != null) {
      this.addresses.put(this.address, node);
    }
  }

  /**
   * Start a way element.
   *
   * @param attributes Element attributes.
   */
  private void startWay(final Attributes attributes) {
    this.element = new Way();
    this.elementId = this.getLong(attributes, "id");
  }

  /**
   * End a way element.
   */
  private void endWay() {
    if (this.element == null) {
      return;
    }

    this.ways.put(this.elementId, (Way) this.element);
  }

  /**
   * Start a relation element.
   *
   * @param attributes Element attributes.
   */
  public void startRelation(final Attributes attributes) {
    this.element = new Relation();
    this.elementId = this.getLong(attributes, "id");
  }

  /**
   * End a relation element.
   */
  public void endRelation() {
    if (this.element == null) {
      return;
    }

    this.relations.put(this.elementId, (Relation) this.element);
  }

  /**
   * Start a {@code tag} element.
   *
   * @param attributes Element attributes.
   */
  private void startTag(final Attributes attributes) {
    if (this.element == null) {
      return;
    }

    String k = this.getString(attributes, "k");
    String v = this.getString(attributes, "v");

    if (v.equals("coastline")) {
      this.land.coastline((Way) this.element);
    }

    if (k.startsWith("addr:") && this.address == null) {
      this.address = new Address();
    }

    this.element.order(Element.Order.fromString(k, v));

    switch (k) {
      case "layer":
        this.element.layer(Integer.parseInt(v));
        break;

      case "addr:city":
        this.address.city(v);
        break;
      case "addr:housenumber":
        this.address.number(v);
        break;
      case "addr:postcode":
        this.address.postcode(v);
        break;
      case "addr:street":
        this.address.street(v);
        break;

      default:
        this.element.tag(k, v);
    }
  }

  /**
   * Start an {@code nd} element.
   *
   * @param attributes Element attributes.
   */
  private void startNd(final Attributes attributes) {
    if (this.element == null) {
      return;
    }

    Node node = this.nodes.get(this.getLong(attributes, "ref"));

    if (node == null) {
      return;
    }

    ((Way) this.element).add(node);
  }

  /**
   * Start a member element.
   *
   * @param attributes Element attributes.
   */
  public void startMember(final Attributes attributes) {
    if (this.element == null) {
      return;
    }

    Relation relation = (Relation) this.element;

    // Get the reference ID of the member.
    long ref = this.getLong(attributes, "ref");

    // Get the role of the member.
    Relation.Role role = Relation.Role.fromString(
      this.getString(attributes, "role")
    );

    switch (this.getString(attributes, "type")) {
      case "node":
        relation.add(this.nodes.get(ref), role);
        break;
      case "way":
        relation.add(this.ways.get(ref), role);
        break;
      case "relation":
        relation.add(this.relations.get(ref), role);
        break;
      default:
        // Do nothing.
    }
  }

  /**
   * Custom SAX event handler.
   *
   * This class is simply a proxy between the SAX parser and the ChartParser
   * instance.
   */
  private class ContentHandler extends DefaultHandler {
    /**
     * Parse an opening element of an OSM XML stream.
     *
     * @param uri         The Namespace URI, or the empty string if the element
     *                    has no Namespace URI or if Namespace processing is not
     *                    being performed.
     * @param localName   The local name (without prefix), or the empty string
     *                    if Namespace processing is not being performed.
     * @param qName       The qualified name (with prefix), or the empty string
     *                    if qualified names are not available.
     * @param attributes  The attributes attached to the element. If there are
     *                    no attributes, it shall be an empty Attributes object.
     */
    @Override
    public void startElement(
      final String uri,
      final String localName,
      final String qName,
      final Attributes attributes
    ) {
      switch (qName.toLowerCase()) {
        // Bounds.
        case "bounds":
          ChartParser.this.startBounds(attributes);
          break;

        // Core elements. These can have elements within them so we also need
        // to parse the closing tags.
        case "node":
          ChartParser.this.startNode(attributes);
          break;
        case "way":
          ChartParser.this.startWay(attributes);
          break;
        case "relation":
          ChartParser.this.startRelation(attributes);
          break;

        // Sub elements. These are always self-closing so we're only interested
        // in the start tag.
        case "tag":
          ChartParser.this.startTag(attributes);
          break;
        case "nd":
          ChartParser.this.startNd(attributes);
          break;
        case "member":
          ChartParser.this.startMember(attributes);
          break;

        default:
          // Do nothing.
      }
    }

    /**
     * Parse a closing element of an OSM XML stream.
     *
     * @param uri       The Namespace URI, or the empty string if the element
     *                  has no Namespace URI or if Namespace processing is not
     *                  being performed.
     * @param localName The local name (without prefix), or the empty string if
     *                  Namespace processing is not being performed.
     * @param qName     The qualified name (with prefix), or the empty string if
     *                  qualified names are not available.
     */
    @Override
    public void endElement(
      final String uri,
      final String localName,
      final String qName
    ) {
      switch (qName.toLowerCase()) {
        // Core elements. Parse the closing tags of the core elements.
        case "node":
          ChartParser.this.endNode();
          ChartParser.this.clear();
          break;
        case "way":
          ChartParser.this.endWay();
          ChartParser.this.clear();
          break;
        case "relation":
          ChartParser.this.endRelation();
          ChartParser.this.clear();
          break;
        default:
          // Do nothing.
      }
    }
  }
}
