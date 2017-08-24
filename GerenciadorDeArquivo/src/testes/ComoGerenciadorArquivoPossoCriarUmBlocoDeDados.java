package testes;

import entidades.BlocoDado;
import entidades.BlocoDadoHeader;
import entidades.GerenciadorArquivo;
import interfaces.IFileManager;
import utils.GlobalVariables;
import org.junit.Assert;
import org.junit.Test;

public class ComoGerenciadorArquivoPossoCriarUmBlocoDeDados {

    @Test
    public void criarUmBloco() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado();
        Assert.assertNotEquals(bloco, null);
    }

    @Test
    public void TerHeader() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado();
        BlocoDadoHeader blocoHeader = bloco.getHeader();
        Assert.assertNotEquals(blocoHeader, null);
    }

    @Test
    public void ConverterBlocoParaByteArray() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado();
        byte[] array = bloco.toByteArray();
        Assert.assertNotEquals(array, null);
    }

    @Test
    public void LengthByteArrayDoBlocoDeveSerOValorGlobalDoBloco() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado();
        byte[] array = bloco.toByteArray();
        Assert.assertEquals(array.length, GlobalVariables.TAMANHO_BLOCO);
    }

    @Test
    public void ConverterBlocoDadoHeaderParaByteArray() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado();
        byte[] array = bloco.getHeader().toByteArray();
        Assert.assertNotEquals(array, null);
    }

    @Test
    public void LengthByteArrayDoBlocoDadoHeaderDeveSerOValorGlobalDoBloco() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado();
        byte[] array = bloco.getHeader().toByteArray();
        Assert.assertEquals(array.length, GlobalVariables.TAMANHO_BLOCO);
    }

    @Test
    public void x() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado();

        byte[] array = bloco.getHeader().toByteArray();
        Assert.assertEquals(array.length, GlobalVariables.TAMANHO_BLOCO);
    }

}
