package logicLayer.tree;

import common.NotPossibleException;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Duc Minh Le (ducmle), Phan Quang Tuan
 * @version 1.7b
 * @Overview Represents a binary, directed edge.
 * @attributes <pre>
 *  src     Node<T>
 *  tgt     Node<T>
 *  weight  Object
 * </pre>
 * @Object <pre>a typical Edge is E:<s, t> where s is src, t is tgt, w is weight
 *          AF(c) = <s, t, w>
 *          </pre>
 * @rep_invariant src!=null /\ src.repOK()==true /\ tgt!=null /\ tgt.repOK()==true /\ src!=tgt
 */
public class Edge<T> implements Serializable {
    private Node<T> src;
    private Node<T> tgt;
    private Object weight;

    /**
     * @requires src != null /\ tgt != null
     * @effects initialise this as Edge(src,tgt)
     */
    public Edge(Node<T> src, Node<T> tgt) throws NotPossibleException {
        if (src == null) {
            throw new NotPossibleException("Null src");
        }
        if (tgt == null) {
            throw new NotPossibleException("Null tgt");
        }
        this.src = src;
        this.tgt = tgt;
    }

    /**
     * @requires src != null
     * @effects sets this.src = src
     */
    public void setSrc(Node<T> src) {
        if (src != null) {
            this.src = src;
        }
    }

    /**
     * @effects return src
     */
    public Node<T> getSrc() {
        return src;
    }

    /**
     * @requires tgt != null
     * @effects sets this.tgt = tgt
     */
    public void setTgt(Node<T> tgt) {
        if (tgt != null) {
            this.tgt = tgt;
        }
    }

    /**
     * @effects return tgt;
     */
    public Node<T> getTgt() {
        return tgt;
    }

    /**
     * @effects if n.equals(src) return true else return false
     */
    public boolean hasSrc(Node<T> n) {
        if (n == null) return false;

        return (src.equals(n));
    }

    /**
     * @effects if n.equals(tgt) return true else return false
     */
    public boolean hasTgt(Node<T> n) {
        if (n == null) return false;

        return (tgt.equals(n));
    }

    /**
     * @effects return weight
     */
    public Object getWeight() {
        return weight;
    }

    /**
     * @effects <pre>
     * set this.weight = weight
     * </pre>
     */
    public void setWeight(Object weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":<" + src.toString() + ", " + tgt.toString() + ", " + (weight != null ? weight.toString() : null) + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge<?> edge = (Edge<?>) o;
        return Objects.equals(src, edge.src) && Objects.equals(tgt, edge.tgt) && Objects.equals(weight, edge.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, tgt, weight);
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
        return src != null && src.repOK() && tgt != null && tgt.repOK() && !src.equals(tgt);
    }
}
