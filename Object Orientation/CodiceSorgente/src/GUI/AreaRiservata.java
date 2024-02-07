package GUI;

import Controller.Controller;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.border.MatteBorder;


public class AreaRiservata {
    Controller controllerPrincipale;
    JFrame frame = new JFrame();
    JPanel topPanel = new JPanel();
    JPanel menuPanel = new JPanel();
    JPanel centralPanel = new JPanel();
    JLabel titleLabel = new JLabel("Area Riservata");
    JPanel profilePanel = new JPanel();
    JLabel profileLabel = new JLabel("  PROFILO");
    JLabel gestProfiloLabel = new JLabel("  Gestione profilo");
    JPanel gestProfiloPanel = new JPanel();
    JLabel datiTitleLabel = new JLabel("Riepilogo Account");
    JLabel usernameLabel = new JLabel();
    JLabel modUsernameLabel = new JLabel("Modifica username");
    JLabel iscrizioneLabel = new JLabel();
    JLabel emailLabel = new JLabel();
    JLabel modEmailLabel = new JLabel("Modifica email");
    JLabel passwordInfo1Label = new JLabel("Password e info di sicurezza");
    JLabel passwordInfo2Label = new JLabel("È consigliabile scegliere una password che non usi in altre posizioni.");
    JLabel modPasswordLabel = new JLabel("Modifica password");
    JLabel deleteAccountLabel = new JLabel("Elimina account");
    JLabel welcomeLabel = new JLabel();
    JLabel proposteLabel = new JLabel("  Proposte di modifica");
    JLabel operazioniLabel = new JLabel("  Operazioni effettuate");
    JLabel esciLabel = new JLabel("  Esci");
    CardLayout cardLayout = new CardLayout();
    JPanel welcomePanel = new JPanel();

