package com.boneless;

import com.boneless.util.ButtonIcon;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.text.AbstractDocument;
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.white);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(),45,45);
    }

    public void addToScore(int scoreToAdd){
        score += scoreToAdd;
        scoreField.setFocusable(false);
        scoreField.setText(String.valueOf(score));
        scoreField.setFocusable(true);
    }

    private static class RoundedEtchedBorder extends AbstractBorder {
        private static final int DEFAULT_CORNER_RADIUS = 10;
        private final int cornerRadius;
        private final Color highlight;
        private final Color shadow;

        public RoundedEtchedBorder() {
            this(DEFAULT_CORNER_RADIUS, UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"));
        }

        public RoundedEtchedBorder(int cornerRadius, Color highlight, Color shadow) {
            this.cornerRadius = cornerRadius;
            this.highlight = highlight;
            this.shadow = shadow;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = cornerRadius * 2;

            g2.setColor(shadow);
            g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);

            g2.setColor(highlight);
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, arc, arc);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 8, 4, 8);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 8;
            insets.top = 4;
            insets.right = 8;
            insets.bottom = 4;
            return insets;
        }
    }
}