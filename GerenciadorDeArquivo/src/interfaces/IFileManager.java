package interfaces;

import entidades.blocos.BlocoContainerHeader;
import entidades.blocos.RowId;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import entidades.index.IndexFileManager;
import exceptions.ContainerNoExistentException;
import factories.ContainerId;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface IFileManager {
    BlocoContainer criarBlocoContainer();
    BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistentException;
    BlocoContainer selectAllFrom(int containerId) throws IOException;

    BlocoContainer criarArquivo(String containerString) throws IOException, ContainerNoExistentException;
    BlocoContainerHeader getContainerHeader(ContainerId containerId) throws ContainerNoExistentException;
    BlocoDado lerBloco(RowId rowId) throws IOException;
    void gravarBloco(BlocoContainer container, BlocoDado bloco) throws IOException;
    BlocoDado adicionarLinha(BlocoContainer container, String linha) throws IOException, ContainerNoExistentException;

    HashMap<ContainerId, String> getContainers() throws IOException;

    List<String> getColumns(ContainerId containerId) throws IOException, ContainerNoExistentException;
    void adicionarIndiceAoContainerId(ContainerId containerId, String indexNam) throws IOException, ContainerNoExistentException, IndexFileManager.IndexNoExistentException;
}