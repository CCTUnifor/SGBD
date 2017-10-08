package testes.ComoGerenciadorDeArquivoPosso.Escrever;

import entidades.GerenciadorArquivo;
import entidades.blocos.BlocoContainer;
import interfaces.IFileManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ArquivoTexto {

    @Test
    public void ECriarUmContainerESalvarEmUmArquivo() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerContainer(1);

        gerenciadorArquivo.gravarArquivoTexto(container);
        Assert.assertNotEquals(container, null);
    }

    @Test
    public void ECriarUmContainerESalvarEmUmArquivo2() throws IOException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerContainer(1);
        BlocoContainer container2 = gerenciadorArquivo.lerContainer(2);

        gerenciadorArquivo.gravarArquivoTexto(container);
        gerenciadorArquivo.gravarArquivoTexto(container2);
        Assert.assertNotEquals(container, null);
    }
}
