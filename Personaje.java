public abstract class Personaje {
    protected String nombre;
    protected int vida;
    protected int ataque;

    public Personaje(String nombre, int vida, int ataque) {
        this.nombre = nombre;
        this.vida = vida;
        this.ataque = ataque;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public void recibirDanio(int danio) {
        vida = Math.max(0, vida - danio);
    }

    public void curar(int cantidad) {
        vida += cantidad;
    }

    public void atacar(Personaje objetivo) {
        System.out.println(nombre + " ataca a " + objetivo.nombre + " y causa " + ataque + " de daño.");
        objetivo.recibirDanio(ataque);
    }

    // Cada subclase define su propia habilidad
    public abstract void habilidadEspecial(Personaje objetivo);

    public String getNombre() { return nombre; }
    public int getVida() { return vida; }

    @Override
    public String toString() {
        return nombre + " [Vida: " + vida + "]";
    }
}
