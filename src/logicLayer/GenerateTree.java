package logicLayer;

import logicLayer.tree.*;

import java.io.File;


/**
 * @Overview this class constructs and returns logicLayer.tree
 * @version 1.1
 * @author Phan Quang Tuan
 */
public abstract class GenerateTree {
    /**
     * @requires directory != null
     * @effects <pre>
     *  if directory == null \/ directory.isDirectory()==false
     *    return empty logicLayer.tree
     *  else
     *    add directory to logicLayer.tree
     *    for all File f in directory.listFiles
     *      add f to logicLayer.tree
     *      if f is dir
     *        addAll buildTree(f) to logicLayer.tree
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
     * This is an operation that constructs the string representation of the logicLayer.tree
     * @requires file == null /\ file.isDirectory()
     * @effects <pre>
     *   if stylize==false
     *     return buildTree(file).toString()
     *   else
     *     return buildTree(file).toString(true)
     * </pre>
     *
     */
    public static String displayFiles(File file, boolean stylize) {
        return buildTree(file).toString(stylize);
    }
}
