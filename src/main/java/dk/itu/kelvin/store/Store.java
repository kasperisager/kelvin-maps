/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// I/O utilities
import java.io.*;

// Functional utilities
import dk.itu.kelvin.util.function.Callback;

/**
 * Store class.
 *
 * @param <E> The type of elements contained within the store.
 * @param <C> The search criteria to use when looking up elements.
 */
public abstract class Store<E, C> implements Serializable {
  /**
   * Save the current store to the specified file.
   *
   * @param file      The file to save the store to.
   * @param callback  The callback to invoke once saving has finished.
   */
  public final void save(final File file, final Callback callback) {
    try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))){
      out.writeObject(this);
      out.close();
      callback.call();
    }catch (Exception e){
      throw new RuntimeException(e);
    }
    //throw new UnsupportedOperationException();
  }

  /**
   * Load a store saved to disk into the current store.
   *
   * @param file      The file containing the saved store.
   * @param callback  The callback to invoke once loading has finished.
   */
  public final void load(final File file, final Callback callback) {
    try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
      Store addressStore = (Store)in.readObject();
      in.close();
      callback.call();
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }

}
