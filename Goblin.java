public class Goblin extends Enemigo {

    public Goblin() {
        super("Goblin", 60, 6, 3);
    }

    @Override
    public void habilidadEspecial(Personaje objetivo) {
        System.out.println(nombre + " lanza una DAGA ENVENENADA y causa 12 de daño.");
        objetivo.recibirDanio(12);
    }
}
