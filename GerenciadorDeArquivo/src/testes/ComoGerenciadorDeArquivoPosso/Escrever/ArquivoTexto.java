package testes.ComoGerenciadorDeArquivoPosso.Escrever;

import entidades.GerenciadorArquivo;
import entidades.blocos.BlocoContainer;
import interfaces.IFileManager;
import org.junit.Assert;
import org.junit.Test;
import utils.GlobalVariables;

import java.io.IOException;

public class ArquivoTexto {

    @Test
    public void ECriarUmContainerESalvarEmUmArquivo() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerArquivoBinario(GlobalVariables.LOCAL_ARQUIVO_FINAL + "Tabela1.bin");

        gerenciadorArquivo.gravarArquivoTexto(container, GlobalVariables.LOCAL_ARQUIVO_FINAL);
        Assert.assertNotEquals(container, null);
    }

    @Test
    public void ECriarUmContainerESalvarEmUmArquivo2() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerArquivoBinario(GlobalVariables.LOCAL_ARQUIVO_FINAL + "Tabela1.bin");
        BlocoContainer container2 = gerenciadorArquivo.lerArquivoBinario(GlobalVariables.LOCAL_ARQUIVO_FINAL + "Tabela2.bin");

        gerenciadorArquivo.gravarArquivoTexto(container, GlobalVariables.LOCAL_ARQUIVO_FINAL);
        gerenciadorArquivo.gravarArquivoTexto(container2, GlobalVariables.LOCAL_ARQUIVO_FINAL);
        Assert.assertNotEquals(container, null);
    }
}
