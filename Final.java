public abstract class Final {
    protected String titulo;

    public Final(String titulo) {
        this.titulo = titulo;
    }

    protected abstract String historia();

    public void mostrar() {
        System.out.println("\n===== FINAL: " + titulo + " =====");
        System.out.println(historia());
    }
}
