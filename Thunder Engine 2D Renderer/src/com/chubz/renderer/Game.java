package com.chubz.renderer;

import com.chubz.renderer.gfx.Screen;
import com.chubz.renderer.gfx.Spritesheet;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

public class Game extends Canvas implements Runnable {
    public static final int WIDTH = 0xf0;
    public static final int HEIGHT = WIDTH / 16 * 9;
    public static final int SCALE = 3;
    public static final String NAME = "Untitled Game";

    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int pixels[] = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    private boolean running = false;
    private Screen screen;
    private InputHandler input = new InputHandler(this);

    private int[] colors1 = new int[512];
    private int[] colors2 = new int[512];

    public void start() {
        new Thread(this, "Game method").start();
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void init() {
        int pp = 0;
        for (int r = 0; r < 6; r++) {
            for (int g = 0; g < 6; g++) {
                for (int b = 0; b < 6; b++) {
                    int rr = (r * 255 / 5);
                    int gg = (g * 255 / 5);
                    int bb = (b * 255 / 5);
                    int mid = (rr * 30 + gg * 59 + bb * 11) / 100;

                    int r1 = ((rr + mid) >> 1) * 200 / 255 + 10;
                    int g1 = ((gg + mid) >> 1) * 200 / 255 + 10;
                    int b1 = ((bb + mid) >> 1) * 200 / 255 + 15;
                    colors1[pp] = r1 << 16 | g1 << 8 | b1;

                    int r2 = ((rr + mid) >> 1) * 200 / 255 + 45;
                    int g2 = ((gg + mid) >> 1) * 200 / 255 + 45;
                    int b2 = ((bb + mid) >> 1) * 200 / 255 + 55;
                    colors2[pp++] = r2 << 16 | g2 << 8 | b2;
                }
            }
        }

        try {
            screen = new Screen(WIDTH, HEIGHT, new Spritesheet(ImageIO.read(Game.class.getResourceAsStream("/spritesheet.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long last = System.nanoTime();
        int frames = 0;
        int ticks = 0;
        double nsPerTick = 1000000000 / 60;
        double unprocessed = 0;
        long lastTimer1 = System.currentTimeMillis();
        boolean shouldRender;

        init();

        while (running) {
            long now = System.nanoTime();
            unprocessed += (now - last) / nsPerTick;
            last = now;
            shouldRender = true;

            while (unprocessed >= 1) {
                unprocessed -= 1;
                tick();
                ticks++;
            }

            if (shouldRender) {
                render();
                frames++;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (System.currentTimeMillis() - lastTimer1 > 1000) {
                lastTimer1 += 1000;
                System.out.println(frames + " fps, " + ticks + " ticks");
                frames = 0;
                ticks = 0;
            }
        }
    }

    public void tick() {
        if (input.up)
            screen.yScroll -= 1;

        if (input.down)
            screen.yScroll += 1;

        if (input.left)
            screen.xScroll -= 1;

        if (input.right)
            screen.xScroll += 1;
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        screen.renderBackground();

        for (int y = 0; y < screen.h; y++) {
            for (int x = 0; x < screen.w; x++) {
                pixels[x + y * WIDTH] = colors2[screen.pixels[x + y * screen.w]];
            }
        }

        screen.clear();

        for (int y = 0; y < screen.h; y++) {
            for (int x = 0; x < screen.w; x++) {
                int cc = screen.pixels[x + y * screen.w];
                if (cc < 0xff)
                    pixels[x + y * WIDTH] = colors2[cc];
            }
        }

        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

        JFrame frame = new JFrame(NAME);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(game);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.start();
    }
}
