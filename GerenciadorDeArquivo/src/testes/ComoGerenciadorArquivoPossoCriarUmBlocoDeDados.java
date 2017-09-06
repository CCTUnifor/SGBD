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

import java.io.FileNotFoundException;

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
    public  void LengthByteArrayBlocoDadoHeaderDeveConter8Bytes () throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco.getHeader().toByteArray().length, 8);
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
    public void OPrimeiroBlocoDeveEstarNoContainerId1() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco.getHeader().getContainerId(), 1);
    }

    @Test
    public void OPrimeiroBlocoDeveEstarNoBlocoId1() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco.getHeader().getBlocoId(), 1);
    }

    @Test
    public  void OSegundoBlocoASerCriadoDeveEstarNoBlocoId2() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        BlocoDado bloco01 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());
        BlocoDado bloco02 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco01.getHeader().getBlocoId(), 1);
        Assert.assertEquals(bloco02.getHeader().getBlocoId(), 2);
    }

    @Test
    public  void OSegundoBlocoASerCriadoDeveEstarNoContainerId1() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        BlocoDado bloco01 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());
        BlocoDado bloco02 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco02.getHeader().getContainerId(), 1);
    }

    @Test(expected = ContainerNoExistent.class)
    public void DeveRetornarUmaExcecaoQuandoNaoTiverContainer() throws Exception {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoDado bloco01 = gerenciadorArquivo.criarBlocoDeDado(0);
    }

    @Test
    public  void DeveCriarUmBlocoContainer() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        Assert.assertNotEquals(container, null);
    }

    @Test
    public void AoCriarOPrimeiroCOntainerOContainerIdDeveSerUm() throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        Assert.assertEquals(container.getContainerId(), 1);
    }

    @Test
    public void AoCriarOSegundoContainerOContainerIdDeveSerDois() throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container1 = gerenciadorArquivo.criarBlocoContainer();
        BlocoContainer container2 = gerenciadorArquivo.criarBlocoContainer();

        Assert.assertEquals(container2.getContainerId(), 2);
    }

    @Test
    public void BlocoContainerHeaderDeveConter11Bytes() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        Assert.assertEquals(container.getBlocoControle().getHeader().toByteArray().length, 11);
    }

    @Test
    public void LigandoBlocoAoSegundoContainerCriado() throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container1 = gerenciadorArquivo.criarBlocoContainer();
        BlocoContainer container2 = gerenciadorArquivo.criarBlocoContainer();

        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container2.getContainerId());

        Assert.assertEquals(bloco.getHeader().getContainerId(), 2);
        Assert.assertEquals(bloco.getHeader().getBlocoId(), 1);
    }

    @Test
    public  void ByteArrayDoPrimeiroBlocoContainerHeaderDeveComecarCom1() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();

        Assert.assertEquals(container.getBlocoControle().getHeader().toByteArray()[0], 1);
    }

    @Test
    public  void ByteArrayDoSegundoBlocoContainerHeaderDeveComecarCom1() {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container1 = gerenciadorArquivo.criarBlocoContainer();
        BlocoContainer container2 = gerenciadorArquivo.criarBlocoContainer();

        Assert.assertEquals(container2.getBlocoControle().getHeader().toByteArray()[0], 2);
    }

    @Test
    public  void ByteArrayDoPrimeiroBlocoDadoHeaderDeveComecarCom1() throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco.getHeader().toByteArray()[0], 1);
    }

    @Test
    public  void ByteArrayDoSegundoBlocoDadoHeaderDeveComecarCom1() throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco1 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());
        BlocoDado bloco2 = gerenciadorArquivo.criarBlocoDeDado(container.getContainerId());

        Assert.assertEquals(bloco2.getHeader().toByteArray()[0], 1);
    }

    @Test
    public void DadoUmByteArrayOBlocoDeDadosDeveLerOContainerIdUm() throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        byte[] bytes = new byte[8];
        bytes[0] = 1;

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(bytes);

        Assert.assertEquals(bloco.getHeader().getContainerId(), 1);
    }

    @Test
    public void DadoUmByteArrayOBlocoDeDadosDeveLerOBlocoIdUm() throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        byte[] bytes = new byte[8];
        bytes[0] = 1; // Container Id
        bytes[3] = 1; // Bloco Id

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(bytes);

        Assert.assertEquals(bloco.getHeader().getContainerId(), 1);
        Assert.assertEquals(bloco.getHeader().getBlocoId(), 1);
    }

    @Test
    public void DadoUmByteArrayOBlocoDeDadosDeveLerOBlocoIdDois() throws ContainerNoExistent {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        byte[] bytes = new byte[8];
        bytes[0] = 1; // Container Id
        bytes[3] = 2; // Bloco Id

        BlocoContainer container = gerenciadorArquivo.criarBlocoContainer();
        BlocoDado bloco = gerenciadorArquivo.criarBlocoDeDado(bytes);

        Assert.assertEquals(bloco.getHeader().getContainerId(), 1);
        Assert.assertEquals(bloco.getHeader().getBlocoId(), 2);
    }

    @Test
    public void GravarArquivo() throws FileNotFoundException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();
        byte[] bytes = new byte[2];
        bytes[0] = 1;
        bytes[1] = 2;

        gerenciadorArquivo.gravarArquivo(bytes, GlobalVariables.LOCAL_ARQUIVO_FINAL);
    }

    @Test
    public void LerArquivo() throws FileNotFoundException {
        IFileManager gerenciadorArquivo = new GerenciadorArquivo();

        gerenciadorArquivo.lerArquivo(GlobalVariables.LOCAL_ARQUIVO_FINAL);
    }

}
