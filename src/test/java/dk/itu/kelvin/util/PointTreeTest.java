package dk.itu.kelvin.util;

// JUnit annotations
import dk.itu.kelvin.model.Node;
import org.junit.Before;
import org.junit.Test;

// JUnit assertions
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PointTreeTest {

  @Before
  public void setUp() {

  }

  @Test
  public void testSize() {
    List<Node> nodes = new ArrayList<>();
    PointTree<Node> pointTree = new PointTree<>(nodes);

    assertTrue(pointTree.isEmpty());
    nodes.add(new Node(1, 1));
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.size() == 1);
    for(int i = 0; i < 50; i++) {
      nodes.add(new Node(i, i*2));
    }
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.size() == 51);

    for(int i = 0; i < 337; i++) {
      nodes.add(new Node(i, i*2));
    }
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.size() == 388);
    assertFalse(pointTree.isEmpty());
  }

  @Test
  public void testContains() {
    List<Node> nodes = new ArrayList<>();
    PointTree<Node> pointTree = new PointTree<>(nodes);
    assertFalse(pointTree.contains(new Node(1, 1)));
    assertFalse(pointTree.contains(null));

    nodes.add(new Node(1, 1));
    pointTree = new PointTree<>(nodes);
    assertFalse(pointTree.contains(null));

    assertTrue(pointTree.contains(new Node(1, 1)));
  }

  @Test
  public void testRange() {
    List<Node> nodes = new ArrayList<>();
    PointTree<Node> pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.range(null) == null);
    assertTrue(pointTree.range(new SpatialIndex.Bounds(1, 1, 3, 3)) == null);

    nodes.add(new Node(1, 1));
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.range(null) == null);


  }
}
