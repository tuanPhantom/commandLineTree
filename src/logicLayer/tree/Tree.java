package logicLayer.tree;

import common.NotPossibleException;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * @overview A treeNew is a set of map that are connected to each other by
 *    edges such that one node, called the root, is connected to some map,
 *    each of these map is connected to some other map that have not been
 *    connected, and so on.
 *    <p>The following is a <b>top-down</b> recursive design that incrementally build a
 *    treeNew by adding leaf map.
 *
 * @attributes <pre>
 * root                     Node<T>
 * parentEdges              HashMap<Node<T>, Edge<T>>
 * properF1DescEdges          HashMap<Node<T>, List<Edge<T>>>
 * </pre>
 * @Object a typical Tree is T:<r,e,d> where r is root, e is parentEdges,
 *         d is properF1DescEdges
 *
 *   <p>Trees are defined recursively as follows:
 *   Basis
 *    For any node r, T = <r,{r},{}> is a logicLayer.tree.
 *   Induction
 *    For all node n and logicLayer.tree T' and for some node p in T':
 *    n is not in T' ->
 *      T = <T'.root, T'.nodes+{n}, T'.edges+{edge(p,n)}> is a logicLayer.tree
 *
 * @rep_invariant
 *   root!=null &&
 *   parentEdges!=null && parentEdges does not have duplicate values &&
 *   parentEdges.Edge<T>[i].getTgt() == parentEdges.Node<T>[i] | 0 < i < parentEdges.size &&
 *   properF1DescEdges!=null && all Lists in properF1DescEdges are not null /\ do not have duplicate values &&
 *     all elements in Lists of properF1DescEdges.values
 *     Edge<T>[i].getTgt() == properF1DescEdges.Node<T>[i] | 0 < i < properF1DescEdges.size &&
 *
 *   parentEdges.size + 1 = properF1DescEdges.size
 *   ~ edges.size + 1 = nodes.size + 1
 * @version 1.1
 * @author Phan Quang Tuan
 */
