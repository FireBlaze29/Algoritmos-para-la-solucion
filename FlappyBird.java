import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Compañeros, para que el jugo arranque, ejecutenlo desde visual studio code o desde la terminal con los siguientes comandos:
 * javac FlappyBird.java
 * java FlappyBird
 */
public class FlappyBird {

    // ---- Configuración ----
    static final int W = 40;           // ancho del campo
    static final int H = 20;           // alto (la última fila es el suelo)
    static final int BIRD_X = 8;       // columna fija del pájaro
    static final int GAP = 6;          // tamaño del hueco entre tuberías
    static final int PIPE_W = 3;       // ancho de la tubería
    static final int SPACING = 16;     // distancia entre tuberías
    static final int TICK_MS = 100;    // duración de cada frame
    static final double GRAVITY = 0.25;
    static final double FLAP = -1.1;

    // ---- Códigos ANSI ----
    static final String CLEAR = "\033[H\033[2J";
    static final String HIDE_CURSOR = "\033[?25l";
    static final String SHOW_CURSOR = "\033[?25h";

    // ---- Plataforma / entrada ----
    static final boolean WINDOWS =
            System.getProperty("os.name").toLowerCase().contains("win");
    static boolean useCls = false;      // Windows: limpiar con el comando "cls"
    static Process keyProc;             // Windows: proceso PowerShell que lee teclas
    static InputStream keyIn;           // Windows: salida de ese proceso

    // ---- Estado del juego ----
    static final Random rnd = new Random();
    static double birdY;
    static double velocity;
    static int score;
    static boolean dead;
    static boolean started;
    static List<int[]> pipes = new ArrayList<>(); // {x, gapTop, pasada(0/1)}

