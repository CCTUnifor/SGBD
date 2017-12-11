package entidades.blocos;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorDeIO;
import entidades.index.IndexFileManager;
import exceptions.ContainerNoExistent;
import factories.ContainerId;
import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlocoControle implements IBinary, IPrint {
    public static final int CONTROLLER_BLOCK_LENGTH = 11;

    private BlocoContainerHeader blocoHeader;
    private ArrayList<Descritor> descritores;

    public BlocoControle(int containerId) {
        this.descritores = new ArrayList<Descritor>();
        this.blocoHeader = new BlocoContainerHeader(containerId);
    }

    public BlocoControle(byte[] bytes) {
        this.blocoHeader = new BlocoContainerHeader(bytes);
        this.descritores = new ArrayList<Descritor>();
        if (blocoHeader.getTamanhoDescritor() > 0)
            this.descritores.addAll(this.descritoresFromByteArray(ByteArrayUtils.subArray(bytes, 11, this.blocoHeader.getTamanhoDescritor())));
    }

    public BlocoContainerHeader getHeader() {
        return blocoHeader;
    }
    public int getContainerId() { return this.blocoHeader.getContainerId(); }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater(this.getHeader().getTamanhoDosBlocos());
        bc.concat(this.blocoHeader.toByteArray())
                .concat(this.bytesDescritores());
        return bc.getFinalByteArray();
    }

    @Override
    public ArrayList<String> print() {
        ArrayList<String> parse = new ArrayList<String>();

        for (Descritor descritor : descritores) {
            parse.addAll(descritor.print());
        }
        return parse;
    }

    @Override
    public BlocoControle fromByteArray(byte[] byteArray) {
        this.blocoHeader.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, getBlockLengthFile(byteArray)));
        if (byteArray.length > CONTROLLER_BLOCK_LENGTH)
            this.descritores.addAll(this.descritoresFromByteArray(ByteArrayUtils.subArray(byteArray, CONTROLLER_BLOCK_LENGTH, this.blocoHeader.getTamanhoDescritor())));

        return this;
    }

    public void adicionarDescritor(Descritor descritor, String path) {
        try {
            int tamanhoNovoDescritor = getHeader().getTamanhoDescritorFile(path) + descritor.getTamanhoTotalDescritor();

            GerenciadorDeIO.atualizarBytes(path, CONTROLLER_BLOCK_LENGTH + this.getHeader().getTamanhoDescritorFile(path), descritor.toByteArray());
            GerenciadorDeIO.atualizarBytes(path, 9, ByteArrayUtils.intTo2Bytes(tamanhoNovoDescritor));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void adicionarDescritores(ArrayList<Descritor> descritores) {
        this.descritores.addAll(descritores);
    }

    public List<String> getColumnsName() {
        return this.descritores.stream()
                .filter(x -> x.getTipoDado() == TipoDado.INTEIRO || x.getTipoDado() == TipoDado.STRING)
                .map(Descritor::getNome).collect(Collectors.toList());
    }

    public List<String> getIndicesName() {
        return this.descritores.stream()
                .filter(x -> x.getTipoDado() == TipoDado.PATH)
                .map(Descritor::getNome).collect(Collectors.toList());
    }

    private byte[] bytesDescritores() {
        ByteArrayConcater bc = new ByteArrayConcater();
        for (Descritor descritor : this.descritores) {
            bc.concat(descritor.toByteArray());
        }
        return bc.getFinalByteArray();
    }

    public ArrayList<Descritor> descritoresFromByteArray(byte[] descritoresByteArray) {
        ArrayList<Descritor> descritores = new ArrayList<Descritor>();
        boolean whileTrue = true;
        int proximoIndex = 0;

        while(whileTrue) {
            int tamanho = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(descritoresByteArray, proximoIndex, 4));
            if (tamanho == 0)
                return descritores;

            descritores.add(new Descritor(ByteArrayUtils.subArray(descritoresByteArray, proximoIndex,  tamanho)));

            proximoIndex += tamanho;
            whileTrue = whileTrue && proximoIndex < descritoresByteArray.length;
        }

        return descritores;
    }

    public static int getBlockLengthFile(String path) throws IOException {
        byte[] byteArray = GerenciadorDeIO.getBytes(path, 1, 3);
        return ByteArrayUtils.byteArrayToInt(byteArray);
    }

    public static int getBlockLengthFile(byte[] byteArray) {
        return ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, 1, 3));
    }

    public static BlocoControle getControllerBlock(ContainerId containerId) throws IOException {
        String path = GerenciadorArquivo.getDiretorio(containerId);
        int length = BlocoControle.getBlockLengthFile(path);

        return new BlocoControle(GerenciadorDeIO.getBytes(path, 0, length));
    }

    public ArrayList<Descritor> getDescritores() {
        return descritores;
    }
}
