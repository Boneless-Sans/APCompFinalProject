package com.boneless;

import com.boneless.util.GeneralUtils;
import com.boneless.util.JsonFile;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.boneless.GameBoard.mainColor;
import static com.boneless.Main.*;
import static com.boneless.util.GeneralUtils.*;

public class JCard extends JPanel {
    public boolean isActive = false;
    private JLabel questionLabel;
    private JLabel answerLabel;
    private JLabel questionQuestion;
    private JLabel answerAnswer;
    private JLabel faggot;
    private boolean hasFaded = false;
    private boolean hasFadedIn = true;

    public JCard(String question, String answer) {
        setLayout(null);

        questionLabel = new JLabel("Question: ");
        questionLabel.setForeground(GeneralUtils.parseColor(JsonFile.read(fileName, "data", "font_color")));
        questionLabel.setOpaque(false);

        questionQuestion = new JLabel(question);
        questionQuestion.setForeground(GeneralUtils.parseColor(JsonFile.read(fileName, "data", "font_color")));
        questionQuestion.setOpaque(false);

        answerLabel = new JLabel("Answer: ");
        answerLabel.setForeground(GeneralUtils.parseColorFade(JsonFile.read(fileName, "data", "font_color"), 0));
        answerLabel.setOpaque(false);

        answerAnswer = new JLabel(answer);
        answerAnswer.setForeground(GeneralUtils.parseColorFade(JsonFile.read(fileName, "data", "font_color"), 0));
        answerAnswer.setOpaque(false);

        faggot = new JLabel("you're a faggot :)");
        faggot.setForeground(GeneralUtils.parseColorFade(JsonFile.read(fileName, "data", "font_color"), 0));
        faggot.setOpaque(false);


        add(questionLabel);
        add(questionQuestion);

        add(answerLabel);
        add(answerAnswer);

        add(faggot);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerLabels();
            }
        });

        setupMouseListeners();
        setUpCharacters();
    }

    private void centerLabels() {
        int sizeX = 400;
        int sizeY = 200;
        int x = (getWidth() - sizeX) / 2;
        int x2 = getWidth() / 2;
        int x3 =  ((getWidth() - sizeX) / 2) / (getWidth() / 2);
        int x4 =  (getWidth() / 2) - ((getWidth() - sizeX) / 2);
        int yQuestion = (getHeight() - sizeY) / 2;
        int yAnswer = yQuestion + sizeY;

        questionLabel.setBounds(x4, yQuestion, sizeX, sizeY);
        questionQuestion.setBounds(x4, yQuestion + 30, sizeX, sizeY);

        answerLabel.setBounds(x, yQuestion + 20, sizeX, sizeY);
        answerAnswer.setBounds(x, yQuestion, sizeX, sizeY);

        faggot.setBounds(x, yQuestion + 40, sizeX, sizeY);


        revalidate();
        repaint();
    }

    public void advance() {
        moveQuestion();
    }

    private void setUpCharacters() {
        questionLabel.setFont(GeneralUtils.generateFont(30));
        answerLabel.setFont(GeneralUtils.generateFont(30));
        faggot.setFont(GeneralUtils.generateFont(10));
        setBackground(mainColor);
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!hasFaded) {
                    moveQuestion();
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private void moveQuestion() {
        if (hasFaded) {
            return;
        }
        hasFaded = true;

        int sizeX = 400;
        int sizeY = 200;
        int x = (getWidth() - sizeX) / 2;
        int yQuestion = (getHeight() - sizeY) / 2;
        int targetY = 50; // Target Y position for question label

        Timer q = new Timer(50, null);
        q.addActionListener(new ActionListener() {
            private int currentY = yQuestion;

            @Override
            public void actionPerformed(ActionEvent e) {
                currentY -= 3; // Adjust this value to control the speed of movement
                if (currentY <= targetY) {
                    currentY = targetY;
                    q.stop();
                    fadeInAnswerAndQuestion();
                }
                questionLabel.setBounds(x, currentY, sizeX, sizeY);
                questionQuestion.setBounds(x, currentY + 30, sizeX, sizeY);
                revalidate();
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
                answerLabel.setForeground(GeneralUtils.parseColorFade(JsonFile.read(fileName, "data","font_color"),(int)(opacity2 * 255)));
                faggot.setForeground(GeneralUtils.parseColorFade(JsonFile.read(fileName, "data","font_color"),(int)(opacity2 * 255)));
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
