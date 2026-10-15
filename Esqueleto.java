public class Esqueleto extends Enemigo {

    public Esqueleto() {
        super("Esqueleto", 50, 7, 2);
    }

    @Override
    public void habilidadEspecial(Personaje objetivo) {
        System.out.println(nombre + " lanza una FLECHA OSCURA y causa 14 de daño.");
        objetivo.recibirDanio(14);
    }
}
