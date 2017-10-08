package testes.ComoGerenciadorDeArquivoPosso.Ler;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorArquivoService;
import entidades.blocos.BlocoContainer;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;
import org.junit.Assert;
import org.junit.Test;
import testes.ComoGerenciadorDeArquivoPosso.ArquivoTesteBase;
import utils.GlobalVariables;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ArquivoInput extends ArquivoTesteBase {


    @Test
    public void DeEntradaECriarUmContainer() throws IOException, ContainerNoExistent {
        GerenciadorArquivoService gaService = new GerenciadorArquivoService(ga);

        BlocoContainer container = gaService.gerarContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA_MENOR);
        Assert.assertEquals(container.getBlocosDados().size(), 2);
        Assert.assertEquals(container.getBlocosDados().get(0).getHeader().getContainerId(), 1);
        Assert.assertEquals(container.getBlocosDados().get(0).getHeader().getContainerId(), 1);
        Assert.assertEquals(container.getBlocosDados().get(0).getHeader().getBlocoId(), 1);
        Assert.assertEquals(container.getBlocosDados().get(1).getHeader().getBlocoId(), 2);
    }

    @Test
    public void DeEntradaCompletoECriarUmContainer() throws IOException, ContainerNoExistent {
        GerenciadorArquivoService gaService = new GerenciadorArquivoService(ga);

        BlocoContainer container = gaService.gerarContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA);
        Assert.assertNotEquals(container, null);
        Assert.assertNotEquals(container.getBlocoControle(), null);
        Assert.assertNotEquals(container.getBlocoControle().getHeader(), null);
        Assert.assertNotEquals(container.getBlocosDados(), null);
    }
}
