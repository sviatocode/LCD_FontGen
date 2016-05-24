/*
 * Copyright © 2016 Sviatoslav Semchyshyn
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class CharIcon implements Icon {

    private int w;
    private int h;
    private int m;

    private boolean[][] pixels;

    public CharIcon(int w, int h, int m) {
        this.w = w;
        this.h = h;
        this.m = m;
        pixels = new boolean[w][h];
    }

    public void setPixel(int x, int y, boolean state) {
        pixels[x][y] = state;
    }

    public boolean getPixel(int x, int y) {
        return pixels[x][y];
    }

    @Override
    public int getIconHeight() {
        return (h * m);
    }

    @Override
    public int getIconWidth() {
        return (w * m);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        for (int xi = 0; xi < w; xi++) {
            for (int yi = 0; yi < h; yi++) {
                if (pixels[xi][yi]) g.setColor(Color.blue);
                else g.setColor(Color.black);
                g.fillRect(x + xi * m, y + yi * m, m, m);
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JFrame frame = new JFrame("CharIcon class test");
                    CharIcon icon = new CharIcon(10, 8, 10);
                    JLabel label = new JLabel();
                    icon.setPixel(0, 0, true);
                    icon.setPixel(0, 1, true);
                    icon.setPixel(0, 2, true);
                    icon.setPixel(0, 4, true);
                    icon.setPixel(1, 1, true);
                    icon.setPixel(2, 2, true);
                    icon.setPixel(3, 3, true);
                    label.setIcon(icon);
                    frame.add(label);
                    frame.pack();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
