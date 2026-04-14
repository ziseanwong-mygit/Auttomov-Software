package main.java.backend.general.service;

import main.java.enums.TextFilePath;

import java.io.*;

public final class FileIOFunc {

    private FileIOFunc() {
        throw new UnsupportedOperationException("This class is a utility class, which cannot be instantiated.");
    }


    public static void writeTextFile(String[] dataList, TextFilePath filePath) {
        try (BufferedWriter bufferWrite = new BufferedWriter(new FileWriter(CUSTOMER_ACCOUNT_FILE_PATH));) {
            int listLength = dataList.length;
            String reftempSent = "";
            String tempSent = "";

            for (int i = 0; i < listLength - 1; i++) {
                tempSent = reftempSent + dataList[i] + ", ";
                reftempSent = tempSent;
            }

            String dataSentence = tempSent + dataList[listLength - 1];

            bufferWrite.write(dataSentence + "\n"); // write data into file

        } catch (IOException e) {
            // TODO: Need to adapt to Swing/AWT interface
            System.out.println("Oops! There was an error writing data to the specified file. Please try again!");
        }
    }









    private static final String CUSTOMER_ACCOUNT_FILE_PATH =
            "../APU-ASC-Group-AT/src/main/java/textfiles/customer-accounts.txt";
    private static final String STAFF_ACCOUNT_FILE_PATH =
            "../APU-ASC-Group-AT/src/main/java/textfiles/staff-accounts.txt";

}
