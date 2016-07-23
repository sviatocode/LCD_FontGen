/*
 * Copyright © 2016 Sviatoslav Semchyshyn
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 */
package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JCheckBox;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FontGenWindow implements MouseListener, KeyListener {

    private JFrame frmLcdFontGenerator;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FontGenWindow window = new FontGenWindow();
                    window.frmLcdFontGenerator.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public FontGenWindow() {
        initialize();
    }

    private static final String tittle = "LCD Font generator v0.7";
    private static final int CHARS = 256;

    private int CHAR_WIDTH = 5;
    private int CHAR_HEIGHT = 8;
    private int PREVIEW_MAG = 5;
    private int EDIT_MAG = 50;
    private int curIndex = 0;
    private JSpinner characterWidthSpinner;
    private JSpinner characterHeightSpinner;
    private JSpinner previewMagSpinner;
    private JSpinner editMagSpinner;
    private JPanel charPreviewPanel;
    private JPanel editPanel;
    private CharIcon[] char_icon;
    private JLabel[] char_label;
    private CharIcon[][] edit_icon;
    private JLabel[][] edit_label;
    private JLabel cur_label;
    private JCheckBox chckbxGrid;

    private void changeProperties() {
        CHAR_WIDTH = (Integer) characterWidthSpinner.getValue();
        CHAR_HEIGHT = (Integer) characterHeightSpinner.getValue();
        PREVIEW_MAG = (Integer) previewMagSpinner.getValue();
        EDIT_MAG = (Integer) editMagSpinner.getValue();
        int TABLE_WIDTH = 16;
        int TABLE_HEIGHT = CHARS / TABLE_WIDTH;

        charPreviewPanel.removeAll();
        if (chckbxGrid.isSelected())
            charPreviewPanel.setLayout(new GridLayout(1 + TABLE_HEIGHT, 1 + TABLE_WIDTH, 1, 1));
        else charPreviewPanel.setLayout(new GridLayout(1 + TABLE_HEIGHT, 1 + TABLE_WIDTH, 0, 0));
        charPreviewPanel.add(new JLabel());

        CharIcon[] prev_icon = char_icon;
        char_icon = new CharIcon[CHARS];
        char_label = new JLabel[CHARS];
        for (int ix = 0; ix < TABLE_WIDTH; ix++) {
            JLabel label = new JLabel(Integer.toHexString(ix).toUpperCase() + "0");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            charPreviewPanel.add(label);
        }

        for (int iy = 0; iy < TABLE_HEIGHT; iy++) {
            JLabel label = new JLabel("0" + Integer.toHexString(iy).toUpperCase());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            charPreviewPanel.add(label);
            for (int ix = 0; ix < TABLE_WIDTH; ix++) {
                int i = iy + ix * TABLE_HEIGHT;
                char_label[i] = new JLabel();
                if (prev_icon != null) char_icon[i] = new CharIcon(CHAR_WIDTH, CHAR_HEIGHT, PREVIEW_MAG, prev_icon[i]);
                else char_icon[i] = new CharIcon(CHAR_WIDTH, CHAR_HEIGHT, PREVIEW_MAG);
                char_label[i].setIcon(char_icon[i]);
                char_label[i].setBorder(new LineBorder(Color.black, 2));
                char_label[i].addMouseListener(this);
                char_label[i].addKeyListener(this);
                charPreviewPanel.add(char_label[i]);
            }
        }

        editPanel.removeAll();
        editPanel.setLayout(new GridLayout(CHAR_HEIGHT, CHAR_WIDTH, 1, 1));
        edit_icon = new CharIcon[CHAR_WIDTH][CHAR_HEIGHT];
        edit_label = new JLabel[CHAR_WIDTH][CHAR_HEIGHT];

        for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
            for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                edit_label[ix][iy] = new JLabel();
                edit_icon[ix][iy] = new CharIcon(1, 1, EDIT_MAG);
                edit_label[ix][iy].setIcon(edit_icon[ix][iy]);
                edit_label[ix][iy].addMouseListener(this);
                editPanel.add(edit_label[ix][iy]);
            }
        }

        selectChar(curIndex);

        charPreviewPanel.updateUI();
    }

    private void selectChar(int i) {
        char_label[curIndex].setBorder(new LineBorder(Color.black, 2));
        char_label[curIndex].updateUI();
        curIndex = i;
        char_label[curIndex].setBorder(new LineBorder(Color.red, 2));
        char_label[curIndex].updateUI();

        for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
            for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                edit_icon[ix][iy].setPixel(0, 0, char_icon[curIndex].getPixel(ix, iy));
                edit_label[ix][iy].updateUI();
            }
        }

        cur_label.setText("'" + (char) i + "' : " + i + " : 0x" + Integer.toHexString(i));
    }

    private int mouse = MouseEvent.NOBUTTON;

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
        if (mouse == MouseEvent.NOBUTTON) return;

        for (int i = 0; i < CHARS; i++) {
            if (e.getSource() == char_label[i]) {
                selectChar(i);
                return;
            }
        }

        for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
            for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                if (e.getSource() == edit_label[ix][iy]) {
                    if (mouse == MouseEvent.BUTTON1) {
                        edit_icon[ix][iy].setPixel(0, 0, true);
                        edit_label[ix][iy].updateUI();
                        char_icon[curIndex].setPixel(ix, iy, true);
                        char_label[curIndex].updateUI();
                    } else if (mouse == MouseEvent.BUTTON3) {
                        edit_icon[ix][iy].setPixel(0, 0, false);
                        edit_label[ix][iy].updateUI();
                        char_icon[curIndex].setPixel(ix, iy, false);
                        char_label[curIndex].updateUI();
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        mouse = e.getButton();

        for (int i = 0; i < CHARS; i++) {
            if (e.getSource() == char_label[i]) {
                selectChar(i);
                return;
            }
        }

        for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
            for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                if (e.getSource() == edit_label[ix][iy]) {
                    if (mouse == MouseEvent.BUTTON1) {
                        edit_icon[ix][iy].setPixel(0, 0, true);
                        edit_label[ix][iy].updateUI();
                        char_icon[curIndex].setPixel(ix, iy, true);
                        char_label[curIndex].updateUI();
                    } else if (mouse == MouseEvent.BUTTON3) {
                        edit_icon[ix][iy].setPixel(0, 0, false);
                        edit_label[ix][iy].updateUI();
                        char_icon[curIndex].setPixel(ix, iy, false);
                        char_label[curIndex].updateUI();
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouse = MouseEvent.NOBUTTON;
    }

    private void save(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write(Integer.toString(CHAR_WIDTH));
            writer.newLine();
            writer.write(Integer.toString(CHAR_HEIGHT));
            writer.newLine();
            writer.write(Integer.toString(PREVIEW_MAG));
            writer.newLine();
            writer.write(Integer.toString(EDIT_MAG));
            writer.newLine();
            writer.write(Integer.toString(curIndex));
            writer.newLine();
            if (chckbxGrid.isSelected()) writer.write("1");
            else writer.write("0");
            writer.newLine();
            writer.write(Integer.toString(frmLcdFontGenerator.getWidth()));
            writer.newLine();
            writer.write(Integer.toString(frmLcdFontGenerator.getHeight()));
            writer.newLine();
            writer.write(Integer.toString(frmLcdFontGenerator.getX()));
            writer.newLine();
            writer.write(Integer.toString(frmLcdFontGenerator.getY()));
            writer.newLine();
            writer.newLine();

            for (int i = 0; i < CHARS; i++) {
                for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
                    for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                        if (char_icon[i].getPixel(ix, iy)) writer.write("X");
                        else writer.write("_");
                    }
                    writer.newLine();
                }
                writer.newLine();
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {}
    }

    private void load(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            characterWidthSpinner.setValue(Integer.parseInt(reader.readLine()));
            characterHeightSpinner.setValue(Integer.parseInt(reader.readLine()));
            previewMagSpinner.setValue(Integer.parseInt(reader.readLine()));
            editMagSpinner.setValue(Integer.parseInt(reader.readLine()));
            curIndex = Integer.parseInt(reader.readLine());
            int grid = Integer.parseInt(reader.readLine());
            if (grid != 0) chckbxGrid.setSelected(true);
            else chckbxGrid.setSelected(false);
            int w = Integer.parseInt(reader.readLine());
            int h = Integer.parseInt(reader.readLine());
            frmLcdFontGenerator.setSize(w, h);
            int x = Integer.parseInt(reader.readLine());
            int y = Integer.parseInt(reader.readLine());
            frmLcdFontGenerator.setLocation(x, y);
            reader.readLine();

            changeProperties();

            for (int i = 0; i < CHARS; i++) {
                for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
                    String line = reader.readLine();
                    for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                        if (line.charAt(ix) == 'X') char_icon[i].setPixel(ix, iy, true);
                        else char_icon[i].setPixel(ix, iy, false);
                    }
                }
                reader.readLine();
            }

            selectChar(curIndex);

            reader.close();
        } catch (Exception e) {}
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmLcdFontGenerator = new JFrame();
        frmLcdFontGenerator.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                save(new File("temp.txt"));
                System.exit(0);
            }
        });
        frmLcdFontGenerator.setTitle(tittle);
        frmLcdFontGenerator.setBounds(100, 100, 900, 600);
        frmLcdFontGenerator.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane();
        frmLcdFontGenerator.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        scrollPane.setViewportView(panel);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel controlPanel = new JPanel();
        panel.add(controlPanel, BorderLayout.NORTH);
        controlPanel.setBorder(new TitledBorder(null, "Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        FlowLayout flowLayout = (FlowLayout) controlPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser saveDialog = new JFileChooser();
                if (saveDialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    save(saveDialog.getSelectedFile());
                }
            }
        });
        controlPanel.add(btnSave);

        JButton btnLoad = new JButton("Load");
        btnLoad.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser loadDialog = new JFileChooser();
                if (loadDialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    load(loadDialog.getSelectedFile());
                }
            }
        });
        controlPanel.add(btnLoad);

        JLabel lblCharacterSize = new JLabel("Character size:");
        controlPanel.add(lblCharacterSize);

        characterWidthSpinner = new JSpinner();
        characterWidthSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                changeProperties();
            }
        });
        characterWidthSpinner.setModel(new SpinnerNumberModel(5, 1, 32, 1));
        controlPanel.add(characterWidthSpinner);

        JLabel lblX = new JLabel("x");
        controlPanel.add(lblX);

        characterHeightSpinner = new JSpinner();
        characterHeightSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                changeProperties();
            }
        });
        characterHeightSpinner.setModel(new SpinnerNumberModel(8, 1, 32, 1));
        controlPanel.add(characterHeightSpinner);

        JLabel lblPreviewSize = new JLabel("Preview size:");
        controlPanel.add(lblPreviewSize);

        previewMagSpinner = new JSpinner();
        previewMagSpinner.setModel(new SpinnerNumberModel(5, 1, 100, 1));
        previewMagSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                changeProperties();
            }
        });
        controlPanel.add(previewMagSpinner);

        JLabel lblEditSize = new JLabel("Edit size:");
        controlPanel.add(lblEditSize);

        editMagSpinner = new JSpinner();
        editMagSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                changeProperties();
            }
        });
        editMagSpinner.setModel(new SpinnerNumberModel(50, 10, 1000, 10));
        controlPanel.add(editMagSpinner);

        chckbxGrid = new JCheckBox("Grid");
        chckbxGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                changeProperties();
            }
        });
        controlPanel.add(chckbxGrid);

        JLabel lblCurrentCharacter = new JLabel("Current character:");
        controlPanel.add(lblCurrentCharacter);

        cur_label = new JLabel();
        controlPanel.add(cur_label);

        JPanel previewPanel = new JPanel();
        panel.add(previewPanel, BorderLayout.WEST);
        previewPanel.setLayout(new BorderLayout());

        charPreviewPanel = new JPanel();
        charPreviewPanel
                .setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        previewPanel.add(charPreviewPanel, BorderLayout.NORTH);

        JPanel editContainer = new JPanel();
        panel.add(editContainer, BorderLayout.CENTER);

        editPanel = new JPanel();
        editContainer.add(editPanel);

        JPanel movePanel = new JPanel();
        FlowLayout fl_movePanel = (FlowLayout) movePanel.getLayout();
        fl_movePanel.setAlignment(FlowLayout.LEFT);
        panel.add(movePanel, BorderLayout.SOUTH);

        JButton btnUp = new JButton("Up");
        btnUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                for (int iy = 0; iy < CHAR_HEIGHT - 1; iy++) {
                    for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                        char_icon[curIndex].setPixel(ix, iy, char_icon[curIndex].getPixel(ix, iy + 1));
                    }
                }
                for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                    char_icon[curIndex].setPixel(ix, CHAR_HEIGHT - 1, false);
                }
                selectChar(curIndex);
            }
        });
        movePanel.add(btnUp);

        JButton btnDown = new JButton("Down");
        btnDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                for (int iy = CHAR_HEIGHT - 1; iy > 0; iy--) {
                    for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                        char_icon[curIndex].setPixel(ix, iy, char_icon[curIndex].getPixel(ix, iy - 1));
                    }
                }
                for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                    char_icon[curIndex].setPixel(ix, 0, false);
                }
                selectChar(curIndex);
            }
        });
        movePanel.add(btnDown);

        JButton btnLeft = new JButton("Left");
        btnLeft.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                for (int ix = 0; ix < CHAR_WIDTH - 1; ix++) {
                    for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
                        char_icon[curIndex].setPixel(ix, iy, char_icon[curIndex].getPixel(ix + 1, iy));
                    }
                }
                for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
                    char_icon[curIndex].setPixel(CHAR_WIDTH - 1, iy, false);
                }
                selectChar(curIndex);
            }
        });
        movePanel.add(btnLeft);

        JButton btnRight = new JButton("Right");
        btnRight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                for (int ix = CHAR_WIDTH - 1; ix > 0; ix--) {
                    for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
                        char_icon[curIndex].setPixel(ix, iy, char_icon[curIndex].getPixel(ix - 1, iy));
                    }
                }
                for (int iy = 0; iy < CHAR_HEIGHT; iy++) {
                    char_icon[curIndex].setPixel(0, iy, false);
                }
                selectChar(curIndex);
            }
        });
        movePanel.add(btnRight);

        JButton btnUpAll = new JButton("UpAll");
        btnUpAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                for (int i = 0; i < CHARS; i++) {
                    for (int iy = 0; iy < CHAR_HEIGHT - 1; iy++) {
                        for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                            char_icon[i].setPixel(ix, iy, char_icon[i].getPixel(ix, iy + 1));
                        }
                    }
                    for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                        char_icon[i].setPixel(ix, CHAR_HEIGHT - 1, false);
                    }
                }
                changeProperties();
            }
        });
        movePanel.add(btnUpAll);

        JButton btnDownAll = new JButton("DownAll");
        btnDownAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                for (int i = 0; i < CHARS; i++) {
                    for (int iy = CHAR_HEIGHT - 1; iy > 0; iy--) {
                        for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                            char_icon[i].setPixel(ix, iy, char_icon[i].getPixel(ix, iy - 1));
                        }
                    }
                    for (int ix = 0; ix < CHAR_WIDTH; ix++) {
                        char_icon[i].setPixel(ix, 0, false);
                    }
                }
                changeProperties();
            }
        });
        movePanel.add(btnDownAll);

        changeProperties();

        load(new File("temp.txt"));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN && curIndex < CHARS - 1) selectChar(curIndex + 1);
        else if (e.getKeyCode() == KeyEvent.VK_UP && curIndex > 0) selectChar(curIndex - 1);
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub

    }
}
