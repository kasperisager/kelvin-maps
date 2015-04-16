/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

// General utilities
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// I/O utilities
import java.io.Serializable;

// Koloboke collections
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;

/**
 * Ternary search tree class.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Ternary_search_tree">
 *      http://en.wikipedia.org/wiki/Ternary_search_tree</a>
 *
 * @see <a href="http://algs4.cs.princeton.edu/52trie/TST.java.html">
 *      http://algs4.cs.princeton.edu/52trie/TST.java.html</a>
 *
 * @param <V> The type of values stored within the ternary search tree.
 */
public class TernarySearchTree<V> implements PrefixTree<V> {
  /**
   * UID for identifying serialized objects.
   */
  private static final long serialVersionUID = 917;

  /**
   * The size of the ternary search tree.
   */
  private int size;

  /**
   * The root node of the ternary search tree.
   */
  private Node<V> root;

  /**
   * Get the size of the ternary search tree.
   *
   * @return The size of the ternary search tree.
   */
  public final int size() {
    return this.size;
  }

  /**
   * Check if the ternary search tree is empty.
   *
   * @return  A boolean indicating whether or not the ternary search tree is
   *          empty.
   */
  public final boolean isEmpty() {
    return this.size == 0;
  }

  /**
   * Initialize a new ternary search tree.
   */
  public TernarySearchTree() {
    super();
  }

  /**
   * Initialize a new ternary search tree bulk-loaded with the specified map of
   * elements.
   *
   * @param elements The elements to add to the tree.
   */
  public TernarySearchTree(final Map<String, V> elements) {
    List<String> keys = new ArrayList<>(elements.keySet());

    Collections.sort(keys);

    this.partition(keys, elements);
  }

  /**
   * Partition the specified list of keys, adding the median key to the current
   * tree and recursively partitioning the keys to the left and right of the
   * median.
   *
   * @param keys      The list of keys to partition.
   * @param elements  The elements to partition.
   */
  private void partition(
    final List<String> keys,
    final Map<String, V> elements
  ) {
    if (keys == null || elements == null || keys.size() <= 0) {
      return;
    }

    int median = keys.size() / 2;

    String key = keys.get(median);

    this.put(key, elements.get(key));

    this.partition(keys.subList(0, median), elements);
    this.partition(keys.subList(median + 1, keys.size()), elements);
  }

  /**
   * Get a value by key from the ternary search tree.
   *
   * @param key The key of the value to get.
   * @return    The value if found.
   */
  public final V get(final String key) {
    if (key == null) {
      return null;
    }

    Node<V> node = this.get(this.root, key, 0);

    if (node != null) {
      return node.value;
    }
    else {
      return null;
    }
  }

  /**
   * Loop up the specified key in the given node.
   *
   * @param node  The node to look through.
   * @param key   The key to look for.
   * @param depth The current tree depth.
   * @return      The node containing the specified key if found.
   */
  private Node<V> get(final Node<V> node, final String key, final int depth) {
    if (node == null || key == null) {
      return null;
    }

    char character = key.charAt(depth);

    if (character < node.character) {
      return this.get(node.less, key, depth);
    }

    if (character > node.character) {
      return this.get(node.greater, key, depth);
    }

    if (depth < key.length() - 1) {
      return this.get(node.equal, key, depth + 1);
    }

    return node;
  }

  /**
   * Check if the specified key exists within the ternary search tree.
   *
   * @param key The key to check for.
   * @return    A boolean indicating whether or not the ternary search tree
   *            contains the specified key.
   */
  public final boolean contains(final String key) {
    if (key == null || key.isEmpty()) {
      return false;
    }

    return this.get(key) != null;
  }

  /**
   * Put a key/value pair in the ternary search tree.
   *
   * @param key   The key to put in the ternary search tree.
   * @param value The value to put in the ternary search tree.
   */
  public final void put(final String key, final V value) {
    if (key == null || value == null || key.isEmpty()) {
      return;
    }

    if (!this.contains(key)) {
      this.size++;
    }

    this.root = this.put(this.root, key, value, 0);
  }

  /**
   * Put the key/value pair into the specified node or one of its child-nodes.
   *
   * @param node  The node to put the key/value pair into.
   * @param key   The key to add.
   * @param value The value to add.
   * @param depth The current tree depth.
   * @return      The node that the key/value was added to.
   */
  private Node<V> put(
    final Node<V> node,
    final String key,
    final V value,
    final int depth
  ) {
    if (key == null || value == null) {
      return null;
    }

    char character = key.charAt(depth);
    Node<V> root = node;

    if (root == null) {
      root = new Node<V>(character);
    }

    if (character < root.character) {
      root.less = this.put(root.less, key, value, depth);
    }
    else if (character > root.character) {
      root.greater = this.put(root.greater, key, value, depth);
    }
    else if (depth < key.length() - 1) {
      root.equal = this.put(root.equal, key, value, depth + 1);
    }
    else {
      root.value = value;
    }

    return root;
  }

  /**
   * Remove a key/value pair from the ternary search tree.
   *
   * @param key The key of the value to remove.
   */
  public final void remove(final String key) {
    throw new UnsupportedOperationException();
  }

  /**
   * Remove all key/value pairs from the ternary search tree.
   */
  public final void clear() {
    this.root = null;
    this.size = 0;
  }

  /**
   * Given a prefix, find all keys within the ternary search tree that contain
   * the specified prefix.
   *
   * @param prefix  The prefix to search for.
   * @return        A map of matching keys and their associated values.
   */
  public final Map<String, V> search(final String prefix) {
    Map<String, V> results = HashObjObjMaps.newMutableMap();

    if (prefix == null) {
      return results;
    }

    Node<V> root = this.get(this.root, prefix, 0);

    if (root == null) {
      return results;
    }

    if (root.value != null) {
      results.put(prefix, root.value);
    }

    this.search(root.equal, new StringBuilder(prefix), results);

    return results;
  }

  /**
   * Search a node for the specified prefix.
   *
   * @param node    The node to search.
   * @param prefix  The prefix to search for.
   * @param results The map to add the results to.
   */
  private void search(
    final Node<V> node,
    final StringBuilder prefix,
    final Map<String, V> results
  ) {
    if (node == null || prefix == null || results == null) {
      return;
    }

    this.search(node.less, prefix, results);

    if (node.value != null) {
      results.put(prefix.toString() + node.character, node.value);
    }

    this.search(node.equal, prefix.append(node.character), results);

    prefix.deleteCharAt(prefix.length() - 1);

    this.search(node.greater, prefix, results);
  }

  /**
   * The {@link Node} class describes a node within the ternary search tree.
   *
   * @param <V> The type of value stored within the node.
   */
  private static final class Node<V> implements Serializable {
    /**
     * UID for identifying serialized objects.
     */
    private static final long serialVersionUID = 918;

    /**
     * The character associated with the node.
     */
    private char character;

    /**
     * The child node whose associated character is less than the character of
     * the current node.
     */
    private Node<V> less;

    /**
     * The child node whose associated character is greater than the character
     * of the current node.
     */
    private Node<V> greater;

    /**
     * The child node whose associated character is equal to the character of
     * the current node.
     */
    private Node<V> equal;

    /**
     * The value associated with the node.
     */
    private V value;

    /**
     * Initialize a new node.
     *
     * @param character The character associated with the node.
     */
    public Node(final char character) {
      this.character = character;
    }
  }
}
