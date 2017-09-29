package entidades.blocos;

import interfaces.IBinary;
import interfaces.IPrint;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

public class Coluna implements IBinary, IPrint {
    private int tamanho;
    private byte[] dados;
    private String informacao; // ignorado no parse para byte.

    public Coluna(byte[] bytes) {
        this.fromByteArray(bytes);
    }

    public int getTamanho() {
        return  this.tamanho;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();

        bc.concat(ByteArrayUtils.intTo2Bytes(this.tamanho))
          .concat(this.dados);

        return bc.getFinalByteArray();
    }

    @Override
    public String print() {
        return getInformacao();
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
