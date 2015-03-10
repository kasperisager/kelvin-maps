/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.parser;

// General utilities
import java.util.HashMap;
import java.util.Map;

// Net utilities
import java.net.URL;

// SAX utilities
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

// SAX helpers
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

// Storage
import dk.itu.kelvin.store.ElementStore;

// Models
import dk.itu.kelvin.model.BoundingBox;
import dk.itu.kelvin.model.Chart;
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
   * The chart to add parsed elements to.
   */
  private Chart chart;

  /**
   * Store nodes.
   */
  private ElementStore<Node> nodes = new ElementStore<>();

  /**
   * Map way IDs to ways.
   */
  private Map<Long, Way> ways = new HashMap<>();

  /**
   * Map relation IDs to relations.
   */
  private Map<Long, Relation> relations = new HashMap<>();

  /**
   * Land element.
   */
  private Land land = new Land();

  /**
   * The currently active element.
   */
  private Element element;

  /**
   * Initialize a new chart parser.
   *
   * @param chart The chart to add the parsed elements to.
   */
  public ChartParser(final Chart chart) {
    this.chart = chart;
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

    reader.setContentHandler(new Handler());
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
  private void clearElement() {
    this.element = null;
  }

  /**
   * Start a bounds element.
   *
   * @param attributes Element attributes.
   */
  private void startBounds(final Attributes attributes) {
    this.chart.bounds(new BoundingBox(
      Chart.lonToX(this.getFloat(attributes, "minlon")),
      Chart.latToY(this.getFloat(attributes, "minlat")),
      Chart.lonToX(this.getFloat(attributes, "maxlon")),
      Chart.latToY(this.getFloat(attributes, "maxlat"))
    ));
  }

  /**
   * Start a node element.
   *
   * @param attributes Element attributes.
   */
  private void startNode(final Attributes attributes) {
    this.element = new Node(
      this.getLong(attributes, "id"),
      Chart.lonToX(this.getFloat(attributes, "lon")),
      Chart.latToY(this.getFloat(attributes, "lat"))
    );
  }

  /**
   * End a node element.
   */
  private void endNode() {
    if (this.element == null) {
      return;
    }

    Node node = (Node) this.element;

    this.nodes.put(node.id(), node);

    this.clearElement();
  }

  /**
   * Start a way element.
   *
   * @param attributes Element attributes.
   */
  private void startWay(final Attributes attributes) {
    this.element = new Way(this.getLong(attributes, "id"));
  }

  /**
   * End a way element.
   */
  private void endWay() {
    if (this.element == null) {
      return;
    }

    Way way = (Way) this.element;

    this.ways.put(way.id(), way);

    this.clearElement();
  }

  /**
   * Start a relation element.
   *
   * @param attributes Element attributes.
   */
  public void startRelation(final Attributes attributes) {
    this.element = new Relation(
      Long.parseLong(attributes.getValue("id"))
    );
  }

  /**
   * End a relation element.
   */
  public void endRelation() {
    if (this.element == null) {
      return;
    }

    Relation relation = (Relation) this.element;

    this.relations.put(relation.id(), relation);

    this.clearElement();
  }

  /**
   * Start a tag element.
   *
   * @param attributes Element attributes.
   */
  private void startTag(final Attributes attributes) {
    if (this.element == null) {
      return;
    }

    String k = this.getString(attributes, "k");
    String v = this.getString(attributes, "v");

    this.element.order(Element.Order.fromString(k, v));

    if (v.equals("coastline")) {
      this.land.coastline((Way) this.element);
    }

    switch (k) {
      case "layer":
        this.element.layer(Integer.parseInt(v));
        break;
      default:
        this.element.tag(k, v);
    }
  }

  /**
   * Start an nd element.
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

    Way way = (Way) this.element;

    way.node(node);
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

    long ref = this.getLong(attributes, "ref");

    // Get the role of the member.
    Relation.Role role = Relation.Role.fromString(
      this.getString(attributes, "role")
    );

    switch (this.getString(attributes, "type")) {
      case "node":
        relation.member(this.nodes.get(ref), role);
        break;
      case "way":
        relation.member(this.ways.get(ref), role);
        break;
      case "relation":
        relation.member(this.relations.get(ref), role);
        break;
      default:
        // Do nothing.
    }
  }

  /**
   * End a document.
   */
  public void endDocument() {
    this.chart.elements(this.ways.values());
    this.chart.elements(this.relations.values());
    this.chart.elements(this.land.coastlines());
  }

  /**
   * Custom SAX event handler.
   *
   * This class is simply a proxy between the SAX parser and the ChartParser
   * instance.
   */
  private class Handler extends DefaultHandler {
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
    public void endElement(
      final String uri,
      final String localName,
      final String qName
    ) {
      switch (qName.toLowerCase()) {
        // Core elements. Parse the closing tags of the core elements.
        case "node":
          ChartParser.this.endNode();
          break;
        case "way":
          ChartParser.this.endWay();
          break;
        case "relation":
          ChartParser.this.endRelation();
          break;
        default:
          // Do nothing.
      }
    }

    /**
     * The end of the document has been reached; hurray!
     */
    public void endDocument() {
      ChartParser.this.endDocument();
    }
  }
}
