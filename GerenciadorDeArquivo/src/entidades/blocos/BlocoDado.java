package entidades.blocos;

import entidades.GerenciadorArquivo;
import entidades.GerenciadorDeIO;
import exceptions.ContainerNoExistentException;
import factories.ContainerId;
import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class BlocoDado implements IBinary, IPrint {

    private BlocoDadoHeader header;
    private ArrayList<Tuple> tuples; //values
    // ponteiros quando for de dados -> não gravar no arquivo, quando for index, gravar no arquivo
    private int posicaoLRU;

    public BlocoDado(int containerId, int blocoId) {
        this.header = new BlocoDadoHeader(containerId, blocoId);
        this.tuples = new ArrayList<Tuple>();
    }

    public BlocoDado(int containerId, int blocoId, ArrayList<Tuple> dados) {
        this.header = new BlocoDadoHeader(containerId, blocoId);
        this.tuples = dados;
    }

    public BlocoDado(byte[] bytes) {
        this.header = new BlocoDadoHeader();
        this.tuples = new ArrayList<Tuple>();
        this.fromByteArray(bytes);
    }

    public BlocoDadoHeader getHeader() {
        return header;
    }

    @Override
    public byte[] toByteArray() {

        ByteArrayConcater byteConcater = new ByteArrayConcater(BlocoDadoHeader.TABLE_BLOCK_LENGTH);
        byteConcater
                .concat(this.header.toByteArray())
                .concat(this.bytesTuples());

        return byteConcater.getFinalByteArray();
    }

    @Override
    public ArrayList<String> print() {
        ArrayList<String> parse = new ArrayList<String>();
        for (Tuple linha : tuples){
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

        return  this;
    }

    private ArrayList<Tuple> linhasFromByteArray(byte[] byteArray) {
        ArrayList<Tuple> linhas = new ArrayList<Tuple>();
        int indexOndeComecaOsDados = 8;

        while(indexOndeComecaOsDados < byteArray.length && indexOndeComecaOsDados < this.header.getTamanhoUsado()) {
            int tamanhoLinha = ByteArrayUtils.byteArrayToInt(ByteArrayUtils.subArray(byteArray, indexOndeComecaOsDados, 4));
            byte[] linhaBytes = ByteArrayUtils.subArray(byteArray, indexOndeComecaOsDados, tamanhoLinha);

            Tuple tuple = new Tuple(linhaBytes);
            indexOndeComecaOsDados += tuple.getTamanho();
            linhas.add(tuple);
        }

        return linhas;
    }

    public boolean adicionarTupla(Tuple tupla) {
        this.tuples.add(tupla);
        this.header.incrementarTamanhoUsado(tupla.getTamanhoCompleto());

        return true;
    }

    public ArrayList<Tuple> getTuples() {
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

    public void atualizar(int offset, int length) throws ContainerNoExistentException {
        String diretorio = GerenciadorArquivo.getDiretorio(this.getHeader().getContainerId());
        try {
            GerenciadorDeIO.atualizarBytes(diretorio, offset, this.toByteArray());
        } catch (FileNotFoundException e) {
            throw new ContainerNoExistentException("");
        }
    }

    public RowId getRowId() {
        return  new RowId(this.header.getContainerId(), this.header.getBlocoId());
    }
}
