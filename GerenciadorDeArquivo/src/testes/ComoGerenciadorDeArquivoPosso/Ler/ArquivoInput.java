package testes.ComoGerenciadorDeArquivoPosso.Ler;

import entidades.GerenciadorArquivo;
import entidades.blocos.BlocoContainer;
import interfaces.IFileManager;
import org.junit.Assert;
import org.junit.Test;
import utils.GlobalVariables;

import java.io.FileNotFoundException;

public class ArquivoInput {
    @Test
    public void DeEntradaECriarUmContainer() throws FileNotFoundException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.getContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA_MENOR);
        Assert.assertEquals(container.getBlocosDados().size(), 2);
        Assert.assertEquals(container.getBlocosDados().get(0).getHeader().getContainerId(), 1);
        Assert.assertEquals(container.getBlocosDados().get(0).getHeader().getContainerId(), 1);
        Assert.assertEquals(container.getBlocosDados().get(0).getHeader().getBlocoId(), 1);
        Assert.assertEquals(container.getBlocosDados().get(1).getHeader().getBlocoId(), 2);
    }

    @Test
    public void DeEntradaCompletoECriarUmContainer() throws FileNotFoundException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.getContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA);
        Assert.assertNotEquals(container, null);
        Assert.assertNotEquals(container.getBlocoControle(), null);
        Assert.assertNotEquals(container.getBlocoControle().getHeader(), null);
        Assert.assertNotEquals(container.getBlocosDados(), null);
    }
}
