package testes;

import entidades.BlocoDado;
import entidades.BlocoDeDadosHeader;
import entidades.GerenciadorArquivo;
import org.junit.Assert;
import org.junit.Test;

public class ComoGerenciadorArquivoPossoCriarUmBlocoDeDados {

    private final int tamanhoBloco = 1024;

    @Test
    public void criarUmBloco() {
        GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo(tamanhoBloco);
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDados();
        Assert.assertNotEquals(bloco, null);
    }

    @Test
    public void SetarOTamanhoDosBlocos() {
        GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo(tamanhoBloco);
        Assert.assertEquals(tamanhoBloco, gerenciadorArquivo.getTamanhoBloco());
    }

    @Test
    public void TerHeader() {
        GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo(tamanhoBloco);
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDados();
        BlocoDeDadosHeader blocoHeader = bloco.getHeader();
        Assert.assertNotEquals(blocoHeader, null);
    }

}
