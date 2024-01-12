package GUI;

import javax.swing.*;
import java.awt.*;

// CLASSE PER DEBUG
public class WelcomePage {
    private JFrame frame = new JFrame();
    private JLabel welcomeLabel = new JLabel("Hello!");

    WelcomePage(String userID) {

        welcomeLabel.setBounds(0, 0, 200, 35);
        welcomeLabel.setFont(new Font(null, Font.PLAIN, 25));
        welcomeLabel.setText("Hello "+userID);

        frame.add(welcomeLabel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 420);
        frame.setLayout(null);
        frame.setVisible(true);
    }
}
