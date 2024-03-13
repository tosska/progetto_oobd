package GUI;

import Controller.Controller;
import Model.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;


public class AreaRiservata {
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
    JPanel propostePanel = new JPanel();
    JPanel operazioniPanel = new JPanel();

    Controller controller;

    public AreaRiservata(Controller controller, JFrame frameChiamante, JLabel userLabel) {

        centralPanel.setLayout(cardLayout);
        frame.setTitle("Danilo Wiki: Area Riservata");
        this.controller = controller;

        // pannello superiore
        topPanel.setBounds(0, 0, 1100, 60);
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

        areaProposte();

        operazioniLabel.setBounds(0, 90, 230, 30);
        operazioniLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        operazioniLabel.setForeground(new Color(47,69,92));
        operazioniLabel.setBorder(new MatteBorder(0, 0, 1, 0, Color.white));



        operazioniLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                // Creazione del modello di tabella vuoto
                DefaultTableModel operazioni = new DefaultTableModel();
                operazioni.addColumn("Utente");
                operazioni.addColumn("Tipo");
                operazioni.addColumn("Proposta");
                operazioni.addColumn("Pagina");
                operazioni.addColumn("Data");
                // Creazione della tabella con il modello vuoto
                JTable tabellaOp = new JTable(operazioni);
                tabellaOp.setEnabled(false); // Rende la tabella non modificabile

                cardLayout.show(centralPanel, "operazioniPanel");

                for (Operazione operazione : controller.storicoOperazioniUtente) {

                    /*
                    long timestamp = operazione.getData();

                    // Rimuovi millisecondi e secondi dal timestamp
                    LocalDateTime dateTime = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), java.time.ZoneId.systemDefault());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Formato desiderato
                    String formattedDateTime = dateTime.format(formatter);
                    */

                    // Aggiunta di una riga vuota al modello della tabella
                    operazioni.addRow(new Object[]{operazione.getUtente().getUsername(), "C", operazione.getProposta(),
                    operazione.getPagina().getTitolo(), operazione.getData()});




                }

                // Imposta il rendering delle linee verticali su nessuna
                tabellaOp.setShowVerticalLines(false);

                // Imposta l'altezza delle righe
                tabellaOp.setRowHeight(30); // Imposta l'altezza desiderata delle righe


                // Renderer personalizzato per l'intestazione
                JTableHeader header = tabellaOp.getTableHeader();
                header.setFont(new Font("Arial", Font.BOLD, 14)); // Imposta il font desiderato per l'intestazione
                header.setForeground(new Color(47,69,92)); // Imposta il colore del testo dell'intestazione
                header.setBackground(new Color(126,179,255)); // Imposta il colore di sfondo dell'intestazione
                header.setPreferredSize(new Dimension(header.getWidth(), 30)); // Imposta l'altezza desiderata dell'intestazione
                header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(47,69,92))); // Aggiunge una sottolineatura all'intestazione

                // Disabilita il riordinamento delle colonne
                header.setReorderingAllowed(false);


                operazioniPanel.add(new JScrollPane(tabellaOp), BorderLayout.CENTER); // Utilizza uno JScrollPane per la visualizzazione della tabella

                /*
                // Aggiunta di margini al pannello
                operazioniPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Imposta i margini a 10 pixel da tutti i lati del pannello

                JLabel titolo = new JLabel("Lista Operazioni");
                titolo.setOpaque(true);
                titolo.setFont(new Font("Segoe UI", Font.PLAIN, 23));
                titolo.setForeground(new Color(47,69,92));
                titolo.setBackground(Color.white);
                operazioniPanel.add(titolo);
                operazioniPanel.add(Box.createVerticalStrut(10)); // Aggiunge un vuoto verticale tra le etichette

                for (Operazione operazione : controller.storicoOperazioniUtente) {

                    JLabel label = new JLabel(operazione.getUtente().getUsername());
                    label.setIcon(profileImagine);
                    label.setHorizontalTextPosition(JLabel.RIGHT);
                    label.setIconTextGap(10);
                    label.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));

                    // Imposta i margini per spaziare il testo dal bordo e aumentare lo spazio tra le etichette
                    label.setBorder(BorderFactory.createCompoundBorder(label.getBorder(), BorderFactory.createEmptyBorder(10, 20, 10, 20)));

                    operazioniPanel.add(label);
                    operazioniPanel.add(Box.createVerticalStrut(1)); // Aggiunge un vuoto verticale tra le etichette


                }

                 */




            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                operazioniLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

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
        centralPanel.setBounds(250, 60, 850, 540);
        centralPanel.setBackground(Color.white);

        // welcome panel
        welcomePanel.setBounds(250, 60, 850, 540);
        welcomePanel.setLayout(null);
        welcomePanel.setBackground(Color.white);

        welcomeLabel.setBounds(20, 10, 530, 50);
        welcomeLabel.setFont(new Font("Monospaced", Font.ITALIC, 20));
        welcomeLabel.setText("<html>Ciao " + controller.utilizzatore.getUsername() + "!<br>" +
                "Benvenuto nella tua area riservata.</html>");

        // proposte panel
        propostePanel.setBounds(250, 60, 850, 540);
        propostePanel.setLayout(new BorderLayout());
        propostePanel.setBackground(Color.white);

        // operazionii panel
        operazioniPanel.setBounds(250, 60, 850, 540);
        operazioniPanel.setLayout(new BorderLayout());
        operazioniPanel.setBackground(Color.white);

        // gestione profilo panel
        gestProfiloPanel.setBounds(250, 60, 850, 540);
        gestProfiloPanel.setLayout(null);
        gestProfiloPanel.setBackground(Color.white);

        datiTitleLabel.setBounds(20, 10, 300, 30);
        datiTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        usernameLabel.setText("<html>Username<br><b>" + controller.utilizzatore.getUsername() + "</b></html>");
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
                    boolean result = controller.modificaUsername(controller.utilizzatore.getUsername(), newUsername);
                    if (result) {
                        JOptionPane.showMessageDialog(null, "Username modificato con successo");
                        controller.utilizzatore.setUsername(newUsername);
                        usernameLabel.setText("<html>Username<br><b>" + controller.utilizzatore.getUsername() + "</b></html>");
                        userLabel.setText(controller.utilizzatore.getUsername()); // modifico il label nella welcomepage così al ritorno sarà già aggiornato
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

        emailLabel.setText("<html>Email<br><b>" + controller.utilizzatore.getEmail() + "</b></html>");
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
                    boolean result = controller.modificaEmail(controller.utilizzatore.getEmail(), newEmail);
                    if (result) {
                        JOptionPane.showMessageDialog(null, "Email modificata con successo");
                        controller.utilizzatore.setEmail(newEmail);
                        emailLabel.setText("<html>Email<br><b>" + controller.utilizzatore.getEmail() + "</b></html>");
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

        iscrizioneLabel.setText("<html>Iscritto in data: <b>" + controller.utilizzatore.getDataIscrizione() + "</b></html>");
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
                    if (oldPassword.equals(controller.utilizzatore.getPassword())) {

                        String newPassword;
                        newPassword = JOptionPane.showInputDialog("Inserisci la nuova password");

                        if ((newPassword != null) && (!newPassword.equals(""))) {

                            String newPasswordConf;
                            newPasswordConf = JOptionPane.showInputDialog("Conferma password");

                            if ((newPasswordConf != null) && (!newPasswordConf.equals(""))) {

                                if (newPassword.equals(newPasswordConf)) {
                                    boolean result = controller.modificaPassword(oldPassword, newPassword);
                                    if (result) {
                                        controller.utilizzatore.setPassword(newPassword);
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
                        if (password.equals(controller.utilizzatore.getPassword())) {
                            String confPassword;
                            confPassword = JOptionPane.showInputDialog("Conferma password");

                            if ((confPassword != null) && (!confPassword.equals(""))) {
                                if (password.equals(confPassword)) {
                                    boolean result2 = controller.eliminaAccount(controller.utilizzatore.getUsername());
                                    if (result2) {
                                        JOptionPane.showMessageDialog(null, "Account eliminato con successo");
                                        controller.utilizzatore = null;
                                        frame.dispose();
                                        LoginPage loginPage = new LoginPage(controller);
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
        centralPanel.add(propostePanel, "propostePanel");
        centralPanel.add(operazioniPanel, "operazioniPanel");








        ImageIcon logo = new ImageIcon(this.getClass().getResource("/icon/wiki.png"));
        frame.setResizable(false);
        frame.setIconImage(logo.getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 600);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    public void areaProposte()
    {
        proposteLabel.setBounds(0, 60, 230, 30);
        proposteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        proposteLabel.setForeground(new Color(47,69,92));
        proposteLabel.setBorder(new MatteBorder(0, 0, 1, 0, Color.white));
        ImageIcon profileImagine = new ImageIcon(this.getClass().getResource("/icon/profile.png"));
        ImageIcon anteprimaImagine = new ImageIcon(this.getClass().getResource("/icon/anteprima.png"));
        ImageIcon approvaImagine = new ImageIcon(this.getClass().getResource("/icon/approva.png"));
        ImageIcon rifiutaImagine = new ImageIcon(this.getClass().getResource("/icon/rifiuta.png"));
        JLabel vuotoLabel = new JLabel("Non vi sono proposte da approvare");


        proposteLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(centralPanel, "propostePanel");

                controller.caricaProposteDaApprovare();

                if(!controller.proposteDaApprovare.isEmpty())
                {
                    // Creazione del modello di tabella vuoto
                    DefaultTableModel proposte = new DefaultTableModel();
                    proposte.addColumn("Utente");
                    proposte.addColumn("Tipo");
                    proposte.addColumn("Pagina");
                    proposte.addColumn("Data");
                    proposte.addColumn("Anteprima");
                    proposte.addColumn("Approva");
                    proposte.addColumn("Rifiuta");
                    // Creazione della tabella con il modello vuoto
                    JTable tabellaProp = new JTable(proposte);
                    tabellaProp.setEnabled(false); // Rende la tabella non modificabile

                    ArrayList <Pagina> anteprime = controller.creaAnteprime();

                    for (Pagina anteprima : anteprime) {

                        // Aggiunta di una riga vuota al modello della tabella
                        proposte.addRow(new Object[]{anteprima.getAutore().getUsername(), anteprima.getTitolo(), anteprima.getDataCreazione()});
                    }

                    tabellaProp.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int column = tabellaProp.columnAtPoint(e.getPoint());
                            int row = tabellaProp.rowAtPoint(e.getPoint());

                            // Controlla se il clic è avvenuto nelle colonne "Anteprima" "Approva" o "Rifiuta"
                            if (column == 4) {
                                controller.paginaAperta = anteprime.get(row);
                                AnteprimaGUI anteprimaGUI = new AnteprimaGUI(controller, frame);
                                frame.setVisible(false);
                            }

                            //pacchetto di proposte approvate
                            if (column == 5) {
                                Pagina anteprima = anteprime.get(row);
                                String dataAnteprima = anteprima.getDataCreazione().toString().split("\\.")[0];
                                String dataProposta;
                                Operazione temp;

                                for (int i = 0; i < controller.proposteDaApprovare.size(); i++) {
                                    temp = controller.proposteDaApprovare.get(i);
                                    dataProposta = temp.getData().toString().split("\\.")[0];

                                    if (dataAnteprima.equals(dataProposta) && temp.getPagina().getId() == anteprima.getId()
                                            && anteprima.getAutore().getUsername().equals(temp.getUtente().getUsername())) {
                                        controller.approvaProposta(temp, true);
                                    }

                                }

                                // Rimuovi la riga dalla tabella
                                DefaultTableModel model = (DefaultTableModel) tabellaProp.getModel();
                                model.removeRow(row);

                                JOptionPane.showMessageDialog(null, "Le proposte sono state approvate.", "Proposte approvate", JOptionPane.INFORMATION_MESSAGE);
                            }

                            //pacchetto di proposte rifiutate
                            if (column == 6) {
                                Pagina anteprima = anteprime.get(row);
                                String dataAnteprima = anteprima.getDataCreazione().toString().split("\\.")[0];
                                String dataProposta;
                                Operazione temp;

                                for (int i = 0; i < controller.proposteDaApprovare.size(); i++) {
                                    temp = controller.proposteDaApprovare.get(i);
                                    dataProposta = temp.getData().toString().split("\\.")[0];

                                    if (dataAnteprima.equals(dataProposta) && temp.getPagina().getId() == anteprima.getId()
                                            && anteprima.getAutore().getUsername().equals(temp.getUtente().getUsername())) {
                                        controller.approvaProposta(temp, false);
                                    }

                                }

                                // Rimuovi la riga dalla tabella
                                DefaultTableModel model = (DefaultTableModel) tabellaProp.getModel();
                                model.removeRow(row);

                                JOptionPane.showMessageDialog(null, "Le proposte sono state rifiutate.", "Proposte rifiutate", JOptionPane.INFORMATION_MESSAGE);

                            }
                        }


                    });



                    // Imposta il renderer per le colonne con ImageIcon
                    tabellaProp.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                        private ImageIcon icon = new ImageIcon(getClass().getResource("/icon/anteprima.png"));

                        @Override
                        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            JLabel label = new JLabel();
                            label.setIcon(icon);
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                            return label;
                        }
                    });
                    tabellaProp.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
                        private ImageIcon icon = new ImageIcon(getClass().getResource("/icon/approva.png"));

                        @Override
                        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            JLabel label = new JLabel();
                            label.setIcon(icon);
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                            return label;
                        }
                    });
                    tabellaProp.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
                        private ImageIcon icon = new ImageIcon(getClass().getResource("/icon/rifiuta.png"));

                        @Override
                        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            JLabel label = new JLabel();
                            label.setIcon(icon);
                            label.setHorizontalAlignment(SwingConstants.CENTER);
                            return label;
                        }
                    });

                    // Imposta il rendering delle linee verticali su nessuna
                    tabellaProp.setShowVerticalLines(false);

                    // Imposta l'altezza delle righe
                    tabellaProp.setRowHeight(30); // Imposta l'altezza desiderata delle righe


                    // Renderer personalizzato per l'intestazione
                    JTableHeader header = tabellaProp.getTableHeader();
                    header.setFont(new Font("Arial", Font.BOLD, 14)); // Imposta il font desiderato per l'intestazione
                    header.setForeground(new Color(47,69,92)); // Imposta il colore del testo dell'intestazione
                    header.setBackground(new Color(126,179,255)); // Imposta il colore di sfondo dell'intestazione
                    header.setPreferredSize(new Dimension(header.getWidth(), 30)); // Imposta l'altezza desiderata dell'intestazione
                    header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(47,69,92))); // Aggiunge una sottolineatura all'intestazione

                    // Disabilita il riordinamento delle colonne
                    header.setReorderingAllowed(false);


                    propostePanel.add(new JScrollPane(tabellaProp), BorderLayout.CENTER); // Utilizza uno JScrollPane per la visualizzazione della tabella

                }
                else
                {
                    propostePanel.add(vuotoLabel);
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
                proposteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
}
