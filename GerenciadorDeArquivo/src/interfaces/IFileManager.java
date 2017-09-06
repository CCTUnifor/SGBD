package interfaces;

import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import exceptions.ContainerNoExistent;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface IFileManager {
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent;
    public BlocoDado criarBlocoDeDado(int containerId, ArrayList<Object> dados) throws ContainerNoExistent;
    BlocoDado criarBlocoDeDado(byte[] bytes) throws ContainerNoExistent;

    public BlocoContainer criarBlocoContainer();

    public void gravarArquivo(byte[] bytes, String diretorio) throws FileNotFoundException;
    public byte[] lerArquivo(String diretorio) throws FileNotFoundException;

}
