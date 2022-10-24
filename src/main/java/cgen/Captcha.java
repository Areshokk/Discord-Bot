package cgen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Random;

public class Captcha {
    /**
     * Generates a image with random text( CAPTCHA )
     */
    public File generateCaptcha() {
        final int width = 300;
        final int height = 57;

        String rand = randomStringGen(5);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0, width, height);

        g2d.setColor(Color.BLACK);
        g2d.setFont(g2d.getFont().deriveFont(30f));
        g2d.drawString(rand, 100,30);

        g2d.dispose();

        try {
            ImageIO.write(bufferedImage, "png", new File(rand + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new File(rand + ".png");
    }

    /**
     * Generates a random String that is `len` length long.
     * @param len       Int Value
     * @return String   Random String
     */
    private String randomStringGen(int len) {
        StringBuilder str = new StringBuilder();
        final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        final String NUMBER = "0123456789";
        final String RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;

        HashMap<Integer, Character> numHash = new HashMap<>();

        for(int x = 0; x < RANDOM_STRING.length(); x++) {
            numHash.put(x, RANDOM_STRING.charAt(x));
        }


        for(int x = 0; x < len; x++) {
            Random rand = new Random();
            int ranNum = rand.nextInt(63);

            str.append(numHash.get(ranNum));
        }
        return str.toString();
    }
}
