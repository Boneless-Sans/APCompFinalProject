package com.boneless;

import com.boneless.util.ButtonIcon;
import com.boneless.util.JsonFile;
import com.boneless.util.ScrollGridPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.boneless.Main.*;
import static com.boneless.util.GeneralUtils.*;

public class MainMenu extends ScrollGridPanel {
    private final JFrame parent;
    public boolean menuIsActive;
    private final ArrayList<JButton> buttonsList = new ArrayList<>();
    private final JLabel currentFile;
    public boolean hasNotBanged = true;
    private final String[] dropDownList = {
            "1 Team", "2 Teams", "3 Teams", "4 Teams", "5 Teams", "6 Teams", "7 Teams", "8 Teams", "9 Teams", "10 Teams", "Other..."
    };

    public MainMenu(JFrame parent){
        this.parent = parent;
        menuIsActive = true;
        setLayout(new BorderLayout());

        //title text, pretty self-explanatory
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Jeopardy!");
        title.setFont(generateFont(150));
        title.setForeground(Color.white);

        //title.add()
        titlePanel.add(title, gbc);

        //button setup
        JPanel menuParentPanel = new JPanel(new GridBagLayout());
        menuParentPanel.setOpaque(false);

        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.setOpaque(false);
        buttonsPanel.setPreferredSize(new Dimension(180,250));

        buttonsPanel.add(new MenuButton("Start Game", 0));
        buttonsPanel.add(new MenuButton("Choose Board File", 1));
        buttonsPanel.add(new MenuButton("Board Creator", 2));
        buttonsPanel.add(new MenuButton("Settings", 3));
        buttonsPanel.add(new MenuButton("Exit", 4));

        add(titlePanel, BorderLayout.NORTH);

        menuParentPanel.add(buttonsPanel, gbc);

        add(menuParentPanel, BorderLayout.CENTER);

        //file select text
        currentFile = new JLabel(fileName == null ? "No Board Selected" : "Current Board: " + fileName);
        currentFile.setFont(generateFont(15));
        currentFile.setForeground(Color.white);

        JPanel filePanel = new JPanel(new FlowLayout());
        filePanel.setOpaque(false);
        filePanel.add(currentFile);

        add(filePanel, BorderLayout.SOUTH);
    }

