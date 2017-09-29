package testes.ComoGerenciadorDeArquivoPosso.Ler;

import entidades.GerenciadorArquivo;
import entidades.blocos.BlocoContainer;
import interfaces.IFileManager;
import org.junit.Assert;
import org.junit.Test;
import utils.GlobalVariables;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ArquivoBinario {
    @Test
    public void ECriarUmContainer() throws FileNotFoundException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.lerArquivoBinario(GlobalVariables.LOCAL_ARQUIVO_FINAL + "Tabela1.bin");

        Assert.assertNotEquals(container, null);
    }

}
