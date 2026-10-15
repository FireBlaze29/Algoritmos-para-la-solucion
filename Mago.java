public class Mago extends Heroe {

    public Mago(String nombre) {
        super(nombre, 70, 10);
    }

    @Override
    public void habilidadEspecial(Personaje objetivo) {
        int danio = ataque * 3;
        System.out.println(nombre + " lanza BOLA DE FUEGO y causa " + danio + " de daño.");
        objetivo.recibirDanio(danio);
    }
}
