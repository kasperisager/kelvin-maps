/**
 * Copyright (C) 2015 The Authors.
 */
package dk.itu.kelvin.util;

/**
 * Ternary search tree class.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Ternary_search_tree">
 *      http://en.wikipedia.org/wiki/Ternary_search_tree</a>
 *
 * @see <a href="http://algs4.cs.princeton.edu/52trie/TST.java.html">
 *      http://algs4.cs.princeton.edu/52trie/TST.java.html</a>
 */
public class TernarySearchTree<V> implements PrefixTree<V> {
  /**
   * The size of the ternary search tree.
   */
  private int size;

  /**
   * The root node of the ternary search tree.
   */
  private Node root;

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
   * Get a value by key from the ternary search tree.
   *
   * @param key The key of the value to get.
   * @return    The value if found.
   */
  public final V get(final Object key) {
    if (key == null) {
      return null;
    }

    String k = this.normalize((String) key);

    if (k.isEmpty()) {
      return null;
    }

    Node node = this.get(this.root, k, 0);

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
  private final Node get(final Node node, final String key, final int depth) {
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
  public final boolean contains(final Object key) {
    if (key == null) {
      return false;
    }

    String k = this.normalize((String) key);

    if (k.isEmpty()) {
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
    if (key == null || value == null) {
      return;
    }

    String k = this.normalize(key);

    if (k.isEmpty()) {
      return;
    }

    if (!this.contains(k)) {
      this.size++;
    }

    this.root = this.put(this.root, k, value, 0);
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
  private Node put(
    final Node node,
    final String key,
    final V value,
    final int depth
  ) {
    char character = key.charAt(depth);
    Node root = node;

    if (root == null) {
      root = new Node(character);
    }

    if (key == null || value == null) {
      return root;
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
  public final void remove(final Object key) {
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
    Map<String, V> results = new HashTable<>();

    if (prefix == null) {
      return results;
    }

    String k = this.normalize(prefix);

    Node root = this.get(this.root, k, 0);

    if (root == null) {
      return results;
    }

    if (root.value != null) {
      results.put(k, root.value);
    }

    this.search(root.equal, new StringBuilder(k), results);

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
    final Node node,
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
   * Normalize the specified key.
   *
   * @param key The key to normalize.
   * @return    The normalized key.
   */
  private String normalize(final String key) {
    if (key == null) {
      return null;
    }

    return key.trim().toLowerCase();
  }

  /**
   * The {@link Node} class describes a node within the ternary search tree.
   */
  private class Node {
    /**
     * The character associated with the node.
     */
    private char character;

    /**
     * The child node whose associated character is less than the character of
     * the current node.
     */
    private Node less;

    /**
     * The child node whose associated character is greater than the character
     * of the current node.
     */
    private Node greater;

    /**
     * The child node whose associated character is equal to the character of
     * the current node.
     */
    private Node equal;

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
