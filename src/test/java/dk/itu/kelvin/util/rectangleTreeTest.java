/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.model.Way;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// JUnit annotations
// JUnit assertions
// Models

/**
 * {@link RectangleTree} test suite.
 */
public class rectangleTreeTest {
  /**
   * Test the size of tree.
   */
  @Test
  public void testSize() {
    List<Way> ways = new ArrayList<>();
    RectangleTree<Way> rectTree = new RectangleTree<>(ways);

    // tree is empty.
    assertTrue(rectTree.isEmpty());

    // add 1 element to tree.
    ways.add(new Way());
    rectTree = new RectangleTree<>(ways);
    assertTrue(1 == rectTree.size());

    // add 51 elements to tree.
    for(int i = 0; i < 50; i++) {
      ways.add(new Way());
    }

    rectTree = new RectangleTree<>(ways);
    assertTrue(51 == rectTree.size());

    // add another 337 elements to tree.
    for(int i = 0; i < 337; i++) {
      ways.add(new Way());
    }

    rectTree = new RectangleTree<>(ways);
    assertTrue(388 == rectTree.size());

    // tree is not empty
    assertFalse(rectTree.isEmpty());
  }

  /**
   * Test if tree contains specified element.
   */
  @Test
  public void testContains() {
    List<Way> ways = new ArrayList<>();
    RectangleTree<Way> rectTree = new RectangleTree<>(ways);

    // tree does not contain a way
    assertFalse(rectTree.contains(new Way()));
    assertFalse(rectTree.contains(null));

    // tree contains way
    Way way = new Way();
    ways.add(way);
    rectTree = new RectangleTree<>(ways);
    assertFalse(rectTree.contains(null));

    assertTrue(rectTree.contains(way));
  }

  /**
   * Test elements within the range of specified bounds.
   */
  @Test
  public void testRange() {
    List<Way> ways = new ArrayList<>();
    RectangleTree<Way> rectTree = new RectangleTree<>(ways);

    // root equals null.
    assertTrue(rectTree.range(null) == null);


    // specified bounds.
    assertTrue(rectTree.range(new SpatialIndex.Bounds(-1, -1, 3, 3)) == null);

    Way way = new Way();
    way.add(new Node(2, 2));
    ways.add(new Way());

    rectTree = new RectangleTree<>(ways);
    assertTrue(rectTree.range(null) == null);
  }

  /**
   * Test elements within the range of specified bounds.
   */
  @Test
  public void testFilter() {
    List<Way> ways = new ArrayList<>();
    RectangleTree<Way> rectTree;

    // fill up tree.
    for (int i = 0; i < 500; i++) {
      Node n = new Node(i*2, i);
      Way way = new Way();
      way.add(n);
      ways.add(way);
    }
    rectTree = new RectangleTree<>(ways);

    List<Way> result;

    List<Way> expected = new ArrayList<>();
    expected.add(new Way());


    Way w1 = new Way();
    Node n1 = new Node(2, 1);
    w1.tag("test tag", "true");
    w1.add(n1);
    expected.add(w1);

    Way w2 = new Way();
    Node n2 = new Node(4, 2);
    w2.tag("test tag", "false");
    w1.add(n2);
    expected.add(w2);

    Way w3 = new Way();
    Node n3 = new Node(6, 3);
    w3.tag("false tag", "true");
    w1.add(n3);
    expected.add(w3);

    // filter == null
    assertTrue(rectTree.range(new SpatialIndex.Bounds(1, 1, 1 , 1), null) == null);

    // bounds && filter == null
    assertTrue(rectTree.range(null, null) == null);

    // bounds == null
    assertTrue(rectTree.range(null, (element) -> {
      return element.tags().containsKey("test tag");
    }) == null);


    // return all elements in filter: "test tag"
    result = rectTree.range(new SpatialIndex.Bounds(1, 1, 10, 10), (element) ->{
      return element.tags().containsKey("test tag");
    });

    // test filter by adding w1 && w2 to an array
    expected.clear();
    expected.add(w1);
    expected.add(w2);
    for (Way way : result) {
      assertTrue(expected.contains(way));
      expected.remove(way);
    }
    expected.isEmpty();

  }

  /**
   *
   */
  @Test
  public void testClosestPoint() {
    List<Way> ways = new ArrayList<>();
    RectangleTree<Way> rectTree;

    // fill tree.
    Node n1 = new Node(2,1);
    Node n2 = new Node(4,2);
    Node n3 = new Node(4,4);
    Node n4 = new Node(7, 9);

    Way w1 = new Way();
    w1.add(n1);
    w1.add(n2);

    Way w2 = new Way();
    w1.add(n2);
    w1.add(n3);
    w1.add(n4);

    ways.add(w1);
    ways.add(w2);

    rectTree = new RectangleTree<>(ways);

    // find closest element.
    assertTrue(w1 == rectTree.nearest(new SpatialIndex.Point(2,1)));

    assertFalse(w2 == rectTree.nearest(new SpatialIndex.Point(2,1)));

    // point == null.
    assertTrue(rectTree.nearest(null) == null);
  }

  /**
   *
   */
  @Test
  public void testClosestPointFilter() {
    List<Way> ways = new ArrayList<>();
    RectangleTree<Way> rectTree = new RectangleTree<>(ways);

    // point && filter == null
    assertTrue(rectTree.nearest(null, null) == null);

    // filter == null
    assertTrue(rectTree.nearest(new SpatialIndex.Point(2,1), null) == null);

    // fill up tree.
    Way w1 = new Way();
    Node n1 = new Node(2, 1);
    w1.tag("test tag", "true");
    w1.add(n1);

    Way w2 = new Way();
    Node n2 = new Node(4, 2);
    w2.tag("test tag", "false");
    w1.add(n2);

    Way w3 = new Way();
    Node n3 = new Node(6, 3);
    w3.tag("false tag", "true");
    w1.add(n3);

    ways.add(w1);
    ways.add(w2);
    ways.add(w3);
    rectTree = new RectangleTree<>(ways);

    // points == null
    assertTrue(rectTree.nearest(null, (element) -> {
      return element.tags().containsKey("test tag");
    }) == null);

    // return the nearest way in filter: "test tag"
    Way testWay = rectTree.nearest(new SpatialIndex.Point(2,1), (element) ->{
      return element.tags().containsKey("test tag");
    });

    // test if testway equals w1
    assertTrue(testWay.equals(w1));
  }






}
