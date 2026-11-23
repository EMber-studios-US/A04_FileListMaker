import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class FileListMaker {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ArrayList<String> list = new ArrayList<>();

        boolean done = false;
        boolean needsToBeSaved = false;
        String currentFileName = "";

        while (!done) {
            printList(list);

            String choice = SafeInput.getRegExString(in,
                    "Menu: Add (a)  Delete (d)  Insert (i)  Print (p)  Open (o)  Save (s)  Quit (q)",
                    "[AaDdIiPpOoSsQq]");

            choice = choice.toUpperCase();

            if (choice.equals("A")) {
                addItem(in, list);
                needsToBeSaved = true;

            } else if (choice.equals("D")) {
                if (list.size() > 0) {
                    deleteItem(in, list);
                    needsToBeSaved = true;
                } else {
                    System.out.println("The list is empty. Nothing to delete.");
                }

            } else if (choice.equals("I")) {
                insertItem(in, list);
                needsToBeSaved = true;

            } else if (choice.equals("P")) {
                printList(list);

            } else if (choice.equals("O")) {
                if (needsToBeSaved) {
                    boolean discard = SafeInput.getYNConfirm(in, "You have unsaved changes. Open a new file anyway?");
                    if (!discard) {
                        continue;
                    }
                }

                String fileName = SafeInput.getNonZeroLenString(in, "Enter the file name to open (e.g., list.txt)");
                if (!fileName.endsWith(".txt")) {
                    fileName = fileName + ".txt";
                }

                try {
                    loadListFile(fileName, list);
                    currentFileName = fileName;
                    needsToBeSaved = false;
                    System.out.println("Loaded " + fileName + " successfully!");
                } catch (IOException e) {
                    System.out.println("Error: Could not open the file. Make sure it exists.");
                }

            } else if (choice.equals("S")) {
                String fileName = "";

                if (currentFileName.equals("")) {
                    fileName = SafeInput.getNonZeroLenString(in, "Enter a file name to save as (e.g., mylist.txt)");
                } else {
                    boolean overwrite = SafeInput.getYNConfirm(in, "Save over " + currentFileName + "?");
                    if (overwrite) {
                        fileName = currentFileName;
                    } else {
                        fileName = SafeInput.getNonZeroLenString(in, "Enter a new file name");
                    }
                }

                if (!fileName.endsWith(".txt")) {
                    fileName = fileName + ".txt";
                }

                try {
                    saveListFile(fileName, list);
                    currentFileName = fileName;
                    needsToBeSaved = false;
                    System.out.println("Saved to " + fileName + " successfully!");
                } catch (IOException e) {
                    System.out.println("Error: Could not save the file.");
                }

            } else if (choice.equals("Q")) {
                if (needsToBeSaved) {
                    boolean saveFirst = SafeInput.getYNConfirm(in, "You have unsaved changes. Save before quitting?");
                    if (saveFirst) {
                        String fileName = currentFileName;
                        if (fileName.equals("")) {
                            fileName = SafeInput.getNonZeroLenString(in, "Enter a file name to save as");
                        }
                        if (!fileName.endsWith(".txt")) {
                            fileName = fileName + ".txt";
                        }

                        try {
                            saveListFile(fileName, list);
                            System.out.println("Saved!");
                        } catch (IOException e) {
                            System.out.println("Error saving file.");
                        }
                    }
                }

                done = SafeInput.getYNConfirm(in, "Are you sure you want to quit?");
            }
        }

        System.out.println("Program ended.");
    }

    public static void addItem(Scanner in, ArrayList<String> list) {
        String item = SafeInput.getNonZeroLenString(in, "Enter item to add");
        list.add(item);
    }

    public static void deleteItem(Scanner in, ArrayList<String> list) {
        int itemNumber = SafeInput.getRangedInt(in, "Enter item number to delete", 1, list.size());
        list.remove(itemNumber - 1);
    }

    public static void insertItem(Scanner in, ArrayList<String> list) {
        int position = 1;
        if (list.size() > 0) {
            position = SafeInput.getRangedInt(in, "Enter position to insert at", 1, list.size() + 1);
        }
        String item = SafeInput.getNonZeroLenString(in, "Enter item to insert");
        list.add(position - 1, item);
    }

    public static void printList(ArrayList<String> list) {
        System.out.println("\n CURRENT LIST ");
        if (list.size() == 0) {
            System.out.println("List is empty.");
        } else {
            for (int i = 0; i < list.size(); i++) {
                System.out.println((i + 1) + ". " + list.get(i));
            }
        }
    }

    public static void loadListFile(String fileName, ArrayList<String> list) throws IOException {
        Path file = Paths.get(fileName);
        list.clear();

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        }
    }

    public static void saveListFile(String fileName, ArrayList<String> list) throws IOException {
        Path file = Paths.get(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (int i = 0; i < list.size(); i++) {
                writer.write(list.get(i));
                writer.newLine();
            }
        }
    }
}