package entidades.blocos;

import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

public class Coluna implements IBinary {
    private int tamanho;
    private byte[] dados;
    private String informacao; // ignorado no parse para byte.

    public Coluna(byte[] bytes) {
        this.fromByteArray(bytes);
    }

    public int getTamanho() {
        return  this.tamanho + 2;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();

        bc.concat(ByteArrayUtils.intTo2Bytes(this.tamanho))
          .concat(this.dados);

        return bc.getFinalByteArray();
    }

    @Override
    public Coluna fromByteArray(byte[] byteArray) {
        this.dados = byteArray;
        this.tamanho = byteArray.length;
        this.informacao = new String(byteArray);

        return this;
    }

    public String getInformacao() { return this.informacao; }
}
