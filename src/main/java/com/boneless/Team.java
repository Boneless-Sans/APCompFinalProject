package com.boneless;

import com.boneless.util.ButtonIcon;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

import static com.boneless.Main.*;
import static com.boneless.util.GeneralUtils.*;

public class Team extends JPanel {
    public static int teamCount;
    private int score;
    private final JTextField scoreField;

    public Team(){
        teamCount++;
        score = 0;

        //setup UI
        setBackground(Color.white);
        setPreferredSize(new Dimension(150,500)); //Panel height controller
        setBorder(null);

        //name field
        JTextField teamName = new JTextField("Team " + teamCount);
        teamName.setPreferredSize(new Dimension(125,25));
        teamName.setBackground(null);
        teamName.setBorder(new RoundedEtchedBorder());
        teamName.setHorizontalAlignment(JTextField.CENTER);
        teamName.setFont(generateFont(15));

        //div line
        JPanel line = new JPanel();
        line.setBackground(Color.black);
        line.setPreferredSize(new Dimension(130,1));

        //score field
        scoreField = new JTextField(String.valueOf(this.score));
        scoreField.setFont(generateFont(15));
        scoreField.setHorizontalAlignment(JTextField.CENTER);
        scoreField.setBorder(new RoundedEtchedBorder());
        scoreField.setBackground(null);
        scoreField.setPreferredSize(new Dimension(125,25));
        ((AbstractDocument) scoreField.getDocument()).setDocumentFilter(new NumberFilter());

        add(teamName);
        add(line);
        add(scoreField);
        ButtonIcon plusScore = new ButtonIcon(45, ButtonIcon.PLUS, ButtonIcon.GREEN);
        plusScore.addActionListener(e -> addToScore(gameBoard.scoreToAdd));
        ButtonIcon minusScore = new ButtonIcon(45, ButtonIcon.MINUS, ButtonIcon.RED);
        minusScore.addActionListener(e -> addToScore(-gameBoard.scoreToAdd));
        add(plusScore);
        add(minusScore);
    }

    public void addToScore(int scoreToAdd){
        score += scoreToAdd;
        scoreField.setFocusable(false);
        scoreField.setText(String.valueOf(score));
        scoreField.setFocusable(true);
    }

    private static class NumberFilter extends DocumentFilter{
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) {
                return;
            }
            if (isNumeric(string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                return;
            }
            if (isNumeric(text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);
        }

        private boolean isNumeric(String text) {
            for (char c : text.toCharArray()) {
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
    }
}