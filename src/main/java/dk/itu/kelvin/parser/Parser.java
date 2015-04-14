/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.parser;

// General utilities
import java.util.Collection;

// I/O utilities
import java.io.File;

// File type utilities
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

// Functional utilities
import dk.itu.kelvin.util.function.Callback;

// Threading
import dk.itu.kelvin.thread.TaskQueue;

// Models
import dk.itu.kelvin.model.Address;
import dk.itu.kelvin.model.BoundingBox;
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.Relation;
import dk.itu.kelvin.model.Way;

/**
 * Parser class.
 */
public abstract class Parser {
  /**
   * Map of file MIME types.
   *
   * @see <a href="http://en.wikipedia.org/wiki/MIME">
   *      http://en.wikipedia.org/wiki/MIME</a>
   */
  private static final FileTypeMap TYPES = new MimetypesFileTypeMap();

  /**
   * Read and parse an input file.
   *
   * <p>
   * All I/O and parsing takes place on a separate thread and the specified
   * callback is invoked once the parsing has finished.
   *
   * @param file      The file to read.
   * @param callback  The callback to invoke once the parsing has finished.
   */
  public final void read(final File file, final Callback callback) {
    TaskQueue.run(() -> {
      try {
        this.parse(file);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      finally {
        callback.call();
      }
    });
  }

  /**
   * Parse the contents of the specified file.
   *
   * <p>
   * This method must be implemented by superclasses and is where the actual
   * file parsing happens.
   *
   * @param file The file whose contents to parse.
   *
   * @throws Exception In case of an error during parsing.
   */
  protected abstract void parse(final File file) throws Exception;

  /**
   * Get the parsed bounds.
   *
   * @return The parsed bounds.
   */
  public abstract BoundingBox bounds();

  /**
   * Get the parsed node elements.
   *
   * @return The parsed node elements.
   */
  public abstract Collection<Node> nodes();

  /**
   * Get the parsed way elements.
   *
   * @return The parsed way elements.
   */
  public abstract Collection<Way> ways();

  /**
   * Get the parsed relation elements.
   *
   * @return The parsed relation elements.
   */
  public abstract Collection<Relation> relations();

  /**
   * Get the parsed land polygons.
   *
   * @return The parsed land polygons.
   */
  public abstract Collection<Way> land();

  /**
   * Get the parsed addresses.
   *
   * @return The parsed addresses.
   */
  public abstract Collection<Address> addresses();

  /**
   * Return a parser instance that can parse the specified file.
   *
   * @param file  The file to find a matching parser for.
   * @return      A parser instance that can parse the specified file or
   *              {@code null} if no matching parser was found.
   */
  public static final Parser probe(final File file) {
    if (file == null || !file.isFile()) {
      return null;
    }

    // Get the MIME type of the file.
    String type = TYPES.getContentType(file);

    switch (type.toLowerCase()) {
      case "application/xml":
      case "application/x-bzip2":
        return new XMLParser();

      case "application/octet-stream":
        return new PBFParser();

      default:
        return null;
    }
  }
}
