package entidades;

import entidades.blocos.*;
import exceptions.ContainerNoExistent;
import interfaces.IFileManager;
import utils.GlobalVariables;

import java.awt.*;
import java.io.File;
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
    public void commit(BlocoContainer container) throws IOException {
        String diretorioCompleto = GlobalVariables.LOCAL_ARQUIVO_FINAL_BINARIO + "Tabela" + container.getContainerId() + ".bin";
        GerenciadorDeIO.gravarBytes(diretorioCompleto, container.toByteArray());
    }

    @Override
    public BlocoContainer getContainerByInput(String diretorio) throws FileNotFoundException {

        try {
            ArrayList<String> linhas = GerenciadorDeIO.getStrings(diretorio);
            BlocoContainer container = new BlocoContainer(++this.containerIdCount);
            container.getBlocoControle().adicionarDescritores(this.popularDescritores(container, linhas.get(0)));

            ArrayList<Linha> tuplas = this.popularLinhas(linhas);
            ArrayList<BlocoDado> blocos = this.popularBlocos(container, tuplas);

            container.adicionarBlocos(blocos);

            return container;

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public BlocoContainer getContainer(int containerId) throws FileNotFoundException {
        BlocoContainer container = this.criarBlocoContainer();
        String diretorio = GlobalVariables.LOCAL_ARQUIVO_FINAL_BINARIO + "Tabela" + containerId + ".bin";

        byte[] bytes = GerenciadorDeIO.getBytes(diretorio);
        container.fromByteArray(bytes);
        return container;
    }

    @Override
    public void gravarArquivoTexto(BlocoContainer container) throws IOException {
        String diretorioCompleto = GlobalVariables.LOCAL_ARQUIVO_FINAL_TEXTO  + "Tabela" + container.getContainerId() + ".txt";
        GerenciadorDeIO.gravarString(diretorioCompleto, container.print());
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

    private ArrayList<Descritor> popularDescritores(BlocoContainer container, String descritor) {
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

    private ArrayList<Linha> popularLinhas(ArrayList<String> linhas) {
        ArrayList<Linha> tuplas = new ArrayList<Linha>();

        for (int i = 1; i < linhas.size(); i++) {
            String linha = linhas.get(i);
            String[] colunas = linha.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

            Linha tuple = new Linha();
            tuple.adicionarColunas(colunas);
            tuplas.add(tuple);
        }

        return tuplas;
    }

    private ArrayList<BlocoDado> popularBlocos(BlocoContainer container, ArrayList<Linha> tuples) throws IOException {
        ArrayList<BlocoDado> blocos = new ArrayList<BlocoDado>();
        blocos.add(new BlocoDado(container.getContainerId(), this.blocoIdCount++)); // primeiro bloco

        for (Linha linha: tuples) {
            int blocoIndex = container.getBlocoControle().getHeader().getProximoBlocoLivre();
            BlocoDado bloco = blocos.get(blocoIndex / container.getBlocoControle().getHeader().getTamanhoDosBlocos());

            if (podeAdicionarMaisTuple(bloco, linha, container))
                bloco.adicionarTupla(linha);
            else {

                BlocoDado blocoAux = new BlocoDado(container.getContainerId(), this.blocoIdCount++);
                blocoAux.adicionarTupla(linha);

                blocos.add(blocoAux);
                container.adicionarBloco(blocoAux);

                container.getBlocoControle().getHeader().adicionarProximoBlocoLivre();
                this.commit(container);
            }
        }

        return blocos;
    }
    private boolean podeAdicionarMaisTuple(BlocoDado bloco, Linha linha, BlocoContainer container) {
        int tamanhoHeader = 8;
        int tamanhoMaximoBloco = container.getBlocoControle().getHeader().getTamanhoDosBlocos();
        int tamanhoUsadoDoBloco = bloco.getHeader().getTamanhoUsado();
        int tamanhoTupla = linha.getTamanhoCompleto();

        return tamanhoUsadoDoBloco + tamanhoTupla + tamanhoHeader <= tamanhoMaximoBloco;
    }
}