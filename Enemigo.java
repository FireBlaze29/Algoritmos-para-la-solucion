public abstract class Enemigo extends Personaje {
    // Nivel de dificultad: enemigos con el mismo nivel son equivalentes en reto
    protected int dificultad;

    public Enemigo(String nombre, int vida, int ataque, int dificultad) {
        super(nombre, vida, ataque);
        this.dificultad = dificultad;
    }

    public int getDificultad() {
        return dificultad;
    }

    // Turno del enemigo: cada 3 turnos usa su habilidad especial
    public void turno(Personaje objetivo, int numeroTurno) {
        if (numeroTurno % 3 == 0) {
            habilidadEspecial(objetivo);
        } else {
            atacar(objetivo);
        }
    }

    @Override
    public String toString() {
        return nombre + " [Vida: " + vida + ", Dif: " + dificultad + "]";
    }
}
