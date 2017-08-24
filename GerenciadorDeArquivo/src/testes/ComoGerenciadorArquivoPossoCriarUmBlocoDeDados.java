package testes;

import entidades.BlocoDado;
import entidades.BlocoDadoHeader;
import entidades.GerenciadorArquivo;
import utils.GlobalVariables;
import org.junit.Assert;
import org.junit.Test;

public class ComoGerenciadorArquivoPossoCriarUmBlocoDeDados {

    @Test
    public void criarUmBloco() {
        GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDados();
        Assert.assertNotEquals(bloco, null);
    }

    @Test
    public void TerHeader() {
        GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDados();
        BlocoDadoHeader blocoHeader = bloco.getHeader();
        Assert.assertNotEquals(blocoHeader, null);
    }

    @Test
    public void ConverterBlocoParaByteArray() {
        GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDados();
        byte[] array = bloco.toByteArray();
        Assert.assertNotEquals(array, null);
    }

    @Test
    public void LengthByteArrayDoBlocoDeveSerOValorGlobalDoBloco() {
        GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDados();
        byte[] array = bloco.toByteArray();
        Assert.assertEquals(array.length, GlobalVariables.TAMANHO_BLOCO);
    }

    @Test
    public void ConverterBlocoDadoHeaderParaByteArray() {
        GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDados();
        byte[] array = bloco.getHeader().toByteArray();
        Assert.assertNotEquals(array, null);
    }

    @Test
    public void LengthByteArrayDoBlocoDadoHeaderDeveSerOValorGlobalDoBloco() {
        GerenciadorArquivo gerenciadorArquivo = new GerenciadorArquivo();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDados();
        byte[] array = bloco.getHeader().toByteArray();
        Assert.assertEquals(array.length, GlobalVariables.TAMANHO_BLOCO);
    }

}
