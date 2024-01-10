package GUI;

import javax.swing.*;

public class Pagina {
    private JPanel panel;
    private JLabel titleField;
    private JTextArea textArea1;
    private JButton modificaButton;
    private JButton indietroButton;
    private JLabel infoLabel;
    private JPanel panelTesto;
    private JPanel panelOperazioni;
    private JPanel panelInfo;
    public JFrame frame;

    public Pagina(JFrame frameChiamante) {
        frame = new JFrame("PaginaGUI");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
