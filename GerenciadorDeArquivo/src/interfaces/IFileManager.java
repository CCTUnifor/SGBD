package interfaces;

import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import entidades.blocos.Linha;
import exceptions.BlocoSemEspacoException;
import exceptions.ContainerNoExistent;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface IFileManager {
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent;
    public BlocoDado criarBlocoDeDado(int containerId, ArrayList<Linha> dados) throws ContainerNoExistent;
    BlocoDado criarBlocoDeDado(byte[] bytes) throws ContainerNoExistent;

    public BlocoContainer criarBlocoContainer();
    public BlocoContainer criarBlocoContainer(String linha) throws BlocoSemEspacoException;

    public void gravarArquivo(byte[] bytes, String diretorio) throws FileNotFoundException;
    public BlocoContainer lerArquivo(String diretorio) throws FileNotFoundException;

//    public byte[] lerArquivoParaBytes(String diretorio) throws FileNotFoundException;
//    public String[] lerArquivoParaString(String diretorio) throws FileNotFoundException;

}
