package interfaces;

import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import exceptions.ContainerNoExistent;

import java.util.ArrayList;

public interface IFileManager {
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent;
    public BlocoDado criarBlocoDeDado(int containerId, ArrayList<Object> dados) throws ContainerNoExistent;

    public BlocoContainer criarBlocoContainer();
}
