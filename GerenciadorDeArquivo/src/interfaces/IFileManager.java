package interfaces;

import entidades.RowId;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import entidades.blocos.Linha;
import exceptions.BlocoSemEspacoException;
import exceptions.ContainerNoExistent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public interface IFileManager {
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent;
    BlocoDado criarBlocoDeDado(byte[] bytes) throws ContainerNoExistent;

    public BlocoContainer criarBlocoContainer();

    public void gravarArquivoBinario(BlocoContainer container, String diretorio) throws IOException;
    public BlocoContainer lerArquivoInput(String diretorio) throws FileNotFoundException;

    public BlocoContainer lerArquivoBinario(String diretorio) throws FileNotFoundException;

    public void gravarArquivoTexto(BlocoContainer container, String localArquivoFinal) throws IOException;

    public BlocoDado lerBloco(RowId rowId) throws FileNotFoundException;
}
