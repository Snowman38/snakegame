package server.game.managers.mapmanager;

import javafx.scene.image.Image;
import logging.Logger;

/**
 * A containing class to hold images for the different box skins used in the game
 */
public class MapSkin {

    private final Image backgroundSkin;
    private final Image wallSkin;
    private final Image foodSkin;
    private final Image coinSkin;
    private final Image freezeSkin;
    private final Image reverseSkin;
    private final Image skipSkin;
    private final Image speedSkin;
    private final Image mineSkin;

    /**
     * Constructs a MapSkin object, using supplied URL's
     *
     * @param backgroundSkinURL Background skin location
     * @param foodSkinURL       Food skin location
     * @param wallSkinURL       Wall skin location
     * @param coinSkinURL       Coin skin location
     * @param freezeSkinURL     Freeze skin location
     * @param reverseSkinURL    Reverse skin location
     * @param skipSkinURL       Skip skin location
     * @param speedSkinURL      Speed skin location
     * @param mineSkinURL       Mine skin location
     * @throws MapImageLoadException If an image failed to load from a URL
     */
    public MapSkin(String backgroundSkinURL, String foodSkinURL, String wallSkinURL, String coinSkinURL, String freezeSkinURL, String reverseSkinURL,
                   String skipSkinURL, String speedSkinURL, String mineSkinURL) throws MapImageLoadException {

        //  Logger.error();("Background skin is loading from url " + backgroundSkinURL);

        this.backgroundSkin = new Image(backgroundSkinURL);
        if (this.backgroundSkin.isError()) {
            Logger.error("BG Skin threw an error while creating an image:");
            Logger.error(this.backgroundSkin.errorProperty());
            throw new MapImageLoadException("Failed loading background skin from " + backgroundSkinURL);
        }

        //   Logger.error();("Food skin is loading from url " + foodSkinURL);

        this.foodSkin = new Image(foodSkinURL);
        if (this.foodSkin.isError()) {
            Logger.error("Food Skin threw an error while creating an image:");
            Logger.error(this.foodSkin.errorProperty());

            throw new MapImageLoadException("Failed loading food skin from " + foodSkinURL);
        }

        //   Logger.error();("Wall skin is loading from url " + wallSkinURL);

        this.wallSkin = new Image(wallSkinURL);
        if (this.wallSkin.isError()) {
            Logger.error("Wall Skin threw an error while creating an image:");
            Logger.error(this.wallSkin.errorProperty());

            throw new MapImageLoadException("Failed loading wall skin from " + wallSkinURL);
        }

        this.coinSkin = new Image(coinSkinURL);
        if (this.coinSkin.isError()) {
            Logger.error("PowerUp Skin threw an error while creating an image:");
            Logger.error(this.wallSkin.errorProperty());

            throw new MapImageLoadException("Failed loading powerUp skin from " + coinSkinURL);
        }

        this.freezeSkin = new Image(freezeSkinURL);
        if (this.freezeSkin.isError()) {
            Logger.error("PowerUp Skin threw an error while creating an image:");
            Logger.error(this.wallSkin.errorProperty());

            throw new MapImageLoadException("Failed loading powerUp skin from " + freezeSkinURL);
        }

        this.reverseSkin = new Image(reverseSkinURL);
        if (this.reverseSkin.isError()) {
            Logger.error("PowerUp Skin threw an error while creating an image:");
            Logger.error(this.wallSkin.errorProperty());

            throw new MapImageLoadException("Failed loading powerUp skin from " + reverseSkinURL);
        }

        this.skipSkin = new Image(skipSkinURL);
        if (this.skipSkin.isError()) {
            Logger.error("PowerUp Skin threw an error while creating an image:");
            Logger.error(this.wallSkin.errorProperty());

            throw new MapImageLoadException("Failed loading powerUp skin from " + skipSkinURL);
        }

        this.speedSkin = new Image(speedSkinURL);
        if (this.speedSkin.isError()) {
            Logger.error("PowerUp Skin threw an error while creating an image:");
            Logger.error(this.wallSkin.errorProperty());

            throw new MapImageLoadException("Failed loading powerUp skin from " + speedSkinURL);
        }

        this.mineSkin = new Image(mineSkinURL);
        if (this.mineSkin.isError()) {
            Logger.error("PowerUp Skin threw an error while creating an image:");
            Logger.error(this.wallSkin.errorProperty());

            throw new MapImageLoadException("Failed loading powerUp skin from " + mineSkinURL);
        }

    }

    public Image getBackgroundSkin() {

        return this.backgroundSkin;
    }

    public Image getWallSkin() {

        return this.wallSkin;
    }

    public Image getFruitSkin() {

        return this.foodSkin;
    }

    public Image getCoinSkin() {

        return this.coinSkin;
    }

    public Image getFreezeSkin() {

        return this.freezeSkin;
    }

    public Image getReverseSkin() {

        return this.reverseSkin;
    }

    public Image getSkipSkin() {

        return this.skipSkin;
    }

    public Image getSpeedSkin() {

        return this.speedSkin;
    }

    public Image getMineSkin() {

        return this.mineSkin;
    }

}
