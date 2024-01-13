package GUI;
import Controller.Controller;

public class Main {
    public static void main(String[] args) {
        Controller controllerPrincipale = new Controller();

        LoginPage loginPage = new LoginPage(controllerPrincipale);

    }
}
