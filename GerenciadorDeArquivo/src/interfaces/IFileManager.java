package interfaces;

import entidades.blocos.RowId;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import exceptions.ContainerNoExistent;
import factories.ContainerId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public interface IFileManager {
    public BlocoContainer criarBlocoContainer();

    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent;
    public BlocoDado criarBlocoDeDado(byte[] bytes) throws ContainerNoExistent;

    public BlocoContainer lerContainer(int containerId) throws FileNotFoundException;
    public void gravarArquivoTexto(BlocoContainer container) throws IOException;

    public BlocoContainer criarArquivo(String containerString) throws IOException, ContainerNoExistent;
    public BlocoDado lerBloco(RowId rowId) throws IOException;
    public void gravarBloco(BlocoContainer container, BlocoDado bloco) throws FileNotFoundException;
    public BlocoDado adicionarLinha(BlocoContainer container, String linha) throws IOException, ContainerNoExistent;

    public HashMap<ContainerId, String> getContainers() throws IOException;
}
