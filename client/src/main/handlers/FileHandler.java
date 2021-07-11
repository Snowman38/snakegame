package handlers;

import interfaces.PathConstants;
import logging.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Handles read and write operations to the data file
 */
public class FileHandler implements PathConstants {

    public static final Path filePath = Path.of(DATA_PATH);

    static File myObj;

    /**
     * Creates the data file if it does not exist
     */
    public static void createFile() {

        try {
            myObj = new File(DATA_PATH);
            if (myObj.createNewFile()) {
                Logger.debug("File created: " + myObj.getName());
                WriteToFile();
            } else {
                Logger.debug("Storage file already exists, FileHandler will not create a new file");
            }
        } catch (IOException e) {
            Logger.error("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Writes default values to the data file
     */
    public static void WriteToFile() {

        try {
            FileWriter myWriter = new FileWriter(DATA_PATH);
            if (getFileContent().length() == 0) {
                myWriter.write("FFFFFF 1200 50 50 False 99");
                myWriter.close();
            }
            Logger.debug("Successfully wrote to the file.");
        } catch (IOException e) {
            Logger.error("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Reads data from the data file
     *
     * @return the data string
     */
    public static String FileReader() {

        try {
            File myObj = new File(DATA_PATH);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                return data;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            Logger.error("An error occurred.");
            e.printStackTrace();
        }
        return "Didn't work!";
    }

    /**
     * Purchases item at index itemIndex with price price
     *
     * @param itemIndex The index of the item to purchase
     * @param price     The price of the item to purchase
     * @throws IOException if the data file fails to read
     */
    public static void purchaseItem(int itemIndex, int price) throws IOException {

        if (getFileContent().charAt(itemIndex) == 'F' && canBuy(price)) {
            StringBuilder sb = new StringBuilder(getFileContent());
            sb.setCharAt(itemIndex, 'T');
            String str = sb.toString();
            String[] full = str.split(" ");
            int balance = Integer.parseInt(full[1]);
            balance = balance - price;
            full[1] = String.valueOf(balance);
            str = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];

            Files.writeString(filePath, str);
            Logger.debug("You have successfully purchased");
        } else if (!canBuy(price)) {
            Logger.debug("You don't have enough money!");
        } else {
            Logger.debug("You already have this!");
        }
    }

    /**
     * Updates the balance by the supplied increment
     *
     * @param increment The amount to increment the balance
     */
    public static void updateBalance(int increment) {

        try {
            String str = getFileContent();
            String[] full = str.split(" ");
            int balance = Integer.parseInt(full[1]);
            balance = balance + increment;
            full[1] = String.valueOf(balance);
            str = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];

            Files.writeString(filePath, str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param musicVolume  The music volume to save
     * @param effectVolume The effect volume to save
     */
    public static void saveAudioSettings(double musicVolume, double effectVolume) {

        try {
            String[] full = Files.readString(filePath).split(" ");
            full[2] = String.valueOf(musicVolume);
            full[3] = String.valueOf(effectVolume);
            String writeString = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];

            Files.writeString(filePath, writeString);
            Logger.debug("Your volume has been saved");
        } catch (IOException e) {
            Logger.error("Error while trying to get apply musicVolume volume");
            e.printStackTrace();
        }
    }

    /**
     * Saves the mute status to the data file
     *
     * @param muted the mute status
     */
    public static void saveMuteStatus(Boolean muted) {

        try {
            String[] full = Files.readString(filePath).split(" ");
            if (!muted) {
                full[4] = "False";
            } else {
                full[4] = "True";
            }
            String writeString = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];

            Files.writeString(filePath, writeString);
            Logger.debug("Mute is now " + full[4]);
        } catch (IOException e) {
            Logger.error("Error occured in saveMuteStatus()");
            e.printStackTrace();
        }
    }

    /**
     * @return the music balance saved in the data file
     */
    public static int getBalance() {

        try {
            String full = Files.readString(filePath);
            String[] splitted = full.split(" ");
            return Integer.parseInt(splitted[1]);
        } catch (IOException e) {
            Logger.error("Error while trying to get balance");
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retrieves the mute status, as a string, from the data file
     *
     * @return the mute status in String form
     */
    public static String getMuteStatus() {

        try {
            String[] splitted = Files.readString(filePath).split(" ");
            return splitted[4];
        } catch (IOException e) {
            Logger.error("Error while trying to get mute status");
            e.printStackTrace();
        }
        return "True";
    }

    /**
     * Retrieves the music volume from the data file
     *
     * @return the current music volume
     */
    public static double getMusicVolume() {

        try {
            String[] splitted = Files.readString(filePath).split(" ");
            if (splitted[4].equals("False")) {
                return Double.parseDouble(splitted[2]);
            }
        } catch (IOException e) {
            Logger.error("Error while trying to get music volume");
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retrieves the effect volume from the data file
     *
     * @return the current effect volume
     */
    public static double getEffectVolume() {

        try {
            String[] splitted = Files.readString(filePath).split(" ");
            if (splitted[4].equals("False")) {
                return Double.parseDouble(splitted[3]);
            }

        } catch (IOException e) {
            Logger.error("Error while trying to get effect volume()");
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param price the price of the item
     * @return whether a player can afford this item
     */
    public static boolean canBuy(int price) {

        return getBalance() >= price;

    }

    /**
     * Returns the file content of the data file
     *
     * @return a string representing the text data in the file
     * @throws IOException if getting file content encounters an error
     */
    public static String getFileContent() throws IOException {

        String actual = Files.readString(filePath);
        return actual;
    }

}