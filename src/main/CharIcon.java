package main;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class CharIcon implements Icon {

    private int w;
    private int h;
    private int m;
    
    private boolean[][] pixels;
    
    public CharIcon(int w, int h, int m)
    {
        this.w = w;
        this.h = h;
        this.m = m;
        pixels = new boolean[w][h];
    }
    
    @Override
    public int getIconHeight() {
        return (h*m);
    }

    @Override
    public int getIconWidth() {
        return (w*m);
    }

    @Override
    public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

}
