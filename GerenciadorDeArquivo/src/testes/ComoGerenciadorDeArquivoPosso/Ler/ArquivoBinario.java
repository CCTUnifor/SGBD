package testes.ComoGerenciadorDeArquivoPosso.Ler;

import entidades.GerenciadorArquivo;
import entidades.blocos.RowId;
import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoDado;
import interfaces.IFileManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ArquivoBinario {
    @Test
    public void ECriarUmContainer() throws FileNotFoundException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerContainer(1);

        Assert.assertNotEquals(container, null);
    }

    @Test
    public void Bloco() throws IOException {
        RowId rowId = new RowId(1, 1);
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoDado container = gerenciadorArquivo.lerBloco(rowId);

        Assert.assertNotEquals(container, null);
        Assert.assertEquals(container.getHeader().getContainerId(), rowId.getContainerId());
        Assert.assertEquals(container.getHeader().getBlocoId(), rowId.getBlocoId());
    }

    @Test
    public void Bloco2() throws IOException {
        RowId rowId = new RowId(1, 4);
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoDado container = gerenciadorArquivo.lerBloco(rowId);

        Assert.assertNotEquals(container, null);
        Assert.assertEquals(container.getHeader().getContainerId(), rowId.getContainerId());
        Assert.assertEquals(container.getHeader().getBlocoId(), rowId.getBlocoId());
    }


    @Test (expected = FileNotFoundException.class)
    public void Bloco3() throws IOException {
        RowId rowId = new RowId(15, 2);
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoDado container = gerenciadorArquivo.lerBloco(rowId);

        Assert.assertNotEquals(container, null);
        Assert.assertEquals(container.getHeader().getContainerId(), rowId.getContainerId());
        Assert.assertEquals(container.getHeader().getBlocoId(), rowId.getBlocoId());
    }
}
