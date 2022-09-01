package client;

import logicLayer.GenerateTree;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Overview A class to input and run the program
 * @attributes <pre>
 * fileCount        int
 * folderCount      int
 * stylize          boolean
 * </pre>
 * @version 1.2
 * @author Phan Quang Tuan
 */
public class Client {
    private int fileCount;
    private int folderCount;
    private boolean stylize;

    private String getDir() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter folder's path: ");
        String dirName = sc.nextLine();

        Pattern p = Pattern.compile("^(.+)( -s)$");
        Matcher m = p.matcher(dirName);
        if (m.find()) {
            stylize = true;
            return m.group(1);
        } else {
            stylize = false;
            return dirName;
        }
    }

    /**
     * @requires files!=null
     * @modifies fileCount, folderCount
     * @effects <pre>
     *   for all file f in files
     *     if f is File
     *       fileCount = fileCount +1
     *     else
     *       if f is Directory
     *         folderCount = folderCount +1
     *         invoke count(f[])
     *       else
     *         do nothing
     * </pre>
     */
    private void count(File[] files) {
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isFile()) {
                fileCount++;
            } else if (f.isDirectory()) {
                folderCount++;
                count(f.listFiles());
            }
        }
    }

    /**
     * @effects <pre>
     *   set fileCount=0
     *   set folderCount=0
     * </pre>
     */
    private void reset() {
        fileCount = 0;
        folderCount = 0;
    }

    /**
     * a method to call logicLayer layer and count number of file(s) if folderPath is valid.
     */
    private void display(String folderPath) {
        File f = new File(folderPath);

        if (f.isDirectory()) {
            long startTime, stopTime, elapsedTime;
            startTime = System.currentTimeMillis();
            System.out.println(GenerateTree.displayFiles(f, stylize));
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            System.out.println("current folder: " + folderPath);
            System.out.println("Algorithm took: " + elapsedTime + " milliseconds to finish");

            reset();
            count(f.listFiles());
            System.out.println("IN TREE (except root): total files: " + fileCount + ", total folders: " + folderCount);
        } else {
            reset();
            System.out.println("\u001B[31m" + "Folder not found!" + "\u001B[0m");
        }
    }

    public void run() {
        String folder = getDir();
        long startTime, stopTime, elapsedTime;
        startTime = System.currentTimeMillis();

        display(folder);

        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("The program took: " + elapsedTime + " milliseconds to finish");

        // Get current size of heap in bytes
        long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("This costs: " + heapSize / (Math.pow(1024, 2)) + " MB");
    }

    // C:\Users\Tuan\Desktop\ts
    public static void main(String[] args) {
        Client c = new Client();
        boolean isContinue = true;
        while (isContinue) {
            c.run();
            System.out.print("Do you want to continue? (y/n):");
            Scanner sc = new Scanner(System.in);
            String answer = sc.nextLine();
            isContinue = !answer.equalsIgnoreCase("n") && !answer.equalsIgnoreCase("no");
            if (isContinue) {
                System.out.println("-------------\n");
            }
        }
    }
}
