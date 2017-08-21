package entidades;

/**
 * Created by José Victor on 09/08/2017.
 */
public class BlocoDado {

    private BlocoDeDadosHeader header;

    public BlocoDado() {
        this.header = new BlocoDeDadosHeader();
    }

    public BlocoDeDadosHeader getHeader() {
        return header;
    }
}
