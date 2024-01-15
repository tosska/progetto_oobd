package GUI;

import Controller.Controller;
import Model.Frase;
import Model.Pagina;

import javax.swing.*;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class PageGUI {

    private JFrame frame = new JFrame();

    private JFrame frameChiamante;
    private Controller controllerPrincipale;
    private JTextPane textPane;
    private JLabel titleLabel;
    private JButton backButton;

    private JButton editButton;
    private Pagina pagina;


    //private ArrayList<JLabel> testo = new ArrayList<>();
    PageGUI(Controller controller, JFrame frameChiamante, Pagina p) { //da decidere se mandare la pagina tramite controller o tramite oggetto a se
        controllerPrincipale = controller;
        this.frameChiamante = frameChiamante;
        pagina = p;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Page: " + p.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);


        textPane = new JTextPane();
        textPane.setBounds(10, 50, 460, 350);
        textPane.setText(caricamentoTesto());
        textPane.setEditable(false);

        titleLabel = new JLabel(pagina.getTitolo());
        titleLabel.setBounds(10, 10, 200, 25);

        backButton = new JButton("Back");
        backButton.setBounds(390, 10, 70, 25);

        editButton= new JButton("Edit");
        editButton.setBounds(390, 410, 70, 25);

        frame.add(titleLabel);
        frame.add(backButton);
        frame.add(textPane);
        frame.add(editButton);
        frame.setVisible(true);

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }

    private String caricamentoTesto()
    {
        String testo="";

        for(Frase f:pagina.getTestoRiferito().getListaFrasi())
        {
            testo = testo + f.getContenuto();
        }

        return testo;
    }

/*
    private void caricamentoTesto()
    {
        int x=15, y=50;
        Frase precedente=null;

        for(Frase f : paginaAperta.getTestoRiferito().getListaFrasi())
        {
            if(precedente != null && precedente.getRiga()!=f.getRiga()) {
                y = y + 20;
                x = 15;
            }

            JLabel fraseLabel = new JLabel(f.getContenuto());
            fraseLabel.setBounds(x, y, f.getContenuto().length()+350, 25);
            x = x + f.getContenuto().length() + 20;

            precedente = f;
            testo.add(fraseLabel);

        }
    }
    */

}
