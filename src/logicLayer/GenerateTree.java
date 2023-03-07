package logicLayer;

import logicLayer.tree.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Phan Quang Tuan
 * @version 1.7
 * @Overview this class constructs and returns tree
 * @jdk_version_requires >= 1.8
 */
public abstract class GenerateTree {
    /**
     * Warning: This method is deprecated since v1.7, please use <code><Strong>buildTreeAsync()</Strong></code>
     * instead.
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
    @Deprecated
    private static Tree<File> buildTree(File directory) {
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
     * Warning: This method is deprecated since v1.7, please use <code><Strong>displayFilesAsync()</Strong></code>
     * instead.<br/> This is an operation that constructs the string representation of the tree
     * @requires file == null /\ file.isDirectory()
     * @effects <pre>
     *   if stylize==false
     *     return buildTree(file).toString()
     *   else
     *     return buildTree(file).toString(true)
     *   at the end: calculate the measurements
     * </pre>
     */
    @Deprecated
    public static String displayFiles(File file, boolean stylize) {
        try {
            return buildTree(file).toString(stylize);
        } finally {
            measurement();
        }
    }

    private static void measurement() {
        System.out.println("\n-------------------------------------------------------");
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();   // Get current size of heap in bytes
        System.out.println("Performance during executing the algorithm:");
        System.out.println("Available Processors: " + processors);
        System.out.println("Max Available Memory: " + maxMemory / (Math.pow(1024, 2)) + " MB");
        System.out.println("Free Memory: " + freeMemory / (Math.pow(1024, 2)) + " MB");
        System.out.println("Current Allocated Memory: " + totalMemory / (Math.pow(1024, 2)) + " MB");
        System.out.println("Max Threads: " + Thread.getAllStackTraces().size());
        System.out.println("-------------------------------------------------------\n");
    }

    // way 5: Side-effect CompletableFuture
    private final static Map<CompletableFuture<Void>, Tree<File>> treeMap = new HashMap<>();    // pairs of promises - trees

    /**
     * An asynchronous method for adding files, folders and its sub-contents to the provided tree.
     * @requires tree != null
     * @modifies tree, treeMap
     * @effects <pre>
     *    1. for all File f in tree.root.listFiles
     *       if f is dir
     *          - initialize new Tree t
     *          - add f to t
     *          - make a promise p = buildTreeAsync(t)
     *          - map p with t;     i.e. In treeMap, make a pair of promise - tree
     *       else
     *          add f to tree
     *
     *    2. await for all promises that have been made in this method call.
     *    3. for each promise has been made, get t from map of { p : t }, then add t to tree
     * </pre>
     */
    private static CompletableFuture<Void> buildTreeAsync(Tree<File> tree) {
        return CompletableFuture.supplyAsync(() -> {
            File file = tree.getRoot();
            File[] files = file.listFiles();
            if (files != null) {
                List<CompletableFuture<Void>> childFutures = new ArrayList<>();
                for (File subfile : files) {
                    if (subfile.isDirectory()) {
                        Tree<File> childNode = new Tree<>();
                        childNode.add(subfile);
                        CompletableFuture<Void> childFuture = buildTreeAsync(childNode);
                        childFutures.add(childFuture);
                        treeMap.put(childFuture, childNode);
                    } else {
                        tree.add(subfile);
                    }
                }
                // .allOf is await for the completion of multiple promises, while .join() is await for a single promise
                // await == pause coroutine == block current thread
                CompletableFuture.allOf(childFutures.toArray(new CompletableFuture[0])).join();
                childFutures.forEach(t -> tree.addAll(treeMap.get(t)));
            }
            return null;
        });
    }

    /**
     * An asynchronous method for adding files, folders and its sub-contents to the provided tree. Any async event,
     * which comes from this method's supplier, will be handled in the given ExecutorService instance.
     * @param executor the specified <code><strong>ExecutorService</strong></code> to handle the events of async
     *                 supplier.
     * @requires tree != null /\ executor != null
     * @modifies tree, treeMap
     * @effects <pre>
     *    1. for all File f in tree.root.listFiles
     *       if f is dir
     *          - initialize new Tree t
     *          - add f to t
     *          - make a promise p = buildTreeAsync(t)
     *          - map p with t;     i.e. In treeMap, make a pair of promise - tree
     *       else
     *          add f to tree
     *
     *    2. await for all promises that have been made in this method call.
     *    3. for each promise has been made, get t from map of { p : t }, then add t to tree
     * </pre>
     */
    private static CompletableFuture<Void> buildTreeAsync(Tree<File> tree, ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            File file = tree.getRoot();
            File[] files = file.listFiles();
            if (files != null) {
                List<CompletableFuture<Void>> childFutures = new ArrayList<>();
                for (File subfile : files) {
                    if (subfile.isDirectory()) {
                        Tree<File> childNode = new Tree<>();
                        childNode.add(subfile);
                        CompletableFuture<Void> childFuture = buildTreeAsync(childNode, executor);
                        treeMap.put(childFuture, childNode);
                        childFutures.add(childFuture);
                    } else {
                        tree.add(subfile);
                    }
                }
                // .allOf is await for the completion of multiple promises, while .join() is await for a single promise
                // await == pause coroutine == block current thread
                CompletableFuture.allOf(childFutures.toArray(new CompletableFuture[0])).join();
                childFutures.forEach(t -> tree.addAll(treeMap.get(t)));
            }
            return null;
        }, executor);
    }

    /**
     * This is an operation that constructs the string representation of the tree using asynchronous steps of building
     * tree.
     * @requires file == null /\ file.isDirectory()
     * @modifies treeMap
     * @effects <pre>
     *   - initialize a new tree t
     *   - build tree asynchronously by calling buildTreeAsync(tree)
     *      -> if exception: build tree asynchronously again, given a custom pool (expected large size)
     *   - at the end: calculate the measurements
     *   - return t.toString(stylize)
     * </pre>
     */
    public static String displayFilesAsync(File file, boolean stylize) {
        treeMap.clear();
        Tree<File> tree = new Tree<>();
        tree.add(file);
        try {
            buildTreeAsync(tree).get();     // await
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.out.println("attempt to use larger number of threads...");
            try {
                buildTreeAsync(tree, new ForkJoinPool(20)).get();       // await
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        } finally {
            measurement();
        }
        return tree.toString(stylize);
    }
}
