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
import java.util.List;

public interface IFileManager {
    BlocoContainer criarBlocoContainer();
    BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent;
    BlocoContainer selectAllFrom(int containerId) throws IOException;

    BlocoContainer criarArquivo(String containerString) throws IOException, ContainerNoExistent;
    BlocoDado lerBloco(RowId rowId) throws IOException;
    void gravarBloco(BlocoContainer container, BlocoDado bloco) throws IOException;
    BlocoDado adicionarLinha(BlocoContainer container, String linha) throws IOException, ContainerNoExistent;

    HashMap<ContainerId, String> getContainers() throws IOException;

    List<String> getDescritores(ContainerId containerId) throws IOException;
    void adicionarIndiceAoContainerId(ContainerId containerId, String indexNam) throws IOException;
}

