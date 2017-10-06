package interfaces;

import entidades.blocos.RowId;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import exceptions.ContainerNoExistent;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IFileManager {
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent;
    public BlocoDado criarBlocoDeDado(byte[] bytes) throws ContainerNoExistent;
    public BlocoDado lerBloco(RowId rowId) throws FileNotFoundException;

    public BlocoContainer criarBlocoContainer();

    public void commit(BlocoContainer container) throws IOException;
    public BlocoContainer getContainerByInput(String diretorio) throws FileNotFoundException;

    public BlocoContainer getContainer(int containerId) throws FileNotFoundException;

    public void gravarArquivoTexto(BlocoContainer container) throws IOException;

}
