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

        gaService.gerarContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA_MENOR);
    }

    @Test
    public void DeEntradaCompletoECriarUmContainer() throws IOException, ContainerNoExistent {
        GerenciadorArquivoService gaService = new GerenciadorArquivoService(ga);

        gaService.gerarContainerByInput(GlobalVariables.LOCAL_ARQUIVO_ENTRADA + GlobalVariables.ARQUIVO_ENTRADA);
    }
}