    private void startGameUI(){
        Color color = parseColor(JsonFile.read(fileName, "data","global_color"));
        JPanel teamChoosePanel = new JPanel(new GridBagLayout()){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                //draw background
                g2d.setPaint(new GradientPaint(0,0,color,getWidth(),getHeight(),adjustColor(color)));
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 0,0));
            }
        };

        //main body
        JPanel contentPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contentPane.setPreferredSize(new Dimension(320,200));
        contentPane.setBackground(Color.white);

        JLabel numTeamText = new JLabel("Number of Teams");
        numTeamText.setFont(generateFont(30));

        JComboBox<String> dropDown = new JComboBox<>(dropDownList);
        dropDown.setPreferredSize(new Dimension(200,40));
        dropDown.setFont(generateFont(20));
        dropDown.setFocusable(false);
        dropDown.addActionListener(e -> {
            if(Objects.requireNonNull(dropDown.getSelectedItem()).toString().toLowerCase().contains("other")){
                contentPane.add(new JLabel("Other Team"));
            } else {
                contentPane.add(new JLabel("Current Team"));
            }

            contentPane.revalidate();
            contentPane.repaint();
        });

        int gapSize = 30; //a wierd font could case misalignment
        contentPane.add(createGap(gapSize, null));
        contentPane.add(numTeamText);
        contentPane.add(createGap(gapSize, null));
        contentPane.add(createGap(gapSize, null));
        contentPane.add(dropDown);
        contentPane.add(createGap(gapSize, null));

        int buttonSize = 60;

        ButtonIcon exitButton = new ButtonIcon(buttonSize, ButtonIcon.BACK, ButtonIcon.RED);
        exitButton.addActionListener(a -> {
            timer.start();
            changeCurrentPanel(mainMenu, teamChoosePanel, false);
        });

        ButtonIcon soundCheck = new ButtonIcon(buttonSize,false);
        soundCheck.addActionListener(a -> {
            soundCheck.toggleIcon();
            playAudio = soundCheck.isChecked();
        });

        ButtonIcon startGame = new ButtonIcon(buttonSize, ButtonIcon.START, ButtonIcon.GREEN);

        startGame.addActionListener(a -> {
            changeCurrentPanel(gameBoard = new GameBoard(dropDown.getSelectedIndex() + 1, parent), teamChoosePanel, true);
            hasNotBanged = true;
        });

        contentPane.add(createTeamChooserButton(exitButton, "Exit"));
        contentPane.add(createTeamChooserButton(soundCheck, "Play Audio"));
        contentPane.add(createTeamChooserButton(startGame, "Start"));
        teamChoosePanel.add(contentPane, gbc);
        changeCurrentPanel(teamChoosePanel, this, true);
    }

    private JPanel createTeamChooserButton(ButtonIcon icon, String text){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(80,120));

        panel.add(icon);
        panel.add(new JLabel(text));

        return panel;
    }

    public void changeFileName(String newFile){
        if(newFile == null) return;

        fileName = newFile;
        currentFile.setText("Current Board: " + fileName);
        buttonsList.get(0).setEnabled(!fileName.isEmpty());
    }

    private ActionListener listener(int UUID){
        return e -> {
            menuIsActive = false;
            timer.stop();
            switch (UUID){
                case 0: { //start
                    startGameUI();
                    break;
                }
                case 1: { //board file
                    timer.start();
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileFilter(new FileNameExtensionFilter("Json File", "json"));

                    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        changeFileName(String.valueOf(file));
                        //changeColor(parseColor(JsonFile.read(fileName, "data", "global_color")));
                        renderIcon(128);
                    }

                    break;
                }
                case 2: { //board creator
                    changeCurrentPanel(boardFactory = new BoardFactory(parent, fileName), this, true);
                    break;
                }
                case 3: { //settings
                    changeCurrentPanel(settings = new Settings(parent), this, true);
                    break;
                }
                case 4: { //exit
                    System.exit(0);
                    break;
                }
            }
        };
    }

    private class MenuButton extends JButton{
        public MenuButton(String text, int UUID){
            setFocusable(false);
            setFont(generateFont(15));
            try {
                if (UUID == 0 && fileName.isEmpty()) { //disable start button if there is no current board file
                    setEnabled(false);
                }
            } catch (NullPointerException ignore){
                setEnabled(false);
            }
            buttonsList.add(this);

            setText(text);
            addActionListener(listener(UUID));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension size = super.getPreferredSize();
            size.width = 160;
            size.height = 45;
            return size;
        }

        @Override
        protected void paintComponent(Graphics g) {

            if (!isEnabled()) {
                g.setColor(Color.lightGray);
                g.setFont(getFont());
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2 - 4;
                g.drawString(getText(), x, y);
            }

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            int arcSize = 45;
            g2d.setColor(getBackground());
            Shape backgroundShape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arcSize, arcSize);
            g2d.fill(backgroundShape);

            // Text
            if(isEnabled()) {
                g2d.setColor(Color.black);
            } else {
                g2d.setColor(fileName != null ? parseColor(JsonFile.read(fileName, "data", "disabled_button_color")) : Color.lightGray);
            }

            Font font = getFont();
            FontMetrics metrics = g2d.getFontMetrics(font);
            int x = (getWidth() - metrics.stringWidth(getText())) / 2;
            int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            g2d.setFont(font);

            g2d.drawString(getText(), x, y);

            g2d.dispose();
        }

        @Override protected void paintBorder(Graphics g) {}
    }
}