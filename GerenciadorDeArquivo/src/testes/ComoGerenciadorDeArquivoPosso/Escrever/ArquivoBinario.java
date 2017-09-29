package testes.ComoGerenciadorDeArquivoPosso.Escrever;

import entidades.GerenciadorArquivo;
import entidades.blocos.BlocoContainer;
import interfaces.IFileManager;
import org.junit.Test;
import utils.GlobalVariables;

import java.io.IOException;

public class ArquivoBinario {
    @Test
    public void GravarArquivoBasico() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerArquivoInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA_MENOR);

        gerenciadorArquivo.gravarArquivoBinario(container, GlobalVariables.LOCAL_ARQUIVO_FINAL);
    }

    @Test
    public void GravarArquivoCompleto() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerArquivoInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA);

        gerenciadorArquivo.gravarArquivoBinario(container, GlobalVariables.LOCAL_ARQUIVO_FINAL);
    }
}
