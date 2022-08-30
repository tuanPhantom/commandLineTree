package client;

import logicLayer.tree.Node;
import common.NotPossibleException;
import logicLayer.tree.Tree;

import java.util.Arrays;

public class Test {
    private static void addAll(Tree t1, Tree t2) {
        System.out.println(t1.addAll(t2));
        System.out.println(t1.getLabels() + "     " + Arrays.toString(t1.getLevelArray()));
        System.out.println(t1);
    }

    public static void main(String[] args) {
        try {
            Tree<Integer> a = new Tree(Arrays.asList(1, 2, 3, 4, 5));
            Tree<Integer> b = new Tree(Arrays.asList(1, 2, 3));
            Tree<Integer> c = new Tree(Arrays.asList(7, 8));
            Tree<Integer> d = new Tree(Arrays.asList(9, 10));
            Tree<Integer> e = new Tree(Arrays.asList(11));
            Tree<Integer> f = new Tree(Arrays.asList(12));
            addAll(c, d); // 7,8,9,10           // 0,1,1,2
            c.add(13);    // 7891013            // 0,1,1,2,1
            System.out.println(Arrays.toString(c.getLevelArray()));
            addAll(b, c); // 1,2,3,7,8,9,10     // 0,1,1,1,1,2,2,3
            b.add(15);
            System.out.println("-----------");
            System.out.println(b);
            System.out.println("-----------");
            System.out.println(b.getLevel(15));
            System.out.println(b.getHeight());
            System.out.println(b.isLeaf(3));
            System.out.println(c.hasRightSiblings(new Node<>(8)));
            System.out.println(b.lca(1, 10));

//            System.out.println("-----------");
//            System.out.println(b.remove(7));
//            System.out.println(b);
            System.out.println("-----------");
            addAll(e,b);
        } catch (NotPossibleException e) {
            e.printStackTrace();
        }
    }
}
