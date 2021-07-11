package handlers;

import interfaces.PathConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logging.Logger;
import server.game.managers.mapmanager.MapSkinManager;
import server.game.managers.snakemanager.SnakeSkinManager;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

/**
 * Class controlling shop rendering and behaviour
 */
public class Shop2 implements Initializable, PathConstants {

    final String price1 = "125";

    // Change prices and descriptions here!
    // Prices must be different else the code will crash!
    final String description1 = " A very graceful coloured skin reveal your friendliness, and also your kindness!";
    final String price2 = "175";
    final String description2 = " Red is the colour of extremes it represents both danger as well as love! Buy this and your enemy will not be able to read you!";
    final String price3 = "550";
    final String description3 = " A frightening yet powerful snake, this skin has a body made with dynamites, showing your explosive power!";
    final String price4 = "1000";
    final String description4 = " This map pack contains a rare gem, only the wealthiest can purchase this!";
    final String price5 = "300";
    final String description5 = " Purchase this map and you will get to eat some awesome magical mushrooms!";
    final String price6 = "450";
    final String description6 = " This purchase will give you some very nice desserts, try them now but don't get diabetes xD!";
    final String defaultSkinDescription = " A very cool green coloured skin for your snake, representing the colour of life!";
    final String defaultMapDescription = " In Greek mythology, it is said those who eat the Golden Apple are granted immortality";
    final SnakeSkinManager ssm = new SnakeSkinManager();
    final Image head0 = ssm.getSnakeSkin(0).getHeadImage();
    final Image body0 = ssm.getSnakeSkin(0).getBodyImage();
    final Image head1 = ssm.getSnakeSkin(1).getHeadImage();
    final Image body1 = ssm.getSnakeSkin(1).getBodyImage();
    final Image head2 = ssm.getSnakeSkin(2).getHeadImage();
    final Image body2 = ssm.getSnakeSkin(2).getBodyImage();
    //DEFAULT SKIN SET
    final Image head3 = ssm.getSnakeSkin(3).getHeadImage();
    final Image body3 = ssm.getSnakeSkin(3).getBodyImage();
    final MapSkinManager msm = new MapSkinManager();
    final Image background0 = msm.getMapSkin(0).getBackgroundSkin();
    final Image fruit0 = msm.getMapSkin(0).getFruitSkin();
    final Image wall0 = msm.getMapSkin(0).getWallSkin();
    final Image background1 = msm.getMapSkin(1).getBackgroundSkin();
    final Image fruit1 = msm.getMapSkin(1).getFruitSkin();
    final Image wall1 = msm.getMapSkin(1).getWallSkin();
    final Image background2 = msm.getMapSkin(2).getBackgroundSkin();
    final Image fruit2 = msm.getMapSkin(2).getFruitSkin();
    final Image wall2 = msm.getMapSkin(2).getWallSkin();
    //DEFAULT MAP SET
    final Image background3 = msm.getMapSkin(3).getBackgroundSkin();
    final Image fruit3 = msm.getMapSkin(3).getFruitSkin();
    final Image wall3 = msm.getMapSkin(3).getWallSkin();
    @FXML
    ImageView head;
    @FXML
    ImageView body;
    @FXML
    ImageView map1;
    @FXML
    ImageView map2;
    @FXML
    ImageView map3;
    @FXML
    Button myButton;
    @FXML
    Button defaultskin;
    @FXML
    Button defaultmap;
    @FXML
    Label description;
    @FXML
    Label price;
    @FXML
    Label notification;
    @FXML
    Label balance;
    @FXML
    Button owned;
    @FXML
    ImageView snakeImage;

    /**
     * Checks whether an item has been purchased
     *
     * @param a the index of the item
     * @return whether the item has been purchased
     * @throws IOException if there is an error reading the data file
     */
    private static boolean itemBought(int a) throws IOException {

        return FileHandler2.getFileContent().charAt(a - 1) == 'T';
    }

