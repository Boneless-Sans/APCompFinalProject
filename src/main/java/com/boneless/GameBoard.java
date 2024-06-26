package com.boneless;

import com.boneless.util.JPopUp;
import com.boneless.util.JRoundedButton;
import com.boneless.util.JsonFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import static com.boneless.GameBoard.HeaderPanel.*;
import static com.boneless.Main.*;
import static com.boneless.util.GeneralUtils.*;

public class GameBoard extends JPanel {
    public boolean GameIsActive;
    public boolean jCardIsActive = false;

    public static final Color mainColor = parseColor(JsonFile.read(fileName, "data", "global_color"));
    public static final Color accentColor = new Color(
            clamp(mainColor.getRed()   - 40),
            clamp(mainColor.getGreen() - 40),
            clamp(mainColor.getBlue()  - 40));
    public static final Color fontColor = parseColor(JsonFile.read(fileName, "data", "font_color"));

    public static int fontSize = 20;
    public int scoreToAdd = 0;
    private final int teamCount;

    public final JPanel boardPanel;
    private final JPopUp popup;

    private final ArrayList<JComponent> boardButtonList = new ArrayList<>();

    public GameBoard(int teamCount, Container parent){
        this.teamCount = teamCount;
        GameIsActive = true;
        mainMenu.menuIsActive = false;

        setLayout(null);

        JPanel masterPanel = new JPanel(new BorderLayout());
        masterPanel.setBackground(mainColor);
        masterPanel.setBounds(0,0,frameWidth, frameHeight);

        setBackground(mainColor);

        masterPanel.add(new HeaderPanel(), BorderLayout.NORTH);
        masterPanel.add(createTeamsPanel(), BorderLayout.SOUTH);
        masterPanel.add(boardPanel = mainBoard(), BorderLayout.CENTER);

        add(popup = new JPopUp(parent));
        add(masterPanel);

        revalidate();
        repaint();
    }

