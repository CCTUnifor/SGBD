package interfaces;

import entidades.BlocoDado;

import java.util.ArrayList;

public interface IFileManager {
    public BlocoDado criarBlocoDeDado();
    public BlocoDado criarBlocoDeDado(ArrayList<Object> dados);
}
