package com.gitlab.aecsocket.minecommons.core;

import java.awt.image.BufferedImage;

/**
 * Provides an index for a foliage color map, using the default Minecraft client's algorithm.
 */
public record FoliageColors(int width, int height, int[] pixels) {
    /** The default color, if the index to be used is outside of the pixels array. */
    public static final int DEFAULT_COLOR = 0x00ff01;

    /**
     * Gets the pixel index of a temperature and rainfall value.
     * <p>
     * The actual color can be looked up using {@link #pixels}, or {@link #get(double, double)}.
     * @param temp The temperature.
     * @param rain The rainfall.
     * @return The index.
     */
    public int index(double temp, double rain) {
        temp = Numbers.clamp01(temp);
        rain = Numbers.clamp01(rain) * temp;
        int x = (int) ((1 - temp) * (width - 1));
        int y = (int) ((1 - rain) * (height - 1));
        return (y * width) + x;
    }

    /**
     * Gets the color of a temperature and rainfall value. Uses {@link #index(double, double)}
     * to get the index.
     * @param temp The temperature.
     * @param rain The rainfall.
     * @return The color.
     */
    public int get(double temp, double rain) {
        int index = pixels[index(temp, rain)];
        return index > pixels.length ? DEFAULT_COLOR : pixels[index];
    }

    /**
     * Loads an instance from an image.
     * @param image The image.
     * @return The instance.
     */
    public static FoliageColors load(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];

        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[i] = image.getRGB(x, y);
                ++i;
            }
        }
        return new FoliageColors(width, height, pixels);
    }
}
