import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneradorEnemigos {

    // Catálogo con instancias nuevas de cada enemigo
    private static List<Enemigo> crearCatalogo() {
        List<Enemigo> catalogo = new ArrayList<>();
        catalogo.add(new Esqueleto());  // dificultad 2
        catalogo.add(new Goblin());     // dificultad 3
        catalogo.add(new Orco());       // dificultad 4
        catalogo.add(new Troll());      // dificultad 5
        catalogo.add(new Dragon());     // dificultad 6
        return catalogo;
    }

    // Presupuesto de dificultad = 3/4 de la cantidad de acciones (mínimo 2)
    public static int calcularPresupuesto(int cantidadAcciones) {
        return Math.max(2, cantidadAcciones * 3 / 4);
    }

    // Elige enemigos al azar hasta gastar el presupuesto de dificultad
    public static List<Enemigo> generar(int cantidadAcciones, Random random) {
        int restante = calcularPresupuesto(cantidadAcciones);
        List<Enemigo> elegidos = new ArrayList<>();

        while (true) {
            List<Enemigo> posibles = new ArrayList<>();
            for (Enemigo e : crearCatalogo()) {
                if (e.getDificultad() <= restante) {
                    posibles.add(e);
                }
            }
            if (posibles.isEmpty()) break;

            Enemigo elegido = posibles.get(random.nextInt(posibles.size()));
            elegidos.add(elegido);
            restante -= elegido.getDificultad();
        }
        return elegidos;
    }
}
