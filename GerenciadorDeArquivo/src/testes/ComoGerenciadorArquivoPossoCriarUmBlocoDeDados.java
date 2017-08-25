package testes;

import entidades.blocos.BlocoContainer;
import entidades.blocos.BlocoContainerHeader;
import entidades.blocos.BlocoDado;
import entidades.blocos.BlocoDadoHeader;
import entidades.GerenciadorArquivo;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;
import utils.GlobalVariables;
import org.junit.Assert;
import org.junit.Test;

public class ComoGerenciadorArquivoPossoCriarUmBlocoDeDados {

    @Test
    public void CriarUmBloco() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertNotEquals(bloco, null);
    }

    @Test
    public void TerHeader() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        BlocoDadoHeader blocoHeader = bloco.getHeader();
        Assert.assertNotEquals(blocoHeader, null);
    }

    @Test
    public void ConverterBlocoParaByteArray() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        byte[] array = bloco.toByteArray();
        Assert.assertNotEquals(array, null);
    }

    @Test
    public void LengthByteArrayDoBlocoDeveSerOValorGlobalDoBloco() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        byte[] array = bloco.toByteArray();
        Assert.assertEquals(array.length, GlobalVariables.TAMANHO_BLOCO);
    }

    @Test
    public void ConverterBlocoDadoHeaderParaByteArray() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        byte[] array = bloco.getHeader().toByteArray();
        Assert.assertNotEquals(array, null);
    }

    @Test
    public void LengthByteArrayDoBlocoDadoHeaderDeveSerOValorGlobalDoBloco() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        byte[] array = bloco.getHeader().toByteArray();
        Assert.assertEquals(array.length, GlobalVariables.TAMANHO_BLOCO);
    }

    @Test
    public void OPrimeiroBlocoDeveEstarNoContainerId1() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco.getHeader().getContainerId().getValue(), 1);
    }

    @Test
    public void OPrimeiroBlocoDeveEstarNoBlocoId1() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco.getHeader().getBlocoId().getValue(), 1);
    }

    @Test
    public  void OSegundoBlocoASerCriadoDeveEstarNoBlocoId2() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        BlocoDado bloco01 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());
        BlocoDado bloco02 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco01.getHeader().getBlocoId().getValue(), 1);
        Assert.assertEquals(bloco02.getHeader().getBlocoId().getValue(), 2);
    }

    @Test
    public  void OSegundoBlocoASerCriadoDeveEstarNoContainerId1() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        BlocoDado bloco01 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());
        BlocoDado bloco02 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco02.getHeader().getContainerId().getValue(), 1);
    }

    @Test(expected = ContainerNoExistent.class)
    public void DeveRetornarUmaExcecãoQuandoNaoTiverContainer() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoDado bloco01 = gerenciadorArquivo.criarBlocoDeDado(0);
    }

    @Test
    public  void ContainerBloco() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        Assert.assertNotEquals(container, null);
    }

    @Test
    public void BLoco() throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());
    }

}
