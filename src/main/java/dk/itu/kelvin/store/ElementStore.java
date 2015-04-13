/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.store;

// Models
import dk.itu.kelvin.model.Element;
import dk.itu.kelvin.util.SpatialIndex;

// parser
import dk.itu.kelvin.parser.Parser;

// Collections
import dk.itu.kelvin.util.PointTree;

import java.util.List;

/**
 * Common store for storing all elements in the chart.
 */
public final class ElementStore extends Store<Element, SpatialIndex.Bounds> {

  private List<Element> all;

  private PointTree water;

  @Override
  public void add(final Element element) {

  }

  @Override
  public void add(final Collection<Element> elements) {


  }

  @Override
  public List<Element> search(SpatialIndex.Bounds criteria) {
    return null;
  }

  @Override
  public void remove(Element element) {

  }

}
