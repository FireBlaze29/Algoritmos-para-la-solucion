import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // La lista de acciones ahora se escribe dentro de la ventana
        SwingUtilities.invokeLater(() -> new VentanaJuego().setVisible(true));
    }
}
