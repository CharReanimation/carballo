package com.alonsoruibal.chess.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ImageLoader {
    public static final BufferedImage imgWhite;
    public static final BufferedImage imgBlack;

    static {
        imgWhite = new BufferedImage(75, 75, BufferedImage.TYPE_4BYTE_ABGR);
        imgBlack = new BufferedImage(75, 75, BufferedImage.TYPE_4BYTE_ABGR);
    }

    public static void loadImages(URL url) {
        ImageIcon icon1 = new ImageIcon(url);
        imgWhite.getGraphics().drawImage(icon1.getImage(), -75, 0, null);
        imgBlack.getGraphics().drawImage(icon1.getImage(), 0, 0, null);
    }
}
