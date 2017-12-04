package entidades;

import entidades.blocos.*;
import exceptions.ContainerNoExistent;
import factories.ContainerId;
import interfaces.IFileManager;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private BlocoContainer criarBlocoContainer(byte[] bytes) {
        BlocoContainer container = this.criarBlocoContainer();
        container.fromByteArray(bytes);

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
    public HashMap<ContainerId, String> getContainers() throws IOException {
        String path = System.getProperty("user.dir") + "\\" + GlobalVariables.LOCAL_ARQUIVO_FINAL_BINARIO;
        File file = new File(path);
        File[] directores = file.listFiles();
        HashMap<ContainerId, String> containers = new HashMap<ContainerId, String>();

        for (int i = 0; i < directores.length; i++) {
            File containerFile = directores[i];
            byte[] idBytes = GerenciadorDeIO.getBytes(containerFile.getAbsolutePath(), 0, 1);
            int idInt = ByteArrayUtils.byteArrayToInt(idBytes);

            ContainerId id = ContainerId.create(idInt);
            containers.put(id, containerFile.getName());
        }

        return containers;
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
//        container.adicionarBloco(this.criarBlocoDeDado(container.getContainerId()));

        GerenciadorDeIO.gravarBytes(container.getDiretorio(), container.toByteArray());
        return container;
    }

    @Override
    public BlocoDado lerBloco(RowId rowId) throws IOException {
        String diretorio = GlobalVariables.LOCAL_ARQUIVO_FINAL_BINARIO + "/Tabela" + rowId.getContainerId() + ".bin";
        byte[] containerBytes = GerenciadorDeIO.getBytes(diretorio, 0, 11);
        BlocoContainer container = this.criarBlocoContainer(containerBytes);

        int indexOndeComecaOBlocoDeDados = 11 + container.getBlocoControle().getHeader().getTamanhoDescritor();
        int tamanhoBloco = container.getBlocoControle().getHeader().getTamanhoDosBlocos();

        byte[] blocoBytes = GerenciadorDeIO.getBytes(diretorio, indexOndeComecaOBlocoDeDados + (tamanhoBloco * (rowId.getBlocoId() - 1)), tamanhoBloco);
        if (blocoBytes == null)
            return null;

        return new BlocoDado(blocoBytes);
    }

    @Override
    public void gravarBloco(BlocoContainer container, BlocoDado bloco) throws FileNotFoundException {
        int offset = 0 + 11 + container.getBlocoControle().getHeader().getTamanhoDescritor() + ((bloco.getHeader().getBlocoId() -1) * container.getBlocoControle().getHeader().getTamanhoDosBlocos());
        int length = container.getBlocoControle().getHeader().getTamanhoDosBlocos();

        container.atualizar();
        bloco.atualizar(offset, length);
    }

    @Override
    public BlocoDado adicionarLinha(BlocoContainer container, String linha) throws IOException, ContainerNoExistent {
        String[] colunas = linha.split(GlobalVariables.REGEX_SEPARADOR_COLUNA);

        Linha tuple = new Linha();
        tuple.adicionarColunas(colunas);

        int proximoBlocoLivre = container.getBlocoControle().getHeader().getProximoBlocoLivre();
        int blocoId = (proximoBlocoLivre / container.getBlocoControle().getHeader().getTamanhoDosBlocos()) + 1;
        BlocoDado bloco = this.lerBloco(new RowId(container.getBlocoControle().getHeader().getContainerId(), blocoId));

        if (bloco == null){
            bloco = this.criarBlocoDeDado(container.getContainerId());
            container.adicionarBloco(bloco);
        }

        if (podeAdicionarMaisTuple(bloco, tuple, container))
            bloco.adicionarTupla(tuple);
        else {
            BlocoDado blocoAux = new BlocoDado(container.getContainerId(), this.blocoIdCount++);
            blocoAux.adicionarTupla(tuple);

            container.adicionarBloco(blocoAux);
            container.getBlocoControle().getHeader().adicionarProximoBlocoLivre();

            return blocoAux;
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

    public List<String> getDescritores(ContainerId containerId) {
        BlocoControle controle = new BlocoControle(containerId.getValue());
        String diretorio = GlobalVariables.LOCAL_ARQUIVO_FINAL_BINARIO + "Tabela" + containerId .getValue()+ ".bin";

        byte[] bytes = new byte[0];
        try {
            bytes = GerenciadorDeIO.getBytes(diretorio);
        } catch (FileNotFoundException e) {
            System.out.println("Não foi achado o Container: " + containerId.getValue());
            return null;
        }
        controle.fromByteArray(bytes);
        return controle.getDescritoresName();
    }
}