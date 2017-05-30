package com.chubz.renderer.gfx;

import java.util.Random;

public class Screen {
    private static final int MAP_WIDTH = 0x40;
    private static final int MAP_WIDTH_MASK = MAP_WIDTH - 0x1;

    public int[] tiles = new int[MAP_WIDTH * MAP_WIDTH];
    public int[] colors = new int[MAP_WIDTH * MAP_WIDTH];
    public int[] databits = new int[MAP_WIDTH * MAP_WIDTH];
    public int xScroll, yScroll;

    public final int w, h;
    public int[] pixels;
    private Spritesheet sheet;

    public Screen(int w, int h, Spritesheet sheet) {
        this.w = w;
        this.h = h;
        this.sheet = sheet;

        pixels = new int[w * h];

        Random random = new Random();
        for (int i = 0; i < MAP_WIDTH * MAP_WIDTH; i++) {
            colors[i] = Color.get(random.nextInt(511), random.nextInt(511), random.nextInt(511), random.nextInt(511));
            tiles[i] = 0;
        }
    }

    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 255;
        }
    }

    public void renderBackground() {
        for (int yt = yScroll >> 3; yt <= (yScroll + h) >> 3; yt++) {
            int yp = (yt << 3) - yScroll;
            for (int xt = xScroll >> 3; xt <= (xScroll + w) >> 3; xt++) {
                int xp = (xt << 3) - xScroll;
                int ti = (xt & (MAP_WIDTH_MASK)) + (yt & (MAP_WIDTH_MASK)) * MAP_WIDTH;
                render(xp, yp, tiles[ti], colors[ti], databits[ti]);
            }
        }
    }

    public void render(int xp, int yp, int tile, int color, int bits) {
        int xTile = tile % 32;
        int yTile = tile / 32;
        int toffs = (xTile << 3) + (yTile << 3) * sheet.width;

        for (int y = 0; y < 8; y++) {
            if (y + yp < 0 || y + yp >= h)
                continue;

            for (int x = 0; x < 8; x++) {
                if (x + xp < 0 || x + xp >= w)
                    continue;

                int col = (color >> (sheet.pixels[x + y * sheet.width + toffs] << 3)) & 0xff;
                if (col < 255)
                    pixels[(x + xp) + (y + yp) * w] = col;
            }
        }
    }
}
