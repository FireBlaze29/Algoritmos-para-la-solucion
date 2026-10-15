public abstract class Heroe extends Personaje {
    // true si la acción anterior fue un intento de ataque especial
    private boolean ultimaAccionFueEspecial = false;

    public Heroe(String nombre, int vida, int ataque) {
        super(nombre, vida, ataque);
    }

    // El especial no se puede usar dos veces seguidas: si se intenta, FALLA
    public void usarEspecial(Personaje objetivo) {
        if (ultimaAccionFueEspecial) {
            System.out.println(nombre + " intenta usar su habilidad especial otra vez seguida... ¡FALLA!");
        } else {
            habilidadEspecial(objetivo);
        }
        ultimaAccionFueEspecial = true;
    }

    @Override
    public void atacar(Personaje objetivo) {
        super.atacar(objetivo);
        ultimaAccionFueEspecial = false;
    }

    public void descansar() {
        System.out.println(nombre + " descansa y recupera 15 de vida.");
        curar(15);
        ultimaAccionFueEspecial = false;
    }

    public void perderTurno() {
        System.out.println(nombre + " no sabe qué hacer y pierde el turno.");
        ultimaAccionFueEspecial = false;
    }
}
