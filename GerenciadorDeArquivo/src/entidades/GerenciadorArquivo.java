package entidades;

import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;

import java.util.ArrayList;

public class GerenciadorArquivo implements IFileManager {

    private int containerIdCount = 0;
    private int blocoIdCount = 1;

    public GerenciadorArquivo() {
    }

    @Override
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent {
        if (this.containerIdCount == 0 || containerId == 0)
            throw new ContainerNoExistent();

        BlocoDado bloco = new BlocoDado(containerId, this.blocoIdCount++);
        return bloco;
    }

    @Override
    public BlocoDado criarBlocoDeDado(int containerId, ArrayList<Object> dados) throws ContainerNoExistent {
        BlocoDado bloco = new BlocoDado(this.containerIdCount, this.blocoIdCount++, dados);
        return bloco;
    }

    @Override
    public BlocoContainer criarBlocoContainer() {
        this.containerIdCount++;
        BlocoContainer container = new BlocoContainer();
        return container;
    }
}
