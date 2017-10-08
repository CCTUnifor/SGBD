package testes.ComoGerenciadorDeArquivoPosso.Escrever;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorArquivoService;
import entidades.blocos.BlocoContainer;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;
import org.junit.Test;
import testes.ComoGerenciadorDeArquivoPosso.ArquivoTesteBase;
import utils.GlobalVariables;

import java.io.IOException;

public class ArquivoBinario extends ArquivoTesteBase {

    @Test
    public void GravarArquivoBasico() throws IOException, ContainerNoExistent {
        GerenciadorArquivoService gaService = new GerenciadorArquivoService(ga);

        BlocoContainer container = gaService.gerarContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA_MENOR);
    }

    @Test
    public void GravarArquivoCompleto() throws IOException, ContainerNoExistent {
        GerenciadorArquivoService gaService = new GerenciadorArquivoService(ga);

        BlocoContainer container = gaService.gerarContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA);
    }

    @Test
    public void GravarDoisArquivosCompleto() throws IOException, ContainerNoExistent {
        GerenciadorArquivoService gaService = new GerenciadorArquivoService(ga);

        BlocoContainer container = gaService.gerarContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA);
        BlocoContainer container2 = gaService.gerarContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA_MENOR);
    }
}
