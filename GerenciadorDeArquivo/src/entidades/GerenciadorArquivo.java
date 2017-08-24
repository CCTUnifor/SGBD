package entidades;

import interfaces.IFileManager;

import java.util.ArrayList;

public class GerenciadorArquivo implements IFileManager {

    private int containerIdCont = 1;
    private int blocoIdCount = 1;

    public GerenciadorArquivo() {
    }

    @Override
    public BlocoDado criarBlocoDeDado() {
        BlocoDado bloco = new BlocoDado(this.containerIdCont, this.blocoIdCount++);
        return bloco;
    }

    @Override
    public BlocoDado criarBlocoDeDado(ArrayList<Object> dados) {
        BlocoDado bloco = new BlocoDado(this.containerIdCont, this.blocoIdCount++, dados);
        return bloco;
    }
}
