public class FinalMuerte extends Final {
    public FinalMuerte() { super("Caído en combate"); }

    @Override
    protected String historia() {
        return "Tus heridas fueron demasiado graves. Tu leyenda termina aquí...";
    }
}
