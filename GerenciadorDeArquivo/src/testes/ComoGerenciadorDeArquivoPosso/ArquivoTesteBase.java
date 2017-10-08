package testes.ComoGerenciadorDeArquivoPosso;

import entidades.GerenciadorArquivo;
import interfaces.IFileManager;

public class ArquivoTesteBase {
    protected IFileManager ga;

    public ArquivoTesteBase() {
        ga = new GerenciadorArquivo();
    }
}
