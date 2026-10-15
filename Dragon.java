public class Dragon extends Enemigo {

    public Dragon() {
        super("Dragón", 120, 9, 6);
    }

    @Override
    public void habilidadEspecial(Personaje objetivo) {
        System.out.println(nombre + " escupe ALIENTO DE FUEGO y causa 20 de daño.");
        objetivo.recibirDanio(20);
    }
}