    public AreaRiservata(Controller controller, JFrame frameChiamante, JLabel userLabel) {

        centralPanel.setLayout(cardLayout);

        controllerPrincipale = controller;

        // pannello superiore
        topPanel.setBounds(0, 0, 800, 60);
        topPanel.setLayout(null);
        topPanel.setBackground(new Color(47,69,92));

        ImageIcon imaginePersonal = new ImageIcon(this.getClass().getResource("/icon/personal.png"));
        titleLabel.setIcon(imaginePersonal);
        titleLabel.setBounds(20, 10, 350, 40);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 30));
        titleLabel.setForeground(Color.white);
        titleLabel.setIconTextGap(10);


        // pannello laterale
        menuPanel.setBounds(0, 60, 250, 540);
        menuPanel.setLayout(null);
        menuPanel.setBackground(new Color(244, 244, 244));
        menuPanel.setBorder(new MatteBorder(0,0,0,2, Color.GRAY));

        profilePanel.setBounds(10, 10, 230, 150);
        profilePanel.setLayout(null);
        profilePanel.setBackground(new Color(222, 222, 222));

        profileLabel.setBounds(0, 0, 230, 30);
        profileLabel.setBackground(Color.lightGray);
        profileLabel.setOpaque(true);
        profileLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        profileLabel.setBorder(new MatteBorder(0, 0, 2, 0, Color.white));

        gestProfiloLabel.setBounds(0, 30, 230, 30);
        gestProfiloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gestProfiloLabel.setForeground(new Color(47,69,92));
        gestProfiloLabel.setBorder(new MatteBorder(0, 0, 1, 0, Color.white));
        gestProfiloLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(centralPanel, "gestProfiloPanel");
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                gestProfiloLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        proposteLabel.setBounds(0, 60, 230, 30);
        proposteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        proposteLabel.setForeground(new Color(47,69,92));
        proposteLabel.setBorder(new MatteBorder(0, 0, 1, 0, Color.white));

        operazioniLabel.setBounds(0, 90, 230, 30);
        operazioniLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        operazioniLabel.setForeground(new Color(47,69,92));
        operazioniLabel.setBorder(new MatteBorder(0, 0, 1, 0, Color.white));

        esciLabel.setBounds(0, 120, 230, 30);
        esciLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        esciLabel.setForeground(new Color(47,69,92));
        esciLabel.addMouseListener(new MouseListener() {
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
                esciLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


            // pannelli centrali
        centralPanel.setBounds(250, 60, 550, 540);
        centralPanel.setBackground(Color.white);

        // welcome panel
        welcomePanel.setBounds(250, 60, 550, 540);
        welcomePanel.setLayout(null);
        welcomePanel.setBackground(Color.white);

        welcomeLabel.setBounds(20, 10, 530, 50);
        welcomeLabel.setFont(new Font("Monospaced", Font.ITALIC, 20));
        welcomeLabel.setText("<html>Ciao " + controllerPrincipale.utilizzatore.getUsername() + "!<br>" +
                "Benvenuto nella tua area riservata.</html>");

        // gestione profilo panel
        gestProfiloPanel.setBounds(250, 60, 550, 540);
        gestProfiloPanel.setLayout(null);
        gestProfiloPanel.setBackground(Color.white);

        datiTitleLabel.setBounds(20, 10, 300, 30);
        datiTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        usernameLabel.setText("<html>Username<br><b>" + controllerPrincipale.utilizzatore.getUsername() + "</b></html>");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setBounds(20, 50, 300, 35);

        modUsernameLabel.setForeground(new Color(47,69,92));
        modUsernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modUsernameLabel.setBounds(20, 95, 300, 15);
        modUsernameLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String newUsername;
                newUsername = JOptionPane.showInputDialog("Inserisci un nuovo username");
                if ((newUsername != null) && (!newUsername.equals("")))    // verifica se l'utente chiude la finestra o preme il tasto Cancella
                {
                    boolean result = controllerPrincipale.modificaUsername(controllerPrincipale.utilizzatore.getUsername(), newUsername);
                    if (result) {
                        JOptionPane.showMessageDialog(null, "Username modificato con successo");
                        controllerPrincipale.utilizzatore.setUsername(newUsername);
                        usernameLabel.setText("<html>Username<br><b>" + controllerPrincipale.utilizzatore.getUsername() + "</b></html>");
                        userLabel.setText(controllerPrincipale.utilizzatore.getUsername()); // modifico il label nella welcomepage così al ritorno sarà già aggiornato
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Errore inserimento", "Alert", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                modUsernameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        emailLabel.setText("<html>Email<br><b>" + controllerPrincipale.utilizzatore.getEmail() + "</b></html>");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setBounds(20, 130, 300, 45);

        modEmailLabel.setForeground(new Color(47,69,92));
        modEmailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modEmailLabel.setBounds(20, 180, 300, 15);
        modEmailLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String newEmail;
                newEmail = JOptionPane.showInputDialog("Inserisci una nuova email");
                if ((newEmail != null) && (!newEmail.equals(""))) {
                    boolean result = controllerPrincipale.modificaEmail(controllerPrincipale.utilizzatore.getEmail(), newEmail);
                    if (result) {
                        JOptionPane.showMessageDialog(null, "Email modificata con successo");
                        controllerPrincipale.utilizzatore.setEmail(newEmail);
                        emailLabel.setText("<html>Email<br><b>" + controllerPrincipale.utilizzatore.getEmail() + "</b></html>");
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Errore inserimento", "Alert", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                modEmailLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        iscrizioneLabel.setText("<html>Iscritto in data: <b>" + controllerPrincipale.utilizzatore.getDataIscrizione() + "</b></html>");
        iscrizioneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        iscrizioneLabel.setBounds(20, 215, 300, 15);

        passwordInfo1Label.setBounds(20, 270, 300, 20);
        passwordInfo1Label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        gestProfiloPanel.add(passwordInfo1Label);

        passwordInfo2Label.setBounds(20, 300, 500, 20);
        passwordInfo2Label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gestProfiloPanel.add(passwordInfo2Label);

        modPasswordLabel.setForeground(new Color(47,69,92));
        modPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        modPasswordLabel.setBounds(20, 330, 300, 15);
        modPasswordLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String oldPassword;
                oldPassword = JOptionPane.showInputDialog("Inserisci la password attuale");
                if ((oldPassword != null) && (!oldPassword.equals(""))) {
                    if (oldPassword.equals(controllerPrincipale.utilizzatore.getPassword())) {

                        String newPassword;
                        newPassword = JOptionPane.showInputDialog("Inserisci la nuova password");

                        if ((newPassword != null) && (!newPassword.equals(""))) {

                            String newPasswordConf;
                            newPasswordConf = JOptionPane.showInputDialog("Conferma password");

                            if ((newPasswordConf != null) && (!newPasswordConf.equals(""))) {

                                if (newPassword.equals(newPasswordConf)) {
                                    boolean result = controllerPrincipale.modificaPassword(oldPassword, newPassword);
                                    if (result) {
                                        controllerPrincipale.utilizzatore.setPassword(newPassword);
                                        JOptionPane.showMessageDialog(null, "Password modificata con successo");
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Errore inserimento", "Errore", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                else {
                                    JOptionPane.showMessageDialog(null, "Errore: la password inserita non corrisponde", "Errore", JOptionPane.ERROR_MESSAGE);
                                }

                            }

                        }

                    }
                    else {
                        JOptionPane.showMessageDialog(null, "La password inserita non è corretta", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                modPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        deleteAccountLabel.setForeground(Color.red);
        deleteAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteAccountLabel.setBounds(20, 410, 300, 15);
        deleteAccountLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Sei sicuro?", "Eliminazione definitiva account", JOptionPane.YES_NO_OPTION);
                if (result == 0) {
                    String password;
                    password = JOptionPane.showInputDialog("Inserisci la tua password");

                    if ((password != null) && (!password.equals(""))) {
                        if (password.equals(controllerPrincipale.utilizzatore.getPassword())) {
                            String confPassword;
                            confPassword = JOptionPane.showInputDialog("Conferma password");

                            if ((confPassword != null) && (!confPassword.equals(""))) {
                                if (password.equals(confPassword)) {
                                    boolean result2 = controllerPrincipale.eliminaAccount(controllerPrincipale.utilizzatore.getUsername());
                                    if (result2) {
                                        JOptionPane.showMessageDialog(null, "Account eliminato con successo");
                                        controllerPrincipale.utilizzatore = null;
                                        frame.dispose();
                                        LoginPage loginPage = new LoginPage(controllerPrincipale);
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(null, "Errore inserimento", "Errore", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                else {
                                    JOptionPane.showMessageDialog(null, "Errore: la password inserita non corrisponde", "Errore", JOptionPane.ERROR_MESSAGE);
                                }
                            }

                        }
                        else {
                            JOptionPane.showMessageDialog(null, "Errore: la password inserita non è corretta", "Alert", JOptionPane.ERROR_MESSAGE);
                        }
                    }



                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                deleteAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });



        frame.add(topPanel);
        frame.add(menuPanel);
        frame.add(centralPanel);

        topPanel.add(titleLabel);

        menuPanel.add(profilePanel);
        profilePanel.add(profileLabel);
        profilePanel.add(gestProfiloLabel);
        profilePanel.add(proposteLabel);
        profilePanel.add(operazioniLabel);
        profilePanel.add(esciLabel);

        welcomePanel.add(welcomeLabel);

        gestProfiloPanel.add(datiTitleLabel);
        gestProfiloPanel.add(usernameLabel);
        gestProfiloPanel.add(modUsernameLabel);
        gestProfiloPanel.add(emailLabel);
        gestProfiloPanel.add(modEmailLabel);
        gestProfiloPanel.add(iscrizioneLabel);
        gestProfiloPanel.add(passwordInfo1Label);
        gestProfiloPanel.add(passwordInfo2Label);
        gestProfiloPanel.add(modPasswordLabel);
        gestProfiloPanel.add(deleteAccountLabel);

        centralPanel.add(welcomePanel, "welcomePanel");
        centralPanel.add(gestProfiloPanel, "gestProfiloPanel");

        ImageIcon logo = new ImageIcon(this.getClass().getResource("/icon/wiki.png"));
        frame.setResizable(false);
        frame.setIconImage(logo.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
