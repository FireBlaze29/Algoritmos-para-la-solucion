public class Troll extends Enemigo {

    public Troll() {
        super("Troll", 100, 10, 5);
    }

    @Override
    public void habilidadEspecial(Personaje objetivo) {
        System.out.println(nombre + " lanza un GARROTAZO BRUTAL y causa 18 de daño.");
        objetivo.recibirDanio(18);
    }
}
