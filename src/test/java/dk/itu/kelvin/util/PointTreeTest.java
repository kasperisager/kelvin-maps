package dk.itu.kelvin.util;

// General utilities
import java.util.ArrayList;
import java.util.List;

// JUnit annotations
import dk.itu.kelvin.model.Node;
import org.junit.Test;

// JUnit assertions
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * {@link PointTree} test suite.
 */
public final class PointTreeTest {
  /**
   * Test the size of tree.
   */
  @Test
  public void testSize() {
    List<Node> nodes = new ArrayList<>();
    PointTree<Node> pointTree = new PointTree<>(nodes);

    assertTrue(pointTree.isEmpty());
    nodes.add(new Node(1, 1));
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.size() == 1);
    for (int i = 0; i < 50; i++) {
      nodes.add(new Node(i, i * 2));
    }
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.size() == 51);

    for (int i = 0; i < 337; i++) {
      nodes.add(new Node(i, i * 2));
    }
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.size() == 388);
    assertFalse(pointTree.isEmpty());
  }

  /**
   * Test if tree contains specified element.
   */
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
    for (int i = 0; i < 20; i++) {
      nodes.add(new Node(i * 2, i * 3));
    }
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.contains(new Node(4, 6)));
    assertTrue(pointTree.contains(new Node(8, 12)));
    assertFalse(pointTree.contains(new Node(8, 11)));
  }

  /**
   * Test elements within the range of specified bounds.
   */
  @Test
  public void testRange() {
    List<Node> nodes = new ArrayList<>();
    PointTree<Node> pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.range(null) == null);

    nodes.add(new Node(1, 1));
    pointTree = new PointTree<>(nodes);
    assertTrue(pointTree.range(null) == null);

    for (int i = 0; i < 500; i++) {
      nodes.add(new Node(i * 2, i));
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

    assertTrue(pointTree.range(
      new SpatialIndex.Bounds(1, 1, 1 , 1), null
    ) == null);
    assertTrue(pointTree.range(null, null) == null);
    assertTrue(pointTree.range(null, (element) -> {
      return element.tags().containsKey("test tag");
    }) == null);

    result = pointTree.range(
      new SpatialIndex.Bounds(1, 1, 10, 10),
      (element) -> {
        return element.tags().containsKey("test tag");
      }
    );
    expected.clear();
    expected.add(n1);
    expected.add(n2);
    for (Node n: result) {
      assertTrue(expected.contains(n));
      expected.remove(n);
    }
    expected.isEmpty();
  }

  /**
   * Test the if important methods work when bucket_max is transcended.
   */
  @Test
  public void testBucket() {
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < 2049; i++) {
      nodes.add(new Node(i, i));
    }

    PointTree<Node> pointTree = new PointTree<>(nodes);
    // Bug, when the PointTree size exceed Maximum Bucket size(2048) the
    // contains method doesn't work.
    assertFalse(pointTree.contains(new Node(75, 75)));

    assertFalse(pointTree.contains(new Node(75, 74)));
    List<Node> expected = new ArrayList<>();
    List<Node> result = pointTree.range(
            new SpatialIndex.Bounds(49, 50, 51, 80));
    assertTrue(pointTree.range(null) == null);

    expected.add(new Node(50, 50));
    expected.add(new Node(51, 51));
    for (Node n: result) {
      assertTrue(expected.contains(n));
      expected.remove(n);
    }
    expected.isEmpty();

    assertTrue(pointTree.range(new SpatialIndex.Bounds(1, 1, 1, 1)) != null);
  }

  /**
   * Test the if important methods work when bucket_max is transcended.
   */
  @Test
  public void testBucket2() {
    List<Node> nodes = new ArrayList<>();
    for (int i = 0; i < 7500; i++) {
      nodes.add(new Node(i * 5 - 2, i * 4 + 3));
    }

    Node n1 = new Node(1902, 4600);
    Node n2 = new Node(5001, 4600);
    n1.tag("key", "test value");
    n1.tag("key2", "test multi-tags");
    n2.tag("key3", "test multi-tags");
    nodes.add(n1);
    nodes.add(n2);

    PointTree<Node> pointTree = new PointTree<>(nodes);

    List<Node> result = pointTree.range(
      new SpatialIndex.Bounds(1901, 1800, 5001, 4601),
      (element) -> {
        return element.tags().containsValue("test multi-tags");
      }
    );
    List<Node> expected = new ArrayList<>();
    expected.add(n1);
    expected.add(n2);
    for (Node n: result) {
      assertTrue(expected.contains(n));
    }

    result = pointTree.range(
      new SpatialIndex.Bounds(1901, 1800, 5001, 4601),
      (element) -> {
        return element.tags().containsValue("test value");
      }
    );
    assertTrue(result.contains(n1));
    assertFalse(result.contains(n2));
  }
}
