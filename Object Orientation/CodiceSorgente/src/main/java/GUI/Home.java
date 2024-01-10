package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home {
    private JPanel panel;
    private JTextField textField1;
    private JButton searchButton;
    public JFrame frame;

    public Home(JFrame frameChiamante) {
        frame= new JFrame("HomeGUI");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pagina paginaGUI = new Pagina(frame);
                paginaGUI.frame.setVisible(true);
                frame.setVisible(false);
            }
        });
    }
}
