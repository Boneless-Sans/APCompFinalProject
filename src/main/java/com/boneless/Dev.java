package com.boneless;

import com.boneless.util.GeneralUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class Dev extends JFrame {
    public static void main(String[] args){
        new Dev();
    }
    public Dev(){
        setSize(500,500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Dev");
        setLayout(null);
        init();
        setVisible(true);
    }
    private void init() {
        JButton button = new JButton("OK");

        int size = 50;
        button.setBounds((getWidth() / 2) - (size / 2),(getWidth() / 2) - (size / 2),size,size);

        add(button);
    }
}
