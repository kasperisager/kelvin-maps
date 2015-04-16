/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.parser;

// General utilities
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

// ZIP utilities
import java.util.zip.InflaterInputStream;

// I/O utilities
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

// Koloboke collections
import net.openhft.koloboke.collect.map.hash.HashLongObjMaps;

// Protocol Buffer File entities
import crosby.binary.Fileformat.Blob;
import crosby.binary.Fileformat.BlobHeader;

// Protocol Buffer OSM entities
import crosby.binary.Osmformat;
import crosby.binary.Osmformat.HeaderBBox;
import crosby.binary.Osmformat.HeaderBlock;
import crosby.binary.Osmformat.StringTable;
import crosby.binary.Osmformat.PrimitiveBlock;
import crosby.binary.Osmformat.PrimitiveGroup;

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
 * PBF parser class.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/PBF_Format">
 *      http://wiki.openstreetmap.org/wiki/PBF_Format</a>
 *
 * @see <a href="https://github.com/scrosby/OSM-binary">
 *      https://github.com/scrosby/OSM-binary</a>
 */
public final class PBFParser extends Parser {
  /**
   * Projection to use for the parsed coordinates.
   */
  private final Projection projection = new MercatorProjection();

  /**
   * The string table containing all parsed strings.
   *
   * <p>
   * From the OpenStreetMap wiki:
   *
   * <blockquote>
   * <p>
   * When creating a PBF file, you need to extract all strings (key, value,
   * role, user) into a separate string table. Thereafter, strings are referred
   * to by their index into this table, except that index=0 is used as a
   * delimiter when encoding DenseNodes.
   * </blockquote>
   */
  private StringTable stringTable;

  /**
   * The granularity for geographical coordinates.
   *
   * <p>
   * From the OpenStreetMap wiki:
   *
   * <blockquote>
   * <p>
   * To flexibly handle multiple resolutions, the granularity, or resolution
   * used for representing locations and timestamps is adjustable in multiples
   * of 1 millisecond and 1 nanodegree. The default scaling factor is 1000
   * milliseconds and 100 nanodegrees, corresponding to about ~1cm at the
   * equator. These are the current resolution of the OSM database.
   * </blockquote>
   */
  private int granularity;

  /**
   * The latitude offset for geographical coordinates.
   *
   * <p>
   * From the OpenStreetMap wiki:
   *
   * <blockquote>
   * <p>
   * In addition to granularity, the primitive block also encodes a latitude and
   * longitude offset value. These values, measured in units of nanodegrees,
   * <b>must</b> be added to each coordinate.
   * </blockquote>
   */
  private long latOffset;

  /**
   * The longitude offset for geographical coordinates.
   *
   * <p>
   * From the OpenStreetMap wiki:
   *
   * <blockquote>
   * <p>
   * In addition to granularity, the primitive block also encodes a latitude and
   * longitude offset value. These values, measured in units of nanodegrees,
   * <b>must</b> be added to each coordinate.
   * </blockquote>
   */
  private long lonOffset;

  /**
   * The parsed bounding box.
   */
  private BoundingBox bounds;

  /**
   * The parsed nodes mapped to their IDs.
   */
  private Map<Long, Node> nodes = HashLongObjMaps.newMutableMap();

  /**
   * The parsed ways mapped to their IDs.
   */
  private Map<Long, Way> ways = HashLongObjMaps.newMutableMap();

  /**
   * The parsed relations mapped to their IDs.
   */
  private Map<Long, Relation> relations = HashLongObjMaps.newMutableMap();

  /**
   * The parsed land.
   */
  private Land land;

  /**
   * The parsed addresses.
   */
  private List<Address> addresses = new ArrayList<>();

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
   * Parse an input file.
   *
   * @see <a href="http://chaosinmotion.com/blog/?p=766">
   *      http://chaosinmotion.com/blog/?p=766</a>
   *
   * @param file The file to parse.
   *
   * @throws Exception In case of an exception during parsing.
   */
  protected void parse(final File file) throws Exception {
    // https://docs.oracle.com/javase/tutorial/essential/exceptions/
    // tryResourceClose.html
    try (
      DataInputStream dataStream = new DataInputStream(
        new BufferedInputStream(new FileInputStream(file))
      );
    ) {
      while (dataStream.available() > 0) {
        // The first piece of data in the stream is an int that represents the
        // length of the next blob of data.
        int blobLength = dataStream.readInt();

        // Create a byte array for storing the blob header bytes.
        byte[] blobHeaderBytes = new byte[blobLength];

        // Read the blob header bytes from the data stream.
        dataStream.read(blobHeaderBytes);

        // Parse the blob header bytes to a blob header object.
        BlobHeader blobHeader = BlobHeader.parseFrom(blobHeaderBytes);

        // Create a byte array for sotring blob bytes.
        byte[] blobBytes = new byte[blobHeader.getDatasize()];

        // Read the blob bytes from the data stream.
        dataStream.read(blobBytes);

        // Parse the blob bytes to a blob object.
        Blob blob = Blob.parseFrom(blobBytes);

        InputStream blobData;

        // If the blob contains data compressed using zlib we need to decompress
        // the data first.
        if (blob.hasZlibData()) {
          blobData = new InflaterInputStream(blob.getZlibData().newInput());
        }
        // Otherwise, we can just get the raw data.
        else {
          blobData = blob.getRaw().newInput();
        }

        switch (blobHeader.getType()) {
          // http://wiki.openstreetmap.org/wiki/PBF_Format
          // #Definition_of_the_OSMHeader_fileblock
          case "OSMHeader":
            this.parse(HeaderBlock.parseFrom(blobData));
            break;

          // http://wiki.openstreetmap.org/wiki/PBF_Format
          // #Definition_of_OSMData_fileblock
          case "OSMData":
            this.parse(PrimitiveBlock.parseFrom(blobData));
            break;

          default:
            // We can't handle the blob. Move on to the next one.
            continue;
        }
      }
    }
  }

