package com.chubz.renderer.gfx;

public class Color {
    public static int get(int alpha, int red, int green, int blue) {
        return (get(alpha) << 24 | get(red) << 16 | get(green) << 8 | get(blue));
    }

    private static int get(int d) {
        if (d < 0)
            return 255;

        int r = d / 100 % 10;
        int g = d / 10 % 10;
        int b = d % 10;

        return r * 36 + g * 6 + b;
    }
}
