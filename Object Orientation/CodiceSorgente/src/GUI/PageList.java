package GUI;

import Controller.Controller;
import Model.Collegamento;
import Model.Pagina;

import javax.swing.*;
import java.util.ArrayList;

public class PageList {

    private JFrame frame = new JFrame();

    private JFrame frameChiamante;

    private Controller controller;

    private JScrollPane scrollPane;

    public PageList(Controller controller, JFrame frameChiamante, ArrayList<Pagina> lista)
    {
        this.controller = controller;
        this.frameChiamante = frameChiamante;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Lista pagine");
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        for(Pagina p : lista)
        {
            JPanel quadrante = new JPanel();
            quadrante.setSize(300,100);
        }



    }
}
