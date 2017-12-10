package entidades.index;

import entidades.GerenciadorDeIO;
import entidades.blocos.BlocoControle;
import entidades.index.abstrations.HeaderIndexBlock;
import entidades.index.abstrations.IndexBlock;
import entidades.index.inner.InnerIndexBlock;
import exceptions.ContainerNoExistent;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;

import java.io.IOException;

public class IndexContainer implements IBinary {
    private int NEXT_BLOCK_ID = 1;

    private BlocoControle blocoControle;
    private IndexBlock block;


    IndexContainer(int containerId) {
        // TODO atualizar o valor do NEXT_BLOCK_ID
        blocoControle = new BlocoControle(containerId);

    }

    public IndexContainer(byte[] containerBytes) {
        blocoControle = new BlocoControle(containerBytes);
        this.NEXT_BLOCK_ID = blocoControle.getHeader().getProximoBlocoLivre() / blocoControle.getHeader().getTamanhoDosBlocos() + 1;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater bc = new ByteArrayConcater();
        bc.concat(blocoControle.toByteArray())
                .concat(this.blockToBytes());

        return bc.getFinalByteArray();
    }

    @Override
    public IndexContainer fromByteArray(byte[] byteArray) {
        this.blocoControle.fromByteArray(byteArray);
        this.blockFromBytesArray();

        return this;
    }

    private void blockFromBytesArray() {
        // TODO

    }

    private byte[] blockToBytes() {
        // TODO
        return new byte[0];
    }

    public static IndexContainer getJustContainer(int indexId) throws IOException, ContainerNoExistent {
        String diretorio = IndexFileManager.getDiretorio(indexId);
        byte[] containerBytes = GerenciadorDeIO.getBytes(diretorio, 0, BlocoControle.CONTROLLER_BLOCK_LENGTH);
        return new IndexContainer(containerBytes);
    }

    public int incrementNextFreeBlock() throws IOException, ContainerNoExistent {
        String indexContainer = IndexFileManager.getDiretorio(this.getBlocoControle().getContainerId());
        int old = this.getNextBlock();
        this.NEXT_BLOCK_ID = ((this.getBlocoControle().getHeader().getTamanhoDosBlocos() * old ) / this.getBlocoControle().getHeader().getTamanhoDosBlocos()) + 1;
        this.blocoControle.getHeader().setProximoBlocoLivre(this.NEXT_BLOCK_ID * this.getBlocoControle().getHeader().getTamanhoDosBlocos());

        GerenciadorDeIO.atualizarBytes(indexContainer, 5, ByteArrayUtils.intToBytes(this.blocoControle.getHeader().getProximoBlocoLivre()));
        return this.NEXT_BLOCK_ID;
    }

    public BlocoControle getBlocoControle() {
        return blocoControle;
    }

    public static IndexBlock getIndexBlock(int indexId, int blockId) throws IOException, ContainerNoExistent {
        IndexContainer ic = getJustContainer(indexId);
        String indexPath = IndexFileManager.getDiretorio(indexId);

        int blockLength = ic.getBlocoControle().getHeader().getTamanhoDosBlocos();
        int offset = BlocoControle.CONTROLLER_BLOCK_LENGTH + (blockLength * (blockId - 1));

        byte[] blockBytes = GerenciadorDeIO.getBytes(indexPath, offset, blockLength);
        return new InnerIndexBlock(blockBytes);
    }

    public int getNextBlock() throws IOException, ContainerNoExistent {
        String indexContainer = IndexFileManager.getDiretorio(this.getBlocoControle().getContainerId());
        this.NEXT_BLOCK_ID = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexContainer, 5, 4)) / this.getBlocoControle().getHeader().getTamanhoDosBlocos();
        return this.NEXT_BLOCK_ID;
    }
}
