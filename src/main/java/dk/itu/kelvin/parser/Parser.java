/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.parser;

// I/O utilities
import java.io.File;

// File type utilities
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

// Functional utilities
import dk.itu.kelvin.util.function.Callback;

// Threading
import dk.itu.kelvin.thread.TaskQueue;

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
  public void read(final File file, final Callback callback) {
    TaskQueue.run(() -> {
      try {
        this.parse(file);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      finally {
        callback.done();
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
        return null;

      case "application/octet-stream":
        return null;

      default:
        return null;
    }
  }
}
