package logicLayer;

import logicLayer.tree.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * @author Phan Quang Tuan
 * @version 1.5d
 * @Overview this class constructs and returns tree
 */
public abstract class GenerateTree {
    /**
     * @requires directory != null
     * @effects <pre>
     *  if directory == null \/ directory.isDirectory()==false
     *    return empty tree
     *  else
     *    add directory to tree
     *    for all File f in f.listFiles
     *      add f to tree
     *      if f is dir
     *        addAll buildTree(f) to tree
     * </pre>
     */
    public static Tree<File> buildTree(File directory) {
        Tree<File> tree = new Tree<>();
        if (directory != null && directory.isDirectory()) {
            tree.add(directory);
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    tree.add(f);
                    if (f.isDirectory()) {
                        tree.addAll(buildTree(f));
                    }
                }
            }
        }
        return tree;
    }

    /**
     * This is an operation that constructs the string representation of the tree
     * @requires file == null /\ file.isDirectory()
     * @effects <pre>
     *   if stylize==false
     *     return buildTree(file).toString()
     *   else
     *     return buildTree(file).toString(true)
     * </pre>
     */
    public static String displayFiles(File file, boolean stylize) {
        return buildTree(file).toString(stylize);
    }
}
