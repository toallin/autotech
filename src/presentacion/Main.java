package presentacion;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Forzar Nimbus (pestañas con relieve, como botones)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    System.out.println(">>> Usando Nimbus");
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(">>> Nimbus no disponible, usando Windows");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) { }
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}