    private static int getSkinEquipped() {

        try {
            String[] splitted = Files.readString(FileHandler2.filePath).split(" ");
            if (splitted[5].charAt(0) == '0') {
                return 0;
            } else if (splitted[5].charAt(0) == '1') {
                return 1;
            } else if (splitted[5].charAt(0) == '2') {
                return 2;
            } else {
                return 3;
            }

        } catch (IOException e) {
            Logger.error("Error while trying to get skin val from file");
            e.printStackTrace();
        }
        return 3;
    }

    private static int getSkinEquipped1() {

        try {
            String[] splitted = Files.readString(FileHandler2.filePath).split(" ");
            if (splitted[5].charAt(0) == '1') {
                return 0;
            } else if (splitted[5].charAt(0) == '2') {
                return 1;
            } else if (splitted[5].charAt(0) == '3') {
                return 2;
            } else {
                return 3;
            }

        } catch (IOException e) {
            Logger.error("Error while trying to get skin val from file");
            e.printStackTrace();
        }
        return 3;
    }

    private static int getMapEquipped() {

        try {
            String[] splitted = Files.readString(FileHandler2.filePath).split(" ");
            if (splitted[5].charAt(1) == '4') {
                return 1;
            } else if (splitted[5].charAt(1) == '5') {
                return 2;
            } else if (splitted[5].charAt(1) == '6') {
                return 3;
            } else {
                return 4;
            }

        } catch (IOException e) {
            Logger.error("Error while trying to get map val from file");
            e.printStackTrace();
        }
        return 4;
    }

