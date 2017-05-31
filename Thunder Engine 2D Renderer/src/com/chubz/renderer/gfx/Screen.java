package com.chubz.renderer.gfx;

import java.util.Random;

public class Screen {
    private static final int MAP_WIDTH = 0x40;
    private static final int MAP_WIDTH_MASK = MAP_WIDTH - 0x1;

    public int[] tiles = new int[MAP_WIDTH * MAP_WIDTH];
    public int[] colors = new int[MAP_WIDTH * MAP_WIDTH];
    public int[] databits = new int[MAP_WIDTH * MAP_WIDTH];
    public int xScroll, yScroll;

    public static final int BIT_MIRROR_X = 0x01;
    public static final int BIT_MIRROR_Y = 0x02;

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
            colors[i] = Color.get(0, 40, 50, 48);
            tiles[i] = 0;
            if (random.nextInt(40) == 0) {
                tiles[i] = 32;
                colors[i] = Color.get(111,40,222,333);
                databits[i] = random.nextInt(2);
            } else if (random.nextInt(40) == 0) {
                tiles[i] = 33;
                colors[i] = Color.get(20,40,30,550);
            } else {
                tiles[i] = random.nextInt(4);
                databits[i] = random.nextInt(4);
            }
        }

        Font.setMap("Hello this is TESTER 101241241241243668698785", this, 0, 0, Color.get(0, 555, 555, 555));
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
        boolean mirrorX = (bits & BIT_MIRROR_X) > 0;
        boolean mirrorY = (bits & BIT_MIRROR_Y) > 0;

        int xTile = tile % 32;
        int yTile = tile / 32;
        int toffs = (xTile << 3) + (yTile << 3) * sheet.width;

        for (int y = 0; y < 8; y++) {
            if (y + yp < 0 || y + yp >= h)
                continue;

            int ys = y;
            if (mirrorY)
                ys = 7 - y;

            for (int x = 0; x < 8; x++) {
                if (x + xp < 0 || x + xp >= w)
                    continue;

                int xs = x;
                if (mirrorX)
                    xs = 7 - x;

                int col = (color >> (sheet.pixels[xs + ys * sheet.width + toffs] << 3)) & 0xff;
                if (col < 255)
                    pixels[(x + xp) + (y + yp) * w] = col;
            }
        }
    }

    public void setTile(int x, int y, int tile, int color, int bits) {
        int tp = (x & MAP_WIDTH_MASK) + (y & MAP_WIDTH_MASK) * MAP_WIDTH;
        tiles[tp] = tile;
        colors[tp] = color;
        databits[tp] = bits;
    }

    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 255;
        }
    }
}
