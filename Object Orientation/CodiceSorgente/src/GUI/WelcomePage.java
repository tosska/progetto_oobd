package GUI;

import Controller.Controller;
import Model.Pagina;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;

// CLASSE PER DEBUG
public class WelcomePage {
    private JFrame frame = new JFrame();
    private Controller controllerPrincipale;


    private JPanel menuPanel = new JPanel();
    private JLabel logoLabel = new JLabel();
    private JLabel logoutLabel = new JLabel("Log out");
    private JLabel createLabel = new JLabel("Create a new page");


    private JPanel panelWhite = new JPanel();
    private JLabel searchLabel = new JLabel();
    private JTextField searchField = new JTextField("Search...");
    private JLabel profileLabel = new JLabel();


    private JPanel centralPanel = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    private JPanel welcomePanel = new JPanel();
    private JLabel welcomeLabel = new JLabel("<html>Benvenuti su <b>Danilo Wiki</b><br>L'enciclopedia libera e collaborativa</html>");
    private JPanel createNewPagePanel = new JPanel();
    private JLabel titleLabel = new JLabel("Inserisci titolo:");
    private JTextField titleField = new JTextField();


    WelcomePage(Controller controller, JFrame frameChiamante) {
        controllerPrincipale = controller;

        menuPanel.setBounds(0, 0, 200, 500);
        menuPanel.setLayout(null);
        menuPanel.setBackground(new Color(47,69,92));
        frame.setTitle("Danilo Wiki: Home");

        ImageIcon logoImagine = new ImageIcon(this.getClass().getResource("/icon/logoHome.png"));
        logoLabel.setIcon(logoImagine);
        logoLabel.setBounds(40, 20, 150, 70);

        ImageIcon logoutImagine = new ImageIcon(this.getClass().getResource("/icon/logout.png"));
        logoutLabel.setIcon(logoutImagine);
        logoutLabel.setBounds(15, 350, 250, 70);
        logoutLabel.setIconTextGap(15);
        logoutLabel.setForeground(Color.white);
        logoutLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                frameChiamante.setVisible(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        ImageIcon createImagine = new ImageIcon(this.getClass().getResource("/icon/create.png"));
        createLabel.setIcon(createImagine);
        createLabel.setBounds(15, 150, 250, 70);
        createLabel.setIconTextGap(15);
        createLabel.setForeground(Color.white);
        createLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(false);
                CreateNewPage createNewPage = new CreateNewPage(controllerPrincipale, frame);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                createLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });




        panelWhite.setBounds(200, 0, 700, 50);
        panelWhite.setLayout(null);
        panelWhite.setBackground(Color.white);
        panelWhite.setBorder(new MatteBorder(0, 0, 1, 0, new Color(47,69,92)));

        searchField.setBounds(30, 10, 300, 30);
        ImageIcon searchImagine = new ImageIcon(this.getClass().getResource("/icon/search.png"));
        searchLabel.setIcon(searchImagine);
        searchLabel.setBounds(335, 10, 30, 30);
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search...");
                }
            }
        });



        profileLabel.setText(controllerPrincipale.utilizzatore.getUsername());
        ImageIcon profileImagine = new ImageIcon(this.getClass().getResource("/icon/profile.png"));
        profileLabel.setIcon(profileImagine);
        profileLabel.setHorizontalTextPosition(JLabel.LEFT);
        profileLabel.setIconTextGap(10);

        // Ottieni le dimensioni preferite della JLabel
        Dimension labelSize = profileLabel.getPreferredSize();

        // Calcola le coordinate x e y per posizionare la JLabel nell'angolo destro con una certa distanza dal bordo destro
        int x = 700 - labelSize.width - 20; // 20 pixel di distanza dal bordo destro
        int y = 12; // 12 pixel di distanza dal bordo superiore

        // Imposta la posizione della JLabel
        profileLabel.setBounds(x, y, labelSize.width, labelSize.height);

        profileLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(false);
                AreaRiservata areaPersonale = new AreaRiservata(controllerPrincipale, frame, profileLabel);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                profileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        // quando premo invio inizia la ricerca
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    Pagina p = controllerPrincipale.cercaPagina(searchField.getText());
                    controllerPrincipale.paginaAperta = p;
                    PageGUI pageGUI = new PageGUI(controllerPrincipale, frame);
                    frame.setVisible(false);
                }
            }
        });


        // pannelli centrale
        centralPanel.setLayout(cardLayout);
        centralPanel.setBounds(200, 50, 700, 450);
        centralPanel.setBackground(Color.white);

        welcomePanel.setBounds(200, 50, 700, 450);
        welcomePanel.setLayout(null);
        welcomePanel.setBackground(Color.white);

        welcomeLabel.setBounds(20, 10, 500, 50);
        welcomeLabel.setFont(new Font("Monospaced", Font.ITALIC, 20));

        createNewPagePanel.setBounds(200, 50, 700, 450);
        createNewPagePanel.setLayout(null);
        createNewPagePanel.setBackground(Color.white);

        titleLabel.setBounds(30, 10, 100, 20);

        titleField.setBounds(30, 40, 300, 30);








        frame.add(menuPanel);
        frame.add(panelWhite);
        frame.add(centralPanel);

        menuPanel.add(logoLabel);
        menuPanel.add(logoutLabel);
        menuPanel.add(createLabel);

        panelWhite.add(searchField);
        panelWhite.add(searchLabel);
        panelWhite.add(profileLabel);

        welcomePanel.add(welcomeLabel);

        createNewPagePanel.add(titleLabel);
        createNewPagePanel.add(titleField);

        centralPanel.add(welcomePanel, "welcomePanel");
        centralPanel.add(createNewPagePanel, "createNewPagePanel");




        ImageIcon logo = new ImageIcon(this.getClass().getResource("/icon/wiki.png"));
        frame.setResizable(false);
        frame.setIconImage(logo.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocusInWindow();
    }
}
