package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.border.TitledBorder;
import java.awt.GridLayout;

public class FontGenWindow {

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

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmLcdFontGenerator = new JFrame();
        frmLcdFontGenerator.setTitle(tittle);
        frmLcdFontGenerator.setBounds(100, 100, 450, 300);
        frmLcdFontGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(new TitledBorder(null, "Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        FlowLayout flowLayout = (FlowLayout) controlPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        frmLcdFontGenerator.getContentPane().add(controlPanel, BorderLayout.NORTH);
        
        JPanel previewPanel = new JPanel();
        previewPanel.setBorder(new TitledBorder(null, "Preview", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        frmLcdFontGenerator.getContentPane().add(previewPanel, BorderLayout.WEST);
        previewPanel.setLayout(new GridLayout(17, 17, 0, 0));
    }

}
