package entidades;

import entidades.blocos.*;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;
import utils.GlobalVariables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class GerenciadorArquivo implements IFileManager {

    private int containerIdCount = 0;
    private int blocoIdCount = 1;

    @Override
    public BlocoDado criarBlocoDeDado(int containerId) throws ContainerNoExistent {
        if (this.containerIdCount == 0 || containerId == 0)
            throw new ContainerNoExistent();
        BlocoDado bloco = new BlocoDado(containerId, this.blocoIdCount++);
        return bloco;
    }

    @Override
    public BlocoDado criarBlocoDeDado(byte[] bytes) throws ContainerNoExistent {
        BlocoDado bloco = new BlocoDado(bytes);
        if (bloco.getHeader().getContainerId() < this.containerIdCount)
            throw new ContainerNoExistent();

        return bloco;
    }

    @Override
    public BlocoContainer criarBlocoContainer() {
        BlocoContainer container = new BlocoContainer(++this.containerIdCount);
        return container;
    }




    @Override
    public BlocoContainer lerContainer(int containerId) throws FileNotFoundException {
        BlocoContainer container = this.criarBlocoContainer();
        String diretorio = GlobalVariables.LOCAL_ARQUIVO_FINAL_BINARIO + "Tabela" + containerId + ".bin";

        byte[] bytes = GerenciadorDeIO.getBytes(diretorio);
        container.fromByteArray(bytes);
        return container;
    }

    @Override
    public void gravarArquivoTexto(BlocoContainer container) throws IOException {
        String diretorioCompleto = GlobalVariables.LOCAL_ARQUIVO_FINAL_TEXTO  + "Tabela" + container.getContainerId() + ".txt";
        ArrayList<String> linhas = container.print();

        GerenciadorDeIO.gravarString(diretorioCompleto, linhas);
    }

    private ArrayList<Descritor> processarDescritores(BlocoContainer container, String descritor) {
        ArrayList<Descritor> descritores = new ArrayList<Descritor>();
        String[] colunas = descritor.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

        for (int i = 0; i < colunas.length; i++) {
            String coluna = colunas[i];
            Descritor _descritor = new Descritor(coluna);
            descritores.add(_descritor);
            container.getBlocoControle().getHeader().atualizarTamanhoDescritor(_descritor.toByteArray().length);
        }

        return descritores;
    }


    @Override
    public BlocoContainer criarArquivo(String containerString) throws IOException, ContainerNoExistent {
        BlocoContainer container = this.criarBlocoContainer();
        container.getBlocoControle().adicionarDescritores(this.processarDescritores(container, containerString));
        container.adicionarBloco(this.criarBlocoDeDado(container.getContainerId()));

        GerenciadorDeIO.gravarBytes(container.getDiretorio(), container.toByteArray());
        return container;
    }

    @Override
    public BlocoDado lerBloco(RowId rowId) throws FileNotFoundException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(GlobalVariables.LOCAL_ARQUIVO_FINAL + "/Tabela" + rowId.getContainerId() + ".bin", "r");
        BlocoContainer container = this.criarBlocoContainer();

        byte[] bytes = null;

        try {
            int tamanho = (int) randomAccessFile.length();
            if (tamanho == 0)
                return null;

            bytes = new byte[tamanho];
            for (int i = 0; i < tamanho; i++){
                bytes[i] = randomAccessFile.readByte();
            }

        }catch (IOException e){
            this.containerIdCount--;
            System.out.println(e.getMessage());
        }
        return container.getBloco(bytes, rowId);
    }

    @Override
    public void gravarBloco(BlocoContainer container, BlocoDado bloco) throws FileNotFoundException {
        int offset = 0 + 11 + container.getBlocoControle().getHeader().getTamanhoDescritor() + ((bloco.getHeader().getBlocoId() -1) * container.getBlocoControle().getHeader().getTamanhoDosBlocos());
        int length = container.getBlocoControle().getHeader().getTamanhoDosBlocos();

        container.atualizar();
        bloco.atualizar(offset, length);
    }

    @Override
    public BlocoDado adicionarLinha(BlocoContainer container, String linha) throws FileNotFoundException {
        String[] colunas = linha.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

        Linha tuple = new Linha();
        tuple.adicionarColunas(colunas);

        int blocoIndex = container.getBlocoControle().getHeader().getProximoBlocoLivre();
        BlocoDado bloco = container.getBlocosDados().get(blocoIndex / container.getBlocoControle().getHeader().getTamanhoDosBlocos());

        if (podeAdicionarMaisTuple(bloco, tuple, container))
            bloco.adicionarTupla(tuple);
        else {
            BlocoDado blocoAux = new BlocoDado(container.getContainerId(), this.blocoIdCount++);
            blocoAux.adicionarTupla(tuple);

            container.adicionarBloco(blocoAux);
            container.getBlocoControle().getHeader().adicionarProximoBlocoLivre();
        }

        return bloco;
    }

    private boolean podeAdicionarMaisTuple(BlocoDado bloco, Linha linha, BlocoContainer container) {
        int tamanhoHeader = 8;
        int tamanhoMaximoBloco = container.getBlocoControle().getHeader().getTamanhoDosBlocos();
        int tamanhoUsadoDoBloco = bloco.getHeader().getTamanhoUsado();
        int tamanhoTupla = linha.getTamanhoCompleto();

        return tamanhoUsadoDoBloco + tamanhoTupla + tamanhoHeader <= tamanhoMaximoBloco;
    }

}