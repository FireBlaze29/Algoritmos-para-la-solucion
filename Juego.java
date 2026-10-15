import java.util.List;

public class Juego {
    private final Heroe heroe;
    private final List<Enemigo> enemigos;
    private final List<Integer> acciones;
    private int enemigoActual = 0;

    public Juego(Heroe heroe, List<Enemigo> enemigos, List<Integer> acciones) {
        this.heroe = heroe;
        this.enemigos = enemigos;
        this.acciones = acciones;
    }

    // Devuelve true si el jugador gana
    public boolean jugar() {
        int dificultadTotal = 0;
        for (Enemigo e : enemigos) dificultadTotal += e.getDificultad();

        System.out.println(heroe.getNombre() + " debe derrotar a " + enemigos.size()
                + " enemigos (dificultad total: " + dificultadTotal + "):");
        for (Enemigo e : enemigos) System.out.println("  - " + e);
        System.out.println("Acciones programadas: " + acciones.size() + " -> " + acciones + "\n");
        System.out.println("¡Aparece " + enemigos.get(enemigoActual).getNombre() + "!\n");

        int turno = 1;
        for (int accion : acciones) {
            Enemigo enemigo = enemigos.get(enemigoActual);

            System.out.println("--- Turno " + turno + " (acción " + accion + ") ---");
            ejecutarAccion(accion, enemigo);

            if (!enemigo.estaVivo()) {
                System.out.println("¡" + enemigo.getNombre() + " ha sido derrotado!");
                enemigoActual++;
                if (enemigoActual >= enemigos.size()) {
                    System.out.println("\n¡Todos los enemigos fueron eliminados!");
                    elegirFinal().mostrar();
                    return true;
                }
                System.out.println("¡Aparece " + enemigos.get(enemigoActual).getNombre() + "!");
            } else {
                enemigo.turno(heroe, turno);
            }

            System.out.println(heroe + " | " + enemigos.get(enemigoActual) + "\n");

            if (!heroe.estaVivo()) {
                new FinalMuerte().mostrar();
                return false;
            }
            turno++;
        }

        // Se acabaron las acciones y todavía quedan enemigos vivos
        new FinalDerrota().mostrar();
        return false;
    }

    private void ejecutarAccion(int accion, Enemigo enemigo) {
        switch (accion) {
            case 1 -> heroe.atacar(enemigo);
            case 2 -> heroe.descansar();
            case 3 -> heroe.usarEspecial(enemigo);
            default -> heroe.perderTurno();
        }
    }

    // Si ganas, el final depende del TAMAÑO de la lista
    private Final elegirFinal() {
        int n = acciones.size();
        if (n <= 5) return new FinalNormal();
        if (n <= 9) return new FinalHeroico();
        return new FinalLegendario();
    }
}