    public static void main(String[] args) throws Exception {
        final String savedTty;

        if (WINDOWS) {
            useCls = args.length > 0 && args[0].equals("--cls");
            savedTty = null;
            setupWindows();
        } else {
            savedTty = enableRawMode();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!useCls) System.out.print(SHOW_CURSOR);
            System.out.println();
            System.out.flush();
            if (WINDOWS) {
                if (keyProc != null) keyProc.destroy();
            } else {
                restoreTty(savedTty);
            }
        }));

        if (!useCls) System.out.print(HIDE_CURSOR);
        reset();

        while (true) {
            boolean space = readSpace();

            if (dead) {
                if (space) reset();
            } else if (!started) {
                if (space) {
                    started = true;
                    velocity = FLAP;
                }
            } else {
                if (space) velocity = FLAP;
                update();
            }

            draw();
            Thread.sleep(TICK_MS);
        }
    }

    // ---- Lógica ----
    static void reset() {
        birdY = H / 2.0;
        velocity = 0;
        score = 0;
        dead = false;
        started = false;
        pipes.clear();
    }

    static void update() {
        // Física del pájaro
        velocity += GRAVITY;
        birdY += velocity;

        // Mover tuberías
        for (int[] p : pipes) p[0]--;

        // Generar nueva tubería
        if (pipes.isEmpty() || pipes.get(pipes.size() - 1)[0] <= W - SPACING) {
            pipes.add(new int[]{W, rnd.nextInt(10) + 2, 0});
        }

        // Quitar tuberías fuera de pantalla y contar puntos
        Iterator<int[]> it = pipes.iterator();
        while (it.hasNext()) {
            int[] p = it.next();
            if (p[0] + PIPE_W < 0) {
                it.remove();
            } else if (p[2] == 0 && p[0] + PIPE_W - 1 < BIRD_X) {
                p[2] = 1;
                score++;
            }
        }

        // Colisiones
        int row = (int) Math.round(birdY);
        if (row < 0 || row >= H - 1) dead = true;

        for (int[] p : pipes) {
            boolean inColumn = BIRD_X >= p[0] && BIRD_X < p[0] + PIPE_W;
            if (inColumn && (row < p[1] || row >= p[1] + GAP)) dead = true;
        }
    }

    // ---- Dibujo ----
    static void draw() {
        char[][] grid = new char[H][W];
        for (char[] line : grid) java.util.Arrays.fill(line, ' ');

        // Suelo
        for (int c = 0; c < W; c++) grid[H - 1][c] = '=';

        // Tuberías
        for (int[] p : pipes) {
            for (int c = p[0]; c < p[0] + PIPE_W; c++) {
                if (c < 0 || c >= W) continue;
                for (int r = 0; r < H - 1; r++) {
                    if (r < p[1] || r >= p[1] + GAP) grid[r][c] = '#';
                }
            }
        }

        // Pájaro
        int row = (int) Math.round(birdY);
        if (row >= 0 && row < H) grid[row][BIRD_X] = dead ? 'X' : (velocity < 0 ? '^' : 'O');

        // Armar el frame completo y escribirlo de una sola vez
        StringBuilder sb = new StringBuilder();
        if (useCls) clsWindows();
        else sb.append(CLEAR);

        sb.append("  FLAPPY BIRD   Puntos: ").append(score).append("\n");
        sb.append('+').append("-".repeat(W)).append("+\n");
        for (char[] line : grid) {
            sb.append('|').append(line).append("|\n");
        }
        sb.append('+').append("-".repeat(W)).append("+\n");

        if (dead) {
            sb.append("  GAME OVER - ESPACIO para reiniciar\n");
        } else if (!started) {
            sb.append("  Presiona ESPACIO para empezar\n");
        } else {
            sb.append("  ESPACIO para saltar\n");
        }

        System.out.print(sb);
        System.out.flush();
    }

    // ---- Entrada: solo ESPACIO cuenta, el resto se ignora ----
    static boolean readSpace() throws IOException {
        boolean space = false;
        InputStream in = WINDOWS ? keyIn : System.in;
        while (in.available() > 0) {
            int c = in.read();
            if (c == ' ' || c == 'S') space = true; // 'S' lo envía el ayudante de Windows
        }
        return space;
    }

    // =====================  WINDOWS  =====================

    static void setupWindows() {
        try {
            // Truco: al lanzar cmd.exe la consola de Windows 10/11 activa
            // los códigos ANSI. Además, "cls" deja la pantalla limpia al inicio.
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            // Ayudante: PowerShell lee teclas sin necesidad de Enter y
            // escribe una "S" por cada ESPACIO. Todas las demás teclas se ignoran.
            String script = "while($true){ $k=[Console]::ReadKey($true); "
                    + "if($k.Key -eq 'Spacebar'){ [Console]::Out.WriteLine('S') } }";

            keyProc = new ProcessBuilder("powershell", "-NoProfile", "-Command", script)
                    .redirectInput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            keyIn = keyProc.getInputStream();
        } catch (Exception e) {
            System.err.println("No se pudo iniciar PowerShell para leer el teclado: " + e.getMessage());
            System.exit(1);
        }
    }

    static void clsWindows() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception ignored) { }
    }

    // =====================  LINUX / MAC  =====================

    static String enableRawMode() {
        try {
            String saved = run("stty -g < /dev/tty").trim();
            run("stty -icanon -echo min 1 time 0 < /dev/tty");
            return saved;
        } catch (Exception e) {
            System.err.println("No se pudo configurar la terminal.");
            System.exit(1);
            return null;
        }
    }

    static void restoreTty(String saved) {
        try {
            if (saved != null && !saved.isEmpty()) run("stty " + saved + " < /dev/tty");
            else run("stty sane < /dev/tty");
        } catch (Exception ignored) { }
    }

    static String run(String cmd) throws IOException, InterruptedException {
        Process p = new ProcessBuilder("sh", "-c", cmd)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        p.getInputStream().transferTo(out);
        int code = p.waitFor();
        if (code != 0) throw new IOException("Falló: " + cmd);
        return out.toString();
    }
}