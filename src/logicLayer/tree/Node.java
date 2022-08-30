package logicLayer.tree;

import common.NotPossibleException;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Overview Represents a labelled node.
 * @attributes <pre>
 * label    T
 * </pre>
 * @Object a typical Node is N:<T>
 * AF(c) = N:<T>
 * @rep_invariant T!=null
 * @version 1.1
 * @author Phan Quang Tuan
 */
public class Node<T> implements Comparable<Node<T>>, Serializable {
    private T label;

    /**
     * @requires label != null
     * @effects <pre>
     *   if label != null
     *     initialise this as Node(label)
     *   else
     *     throw new NotPossibleException
     *  </pre>
     */
    public Node(T label) throws NotPossibleException {
        if (label == null) {
            throw new NotPossibleException("Null label");
        }
        this.label = label;
    }

    /**
     * @effects
     *  return label
     */
    public T getLabel() {
        return label;
    }

    /**
     * @requires label != null
     * @effects
     *  sets this.label = label
     */
    public void setLabel(T label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(label, node.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    /**
     * @effects <pre>
     * if this satisfies rep_invariant
     *   return true
     * else
     *   return false
     * </pre>
     */
    public boolean repOK() {
        return label != null;
    }

    @Override
    public int compareTo(Node<T> o) {
        if (label.getClass().getSimpleName().equals("Integer")) {
            return (Integer) label > (Integer) o.label ? 1 : -1;
        } else if (label.getClass().getSimpleName().equals("File")) {
            File f1 = (File) label;
            File f2 = (File) o.label;
            List<String> s = Arrays.asList(f1.getName(), f2.getName());
            Collections.sort(s);
            return s.get(1).equals(f2.getName()) ? 1 : -1;
        }
        return 0;
    }
}
