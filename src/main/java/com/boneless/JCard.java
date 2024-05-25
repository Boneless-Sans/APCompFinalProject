package com.boneless;

import com.boneless.util.GeneralUtils;
import com.boneless.util.JsonFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.boneless.Main.*;
import static com.boneless.util.GeneralUtils.*;

public class JCard extends JPanel {
    public boolean isActive = false;
    private JLabel questionLabel;
    private JLabel answerLabel;
    private boolean hasFaded = false;
    private boolean hasFadedIn = true;

    public static Color mainColor;

    public JCard(int score, String question, String answer, Color mainColor) {
        setLayout(null);

        JCard.mainColor = mainColor;


        questionLabel = new JLabel("Question: " + question, JLabel.CENTER);
        questionLabel.setForeground(GeneralUtils.parseColor(JsonFile.read(fileName, "data","font_color")));
        questionLabel.setOpaque(false);

        answerLabel = new JLabel("Answer: " + answer, JLabel.CENTER);
        answerLabel.setForeground(GeneralUtils.parseColorFade(JsonFile.read(fileName, "data","font_color"), 0));
        answerLabel.setOpaque(false);

        add(questionLabel);
        add(answerLabel);

        setupMouseListeners();
        setColors();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Center labels
        int sizeX = 400;
        int sizeY = 200;
        int x = (getWidth() - sizeX) / 2;
        int y = (getHeight() - sizeY) / 2;
        int yQuestion = (getHeight() - sizeY) / 2;
        int yAnswer = yQuestion + sizeY;
        questionLabel.setBounds(x, yQuestion, sizeX, sizeY);
        answerLabel.setBounds(x, yAnswer, sizeX, sizeY);
    }

    public void advance() {
        fadeQuestion();
    }

    private void setColors() {
        questionLabel.setFont(GeneralUtils.generateFont(15));
        answerLabel.setFont(GeneralUtils.generateFont(15));
        setBackground(mainColor);
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!hasFaded) {
                    fadeQuestion();
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private void fadeQuestion() {
        int r = mainColor.getRed();
        int g = mainColor.getGreen();
        int b = mainColor.getBlue();

        if (hasFaded) {
            return;
        }
        hasFaded = true;

        Timer q = new Timer(50, null);
        q.addActionListener(new ActionListener() {
            private float opacity = 1.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    q.stop();
                    fadeInAnswerAndQuestion();
                }
                questionLabel.setForeground(GeneralUtils.parseColorFade(JsonFile.read(fileName, "data","font_color"),(int)(opacity * 255)));
                repaint();
            }
        });
        q.start();
    }

    private void fadeInAnswerAndQuestion() {
        if (!hasFadedIn) {
            return;
        }
        hasFadedIn = false;

        Timer j = new Timer(50, null);
        j.addActionListener(new ActionListener() {
            private float opacity2 = 0.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity2 += 0.05f;
                if (opacity2 >= 1.0f) {
                    opacity2 = 1.0f;
                    j.stop();
                }
                questionLabel.setForeground(GeneralUtils.parseColorFade(JsonFile.read(fileName, "data","font_color"),(int)(opacity2 * 255)));
                answerLabel.setForeground(GeneralUtils.parseColorFade(JsonFile.read(fileName, "data","font_color"),(int)(opacity2 * 255)));
                repaint();
            }
        });
        j.start();
    }

    public void exit() {
        GameBoard.HeaderPanel.leftText.setText("Exit");
        changeCurrentPanel(GAME_BOARD.boardPanel, this);
        GAME_BOARD.jCardIsActive = false;
        GAME_BOARD.GameIsActive = true;
        hasFaded = false;
    }
}
