/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

import java.io.Serializable;

public interface Graph<N extends Graph.Node, E extends Graph.Edge>
  extends Serializable {

  void add(final E edge);

  public interface Node {
    float x();
    float y();
  }

  public interface Edge {
    Node from();
    Node to();
  }

}
