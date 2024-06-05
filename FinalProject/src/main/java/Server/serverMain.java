package Server;

import Utilities.DriverManagers;
import View.AdminGUI;

import java.io.IOException;

public class serverMain {

    public static void main(String args[]) throws IOException, InterruptedException {
        DriverManagers.setAllOffline();
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec(new String[]{"orbd", "-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"});
        AdminGUI adminGUI = new AdminGUI();
        adminGUI.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
