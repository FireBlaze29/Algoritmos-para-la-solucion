import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

// Convierte lo que se imprime con System.out.println en texto dentro de un JTextArea
public class AreaTextoOutputStream extends OutputStream {
    private final JTextArea area;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public AreaTextoOutputStream(JTextArea area) {
        this.area = area;
    }

    @Override
    public void write(int b) {
        buffer.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) {
        buffer.write(b, off, len);
    }

    @Override
    public void flush() {
        String texto = buffer.toString(StandardCharsets.UTF_8);
        buffer.reset();
        if (texto.isEmpty()) return;
        SwingUtilities.invokeLater(() -> {
            area.append(texto);
            area.setCaretPosition(area.getDocument().getLength());
        });
    }
}
