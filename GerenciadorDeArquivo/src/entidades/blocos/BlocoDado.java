package entidades.blocos;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorDeIO;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.awt.color.ICC_ProfileRGB;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class BlocoDado implements IBinary, IPrint {

    private BlocoDadoHeader header;
    private ArrayList<Linha> tuples;
    private int posicaoLRU;

    public BlocoDado(int containerId, int blocoId) {
        this.header = new BlocoDadoHeader(containerId, blocoId);
        this.tuples = new ArrayList<Linha>();
    }

    public BlocoDado(int containerId, int blocoId, ArrayList<Linha> dados) {
        this.header = new BlocoDadoHeader(containerId, blocoId);
        this.tuples = dados;
    }

    public BlocoDado(byte[] bytes) {
        this.header = new BlocoDadoHeader();
        this.tuples = new ArrayList<Linha>();
        this.fromByteArray(bytes);
    }

    public BlocoDado(byte[] bytes, boolean b) {
        this.header = new BlocoDadoHeader(bytes);
        this.tuples = new ArrayList<Linha>();
        if (!b)
            this.fromByteArray(bytes);
    }

    public BlocoDadoHeader getHeader() {
        return header;
    }

    @Override
    public byte[] toByteArray() {

        ByteArrayConcater byteConcater = new ByteArrayConcater(GlobalVariables.TAMANHO_BLOCO);
        byteConcater
                .concat(this.header.toByteArray())
                .concat(this.bytesTuples());

        return byteConcater.getFinalByteArray();
    }

    @Override
    public ArrayList<String> print() {
        ArrayList<String> parse = new ArrayList<String>();
        for (Linha linha : tuples) {
            parse.addAll(linha.print());
            parse.add("\n");
        }
        return parse;
    }

    private byte[] bytesTuples() {
        if (this.tuples.size() == 0)
            return new byte[0];

        ByteArrayConcater bc = new ByteArrayConcater();
        this.tuples.stream().forEach(tuple -> bc.concat(tuple.toByteArray()));
        return bc.getFinalByteArray();
    }

    @Override
    public BlocoDado fromByteArray(byte[] byteArray) {
        this.header = this.header.fromByteArray(ByteArrayUtils.subArray(byteArray, 0, 8));
        this.tuples.addAll(this.linhasFromByteArray(byteArray));

        return this;
    }

    private ArrayList<Linha> linhasFromByteArray(byte[] byteArray) {
        ArrayList<Linha> linhas = new ArrayList<Linha>();
        int indexOndeComecaOsDados = 8;

        while (indexOndeComecaOsDados < byteArray.length && indexOndeComecaOsDados < this.header.getTamanhoUsado()) {
            int tamanhoLinha = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, indexOndeComecaOsDados, 4));
            byte[] linhaBytes = ByteArrayUtils.subArray(byteArray, indexOndeComecaOsDados, tamanhoLinha);

            Linha tuple = new Linha(linhaBytes);
            indexOndeComecaOsDados += tuple.getTamanho();
            linhas.add(tuple);
        }

        return linhas;
    }

    public boolean adicionarTupla(Linha tupla) {
        this.tuples.add(tupla);
        this.header.incrementarTamanhoUsado(tupla.getTamanhoCompleto());

        return true;
    }

    public ArrayList<Linha> getTuples() {
        return this.tuples;
    }

    public int getPosicaoLRU() {
        return posicaoLRU;
    }

    public void setPosicaoLRU(int posicaoLRU) {
        this.posicaoLRU = posicaoLRU;
    }

    public String toString() {
        return "Row Id: " + this.getHeader().getContainerId() + "." + this.getHeader().getBlocoId() + " | Tipo: " + this.header.getTipoBloco().toString() + " | Tuplas: " + this.tuples.size();
    }

    public void atualizar(int offset, int length) throws IOException {
        String diretorio = GerenciadorArquivo.getDiretorio(ContainerId.create(this.getHeader().getContainerId()));
        GerenciadorDeIO.atualizarBytes(diretorio, offset, this.toByteArray());
    }

    public RowId getRowId() {
        return new RowId(this.header.getContainerId(), this.header.getBlocoId());
    }

    public static BlocoDado loadDataBlock(RowId rowId) throws IOException {
        String path = GerenciadorArquivo.getDiretorio(ContainerId.create(rowId.getContainerId()));
        int blockLenth = BlocoControle.getBlockLengthFile(path);
        int offset = BlocoDado.getPosition(rowId);

        return new BlocoDado(GerenciadorDeIO.getBytes(path, offset, blockLenth));
    }

    private static int getPosition(RowId rowId) throws IOException {
        String path = GerenciadorArquivo.getDiretorio(ContainerId.create(rowId.getContainerId()));
        int blockLenth = BlocoControle.getBlockLengthFile(path);
        return blockLenth + ((rowId.getBlocoId() - 1) * blockLenth);
    }
}
