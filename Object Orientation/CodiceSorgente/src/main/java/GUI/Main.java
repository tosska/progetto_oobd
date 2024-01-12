package GUI;
import Controller.Controller;

public class Main {
    public static void main(String[] args) {
        Controller controllerPrincipale = new Controller();

        // debug
        controllerPrincipale.aggiungiUtente("Lorenzo", "Email", "0000");
        controllerPrincipale.aggiungiUtente("Francesco", "Email", "0000");
        controllerPrincipale.aggiungiUtente("Isgro", "Email", "1111");

        LoginPage loginPage = new LoginPage(controllerPrincipale);

    }
}
