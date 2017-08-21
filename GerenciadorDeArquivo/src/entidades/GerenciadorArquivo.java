package entidades;

public class GerenciadorArquivo {

    private int tamanhoBloco;

    public GerenciadorArquivo(int tamanhoBloco) {
        this.tamanhoBloco = tamanhoBloco;
    }

    public BlocoDado criarBlocoDeDados() {
        BlocoDado bloco = new BlocoDado();
        return new BlocoDado();
    }

    public int getTamanhoBloco() {
        return this.tamanhoBloco;
    }
}
