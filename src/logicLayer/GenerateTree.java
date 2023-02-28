package logicLayer;

import logicLayer.tree.*;

import java.io.File;
import java.util.concurrent.*;

/**
 * @author Phan Quang Tuan
 * @version 1.6
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
        metric();
        return buildTree(file).toString(stylize);
    }

    private static void metric() {
        System.out.println("\n-------------------------------------------------------");
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();   // Get current size of heap in bytes
        System.out.println("Performance during executing Algorithm:");
        System.out.println("Available Processors: " + processors);
        System.out.println("Max available Memory: " + maxMemory / (Math.pow(1024, 2)) + " MB");
        System.out.println("Free Memory: " + freeMemory / (Math.pow(1024, 2)) + " MB");
        System.out.println("Current allocated Memory: " + totalMemory / (Math.pow(1024, 2)) + " MB");
        System.out.println("Max Threads: " + Thread.getAllStackTraces().size());
        System.out.println("-------------------------------------------------------\n");
    }

    // way 1: CompletableFuture
    public static CompletableFuture<Tree<File>> buildTreeAsync(File directory) {
        return CompletableFuture.supplyAsync(() -> {
            Tree<File> tree = new Tree<>();
            if (directory != null && directory.isDirectory()) {
                tree.add(directory);
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File f : files) {
                        tree.add(f);
                        tree.addAll(buildTreeAsync(f).join());
                    }
                }
            }
            return tree;
        });
    }

    public static String displayFilesAsync(File file, boolean stylize) {
        CompletableFuture<Tree<File>> treeFuture = buildTreeAsync(file);
        treeFuture.thenAccept(tree -> {
            // Do something with the tree, such as print its size
            System.out.println("haha");
        });
        // Wait for the tree to be built
        try {
            return treeFuture.get().toString(stylize);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            metric();
        }
        return null;
    }

    static {
    }

    // way 2: Enhance CompletableFuture
//    public static CompletableFuture<Tree<File>> buildTreeAsync(File directory) {
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
//            metric();
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
//    public static CompletableFuture<Tree<File>> buildTreeAsync(File directory) {
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
//            metric();
//            executor.shutdown();
//        }
//        return null;
//    }


}
