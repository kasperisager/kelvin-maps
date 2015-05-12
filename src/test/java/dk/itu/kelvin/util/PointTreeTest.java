package dk.itu.kelvin.util;

// JUnit annotations
import dk.itu.kelvin.model.Node;
import dk.itu.kelvin.util.function.Filter;
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
    //assertTrue(pointTree.range(new SpatialIndex.Bounds(1, 1, 3, 3)) == null);

    nodes.add(new Node(1, 1));
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.range(null) == null);

    for (int i = 0; i < 500; i++) {
      nodes.add(new Node(i*2, i));
    }
    pointTree = new PointTree<>(nodes);

    List<Node> result = pointTree.range(new SpatialIndex.Bounds(1, 1, 10, 10));
    List<Node> expected = new ArrayList<>();
    expected.add(new Node(1, 1));

    Node n1 = new Node(2, 1);
    n1.tag("test tag", "true");
    expected.add(n1);
    Node n2 = new Node(4, 2);
    n2.tag("test tag", "false");
    expected.add(n2);
    Node n3 = new Node(6, 3);
    n3.tag("false tag", "true");
    expected.add(n3);

    expected.add(new Node(8, 4));
    expected.add(new Node(10, 5));
    for (Node n: result) {
      assertTrue(expected.contains(n));
      expected.remove(n);
    }
    assertTrue(expected.isEmpty());

    assertTrue(pointTree.range(new SpatialIndex.Bounds(1, 1, 1 , 1), null) == null);
    assertTrue(pointTree.range(null, null) == null);
    assertTrue(pointTree.range(null, (element) -> {
      return element.tags().containsKey("test tag");
    }) == null);

    result = pointTree.range(new SpatialIndex.Bounds(1, 1, 10, 10), (element) ->{
      return element.tags().containsKey("test tag");
    });
    expected.clear();
    expected.add(n1);
    expected.add(n2);
    for (Node n : result) {
      assertTrue(expected.contains(n));
      expected.remove(n);
    }
    expected.isEmpty();
  }

  @Test

}
