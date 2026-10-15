import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VentanaJuego extends JFrame {

    private final JTextField campoNombre = new JTextField("Thorin", 10);
    private final JComboBox<String> comboHeroe = new JComboBox<>(new String[]{"Guerrero", "Mago"});
    private final JTextField campoAcciones = new JTextField(32);
    private final JLabel etiquetaInfo = new JLabel(" ");
    private final JLabel etiquetaResultado = new JLabel(" ");
    private final JTextArea areaResultados = new JTextArea();
    private final JButton botonJugar = new JButton("¡Jugar!");

    public VentanaJuego() {
        super("Juego de herencia - Héroes vs Enemigos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Todo lo que las clases imprimen con System.out aparece en el área de texto
        System.setOut(new PrintStream(new AreaTextoOutputStream(areaResultados), true, StandardCharsets.UTF_8));

        construirInterfaz();
        actualizarInfo();
        setSize(760, 640);
        setLocationRelativeTo(null);
    }

    private void construirInterfaz() {
        // ---- Panel superior: héroe + lista de acciones ----
        JPanel panelHeroe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHeroe.add(new JLabel("Nombre:"));
        panelHeroe.add(campoNombre);
        panelHeroe.add(new JLabel("Clase:"));
        panelHeroe.add(comboHeroe);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAcciones.add(new JLabel("Lista de acciones:"));
        panelAcciones.add(campoAcciones);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.add(crearBotonAccion("1 Atacar", "1"));
        panelBotones.add(crearBotonAccion("2 Descansar", "2"));
        panelBotones.add(crearBotonAccion("3 Especial", "3"));

        JButton quitar = new JButton("Quitar último");
        quitar.addActionListener(e -> quitarUltimo());
        panelBotones.add(quitar);

        JButton limpiar = new JButton("Limpiar");
        limpiar.addActionListener(e -> campoAcciones.setText(""));
        panelBotones.add(limpiar);

        JLabel ayuda = new JLabel("<html>Escribe números separados por comas (ej: 3, 1, 3, 1, 2) o usa los botones. "
                + "1 = Atacar, 2 = Descansar, 3 = Especial (no se puede repetir seguido), otro = pierde turno.</html>");

        JPanel arriba = new JPanel();
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        arriba.add(panelHeroe);
        arriba.add(panelAcciones);
        arriba.add(panelBotones);
        arriba.add(ayuda);
        arriba.add(Box.createVerticalStrut(4));
        arriba.add(etiquetaInfo);

        campoAcciones.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarInfo(); }
            public void removeUpdate(DocumentEvent e) { actualizarInfo(); }
            public void changedUpdate(DocumentEvent e) { actualizarInfo(); }
        });

        // ---- Centro: resultados ----
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaResultados.setLineWrap(true);
        areaResultados.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(areaResultados);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados"));

        // ---- Abajo: botón jugar + resultado final ----
        botonJugar.setFont(botonJugar.getFont().deriveFont(Font.BOLD, 15f));
        botonJugar.addActionListener(e -> jugar());
        etiquetaResultado.setFont(etiquetaResultado.getFont().deriveFont(Font.BOLD, 14f));

        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        abajo.add(botonJugar);
        abajo.add(etiquetaResultado);

        add(arriba, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
    }

    private JButton crearBotonAccion(String texto, String numero) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> {
            String actual = campoAcciones.getText().trim();
            campoAcciones.setText(actual.isEmpty() ? numero : actual + ", " + numero);
        });
        return boton;
    }

    private void quitarUltimo() {
        try {
            List<Integer> lista = parsearAcciones(campoAcciones.getText());
            if (!lista.isEmpty()) lista.remove(lista.size() - 1);
            campoAcciones.setText(unir(lista));
        } catch (NumberFormatException ex) {
            // texto inválido: no se modifica
        }
    }

    // Convierte "3, 1, 3" en [3, 1, 3]
    public static List<Integer> parsearAcciones(String texto) {
        List<Integer> lista = new ArrayList<>();
        for (String parte : texto.split("[,;\\s]+")) {
            if (!parte.isBlank()) {
                lista.add(Integer.parseInt(parte.trim()));
            }
        }
        return lista;
    }

    private static String unir(List<Integer> lista) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(lista.get(i));
        }
        return sb.toString();
    }

    private static String nombreFinalSiGanas(int n) {
        if (n <= 5) return "Normal";
        if (n <= 9) return "Héroe";
        return "Leyenda";
    }

    private void actualizarInfo() {
        try {
            int n = parsearAcciones(campoAcciones.getText()).size();
            etiquetaInfo.setForeground(Color.DARK_GRAY);
            if (n == 0) {
                etiquetaInfo.setText("Aún no hay acciones.");
            } else {
                etiquetaInfo.setText("Acciones: " + n
                        + "  |  Presupuesto de dificultad de enemigos: " + GeneradorEnemigos.calcularPresupuesto(n)
                        + "  |  Final si ganas: " + nombreFinalSiGanas(n));
            }
        } catch (NumberFormatException ex) {
            etiquetaInfo.setForeground(Color.RED);
            etiquetaInfo.setText("Lista inválida: usa solo números enteros separados por comas.");
        }
    }

    private void jugar() {
        final List<Integer> acciones;
        try {
            acciones = parsearAcciones(campoAcciones.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La lista solo puede tener números enteros separados por comas.",
                    "Lista inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (acciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe al menos una acción.",
                    "Lista vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = campoNombre.getText().isBlank() ? "Héroe" : campoNombre.getText().trim();
        Heroe heroe = comboHeroe.getSelectedIndex() == 0 ? new Guerrero(nombre) : new Mago(nombre);
        List<Enemigo> enemigos = GeneradorEnemigos.generar(acciones.size(), new Random());

        areaResultados.setText("");
        etiquetaResultado.setText(" ");
        botonJugar.setEnabled(false);

        // El juego corre en un hilo aparte para no congelar la ventana
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                boolean gano = new Juego(heroe, enemigos, acciones).jugar();
                System.out.flush();
                return gano;
            }

            @Override
            protected void done() {
                try {
                    boolean gano = get();
                    etiquetaResultado.setForeground(gano ? new Color(0, 130, 0) : Color.RED);
                    etiquetaResultado.setText(gano ? "¡VICTORIA!" : "DERROTA");
                } catch (Exception ex) {
                    etiquetaResultado.setText("Error: " + ex.getMessage());
                }
                botonJugar.setEnabled(true);
            }
        }.execute();
    }
}