  /**
   * Get a string by index from the string table.
   *
   * @param index The index of the string to get.
   * @return      The string if found.
   */
  private String getString(final int index) {
    return this.stringTable.getS(index).toStringUtf8();
  }

  /**
   * Parse the specified nanodegrees to a longitude.
   *
   * @param degrees The nanodegrees to parse to a longitude.
   * @return        The parsed longitude.
   */
  private double parseLon(final long degrees) {
    return this.projection.lonToX(
      .000000001 * (this.lonOffset + (this.granularity * degrees))
    );
  }

  /**
   * Parse the specified nanodegrees to a latitude.
   *
   * @param degrees The nanodegrees to parse to a latitude.
   * @return        The parsed latitude.
   */
  private double parseLat(final long degrees) {
    return this.projection.latToY(
      .000000001 * (this.latOffset + (this.granularity * degrees))
    );
  }

  /**
   * Parse a header block.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/PBF_Format
   * #Definition_of_the_OSMHeader_fileblock">http://wiki.openstreetmap.org/wiki
   * /PBF_Format#Definition_of_the_OSMHeader_fileblock</a>
   *
   * @param block The header block to parse.
   */
  private void parse(final HeaderBlock block) {
    if (block.hasBbox()) {
      this.parse(block.getBbox());
    }
  }

  /**
   * Parse a bounding box.
   *
   * <p>
   * From {@code osmformat.proto}:
   *
   * <blockquote>
   * <p>
   * Units are always in nanodegrees -- they do not obey granularity rules.
   * </blockquote>
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/PBF_Format#File_format">
   *      http://wiki.openstreetmap.org/wiki/PBF_Format#File_format</a>
   *
   * @param bbox The bounding box to parse.
   */
  private void parse(final HeaderBBox bbox) {
    this.bounds = new BoundingBox(
      (float) this.projection.lonToX(.000000001 * bbox.getLeft()),
      (float) this.projection.latToY(.000000001 * bbox.getTop()),
      (float) this.projection.lonToX(.000000001 * bbox.getRight()),
      (float) this.projection.latToY(.000000001 * bbox.getBottom())
    );

    this.land = new Land(this.bounds);
  }

  /**
   * Parse a primitve block.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/PBF_Format
   * #Definition_of_OSMData_fileblock">http://wiki.openstreetmap.org/wiki
   * /PBF_Format#Definition_of_OSMData_fileblock</a>
   *
   * @param block The primitive block to parse.
   */
  private void parse(final PrimitiveBlock block) {
    this.granularity = block.getGranularity();
    this.latOffset = block.getLatOffset();
    this.lonOffset = block.getLonOffset();
    this.stringTable = block.getStringtable();

    for (PrimitiveGroup group: block.getPrimitivegroupList()) {
      this.parse(group);
    }
  }

  /**
   * Parse a primitive group.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/PBF_Format
   * #Definition_of_OSMData_fileblock">http://wiki.openstreetmap.org/wiki
   * /PBF_Format#Definition_of_OSMData_fileblock</a>
   *
   * @param group The primitive group to parse.
   */
  private void parse(final PrimitiveGroup group) {
    if (group.hasDense()) {
      this.parse(group.getDense());
    }

    for (Osmformat.Node node: group.getNodesList()) {
      this.parse(node);
    }

    for (Osmformat.Way way: group.getWaysList()) {
      this.parse(way);
    }

    for (Osmformat.Relation relation: group.getRelationsList()) {
      this.parse(relation);
    }
  }

