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
     * This is an operation that constructs the string representation of the tree
     * @requires file == null /\ file.isDirectory()
     * @effects <pre>
     *   if stylize==false
     *     return buildTree(file).toString()
     *   else
     *     return buildTree(file).toString(true)
     *   at the end: calculate the measurements
     * </pre>
     */
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

    // way 1: CompletableFuture
//    private static CompletableFuture<Tree<File>> buildTreeAsync(File directory) {
//        return CompletableFuture.supplyAsync(() -> {
//            Tree<File> tree = new Tree<>();
//            if (directory != null && directory.isDirectory()) {
//                tree.add(directory);
//                File[] files = directory.listFiles();
//                if (files != null) {
//                    for (File f : files) {
//                        tree.add(f);
//                        tree.addAll(buildTreeAsync(f).join());
//                    }
//                }
//            }
//            return tree;
//        });
//    }
//
//    public static String displayFilesAsync(File file, boolean stylize) {
//        CompletableFuture<Tree<File>> treeFuture = buildTreeAsync(file);
//        treeFuture.thenAccept(tree -> {
//            // Do something with the tree, such as print its size
//            System.out.println("haha");
//        });
//        // Wait for the tree to be built
//        try {
//            return treeFuture.get().toString(stylize);
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        } finally {
//            measurement();
//        }
//        return null;
//    }

    static {
    }

    // way 2: Enhance CompletableFuture
//    private static CompletableFuture<Tree<File>> buildTreeAsync(File directory) {
//        return CompletableFuture.supplyAsync(() -> {
//            Tree<File> tree = new Tree<>();
//            if (directory != null && directory.isDirectory()) {
//                tree.add(directory);
//                File[] files = directory.listFiles();
//                if (files != null) {
//                    CompletableFuture<Tree<File>>[] childFutures = new CompletableFuture[files.length];
//                    for (int i = 0; i < files.length; i++) {
//                        int finalI = i;
//                        childFutures[i] = CompletableFuture.supplyAsync(() -> {
//                            Tree<File> childTree = new Tree<>();
//                            childTree.add(files[finalI]);
//                            if (files[finalI].isDirectory()) {
//                                childTree.addAll(buildTreeAsync(files[finalI]).join());
//                            }
//                            return childTree;
//                        });
//                    }
//
//                    for (CompletableFuture<Tree<File>> childFuture : childFutures) {
//                        try {
//                            tree.addAll(childFuture.get());
//                        } catch (InterruptedException | ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            return tree;
//        });
//    }
//
//    public static String displayFilesAsync(File file, boolean stylize) {
//        CompletableFuture<Tree<File>> treeFuture = buildTreeAsync(file);
//        treeFuture.thenAccept(tree -> {
//            // Do something with the tree, such as print its size
//            System.out.println("haha");
//        });
//        // Wait for the tree to be built
//        try {
//            return treeFuture.get().toString(stylize);
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        } finally {
//            measurement();
//        }
//        return null;
//    }

    static {
    }

    // way 3: Executor
//    private static ExecutorService executor;
//
//    private static void reset() {
//        if (executor == null || executor.isShutdown()) {
//            executor = Executors.newFixedThreadPool(Integer.MAX_VALUE);
//        }
//    }
//
//    private static CompletableFuture<Tree<File>> buildTreeAsync(File directory) {
//        return CompletableFuture.supplyAsync(() -> {
//            Tree<File> tree = new Tree<>();
//            if (directory != null && directory.isDirectory()) {
//                tree.add(directory);
//                File[] files = directory.listFiles();
//                if (files != null) {
//                    CompletableFuture<Tree<File>>[] childFutures = new CompletableFuture[files.length];
//                    for (int i = 0; i < files.length; i++) {
//                        int finalI = i;
//                        childFutures[i] = CompletableFuture.supplyAsync(() -> {
//                            Tree<File> childTree = new Tree<>();
//                            childTree.add(files[finalI]);
//                            if (files[finalI].isDirectory()) {
//                                childTree.addAll(buildTreeAsync(files[finalI]).join());
//                            }
//                            return childTree;
//                        }, executor);
//                    }
//
//                    for (CompletableFuture<Tree<File>> childFuture : childFutures) {
//                        try {
//                            tree.addAll(childFuture.get());
//                        } catch (InterruptedException | ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//            return tree;
//        }, executor);
//    }
//
//    public static String displayFilesAsync(File file, boolean stylize) {
//        reset();
//        CompletableFuture<Tree<File>> treeFuture = buildTreeAsync(file);
//        treeFuture.thenAccept(tree -> {
//            // Do something with the tree, such as print its size
//            System.out.println("Total threads: " + ((ThreadPoolExecutor) executor).getLargestPoolSize());
//        });
//        // Wait for the tree to be built
//        try {
//            return treeFuture.get().toString(stylize);
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        } finally {
//            measurement();
//            executor.shutdown();
//        }
//        return null;
//    }

    static {

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
     *   - return t.toString(stylize?)
     *   - at the and: calculate the measurements
     * </pre>
     */
    public static String displayFilesAsync(File file, boolean stylize) {
        treeMap.clear();
        Tree<File> tree = new Tree<>();
        tree.add(file);
        try {
            buildTreeAsync(tree).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            System.out.println("attempt to use larger number of threads...");
            try {
                buildTreeAsync(tree, new ForkJoinPool(20)).get();
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        } finally {
            measurement();
        }
        return tree.toString(stylize);
    }
}