    // panel to contain the main board grid
    public JPanel mainBoard() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.black);

        int boardX = Integer.parseInt(JsonFile.read(fileName, "data", "categories"));
        int boardY = Integer.parseInt(JsonFile.read(fileName, "data", "rows")) + 1;
        panel.setLayout(new GridLayout(boardY, boardX, 0, 0));

        for (int i = 0; i < boardX; i++) {
            panel.add(createCatPanel(i));
        }

        //setup board
        for (int i = 0; i < boardY - 1; i++) {
            for (int j = 0; j < boardX; j++) {
                String scoreString = JsonFile.readWith3Keys(fileName, "board", "scores", "row_" + i);
                int score = Integer.parseInt(scoreString);
                String question = JsonFile.readWith3Keys(fileName, "board", "col_" + j, "question_" + i);
                String answer = JsonFile.readWith3Keys(fileName, "board", "col_" + j, "answer_" + i);

                BoardButton button = new BoardButton(score, question, answer, 20);
                button.setBackground(mainColor);
                button.setForeground(fontColor);
                button.setFont(generateFont(fontSize));

                boardButtonList.add(button);
                panel.add(button);
            }
        }
        return panel;
    }

    private JPanel createCatPanel(int index) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createBevelBorder(0));
        panel.setBackground(accentColor);

        JLabel label = new JLabel(JsonFile.readWith3Keys(fileName, "board", "categories", "cat_" + index));
        label.setFont(generateFont(fontSize));
        label.setForeground(fontColor);

        panel.add(label, gbc);
        return panel;
    }

    private JScrollPane createTeamsPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(accentColor);
        panel.setBorder(null);

        int teamPanelWidth = 150;
        int totalTeamsWidth = teamCount * teamPanelWidth;
        int availableWidth = getWidth();

        //gap size calc
        int dynamicGapSize = (availableWidth - totalTeamsWidth) / (teamCount + 1);
        if (dynamicGapSize < 0) {
            dynamicGapSize = 0;
        }

        for (int i = 0; i < teamCount; i++) {
            panel.add(createGap(dynamicGapSize, mainColor));
            panel.add(new Team());
        }
        panel.add(createGap(dynamicGapSize, mainColor));

        HiddenScroller pane = new HiddenScroller(panel, true);
        pane.setPreferredSize(new Dimension(getWidth(), 120));
        pane.setBackground(accentColor);
        pane.getHorizontalScrollBar().setUnitIncrement(15);

        return pane;
    }

    public void exit() {
        setButtonsEnabled(false);
        JRoundedButton exit = new JRoundedButton("Exit");
        exit.addActionListener(e -> {
            gameBoard.GameIsActive = false;
            mainMenu.menuIsActive = true;
            mainMenu.timer.start();
            Team.teamCount = 0;
            changeCurrentPanel(mainMenu, gameBoard, false);
        });

        JRoundedButton resume = new JRoundedButton("Resume");
        resume.addActionListener(e -> {
            popup.hidePopUp();
            setButtonsEnabled(true);
        });

        popup.showPopUp("Exit Confirmation", "Do you wish to leave?", null, JPopUp.MESSAGE, exit, resume);
    }

    private void setButtonsEnabled(boolean isEnabled){
        for(JComponent button : boardButtonList){
            button.setEnabled(isEnabled);
        }
    }

    public class HeaderPanel extends JPanel {
        public static JLabel leftText;
        public static JPanel rightPanel;

        public HeaderPanel() {
            setBackground(accentColor);
            setLayout(new GridLayout());

            leftText = new JLabel("Exit");
            leftText.setForeground(fontColor);
            leftText.setFont(generateFont(fontSize));

            JButton exitButton = createHeaderButton("exit", true);

            boardButtonList.add(exitButton);

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.setOpaque(false);
            leftPanel.add(exitButton);
            leftPanel.add(leftText);

            JLabel title = new JLabel(JsonFile.read(fileName, "data", "board_name"));
            title.setForeground(fontColor);
            title.setFont(generateFont(fontSize));

            JPanel titlePanel = new JPanel(new GridBagLayout());
            titlePanel.setOpaque(false);

            titlePanel.add(title, gbc);

            rightPanel = createRightPanel(true);

            add(leftPanel);
            add(titlePanel);
            add(rightPanel);
        }

        public static JPanel createRightPanel(boolean blank) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
            panel.setBackground(accentColor);

            if(!blank) {
                JLabel rightText = new JLabel("Reveal Correct Answer");
                rightText.setForeground(fontColor);
                rightText.setFont(generateFont(fontSize));

                panel.add(rightText);
                panel.add(createHeaderButton("continue", false));
            }
            return panel;
        }

        public static JButton createHeaderButton(String text, boolean isExit) {
            String rawKeyBind = JsonFile.read(settingsFile, "key_binds", text);
            String keyBind = rawKeyBind.substring(0, 1).toUpperCase() + rawKeyBind.substring(1);
            JButton button = new JButton(keyBind);
            button.setFocusable(false);
            button.setFont(generateFont(20));
            button.addActionListener(e -> {
                if (isExit) {
                    if (gameBoard.GameIsActive)
                        gameBoard.exit();
                    else
                        jCard.exit();
                } else {
                    jCard.moveQuestion();
                }
            });

            return button;
        }
    }

    private class BoardButton extends JButton {
        private final int score;
        private final String question;
        private final String answer;
        private final int arcSize;

        public BoardButton(int score, String question, String answer, int arcSize) {
            this.score = score;
            this.question = question;
            this.answer = answer;
            this.arcSize = arcSize;
            setText(String.valueOf(score));
            setBackground(mainColor);
            setFocusable(false);
            addActionListener(listener());
        }

        private ActionListener listener() {
            return e -> {
                leftText.setText("Back");

                rightPanel.removeAll();
                rightPanel.add(createRightPanel(false));
                rightPanel.revalidate();
                rightPanel.repaint();

                JPanel parentPanel = (JPanel) getParent();
                jCard = new JCard(question, answer, this);

                jCardIsActive = true;
                GameIsActive = false;
                scoreToAdd = score;
                setEnabled(false);
                changeCurrentPanel(jCard, parentPanel, true, 200);
            };
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            g2d.setColor(getBackground());
            Shape backgroundShape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arcSize, arcSize);
            g2d.fill(backgroundShape);

            // Text
            if(isEnabled()) {
                g2d.setColor(fontColor);
            } else {
                g2d.setColor(parseColor(JsonFile.read(fileName, "data", "disabled_button_color")));
            }
            Font font = getFont();
            FontMetrics metrics = g2d.getFontMetrics(font);
            int x = (getWidth() - metrics.stringWidth(getText())) / 2;
            int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            g2d.setFont(font);
            g2d.drawString(getText(), x, y);

            g2d.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            super.paintBorder(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Border
            g2d.setColor(Color.black);
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arcSize, arcSize);

            g2d.dispose();
        }
    }
}