  /**
   * Parse a block of densely formatted nodes.
   *
   * <p>
   * From the OpenStreetMap wiki:
   *
   * <blockquote>
   * <p>
   * Nodes can be encoded one of two ways, as a Node (defined above) and a
   * special dense format. In the dense format, I store the group 'columnwise',
   * as an array of ID's, array of latitudes, and array of longitudes. Each
   * column is delta-encoded. This reduces header overheads and allows delta-
   * coding to work very effectively.
   *
   * <p>
   * Keys and values for all nodes are encoded as a single array of stringid's.
   * Each node's tags are encoded in alternating &lt;keyid&gt; &lt;valid&gt;. We
   * use a single stringid of 0 to delimit when the tags of a node ends and the
   * tags of the next node begin. The storage pattern is:
   * ((&lt;keyid&gt; &lt;valid&gt;)* '0' )*
   * </blockquote>
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/PBF_Format#Nodes">
   *      http://wiki.openstreetmap.org/wiki/PBF_Format#Nodes</a>
   *
   * @param nodes The block of densely formatted nodes to parse.
   */
  private void parse(final Osmformat.DenseNodes nodes) {
    long nodeId = 0L;
    long lon = 0L;
    long lat = 0L;

    int keyValCount = nodes.getKeysValsCount();
    int keyValPointer = 0;

    for (int i = 0; i < nodes.getIdCount(); i++) {
      nodeId += nodes.getId(i);
      lon += nodes.getLon(i);
      lat += nodes.getLat(i);

      Node node = new Node(
        (float) this.parseLon(lon), (float) this.parseLat(lat)
      );

      Address address = null;

      while (keyValPointer < keyValCount) {
        // Grab the next key ID.
        int keyId = nodes.getKeysVals(keyValPointer++);

        // If the key ID is 0 then we've reached the node delimiter.
        if (keyId == 0) {
          break;
        }

        // Grab the next value ID.
        int valId = nodes.getKeysVals(keyValPointer++);

        // Grab the key and value from the string table.
        String k = this.getString(keyId);
        String v = this.getString(valId);

        if (k.startsWith("addr:") && address == null) {
          address = new Address();
        }

        switch (k) {
          case "addr:city":
            address.city(v);
            break;
          case "addr:housenumber":
            address.number(v);
            break;
          case "addr:postcode":
            address.postcode(v);
            break;
          case "addr:street":
            address.street(v);
            break;

          default:
            node.tag(k, v);
        }
      }

      if (address != null) {
        address.x(node.x());
        address.y(node.y());

        this.addresses.add(address);
      }
      else {
        this.nodes.put(nodeId, node);
      }
    }
  }

  /**
   * Parse a node.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/PBF_Format#Nodes">
   *      http://wiki.openstreetmap.org/wiki/PBF_Format#Nodes</a>
   *
   * @param node The node to parse.
   */
  private void parse(final Osmformat.Node node) {
    throw new UnsupportedOperationException();
  }

  /**
   * Parse a way.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/PBF_Format
   * #Ways_and_Relations">http://wiki.openstreetmap.org/wiki/PBF_Format
   * #Ways_and_Relations</a>
   *
   * @param way The way to parse.
   */
  private void parse(final Osmformat.Way way) {
    long wayId = way.getId();

    Way parsedWay = new Way();

    for (int i = 0; i < way.getKeysCount(); i++) {
      int keyId = way.getKeys(i);
      int valId = way.getVals(i);

      String k = this.getString(keyId);
      String v = this.getString(valId);

      if (k.equals("coastline")) {
        this.land.add(parsedWay);
      }

      parsedWay.tag(k, v);
    }

    long ref = 0L;

    for (long nextRef: way.getRefsList()) {
      ref += nextRef;

      parsedWay.add(this.nodes.get(ref));
    }

    this.ways.put(wayId, parsedWay);
  }

  /**
   * Parse a relation.
   *
   * @see <a href="http://wiki.openstreetmap.org/wiki/PBF_Format
   * #Ways_and_Relations">http://wiki.openstreetmap.org/wiki/PBF_Format
   * #Ways_and_Relations</a>
   *
   * @param relation The relation to parse.
   */
  private void parse(final Osmformat.Relation relation) {
    long relationId = relation.getId();

    Relation parsedRelation = new Relation();

    for (int i = 0; i < relation.getKeysCount(); i++) {
      int keyId = relation.getKeys(i);
      int valId = relation.getVals(i);

      parsedRelation.tag(this.getString(keyId), this.getString(valId));
    }

    long memId = 0L;

    for (int i = 0; i < relation.getMemidsCount(); i++) {
      memId += relation.getMemids(i);

      Element element;

      switch (relation.getTypes(i)) {
        case NODE:
          element = this.nodes.get(memId);
          break;
        case WAY:
          element = this.ways.get(memId);
          break;
        case RELATION:
          element = this.relations.get(memId);
          break;
        default:
          continue;
      }

      if (element == null) {
        return;
      }

      element.tag("role", this.getString(relation.getRolesSid(i)));
    }

    this.relations.put(relationId, parsedRelation);
  }
}
