/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.parser;

// General utilities
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

// I/O utilities
import java.io.File;

// SAX utilities
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

// SAX helpers
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

// Utilities
import dk.itu.kelvin.util.HashTable;

// Math
import dk.itu.kelvin.math.Projection;
import dk.itu.kelvin.math.MercatorProjection;

// Models
import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.model.BoundingBox;
import dk.itu.kelvin.model.Element;
import dk.itu.kelvin.model.Land;
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.Relation;
import dk.itu.kelvin.model.Way;

/**
 * XML parser class.
 */
public final class XMLParser extends Parser {
  /**
   * Projection to use for the parsed coordinates.
   */
  private final Projection projection = new MercatorProjection();

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
  private Map<Long, Node> nodes = new HashTable<>();

  /**
   * Store ways.
   */
  private Map<Long, Way> ways = new HashTable<>();

  /**
   * Store relations.
   */
  private Map<Long, Relation> relations = new HashTable<>();

  /**
   * Store addresses.
   */
  private List<Address> addresses = new ArrayList<>();

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
  public Collection<Way> land() {
    return this.land.coastlines();
  }

  /**
   * Get the parsed node elements.
   *
   * @return The parsed node elements.
   */
  public Collection<Node> nodes() {
    return this.nodes.values();
  }

  /**
   * Get the parsed way elements.
   *
   * @return The parsed way elements.
   */
  public Collection<Way> ways() {
    return this.ways.values();
  }

  /**
   * Get the parsed relation elements.
   *
   * @return The parsed relation elements.
   */
  public Collection<Relation> relations() {
    return this.relations.values();
  }

  /**
   * Get the parsed addresses.
   *
   * @return The parsed addresses.
   */
  public Collection<Address> addresses() {
    return this.addresses;
  }

  /**
   * Read and parse an OSM XML file.
   *
   * @param file The path to the file to parse.
   *
   * @throws Exception In case of an exception during parsing.
   */
  protected void parse(final File file) throws Exception {
    XMLReader reader = XMLReaderFactory.createXMLReader();

    reader.setContentHandler(new ContentHandler());

    reader.parse(file.toURI().toURL().toExternalForm());
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
    return attributes.getValue(value);
  }

  /**
   * Given an attributes object look up a value by name and return it as an
   * Integer.
   *
   * @param attributes  The attributes object to look through.
   * @param value       The name of the value to look for.
   * @return            The value if found, otherwise null.
   */
  private int getInteger(final Attributes attributes, final String value) {
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
  private long getLong(final Attributes attributes, final String value) {
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
  private float getFloat(final Attributes attributes, final String value) {
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
  private double getDouble(final Attributes attributes, final String value) {
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
      (float) this.projection.lonToX(this.getDouble(attributes, "minlon")),
      (float) this.projection.latToY(this.getDouble(attributes, "maxlat")),
      (float) this.projection.lonToX(this.getDouble(attributes, "maxlon")),
      (float) this.projection.latToY(this.getDouble(attributes, "minlat"))
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
      (float) this.projection.lonToX(this.getDouble(attributes, "lon")),
      (float) this.projection.latToY(this.getDouble(attributes, "lat"))
    );

    this.elementId = this.getLong(attributes, "id");
  }

  /**
   * End a node element.
   */
  private void endNode() {
    if (this.element == null || !(this.element instanceof Node)) {
      return;
    }

    Node node = (Node) this.element;

    if (this.address != null) {
      this.address.x(node.x());
      this.address.y(node.y());

      this.addresses.add(this.address);
    }
    else {
      this.nodes.put(this.elementId, node);
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
    if (this.element == null || !(this.element instanceof Way)) {
      return;
    }

    Way way = (Way) this.element;

    ((ArrayList) way.nodes()).trimToSize();


    this.ways.put(this.elementId, way);
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
    if (this.element == null || !(this.element instanceof Relation)) {
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

    if (k.startsWith("addr:") && this.address == null) {
      this.address = new Address();
    }

    if (this.element instanceof Way) {
      Way way = (Way) this.element;

      if (v.equals("coastline")) {
        this.land.add(way);
      }
    }

    switch (k) {
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
    if (this.element == null || !(this.element instanceof Way)) {
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
    if (this.element == null || !(this.element instanceof Relation)) {
      return;
    }

    Relation relation = (Relation) this.element;

    // Get the reference ID of the member.
    long ref = this.getLong(attributes, "ref");

    Element element;

    switch (this.getString(attributes, "type")) {
      case "node":
        element = this.nodes.get(ref);
        break;
      case "way":
        element = this.ways.get(ref);
        break;
      case "relation":
        element = this.relations.get(ref);
        break;
      default:
        return;
    }

    if (element == null) {
      return;
    }

    element.tag("role", this.getString(attributes, "role"));

    relation.add(element);
  }

  /**
   * Custom SAX event handler.
   *
   * <p>
   * This class is simply a proxy between the SAX parser and the XMLParser
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
        case "bounds":
          XMLParser.this.startBounds(attributes);
          break;

        // Core elements. These can have elements within them so we also need
        // to parse the closing tags.
        case "node":
          XMLParser.this.startNode(attributes);
          break;
        case "way":
          XMLParser.this.startWay(attributes);
          break;
        case "relation":
          XMLParser.this.startRelation(attributes);
          break;

        // Sub elements. These are always self-closing so we're only interested
        // in the start tag.
        case "tag":
          XMLParser.this.startTag(attributes);
          break;
        case "nd":
          XMLParser.this.startNd(attributes);
          break;
        case "member":
          XMLParser.this.startMember(attributes);
          break;

        default:
          return;
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
          XMLParser.this.endNode();
          XMLParser.this.clear();
          break;
        case "way":
          XMLParser.this.endWay();
          XMLParser.this.clear();
          break;
        case "relation":
          XMLParser.this.endRelation();
          XMLParser.this.clear();
          break;
        default:
          return;
      }
    }
  }
}
