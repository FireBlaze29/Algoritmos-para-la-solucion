public class Orco extends Enemigo {

    public Orco() {
        super("Orco", 80, 8, 4);
    }

    @Override
    public void habilidadEspecial(Personaje objetivo) {
        System.out.println(nombre + " descarga un HACHAZO FURIOSO y causa 16 de daño.");
        objetivo.recibirDanio(16);
    }
}
