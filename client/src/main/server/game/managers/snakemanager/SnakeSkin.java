package server.game.managers.snakemanager;

import javafx.scene.image.Image;

/**
 * A containing class to hold images for the different snake skins used in the game
 */
public class SnakeSkin {

    private final Image headSkin;
    private final Image bodySkin;

    /**
     * Constructs a SnakeSkin object, using supplied URL's
     *
     * @param headSkinURL Head skin URL
     * @param bodySkinURL Body skin URL
     * @throws SnakeImageLoadException If an image failed to load from a URL
     */
    public SnakeSkin(String headSkinURL, String bodySkinURL) throws SnakeImageLoadException {

        this.headSkin = new Image(headSkinURL);
        if (this.headSkin.isError()) {
            throw new SnakeImageLoadException("Failed to load head skin file from " + headSkinURL);
        }

        this.bodySkin = new Image(bodySkinURL);
        if (this.bodySkin.isError()) {
            throw new SnakeImageLoadException("Failed to load body skin file from " + bodySkinURL);
        }
    }

    public Image getHeadImage() {

        return this.headSkin;
    }

    public Image getBodyImage() {

        return this.bodySkin;
    }

}
