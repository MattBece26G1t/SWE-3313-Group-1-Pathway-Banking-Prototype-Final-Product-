/*
 * FileHandler is the low-level file utility used by DataManager.
 *
 * This class does not know anything about users, accounts, transactions, etc.
 * Its only job is to deal with plain text files:
 * - read every line from a file
 * - overwrite a file with a new list of lines
 * - check if a file exists
 * - create the file if needed
 *
 * So basically, DataManager decides what data should be saved,
 * and FileHandler is the class that actually talks to the file system.
 */

package org.example.pathwayver1;

import java.io.*;
import java.util.ArrayList;

public class FileHandler {

    // Path to the file this handler is working with
    private String filePath;

    // Stores the file path so the same handler object can keep using it.
    public FileHandler(String filePath) {
        this.filePath = filePath;
    }

    /*
     * Reads the whole file line by line and returns the contents as an ArrayList.
     *
     * If the file does not exist yet, this just returns an empty list instead
     * of crashing. That makes the rest of the save/load system easier to manage.
     */
    public ArrayList<String> readLines() {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines; // If the file is missing, just return an empty list

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            // Keep reading until there are no more lines left
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /*
     * Rewrites the file using the lines passed in.
     * Important detail:
     * this method overwrites the whole file, it does not append.
     * That matches how DataManager works, because DataManager usually rebuilds
     * a full updated version of the file first, then writes that full version out.
     */
    public void writeLines(ArrayList<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // keeps each entry on its own line
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Simple helper to check whether the target file already exists.
     * DataManager uses this during startup so it can make missing files first.
     */
    public boolean fileExists() {
        return new File(filePath).exists();
    }

    /*
     * Creates the file if it does not already exist.
     * This is mostly used when the program starts for the first time
     * and the data folder/files are not there yet.
     */
    public void createFile() {
        try {
            new File(filePath).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}