public class Tree<T> implements Collection<T>, Serializable {
    private Node<T> root;
    private HashMap<Node<T>, Edge<T>> parentEdges;      // as edges
    private HashMap<Node<T>, List<Edge<T>>> properF1DescEdges;    // as nodes


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
    public Tree(T label) throws NotPossibleException {
        this();
        if (label != null) {
            Node<T> r = new Node<>(label);
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
     *     addAll c to T
     *   else
     *     throw new NotPossibleException
     * </pre>
     */
    public Tree(Collection<? extends T> c) throws NotPossibleException {
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
     * @effects return root==null
     */
    @Override
    public boolean isEmpty() {
        return root == null && size() > 0;
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
            Node node = new Node<>(o);
            return properF1DescEdges.containsKey(node);
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
    public Iterator<T> iterator() {
        return new Generator();
    }

    private class Generator implements Iterator<T> {
        private Set<Node<T>> nodes;
        private int index;

        public Generator() {
            nodes = Tree.this.properF1DescEdges.keySet();
        }

        @Override
        public boolean hasNext() {
            return index < nodes.size();
        }

        @Override
        public T next() throws NoSuchElementException {
            if (hasNext()) {
                Object[] arr = nodes.toArray();
                return ((Node<T>) arr[index++]).getLabel();
            }
            throw new NoSuchElementException("There is no element");
        }

        @Override
        public void remove() {
            Object[] arr = nodes.toArray();
            Node<T> node = ((Node<T>) arr[index - 1]);
            if (Tree.this.remove(node)) {
                index--;
            }
        }
    }

    @Override
    public Object[] toArray() {
        return getLabels().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return (T1[]) getLabels().toArray(new Object[a.length]);
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
    public boolean add(T label) {
        if (label == null || contains(label)) {
            return false;
        } else {
            Node<T> node;
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
                return addNode(root, node);
            }
        }
    }

    /**
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
    @Override
    public boolean remove(Object o) {
        if (contains(o)) {
            try {
                Node<T> node = new Node<>((T) o);
                if (node.equals(root)) {
                    clear();
                } else {
                    // remove node from its parent's properF1DescEdges list
                    Edge<T> parentEdge = parentEdges.get(node);
                    Node<T> parentNode = parentEdge.getSrc();
                    List<Edge<T>> list = properF1DescEdges.get(parentNode);
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
        return getLabels().containsAll(c);
    }

    /**
     * This method adds all elements of the given collection to this Tree in pre-order traversal
     * @requires c!=null /\ c is empty
     * @effects <pre>
     *   if c instance of Tree
     *     boolean a = add c[0] to this
     *     boolean b = add { c[i] | 1<=i<c.size } to c[0]
     *     return a /\ b
     *   else
     *     for all elements o in c
     *       if add(o)==false
     *         return false
     *       return true
     * </pre>
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c == null || c.isEmpty()) {
            return false;
        }
        if (c instanceof Tree) {
            return addTree((Tree<T>) c, root);
        } else {
            for (T o : c) {
                if (!add(o)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * This method adds all elements of Tree src to the Node tgt in pre-order traversal
     * @requires src!=null /\ tgt!=null /\ tgt is in this.properF1DescEdges
     * @modifies this.properF1DescEdges, this.parentEdges
     * @effects <pre>
     *   for all Node n in src.properF1DescEdges (in pre-order)
     *   -> so it should look like this:
     *   for all Node n in src.preOrderTraversal(src.root))
     *     if nearest parent node of n is in this.properF1DescEdges
     *        invoke addNode(that node, n) and record its return value
     *     else
     *        invoke addNode(tgt, n) and record its return value
     *   if all return values are true
     *     return true
     *   else
     *     return false
     * </pre>
     */
    private boolean addTree(Tree<T> src, Node<T> tgt) {
        boolean success = false;
        for (Node<T> n : src.preOrderTraversal(src.root)) {
            Edge<T> parentEdgeOfN = src.parentEdges.get(n);
            Node<T> parentNodeOfN = parentEdgeOfN != null ? parentEdgeOfN.getSrc() : null;
            if (properF1DescEdges.containsKey(parentNodeOfN)) {
                success = addNode(parentNodeOfN, n);
            } else {
                success = addNode(tgt, n);
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
        for (Object o : c) {
            if (!remove(o)) {
                return false;
            }
        }
        return true;
    }


    /**
     * @Time_complexity O(n ^ 2)
     * @requires c!=null;
     * @effects <pre>
     *   for all Node n in nodes
     *     for all elements o in c
     *       if n.label==o.label
     *         remove n
     * </pre>
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) return false;
        Generator g = new Generator();
        while (g.hasNext()) {
            T label = g.next();
            for (Object o : c) {
                if (!label.equals(o)) {
                    g.remove();
                }
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
     * @effects return root
     */
    protected Node<T> getRoot() {
        try {
            return new Node<>(root.getLabel());
        } catch (NotPossibleException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @effects return a shallow copy of properF1DescEdges
     */
    protected HashMap<Node<T>, List<Edge<T>>> getproperF1DescEdges() {
        return new HashMap<>(properF1DescEdges);
    }

    /**
     * @effects return a shallow copy of parentEdges
     */
    protected HashMap<Node<T>, Edge<T>> getParentEdges() {
        return new HashMap<>(parentEdges);
    }


    /**
     * This method return a new list capturing node and its proper descendants in pre-order traversal.
     * @requires node!=null /\ nodes is in properF1DescEdges
     * @effects <pre>
     *   init a new List
     *   add 'node' to list
     *   for all Node n that is descendant of 'node'
     *     add preOrderTraversal(n) to list
     *   return list
     * </pre>
     */
    private List<Node<T>> preOrderTraversal(Node<T> node) {
        List<Node<T>> list = new ArrayList<>();
        list.add(node);
        List<Edge<T>> subtrees = properF1DescEdges.get(node);
        if (subtrees != null) {
            for (Edge<T> e : subtrees) {
                Node<T> n = e.getTgt();
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
    public List<T> getLabels() {
        List<T> labels = new ArrayList<>();
        List<Node<T>> data = preOrderTraversal(root);
        for (Node<T> n : data) {
            labels.add(n.getLabel());
        }
        return labels;
    }

    /**
     * @requires parent!=null, child!=null /\ parent.repOK()==true, child.repOK()==true /\
     *           parent is in properF1DescEdges, child is not in properF1DescEdges
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
    private boolean addNode(Node<T> parent, Node<T> child) {
        if (parent == null || child == null || !parent.repOK() || !child.repOK()) {
            return false;
        } else {
            if (!properF1DescEdges.containsKey(parent) || properF1DescEdges.containsKey(child)) {
                return false;
            } else {
                Edge<T> e;
                try {
                    e = new Edge<>(parent, child);
                } catch (NotPossibleException ex) {
                    ex.printStackTrace();
                    return false;
                }
                List<Edge<T>> list = properF1DescEdges.get(parent);
                list.add(e);
                properF1DescEdges.put(child, new ArrayList<>());
                parentEdges.put(child, e);
                return true;
            }
        }
    }

    /**
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
    private void recursiveRemove(Node<T> node) {
        parentEdges.remove(node);
        List<Edge<T>> list = properF1DescEdges.get(node);
        properF1DescEdges.remove(node);
        if (list != null) {
            for (Edge<T> e : list) {
                Node<T> n = e.getTgt();
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
    public boolean addNode(T parent, T child) {
        try {
            Node<T> p = new Node<>(parent);
            Node<T> c = new Node<>(child);
            return addNode(p, c);
        } catch (NotPossibleException e) {
            e.printStackTrace();
            return false;
        }
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
    public int getLevel(T label) {
        if (!contains(label)) {
            return -1;
        } else {
            int level = 0;
            Node<T> expectedRoot;
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
        List<Node<T>> nodes = preOrderTraversal(root);
        int[] array = new int[nodes.size()];
        int i = 0;
        for (Node<T> n : nodes) {
            array[i++] = getLevel(n.getLabel());
        }
        return array;
    }

    /**
     * Return the height of this logicLayer.tree.
     * Remember that:
     * The height of a node is the length of the longest path from it to a leaf. The
     * height of the logicLayer.tree is the height of the root.
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
    private boolean isLeaf(Node<T> node) {
        if (node == null || !node.repOK()) {
            return false;
        } else {
            List<Edge<T>> list = properF1DescEdges.get(node);
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
    public boolean isLeaf(T label) {
        if (label == null || !contains(label)) {
            return false;
        } else {
            try {
                return properF1DescEdges.get(new Node<>(label)).size() == 0;
            } catch (NotPossibleException e) {
                return false;
            }
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
    public boolean hasRightSiblings(Node<T> node) {
        if (!contains(node.getLabel())) {
            return false;
        } else {
            Edge<T> edge = parentEdges.get(node);
            if (edge == null) {
                return false;
            } else {
                List<Edge<T>> list = properF1DescEdges.get(edge.getSrc());
                return list != null && list.indexOf(edge) < list.size() - 1;
            }
        }
    }

    /**
     * <tt>
     * Return the label of the lowest common ancestor of two given labels in this logicLayer.tree. Return null if
     * there is no common ancestor or there is a label that equals to the root's label
     * or two label are equal.
     * REMEMBER THAT:
     * In a logicLayer.tree, a node c is the lowest common ancestor of nodes x and y
     * if c is an ancestor of both x and y, and no proper descendant of c is an ancestor of
     * x and y.
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
    public T lca(T label1, T label2) {
        if (contains(label1) && contains(label2) && !label1.equals(root.getLabel()) && !label2.equals(root.getLabel()) && !label1.equals(label2)) {
            Node<T> n1, n2;
            try {
                n1 = new Node<>(label1);
                n2 = new Node<>(label2);
            } catch (NotPossibleException e) {
                e.printStackTrace();
                return null;
            }
            ArrayList<Node<T>> a = new ArrayList<>();
            while (n1 != root) {
                n1 = parentEdges.get(n1).getSrc();
                a.add(n1);
            }

            while ((n2 != root)) {
                n2 = parentEdges.get(n2).getSrc();
                for (Node<T> n : a) {
                    if (n == n2) {
                        return n2.getLabel();
                    }
                }
            }
        }
        return null;
    }

    /**
     * shallow cloning method
     * @effects return a shallow copy of this
     */
    @Override
    public Tree<T> clone() {
        Tree<T> t = new Tree<>();
        t.root = root;
        t.parentEdges.putAll(parentEdges);
        t.properF1DescEdges.putAll(properF1DescEdges);
        return t;
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
     * This method implemented with pre-order traversal (Visit each node, followed by its children (in pre-order)
     * from left to right.)
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
     * This method implemented with pre-order traversal (Visit each node, followed by its children (in pre-order)
     * from left to right.)
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
    public void toString(StringBuilder sb, Node<T> node, boolean stylize) {
        Node<T> expectedRoot = node;
        List<Node<T>> list = new ArrayList<>();
        while (expectedRoot != root) {
            Node<T> tmp = expectedRoot;
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
        List<Edge<T>> subtrees = properF1DescEdges.get(node);
        int size = 0;
        if (subtrees != null) {
            for (Edge<T> e : subtrees) {
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
     *         sb.append("├───")
     *     else
     *       sb.append("│   ")
     */
    private void stylizedDeterminer(StringBuilder sb, Node<T> node, boolean isTheAppendingNode) {
        T label = node.getLabel();
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
    private void determiner(StringBuilder sb, Node<T> node, boolean isTheAppendingNode) {
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
