package org.lisasp.swing.filechooser.jfx;

import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

class IconConverter {

    public Optional<Image[]> getIcons(Window window) {
        if (window == null) {
            return Optional.empty();
        }
        List<java.awt.Image> awtIcons = getMostParent(window).getIconImages();
        if (awtIcons == null || awtIcons.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(convert(awtIcons));
    }

    private Window getMostParent(Window parent) {
        if (parent == null) {
            return null;
        }
        while (parent.getOwner() != null) {
            parent = parent.getOwner();
        }
        return parent;
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    private static BufferedImage toBufferedImage(java.awt.Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private Image[] convert(List<java.awt.Image> iconImages) {
        return iconImages.stream().map(i -> i instanceof BufferedImage ? (BufferedImage) i : toBufferedImage(i))
                .map(i -> convertToFxImage(i)).toArray(Image[]::new);
    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }
}
