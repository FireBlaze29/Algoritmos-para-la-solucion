public class Guerrero extends Heroe {

    public Guerrero(String nombre) {
        super(nombre, 100, 12);
    }

    @Override
    public void habilidadEspecial(Personaje objetivo) {
        int danio = ataque * 2;
        System.out.println(nombre + " usa GOLPE DOBLE y causa " + danio + " de daño.");
        objetivo.recibirDanio(danio);
    }
}
