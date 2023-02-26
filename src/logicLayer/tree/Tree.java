package logicLayer.tree;

import common.NotPossibleException;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Phan Quang Tuan
 * @version 1.5d
 * @overview <pre>A tree is a set of map that are connected to each other by
 *    edges such that one node, called the root, is connected to some map,
 *    each of these map is connected to some other map that have not been
 *    connected, and so on.
 *    A tree does not contain duplicate values.
 *    <p>The following is a <b>top-down</b> recursive design that incrementally build a
 *    tree by adding leaf map.<pre>
 *
 * @attributes <pre>
 * root                     Node<E>
 * parentEdges              HashMap<Node<E>, Edge<E>>
 * properF1DescEdges        HashMap<Node<E>, List<Edge<E>>>
 * </pre>
 * @Object <pre>
 *         a typical Tree is T:<r,e,d> where r is root, e is parentEdges,
 *         d is properF1DescEdges
 *
 *   <p>Trees are defined recursively as follows:
 *   Basis
 *    For any node r, T = <r,{r},{}> is a tree.
 *   Induction
 *    For all node n and tree T' and for some node p in T':
 *    n is not in T' ->
 *      T = <T'.root, T'.nodes+{n}, T'.edges+{edge(p,n)}> is a tree
 *      </pre>
 * @rep_invariant <pre>
 *   root!=null &&
 *   parentEdges.size = properF1DescEdges.size &&
 *   parentEdges!=null && parentEdges does not have duplicate values &&
 *   parentEdges.Edge<E>[i].getTgt() == parentEdges.Node<E>[i] | 0 < i < parentEdges.size &&
 *   properF1DescEdges!=null && all Lists in properF1DescEdges are not null /\ do not have duplicate values &&
 *     all elements in Lists of properF1DescEdges.values
 *     Edge<E>[i].getTgt() == properF1DescEdges.Node<E>[i] | 0 < i < properF1DescEdges.size
 * @jdk_version_requires 1.8
 * </pre>
 */
public class Tree<E> implements Set<E>, Serializable {
    private Node<E> root;
    private final HashMap<Node<E>, Edge<E>> parentEdges;      // as edges
    private final HashMap<Node<E>, List<Edge<E>>> properF1DescEdges;    // as nodes without the root.

    /**
     * @effects init this as T:<null, {}, {}>
     */
    public Tree() {
        parentEdges = new HashMap<>();
        properF1DescEdges = new HashMap<>();
    }

    /**
     * @requires label!=null
     * @effects <pre>
     *   if label!=null
     *     init this as T:<N:<label>, {}, {N:<label> : []}]>
     *   else
     *     throw new NotPossibleException
     * </pre>
     */
    public Tree(E label) throws NotPossibleException {
        this();
        if (label != null) {
            Node<E> r = new Node<>(label);
            root = r;
            properF1DescEdges.put(r, new ArrayList<>());
        } else {
            throw new NotPossibleException("Null Node");
        }
    }

    /**
     * @requires c!=null
     * @effects <pre>
     *   if c!=null
     *     init this as T:<null, {}, {}>
     *     addAll c to E
     *   else
     *     throw new NotPossibleException
     * </pre>
     */
    public Tree(Collection<? extends E> c) throws NotPossibleException {
        this();
        boolean valid = addAll(c);
        if (!valid) {
            throw new NotPossibleException("Invalid Collection: " + c);
        }
    }

    /**
     * @effects return properF1DescEdges.size()
     */
    @Override
    public int size() {
        return properF1DescEdges.size();
    }

    /**
     * @effects return root==null /\ have no subtrees.
     */
    @Override
    public boolean isEmpty() {
        return root == null && size() == 0;
    }

