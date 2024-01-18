package GUI;

import Controller.Controller;
import Model.Operazione;
import Model.Pagina;
import Model.Storico;

import javax.swing.*;
import javax.swing.table.TableColumn;

public class StoricoGUI {

    private JFrame frame = new JFrame();
    private JFrame frameChiamante;
    private JScrollPane scrollPane;
    private JTable table;
    private Controller controllerPrincipale;

    private Pagina pagina;

    public StoricoGUI(JFrame frameChiamante, Controller controller, Pagina pagina)
    {
        this.frameChiamante = frameChiamante;
        controllerPrincipale = controller;
        this.pagina = pagina;

        pagina.getStorico().stampaOperazioni();

        creationGUI();

    }

    private void creationGUI()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Page: " + pagina.getTitolo());
        frame.setSize(500, 500);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        scrollPane = new JScrollPane();
        scrollPane.setSize(400, 400);

        frame.add(scrollPane);

    }

}