    private void itemChecker(String a, int b) throws NumberFormatException, IOException {
        //price   index
        if (price.getText().equals(a)) {
            if (FileHandler2.getFileContent().charAt(b) == 'T') {
                String[] full = Files.readString(FileHandler2.filePath).split(" ");
                String first = String.valueOf(b + 1);
                String second = full[5].substring(1);
                full[5] = first + second;
                String writeString = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];
                Files.writeString(FileHandler2.filePath, writeString);

                notification.setText("Skin " + getSkinEquipped() + " and Map " + noMap() + " has been selected");
                owned.setText("Skin Equipped");

            } else if (FileHandler2.getFileContent().charAt(b) == 'F' && FileHandler2.canBuy(Integer.parseInt(a))) {
                StringBuilder sb = new StringBuilder(FileHandler2.getFileContent());
                sb.setCharAt(b, 'T');
                String str = sb.toString();
                String[] full = str.split(" ");
                int balance = Integer.parseInt(full[1]);
                balance = balance - (Integer.parseInt(a));
                full[1] = String.valueOf(balance);
                str = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];

                Files.writeString(FileHandler2.filePath, str);
                notification.setText("You have successfully purchased Skin " + (b + 1) + " :D");
                owned.setText("Equip");
            } else if (!FileHandler2.canBuy(Integer.parseInt(a))) {
                notification.setText("You don't have enough money!");
            } else {
                notification.setText("You already have this!");
            }
        }
        balance.setText(Integer.toString(FileHandler2.getBalance()));
    }

    private void itemChecker1(String a, int b) throws NumberFormatException, IOException {
        //price   index
        if (price.getText().equals(a)) {
            if (FileHandler2.getFileContent().charAt(b) == 'T') {
                String[] full = Files.readString(FileHandler2.filePath).split(" ");
                String first = full[5].substring(0, 1);
                String second = String.valueOf(b + 1);
                full[5] = first + second;
                String writeString = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];
                Files.writeString(FileHandler2.filePath, writeString);

                notification.setText("Skin " + noSkin() + " and Map " + getMapEquipped() + " has been selected");

                owned.setText("Map Equipped");

            } else if (FileHandler2.getFileContent().charAt(b) == 'F' && FileHandler2.canBuy(Integer.parseInt(a))) {
                StringBuilder sb = new StringBuilder(FileHandler2.getFileContent());
                sb.setCharAt(b, 'T');
                String str = sb.toString();
                String[] full = str.split(" ");
                int balance = Integer.parseInt(full[1]);
                balance = balance - (Integer.parseInt(a));
                full[1] = String.valueOf(balance);
                str = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];

                Files.writeString(FileHandler2.filePath, str);
                notification.setText("You have successfully purchased Map " + (b + 1 - 3) + " :D");
                owned.setText("Equip");
            } else if (!FileHandler2.canBuy(Integer.parseInt(a))) {
                notification.setText("You don't have enough money!");
            } else {
                notification.setText("You already have this!");
            }
        }
        balance.setText(Integer.toString(FileHandler2.getBalance()));
    }

    private String noMap() {

        if (getMapEquipped() == 4) {
            return "DEFAULT";
        } else {
            return String.valueOf(getMapEquipped());
        }
    }

    private String noSkin() {

        if (getSkinEquipped1() == 3) {
            return "DEFAULT";
        } else {
            return String.valueOf(getSkinEquipped());
        }
    }

    /**
     * Overriden method initializing the shop balance
     *
     * @param arg0 unused
     * @param arg1 unused
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        balance.setText(Integer.toString(FileHandler2.getBalance()));
    }

    /**
     * Returns the main menu
     *
     * @param event The ActionEvent calling the function (From fxml)
     */
    public void getMain(ActionEvent event) {

        Util.getWindow(event, "MainMenu");
    }

    /**
     * Updates the balance text
     *
     * @param event The ActionEvent calling the function (From fxml)
     */
    public void getBalance(ActionEvent event) {

        balance.setText(Integer.toString(FileHandler2.getBalance()));

    }

    /**
     * Set's item 1 description in the shop menu
     *
     * @throws IOException if the data file cannot be read
     */
    public void itemOneDescription() throws IOException {

        head.setImage(head0);
        body.setImage(body0);
        map1.setImage(null);
        map2.setImage(null);
        map3.setImage(null);
        snakeImage.setImage(null);

        if (itemBought(1) && getSkinEquipped() == 1) {
            owned.setText("Skin Equipped");
        } else if (itemBought(1)) {
            owned.setText("Equip");
        } else {
            owned.setText("Purchase");
        }

        description.setText(description1);
        int one = Integer.parseInt(price1);
        price.setText(Integer.toString(one));
    }

    /**
     * Set's item 2 description in the shop menu
     *
     * @throws IOException if the data file cannot be read
     */
    public void itemTwoDescription() throws IOException {

        head.setImage(head1);
        body.setImage(body1);
        map1.setImage(null);
        map2.setImage(null);
        map3.setImage(null);
        snakeImage.setImage(null);

        if (itemBought(2) && getSkinEquipped() == 2) {
            owned.setText("Skin Equipped");
        } else if (itemBought(2)) {
            owned.setText("Equip");
        } else {
            owned.setText("Purchase");
        }

        description.setText(description2);
        int two = Integer.parseInt(price2);
        price.setText(Integer.toString(two));
    }

    /**
     * Set's item 3 description in the shop menu
     *
     * @throws IOException if the data file cannot be read
     */
    public void itemThreeDescription() throws IOException {

        head.setImage(head2);
        body.setImage(body2);
        map1.setImage(null);
        map2.setImage(null);
        map3.setImage(null);
        snakeImage.setImage(null);

        if (itemBought(3) && getSkinEquipped() == 3 && (ssm.getSelectedSkin() != 8)) {
            owned.setText("Skin Equipped");
        } else if (itemBought(3)) {
            owned.setText("Equip");
        } else {
            owned.setText("Purchase");
        }

        description.setText(description3);
        int three = Integer.parseInt(price3);
        price.setText(Integer.toString(three));
    }

    /**
     * Set's item 4 description in the shop menu
     *
     * @throws IOException if the data file cannot be read
     */
    public void itemFourDescription() throws IOException {

        map1.setImage(background0);
        map2.setImage(fruit0);
        map3.setImage(wall0);
        head.setImage(null);
        body.setImage(null);
        snakeImage.setImage(null);

        if (itemBought(4) && getMapEquipped() == 1) {
            owned.setText("Map Equipped");
        } else if (itemBought(4)) {
            owned.setText("Equip");
        } else {
            owned.setText("Purchase");
        }

        description.setText(description4);
        int four = Integer.parseInt(price4);
        price.setText(Integer.toString(four));

    }

    /**
     * Set's item 5 description in the shop menu
     *
     * @throws IOException if the data file cannot be read
     */
    public void itemFiveDescription() throws IOException {

        map1.setImage(background1);
        map2.setImage(fruit1);
        map3.setImage(wall1);
        head.setImage(null);
        body.setImage(null);
        snakeImage.setImage(null);

        if (itemBought(5) && getMapEquipped() == 2) {
            owned.setText("Map Equipped");
        } else if (itemBought(5)) {
            owned.setText("Equip");
        } else {
            owned.setText("Purchase");
        }

        description.setText(description5);
        int five = Integer.parseInt(price5);
        price.setText(Integer.toString(five));
    }

    /**
     * Set's item 6 description in the shop menu
     *
     * @throws IOException if the data file cannot be read
     */
    public void itemSixDescription() throws IOException {

        map1.setImage(background2);
        map2.setImage(fruit2);
        map3.setImage(wall2);
        head.setImage(null);
        body.setImage(null);
        snakeImage.setImage(null);

        if (itemBought(6) && getMapEquipped() == 3) {
            owned.setText("Map Equipped");
        } else if (itemBought(6)) {
            owned.setText("Equip");
        } else {
            owned.setText("Purchase");
        }

        description.setText(description6);
        int six = Integer.parseInt(price6);
        price.setText(Integer.toString(six));
    }

    /**
     * Loads the default skin
     *
     * @throws IOException if there is an error reading the data file
     */
    public void defaultSkin() throws IOException {

        String[] full = Files.readString(FileHandler2.filePath).split(" ");
        String first = "9";
        String second = full[5].substring(1, 2);
        full[5] = first + second;
        String str = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];
        Files.writeString(FileHandler2.filePath, str);
        owned.setText("Equipped");
        notification.setText(" You have chosen Skin DEFAULT and have Map " + noMap() + " equipped");
        price.setText("Free");
        description.setText(defaultSkinDescription);
        head.setImage(head3);
        body.setImage(body3);
        map1.setImage(null);
        map2.setImage(null);
        map3.setImage(null);
        snakeImage.setImage(null);

    }

    /**
     * Loads the default map
     *
     * @throws IOException if there is an error reading the data file
     */
    public void defaultMap() throws IOException {

        String[] full = Files.readString(FileHandler2.filePath).split(" ");
        String first = full[5].substring(0, 1);
        String second = "9";
        full[5] = first + second;
        String str = full[0] + " " + full[1] + " " + full[2] + " " + full[3] + " " + full[4] + " " + full[5];
        Files.writeString(FileHandler2.filePath, str);
        owned.setText("Equipped");
        notification.setText(" You have chosen Map DEFAULT and have Skin " + noSkin() + " equipped");
        price.setText("Free");
        description.setText(defaultMapDescription);
        map1.setImage(background3);
        map2.setImage(fruit3);
        map3.setImage(wall3);
        head.setImage(null);
        body.setImage(null);
        snakeImage.setImage(null);

    }

    /**
     * Initialises the shop items
     *
     * @param event The calling ActionEvent (Called by FXML)
     * @throws IOException if there is an error reading the data file
     */
    public void itemPurchaser(ActionEvent event) throws IOException {

        itemChecker(price1, 0);
        itemChecker(price2, 1);
        itemChecker(price3, 2);
        itemChecker1(price4, 3);
        itemChecker1(price5, 4);
        itemChecker1(price6, 5);
    }
}

