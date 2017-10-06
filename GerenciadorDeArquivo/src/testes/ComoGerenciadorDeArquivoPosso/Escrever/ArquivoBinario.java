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
        BlocoContainer container = gerenciadorArquivo.getContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA_MENOR);
    }

    @Test
    public void GravarArquivoCompleto() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.getContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA);
    }

    @Test
    public void GravarDoisArquivosCompleto() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.getContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA);
        BlocoContainer container2 = gerenciadorArquivo.getContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA_MENOR);
    }
}