    /**
     * @effects <pre>
     *   for all Node n in nodes
     *     if n.eq(N:<o>)
     *       return true
     *     else
     *       return false
     * </pre>
     */
    @Override
    public boolean contains(Object o) {
        try {
            Node<?> node = new Node<>(o);
            return root != null && root.equals(o) || properF1DescEdges.containsKey(node);
        } catch (NotPossibleException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @effects <pre>
     *   return a generator that contains all labels of this.nodes in pre-order.
     * </pre>
     */
    @Override
    public Iterator<E> iterator() {
        return new Generator();
    }

    private class Generator implements Iterator<E> {
        private final List<Node<E>> nodes;
        private int index;

        public Generator() {
            //nodes = Tree.this.properF1DescEdges.keySet();     // wrong order
            nodes = preOrderTraversal(root);
        }

        @Override
        public boolean hasNext() {
            return index < nodes.size();
        }

        @Override
        public E next() throws NoSuchElementException {
//            Object[] arr = nodes.toArray();
//            return ((Node<E>) arr[index++]).getLabel();
            return nodes.get(index++).getLabel();
        }

        @Override
        public void remove() {
//            Object[] arr = nodes.toArray();
//            Node<E> node = ((Node<E>) arr[index - 1]);
            Node<E> node = nodes.get(index - 1);
            if (Tree.this.remove(node.getLabel())) {
                index--;

                // fetch data from the tree. Time complexity: O^n
                List<Node<E>> treeNodes = new ArrayList<>();
                treeNodes.add(root);
                treeNodes.addAll(Tree.this.properF1DescEdges.keySet());
                nodes.retainAll(treeNodes);
            }
        }
    }

    @Override
    public Object[] toArray() {
        return getLabels().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
//        if (isEmpty()) throw new RuntimeException("Empty tree!");
//        Class<?> rootType = getRoot().getClass();
//        Class<?> elementType = a.getClass().getComponentType();
//        if (!elementType.isAssignableFrom(rootType))
//            throw new RuntimeException("data type mismatch between tree elements and input array elements");
        return getLabels().toArray(a);
    }

    /**
     * @effects <pre>
     *  if preserveStructure is true
     *      return an new array of the tree's structure
     *  else
     *      return 1-D array of Object(s) with all elements of the tree
     * </pre>
     */
    public Object[] toArray(boolean preserveStructure) {
        return preserveStructure ? preOrderTraversal_PS(root).toArray() : toArray();
    }

    /**
     * @effects <pre>
     *  if preserveStructure is true
     *      return an new array of the tree's structure
     *  else
     *      return 1-D array of T(s) with all elements of the tree
     * </pre>
     */
    public <T> T[] toArray(T[] a, boolean preserveStructure) {
        return preserveStructure ? preOrderTraversal_PS(root).toArray(a) : toArray(a);
    }

    /**
     * @modifies all attributes of this
     * @effects <pre>
     *   if label==null \/ contain(label)==true
     *     return false
     *   else
     *     if root==null
     *       set root = N:<label>
     *       add N:<label> to nodes
     *     else
     *       return addNode(root, N:<label>)
     * </pre>
     */
    @Override
    public boolean add(E label) {
        if (label == null || contains(label)) {
            return false;
        } else {
            Node<E> node;
            try {
                node = new Node<>(label);
            } catch (NotPossibleException e) {
                e.printStackTrace();
                return false;
            }

            if (root == null) {
                root = node;
                properF1DescEdges.put(root, new ArrayList<>());
                return true;
            } else {
                return addNode(root, node, false);
            }
        }
    }

    /**
     * Remove an object from this tree. In other words, terminates any connection between the given object and its
     * parent, as well as its descendants.
     * @modifies all attributes of this
     * @effects <pre>
     *   if contains(label)==false
     *     return false
     *   else
     *     if root.eq(N:<o>)
     *       clear()
     *       return true
     *     else
     *       remove node from its parent's properF1DescEdges list
     *       recursiveRemove(N:<o>);
     *       return true
     * </pre>
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        if (contains(o)) {
            try {
                Node<E> node = new Node<>((E) o);
                if (node.equals(root)) {
                    clear();
                } else {
                    // remove node from its parent's properF1DescEdges list
                    Edge<E> parentEdge = parentEdges.get(node);
                    Node<E> parentNode = parentEdge.getSrc();
                    List<Edge<E>> list = properF1DescEdges.get(parentNode);
                    list.remove(parentEdge);
                    recursiveRemove(node);
                }
                return true;
            } catch (NotPossibleException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
//        return getLabels().containsAll(c);
        List<E> labels = new ArrayList<>();
        labels.add(root.getLabel());
        labels.addAll(properF1DescEdges.keySet().stream().map(Node::getLabel).collect(Collectors.toList()));
        return labels.containsAll(c);
    }

    /**
     * This method adds all elements of the given collection to this Tree in pre-order traversal
     * @requires c!=null /\ c is empty /\ { { {this}-{leaves of this} } /\ c}=null
     * @effects <pre>
     *   if c instance of Tree
     *      if this.isEmpty()==true
     *          invoke treeCopy()
     *      else
     *          boolean a = add c[0] to this
     *          boolean b = add { c[i] | 1<=i<c.size } to c[0]
     *          return a /\ b
     *   else
     *     for all elements o in c
     *       if add(o)==false
     *         return false
     *       return true
     * </pre>
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null || c.isEmpty()) {
            return false;
        }

//        for (E o : c) {
//            if (contains(o) && !isLeaf(o)) {
//                return false;
//            }
//        }
        if (c.stream().anyMatch(o -> contains(o) && !isLeaf(o))) return false;

        if (c instanceof Tree) {
            if (isEmpty()) {
                return treeCopy((Tree<E>) c);
            } else {
                return addTree((Tree<E>) c, root);
            }
        } else {
//            for (E o : c) {
//                if (!add(o)) {
//                    return false;
//                }
//            }
            return c.stream().allMatch(this::internal_add);
        }
    }

    /**
     * @requires this.isEmpty()==true, src.isEmpty()==false
     * @modifies root, parentEdges, properF1DescEdges
     * @effects deep copy the structure and all elements from src to this
     */
    private boolean treeCopy(Tree<E> src) {
        if (!src.isEmpty()) {
            root = src.getRootNode();
            parentEdges.putAll(src.parentEdges);
            Set<Map.Entry<Node<E>, List<Edge<E>>>> pairs = src.properF1DescEdges.entrySet();
            for (Map.Entry<Node<E>, List<Edge<E>>> entry : pairs) {
                properF1DescEdges.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            return true;
        }
        return false;
    }

    /**
     * This method adds all elements of Tree src to the Node tgt in pre-order traversal
     * @requires this.isEmpty()==false /\ src!=null /\ tgt!=null /\ tgt is in this.properF1DescEdges
     * @modifies this.properF1DescEdges, this.parentEdges
     * @effects <pre>
     *   for all Node n in src.properF1DescEdges (in pre-order)
     *   -> so it should look like this:
     *   for all Node n in src.preOrderTraversal(src.root))
     *     if parent node of n is in this.properF1DescEdges (case: after adding \/ src.root is leaf of this)
     *        invoke addNode(that node, n) and record its return value
     *     else
     *        invoke addNode(tgt, n) and record its return value (case: beginning of adding new tree to a node)
     *   if all return values are true
     *     return true
     *   else
     *     return false
     * </pre>
     */
    private boolean addTree(Tree<E> src, Node<E> tgt) {
        boolean success = false;
        List<Node<E>> nodes = src.preOrderTraversal(src.root);
        for (Node<E> n : nodes) {
            Edge<E> parentEdgeOfN = src.parentEdges.get(n);
            Node<E> parentNodeOfN = parentEdgeOfN != null ? parentEdgeOfN.getSrc() : null;
            if (properF1DescEdges.containsKey(parentNodeOfN)) {
                success = addNode(parentNodeOfN, n, false);
            } else {
                success = addNode(tgt, n, false);
            }
        }
        return success;
    }

    /**
     * @requires c!=null
     * @effects <pre>
     *   for all elements o in c
     *     if remove(o)==false
     *       return false
     *   return true
     * </pre>
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) return false;
//        for (Object o : c) {
//            if (!remove(o)) {
//                return false;
//            }
//        }
//        return true;
        return c.stream().allMatch(this::remove);
    }

    /**
     * @Time_complexity O(n ^ 2)
     * @requires c!=null;
     * @effects <pre>
     *   for all Node n in node
     *      if n.label is not in c
     *          remove n
     * </pre>
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) return false;
        Generator g = new Generator();
        while (g.hasNext()) {
            E label = g.next();
            if (!c.contains(label)) {
                g.remove();
            }
        }
        return true;
    }

    @Override
    public void clear() {
        root = null;
        parentEdges.clear();
        properF1DescEdges.clear();
    }

    /**
     * @effects return a copy of root's node
     */
    protected Node<E> getRootNode() {
        try {
            return new Node<>(root.getLabel());
        } catch (NotPossibleException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @effects return root's label
     */
    public E getRoot() {
        return root.getLabel();
    }

    /**
     * @effects return a shallow copy of properF1DescEdges
     */
    protected HashMap<Node<E>, List<Edge<E>>> getProperF1DescEdges() {
        return new HashMap<>(properF1DescEdges);
    }

    /**
     * @effects return a shallow copy of parentEdges
     */
    protected HashMap<Node<E>, Edge<E>> getParentEdges() {
        return new HashMap<>(parentEdges);
    }

    /**
     * This method return a new list capturing node and its proper descendants in pre-order traversal while preserving
     * the structure of the tree in the return data.
     * @requires node!=null /\ nodes is in properF1DescEdges
     * @effects <pre>
     *   init a new List of Objects
     *   add 'node' to list
     *   for all Node n that is descendant of 'node'
     *     add list of preOrderTraversal(n) to list
     *   return list
     * </pre>
     */
    private List<Object> preOrderTraversal_PS(Node<E> node) {
        List<Object> list = new ArrayList<>();
        list.add(node);
        List<Edge<E>> subtrees = properF1DescEdges.get(node);
        if (subtrees != null) {
            for (Edge<E> e : subtrees) {
                Node<E> n = e.getTgt();
                List<Object> tmp = new ArrayList<>(preOrderTraversal_PS(n));
                list.add(tmp);
            }
        }
        return list;
    }

    /**
     * This method return a new list capturing node and its proper descendants in pre-order traversal. However, this
     * method also flattens the returned data into a 1-Dimension list.
     * @requires node!=null /\ nodes is in properF1DescEdges
     * @effects <pre>
     *   init a new List of nodes
     *   add 'node' to list
     *   for all Node n that is descendant of 'node'
     *     add all nodes of preOrderTraversal(n) to list
     *   return list
     * </pre>
     */
    private List<Node<E>> preOrderTraversal(Node<E> node) {
        List<Node<E>> list = new ArrayList<>();
        list.add(node);
        List<Edge<E>> subtrees = properF1DescEdges.get(node);
        if (subtrees != null) {
            for (Edge<E> e : subtrees) {
                Node<E> n = e.getTgt();
                list.addAll(preOrderTraversal(n));
            }
        }
        return list;
    }

    /**
     * return all labels of labels in pre-order traversal
     * @effects <pre>
     *   for all Node n of this.properF1DescEdges in pre-order traversal
     *     add(Node.label)
     * </pre>
     */
    public List<E> getLabels() {
//        List<E> labels = new ArrayList<>();
//        List<Node<E>> data = preOrderTraversal(root);
//        for (Node<E> n : data) {
//            labels.add(n.getLabel());
//        }
//        return labels;
        return preOrderTraversal(root).stream().map(Node::getLabel).collect(Collectors.toList());
    }

    /**
     * This method add a new node to the specified parent node. The tree must not be empty, otherwise this method has no
     * effect and returns false.
     * @param bypassCondition ensure that pre-conditions are satisfied
     * @requires parent!=null, child!=null /\ parent.repOK()==true, child.repOK()==true /\ parent is in
     * properF1DescEdges, child is not in properF1DescEdges /\ parent neq child
     * @modifies properF1DescEdges, parentEdges
     * @effects <pre>
     *   if requirements are not satisfied
     *     return false
     *   else
     *     add E:<parent, child> to the list of properF1DescEdges.get(parent)
     *     put {child : []} to properF1DescEdges
     *     put <child : E:<parent, child>> to parentEdges
     * </pre>
     */
    private boolean addNode(Node<E> parent, Node<E> child, boolean bypassCondition) {
        if (!bypassCondition) {
            if (parent == null || child == null || !parent.repOK() || !child.repOK() || parent.equals(child) || !properF1DescEdges.containsKey(parent) || properF1DescEdges.containsKey(child)) {
                return false;
            }
        }
        Edge<E> e;
        try {
            e = new Edge<>(parent, child);
        } catch (NotPossibleException ex) {
            ex.printStackTrace();
            return false;
        }
        List<Edge<E>> list = properF1DescEdges.get(parent);
        list.add(e);
        properF1DescEdges.put(child, new ArrayList<>());
        parentEdges.put(child, e);
        return true;
    }

    /**
     * Remove any node that is in subtree of the given node.
     * @requires node is in nodes /\ node!=root
     * @modifies all attributes of this
     * @effects <pre>
     *   for all Node n in nodes
     *     if n.eqI(N:<o>)
     *       descendants = properDescendantsOfNode.get(n)
     *       remove n from nodes
     *       remove n from properDescendantsOfNode
     *       remove n from rightSiblingsOf
     *   if descendants!=null
     *     for all Node n in descendants
     *       recursiveRemove(n)
     * </pre>
     */
    private void recursiveRemove(Node<E> node) {
        parentEdges.remove(node);
        List<Edge<E>> list = properF1DescEdges.get(node);
        properF1DescEdges.remove(node);
        if (list != null) {
            for (Edge<E> e : list) {
                Node<E> n = e.getTgt();
                recursiveRemove(n);
            }
        }
    }

    /**
     * @modifies properF1DescEdges, parentEdges
     * @effects <pre>
     *   if parent!=null \/ child!=null
     *     return false
     *   else
     *     return addNode(N:<parent>, N:<child>)
     * </pre>
     */
    public boolean addNode(E parent, E child) {
        try {
            Node<E> p = new Node<>(parent);
            Node<E> c = new Node<>(child);
            return addNode(p, c, false);
        } catch (NotPossibleException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method adds all elements of tree `children` to label `parent`. Note that since Tree is a set, it will not
     * allow duplicate labels. Therefore, before being added, all elements in `children` must not be included in this
     * Tree instance. Otherwise, this method has no effect and returns false.
     * @requires parent != null /\ children != null
     * @modifies this
     * @effects <pre>
     *  if parent != null /\ children != null
     *      return boolean value of addTree()
     *  else
     *      return false
     * </pre>
     */
    public boolean addNode(E parent, Tree<E> children) {
        try {
            return addTree(children, new Node<>(parent));
        } catch (NotPossibleException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @effects <pre>
     *   if contains(label)==false
     *     return -1
     *   else
     *     while expectedRoot!=root
     *       expectedRoot = parentEdges[expectedRoot]
     *       level = level + 1
     *     return level
     * </pre>
     */
    public int getLevel(E label) {
        if (!contains(label)) {
            return -1;
        } else {
            int level = 0;
            Node<E> expectedRoot;
            try {
                expectedRoot = new Node<>(label);
            } catch (NotPossibleException e) {
                return -1;
            }
            while (!expectedRoot.equals(root)) {
                expectedRoot = parentEdges.get(expectedRoot).getSrc();
                level++;
            }
            return level;
        }
    }

    /**
     * @effects <pre>
     *   for all Node n in properF1DescEdges.keySet in pre-order traversal
     *     add freq of n to the return array
     * </pre>
     */
    public int[] getLevelArray() {
        List<Node<E>> nodes = preOrderTraversal(root);
        int[] array = new int[nodes.size()];
        int i = 0;
        for (Node<E> n : nodes) {
            array[i++] = getLevel(n.getLabel());
        }
        return array;
    }

    /**
     * Return the height of this logicLayer.tree. Remember that: The height of a node is the length of the longest path
     * from it to a leaf. The height of the tree is the height of the root.
     * @effects <pre>
     *   max = 0
     *   for all number num in getLevelArray()
     *     if num>max
     *       set max = num
     *   return num
     * </pre>
     */
    public int getHeight() {
        int[] a = getLevelArray();
        int max = 0;
        for (int num : a) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

    /**
     * @requires node!=null /\ node.repOK==true
     * @effects <pre>
     *   if requirements are not satisfied
     *     return false
     *   else
     *     list = properF1DescEdges.get(node)
     *     return list!=null /\ list.size()==0
     * </pre>
     */
    private boolean isLeaf(Node<E> node) {
        if (node == null || !node.repOK()) {
            return false;
        } else {
            List<Edge<E>> list = properF1DescEdges.get(node);
            return list != null && list.size() == 0;
        }
    }

    /**
     * @requires node!=null /\ node is in properF1DescEdges
     * @effects <pre>
     *   if requirements are not satisfied
     *     return false
     *   else
     *     return properF1DescEdges.get(node).size()==0
     * </pre>
     */
    public boolean isLeaf(E label) {
        try {
            List<Edge<E>> children = properF1DescEdges.get(new Node<>(label));
            return children != null && children.size() == 0;
        } catch (NotPossibleException e) {
            return false;
        }
    }

    /**
     * @requires node!=null /\ node is in properF1DescEdges
     * @effects <pre>
     *   if requirements are not satisfied
     *     return false
     *   else
     *     if node is root
     *       return false
     *     else
     *       edge = parentEdges.get(node)
     *       list = properF1DescEdges.get(edge.getSrc())
     *       if (index of edge in list) > (list.size - 2)
     *         return true
     *       else
     *         return false
     * </pre>
     */
    public boolean hasRightSiblings(Node<E> node) {
        if (!contains(node.getLabel())) {
            return false;
        } else {
            Edge<E> edge = parentEdges.get(node);
            if (edge == null) {
                return false;
            } else {
                List<Edge<E>> list = properF1DescEdges.get(edge.getSrc());
                return list != null && list.indexOf(edge) < list.size() - 1;
            }
        }
    }

    /**
     * <tt>
     * Return the label of the lowest common ancestor of two given labels in this tree. Return null if there is no
     * common ancestor or there is a label that equals to the root's label or two label are equal. REMEMBER THAT: In a
     * tree, a node c is the lowest common ancestor of nodes x and y if c is an ancestor of both x and y, and no proper
     * descendant of c is an ancestor of x and y.
     * </tt>
     * @Time_complexity O(n ^ 2)
     * @effects <pre>
     *   if contains(label1)==false \/ contains(label2)==false \/ label1 eq root.label \/
     *      label2 eq root.label \/ label1 eq label2
     *     return null
     *   else
     *     n1 = new Node(label1)
     *     n2 = new Node(label2)
     *     add all n1 ancestor to a list
     *
     *     for each ancestor of n2
     *       for all Node n of n1's ancestors list
     *         if n==n2
     *           return n1.label (or n2.label)
     *     return null
     * </pre>
     */
    public E lca(E label1, E label2) {
        if (contains(label1) && contains(label2) && !label1.equals(root.getLabel()) && !label2.equals(root.getLabel()) && !label1.equals(label2)) {
            Node<E> n1, n2;
            try {
                n1 = new Node<>(label1);
                n2 = new Node<>(label2);
            } catch (NotPossibleException e) {
                e.printStackTrace();
                return null;
            }
            ArrayList<Node<E>> a = new ArrayList<>();
            while (n1 != root) {
                n1 = parentEdges.get(n1).getSrc();
                a.add(n1);
            }

            while ((n2 != root)) {
                n2 = parentEdges.get(n2).getSrc();
                for (Node<E> n : a) {
                    if (n == n2) {
                        return n2.getLabel();
                    }
                }
            }
        }
        return null;
    }

    /**
     * A new tree that is a subtree of this class instance is returned by this method. The root of the new tree will be
     * the specified label. If `remove` argument is true, detach the subtree of the given label from this tree.
     * @effects <pre>
     *  if this.contains(label)
     *      if label == root
     *          if remove==true
     *              clear this
     *          return a deep clone of this
     *      else
     *          remove label from its parent's properF1DescEdges list if remove==true
     *          Declare new tree t
     *          recursive add node that is subtree of t, starting at the specified label
     *          return t
     *   else
     *      return null
     * </pre>
     */
    public Tree<E> subTree(E label, boolean remove) {
        if (contains(label)) {
            try {
                Node<E> node = new Node<>(label);
                if (node.equals(root)) {
                    Tree<E> t = this.clone();
                    if (remove) clear();
                    return t;
                } else {
                    if (remove) {
                        // remove node from its parent's properF1DescEdges list
                        Edge<E> parentEdge = parentEdges.get(node);
                        Node<E> parentNode = parentEdge.getSrc();
                        List<Edge<E>> list = properF1DescEdges.get(parentNode);
                        list.remove(parentEdge);
                    }
                    Tree<E> tree = new Tree<>(label);
                    recursiveSubtree(tree, node, remove);
                    return tree;
                }
            } catch (NotPossibleException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Recursive add nodes that are proper descendant of the specified label `parent` to the given tree. If `remove`
     * argument is true, detach all the added nodes to `tree` from this instance.
     * @param remove indicates that the method will remove all the added nodes to `tree` from this instance.
     * @requires tree != null /\ tree != this /\ parent != null /\ parent is not in this
     * @modifies tree
     * @effects <pre>
     *  add `parent` to tree
     *  for any edge e that connect `parent` to its children,
     *      n = child from e
     *      add n to tree, with its parent node is `parent`
     *      invoke recursiveSubtree(tree, n)
     *  if remove == true
     *      remove node from this
     * </pre>
     */
    private void recursiveSubtree(Tree<E> tree, Node<E> parent, boolean remove) {
        List<Edge<E>> children = this.properF1DescEdges.get(parent);
        for (Edge<E> e : children) {
            Node<E> child = e.getTgt();
            tree.addNode(parent, child, true);
            recursiveSubtree(tree, child, remove);
        }
        if (remove) deleteSingleNode(parent);
    }

    /**
     * @modifies parentEdges, properF1DescEdges
     * @effects remove only the given node from this
     */
    private void deleteSingleNode(Node<E> node) {
        parentEdges.remove(node);
        properF1DescEdges.remove(node);
    }

    /**
     * This method moves a node's subtree from the departure node to the arrival node in the tree.
     * @param departure root label of the subtree that is about to move
     * @param arrival   the label of the node to which the `departure` subtree will be transferred
     * @requires <pre>departure != null /\ departure is in this /\ arrival != null /\ arrival is in this
     *              /\ arrival is not in subtree of departure's subtree</pre>
     * @modifies this
     * @effects <pre>
     *  - remove the subtree of `departure` label from this
     *  - add subtree to node of `arrival` label in this
     * </pre>
     */
    public void move(E departure, E arrival) {
        Tree<E> subtree = subTree(departure, true);
        addNode(arrival, subtree);
    }

    /**
     * Retrieve one element from this tree at the specified index after traversing it in pre-order and flattening it
     * into a 1-D array. This method returns null if the index is out of bounds. Remember that indices start at 0.
     * @effects <pre>
     *  if index < 0 \/ index >= tree.size
     *      return null
     *  else
     *      return toArray()[index]
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public E get(int index) {
        //Array.newInstance(getRoot().getClass(), 0);
        return (index < 0 || index >= size()) ? null : ((E[]) toArray(new Object[0]))[index];
    }

    /**
     * This method operates the same as add(), except it does not check the input conditions. Please make sure these
     * conditions are satisfied before using.
     * @requires label != null /\ label is not in this
     * @modifies all attributes of this
     * @effects <pre>
     *   if label==null \/ contain(label)==true
     *     return false
     *   else
     *     if root==null
     *       set root = N:<label>
     *       add N:<label> to nodes
     *     else
     *       return addNode(root, N:<label>)
     * </pre>
     */
    private boolean internal_add(E label) {
        Node<E> node;
        try {
            node = new Node<>(label);
        } catch (NotPossibleException e) {
            e.printStackTrace();
            return false;
        }

        if (root == null) {
            root = node;
            properF1DescEdges.put(root, new ArrayList<>());
            return true;
        } else {
            return addNode(root, node, true);
        }
    }

    /**
     * Deep cloning method
     * @effects return a deep copy of this
     */
    @Override
    public Tree<E> clone() {
        Tree<E> t = new Tree<>();
        t.addAll(this);
        return t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tree<?> tree = (Tree<?>) o;
        return Objects.equals(root, tree.root) && Objects.equals(parentEdges, tree.parentEdges) && Objects.equals(properF1DescEdges, tree.properF1DescEdges);
    }

    //    /**
//     * @effects <pre>
//     * if this satisfies rep_invariant
//     *   return true
//     * else
//     *   return false
//     * </pre>
//     */
//    private boolean repOK() {
//
//    }


    /**
     * This method implemented with pre-order traversal (Visit each node, followed by its children (in pre-order) from
     * left to right.)
     */
    public String toString(boolean stylize) {
        if (stylize) {
            StringBuilder sb = new StringBuilder();
            if (!isEmpty()) {
                toString(sb, root, true);
            }
            return sb.toString();
        } else {
            return toString();
        }
    }

    /**
     * This method implemented with pre-order traversal (Visit each node, followed by its children (in pre-order) from
     * left to right.)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!isEmpty()) {
            toString(sb, root, false);
        }
        return sb.toString();
    }

    /**
     * @requires sb!=null /\ node in properF1DescEdges
     * @effects <pre>
     *   expectedRoot = node
     *   while expectedRoot!=root
     *     expectedRoot = its parent
     *     add expectedRoot to list
     *   for all element in list in reverse order
     *     append determiner to sb
     *   append node.label to sb
     *   for all Edge e in the properF1DescEdges.get(node)
     *     if e is not the last Edge: append \n to sb
     *     invoke toString(sb, e.getTgt(), stylize)
     * </pre>
     */
    public void toString(StringBuilder sb, Node<E> node, boolean stylize) {
        Node<E> expectedRoot = node;
        List<Node<E>> list = new ArrayList<>();
        while (!expectedRoot.equals(root)) {
            Node<E> tmp = expectedRoot;
            list.add(tmp);
            expectedRoot = parentEdges.get(expectedRoot).getSrc();
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (stylize) {
                stylizedDeterminer(sb, list.get(i), i == 0);
            } else {
                determiner(sb, list.get(i), i == 0);
            }
        }

        if (node.getLabel() instanceof File) {
            sb.append(((File) node.getLabel()).getName());
        } else {
            sb.append(node.getLabel());
        }
        List<Edge<E>> subtrees = properF1DescEdges.get(node);
        int size = 0;
        if (subtrees != null) {
            for (Edge<E> e : subtrees) {
                if (size++ < subtrees.size()) {
                    sb.append("\n");
                }
                toString(sb, e.getTgt(), stylize);
            }
        }
    }

    /**
     * This is an alter version of determiner that designed only for File
     * @requires sb!=null /\ node in properF1DescEdges
     * @effects <pre>
     *   if node has right sibling
     *     if node is the node we are appending label (not its ancestors)
     *       if node.label is File
     *         sb.append("│   ")
     *       else
     *         sb.append("├───")
     *     else
     *       sb.append("│   ")
     *   else
     *     if isTheAppendingNode
     *       if node.label is File
     *         sb.append("│   ")
     *       else
     *         sb.append("└───")
     *     else
     *       sb.append("    ")
     */
    private void stylizedDeterminer(StringBuilder sb, Node<E> node, boolean isTheAppendingNode) {
        E label = node.getLabel();
        // has right sibling(s)
        if (hasRightSiblings(node)) {
            if (isTheAppendingNode) {
                if (((File) label).isFile()) {
                    sb.append("│   ");
                } else {
                    sb.append("├───");
                }
            } else {
                sb.append("│   ");
            }

            // does not have right sibling(s)
        } else if (isTheAppendingNode) {
            if (((File) label).isFile()) {
                sb.append("╵   ");
            } else {
                sb.append("└───");
            }
        } else {
            sb.append("    ");
        }
    }

    /**
     * @requires sb!=null /\ node in properF1DescEdges
     * @effects <pre>
     *   if node has right sibling
     *     if node is the node we are appending label (not its ancestors)
     *       sb.append("├───")
     *     else
     *       sb.append("│   ")
     *   else
     *     if isTheAppendingNode
     *       sb.append("└───");
     *     else
     *       sb.append("    ");
     */
    private void determiner(StringBuilder sb, Node<E> node, boolean isTheAppendingNode) {
        // has right sibling(s)
        if (hasRightSiblings(node)) {
            if (isTheAppendingNode) {
                sb.append("├───");
            } else {
                sb.append("│   ");
            }
            // does not have right sibling(s)
        } else if (isTheAppendingNode) {
            sb.append("└───");
        } else {
            sb.append("    ");
        }
    }
}
