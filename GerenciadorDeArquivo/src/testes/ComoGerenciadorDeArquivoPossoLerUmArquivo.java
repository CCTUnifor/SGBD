package testes;

import entidades.GerenciadorArquivo;
import entidades.blocos.BlocoContainer;
import interfaces.IFileManager;
import org.junit.Assert;
import org.junit.Test;
import utils.GlobalVariables;

import java.io.FileNotFoundException;

public class ComoGerenciadorDeArquivoPossoLerUmArquivo {
    @Test
    public void ECriarUmContainerId() throws FileNotFoundException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarContainerPeloArquivo(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA_MENOR);
        Assert.assertEquals(container.getBlocosDados().size(), 2);
        Assert.assertEquals(container.getBlocosDados().get(0).getHeader().getContainerId(), 1);
        Assert.assertEquals(container.getBlocosDados().get(0).getHeader().getContainerId(), 1);
        Assert.assertEquals(container.getBlocosDados().get(0).getHeader().getBlocoId(), 1);
        Assert.assertEquals(container.getBlocosDados().get(1).getHeader().getBlocoId(), 2);
    }
}
