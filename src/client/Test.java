package client;

import logicLayer.tree.Node;
import common.NotPossibleException;
import logicLayer.tree.Tree;

import java.util.*;

public class Test {
    private static <T> void addAll(Tree<T> des, Tree<T> src) {
        System.out.println(des.addAll(src));
        System.out.println(Arrays.toString(des.toArray()) + "     " + Arrays.toString(des.getLevelArray()));
        System.out.println(Arrays.toString(des.toArray(true)));
        System.out.println(des);
    }

    public static void main(String[] args) {
        try {
            Tree<Integer> a = new Tree<>(Arrays.asList(1, 2, 3, 4, 5));
            Tree<Integer> b = new Tree<>(Arrays.asList(1, 2, 3));
            Tree<Integer> c = new Tree<>(Arrays.asList(7, 8));
            Tree<Integer> d = new Tree<>(Arrays.asList(9, 10));
            Tree<Integer> e = new Tree<>(Collections.singletonList(11));
            Tree<Integer> f = new Tree<>(Collections.singletonList(12));
            System.out.println("Add d to c:");
            addAll(c, d); // 7,8,9,10           // 0,1,1,2
            System.out.println("-----------");
            System.out.println("Add 13 to c:");
            c.add(13);    // 7891013            // 0,1,1,2,1
            System.out.println(Arrays.toString(c.getLevelArray()));
            System.out.println("-----------");
            System.out.println("Add b to c:");
            addAll(b, c); // 1,2,3,7,8,9,10     // 0,1,1,1,1,2,2,3
            System.out.println("-----------");
            System.out.println("Add 15 to b:");
            b.add(15);
            System.out.println(b);
            System.out.println("-----------");
            System.out.println(b.getLevel(15));
            System.out.println(b.getHeight());
            System.out.println(b.isLeaf(3));
            System.out.println(c.hasRightSiblings(new Node<>(8)));
            System.out.println(b.lca(1, 10));

            System.out.println("-----------");
            System.out.println("Add b to e:");
            addAll(e, b);

            System.out.println("-----------");
            System.out.println("Clone e then add 88 to e2 to check the preservation of Encapsulation of e:");
            Tree<Integer> e2 = e.clone();
            e2.add(88);
            System.out.println("e2:\n" + e2);
            System.out.println("e:\n" + e);
            System.out.println("-----------\n");
            System.out.println("check the Iterator of e2:");
            Iterator<Integer> iter = e2.iterator();
            List<Integer> tmp = new ArrayList<>();
            while (iter.hasNext()) {
                tmp.add(iter.next());
            }
            System.out.println(tmp);
            System.out.println("-----------\n");

            System.out.println("check when removing 7 from e2");
            e2.remove(7);
            System.out.println(e2);
            System.out.println("-----------\n");

            System.out.println("check when retaining a list from from e3");
            Tree<Integer> e3 = e.clone();
            e3.retainAll(Arrays.asList(11, 1, 2, 15));
            System.out.println(e3);
            System.out.println("-----------\n");

            System.out.println("check iterator when removing from e4");
            Tree<Integer> e4 = e.clone();
            System.out.println("tree at the beginning:");
            System.out.println(e4);
            Iterator<Integer> iter4 = e4.iterator();
            List<Integer> tmp4 = new ArrayList<>();
            int i = 0;
            while (iter4.hasNext()) {
                Integer o = iter4.next();
                if (i == 4) {
                    iter4.remove();
                } else {
                    tmp4.add(o);
                }
                i++;
            }

            System.out.println("tree after:");
            System.out.println(e4);
            System.out.println(Arrays.toString(e4.toArray()));
            System.out.println(tmp4);

            System.out.println("-----------\n");

            System.out.println("test sub tree:");
            Tree<Integer> e5 = e.clone();
            System.out.println("e5:\n" + e5);
            System.out.println("\nsub tree from value '7' of e5:\n" + e5.subTree(7));
            System.out.println("-----------\n");

            System.out.println("test get the object at index `8` from tree in pre-order traversal:");
            System.out.println("tree in pre-order traversal: " + Arrays.toString(e5.toArray(new Integer[0])));
            System.out.println("index `8` object: " + e5.get(8));
            System.out.println("-----------\n");

            System.out.println("test toArray(T[] a) in case of polymorphism");
            Tree<Object> tree = new Tree<>();
            tree.add("haha");
            tree.add(5);
            System.out.println(Arrays.toString(tree.toArray(new Object[5])));
        } catch (NotPossibleException e) {
            e.printStackTrace();
        }
    }
}
