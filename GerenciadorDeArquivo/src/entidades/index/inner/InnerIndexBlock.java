package entidades.index.inner;

import entidades.GerenciadorDeIO;
import entidades.blocos.RowId;
import entidades.blocos.TipoBloco;
import entidades.index.IndexContainer;
import entidades.index.IndexFileManager;
import entidades.index.abstrations.HeaderIndexBlock;
import entidades.index.abstrations.IndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.IncorrectTypeToPushPointerException;
import exceptions.innerBlock.*;
import factories.BlocoId;
import factories.ContainerId;
import interfaces.IBinary;
import utils.ByteArrayConcater;
import utils.ByteArrayUtils;
import utils.GlobalVariables;

import java.io.IOException;
import java.util.ArrayList;

public class InnerIndexBlock extends IndexBlock implements IBinary {

    private InnerIndexBlock() {
        super();
        this.header = new InnerHeaderIndexBlock();
    }

    public InnerIndexBlock(int numberOfChildrens) {
        super();
        this.header = new InnerHeaderIndexBlock(numberOfChildrens);
    }

    public InnerIndexBlock(ContainerId containerId, BlocoId blockId, int numberOfChildrens) {
        super();
        this.header = new InnerHeaderIndexBlock(containerId, blockId, numberOfChildrens);
    }

    public InnerIndexBlock(byte[] blockBytes) {
        super();
        this.header = new InnerHeaderIndexBlock(blockBytes);
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayConcater byteConcater = new ByteArrayConcater(GlobalVariables.TAMANHO_BLOCO);
        byteConcater
                .concat(super.toByteArray());

        return byteConcater.getFinalByteArray();
    }

    @Override
    public InnerIndexBlock fromByteArray(byte[] byteArray) {
        super.fromByteArray(byteArray);
        if (byteArray.length > InnerHeaderIndexBlock.HEADER_LENGTH)
            System.out.println("fromByteArray para o conteudo do InnerIndexBlock");

        return this;
    }

    public InnerHeaderIndexBlock getHeader() {
        return (InnerHeaderIndexBlock) this.header;
    }

    public static InnerIndexBlock getJustWithHeader(byte[] blockBytes) {
        return new InnerIndexBlock().fromByteArray(blockBytes);
    }

    public void pushColumnValue(String value) throws ContainerNoExistent, IOException, InnerIndexBlockFullCollumnValueException {
        if (this.header.getContainerId() == 0 || this.header.getContainerId() == -1)
            throw new ContainerNoExistent();

        this.pushColumnValue(new CollumnValue(value));
    }

    public void pushColumnValue(CollumnValue col) throws IOException, ContainerNoExistent, InnerIndexBlockFullCollumnValueException {
        String indexPath = IndexFileManager.getDiretorio(header.getContainerId());
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByCollumnValue();

        if (!this.getHeader().existsSpaceForNewValueCollumn(col))
            throw new InnerIndexBlockFullCollumnValueException();

        GerenciadorDeIO.atualizarBytes(indexPath, offset, col.toByteArray());
        this.incrementCollumnCount(col);

//        this.switchInOrder(col);
    }

    private void switchInOrder(CollumnValue col) throws IOException, ContainerNoExistent {
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getMaxLengthChildren() + 1;
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());

        int offsetMax = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH + this.getHeader().getBytesUsedByCollumnValue();

        CollumnValue colToSwitch = null;

        while (offset < offsetMax) {
            int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH));
            CollumnValue currentCol = new CollumnValue(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH + length));

            offset += CollumnValue.LENGTH + length;

            if (col.compareTo(currentCol) < 0) {
                colToSwitch = currentCol;
                break;
            }
        }

        if (colToSwitch != null) {
            GerenciadorDeIO.atualizarBytes(indexPath, offset, col.toByteArray());

            while (offset < offsetMax) {
                int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH));
                CollumnValue currentCol = new CollumnValue(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH + length));

                GerenciadorDeIO.atualizarBytes(indexPath, offset, colToSwitch.toByteArray());
                colToSwitch = currentCol;

                offset += CollumnValue.LENGTH + length;
            }

        }
    }

    private void incrementCollumnCount(CollumnValue col) throws IOException, ContainerNoExistent {
        int newIncrementedValue = this.getHeader().getBytesUsedByCollumnValue() + col.getFullLength();
        this.getHeader().setLastByteUsedByCollumnValue(newIncrementedValue);
        String indexPath = IndexFileManager.getDiretorio(this.getHeader().getContainerId());

        int offset = this.getHeader().getBlockPosition() + 5;
        GerenciadorDeIO.atualizarBytes(indexPath, offset, ByteArrayUtils.intTo3Bytes(newIncrementedValue));
    }


    public CollumnValue loadCollumnValue(int i) {
        return this.getCollumns().get(i);
    }

    public void pushPointerToChild(BlocoId refToChild) throws IOException, ContainerNoExistent, IndexLeafBlockCannotPushPointerChildException, InnerIndexBlockPointerToChildIsFullException, IndexBlockNotFoundException, IncorrectTypeToPushPointerException {
        if (this.getHeader().isLeaf())
            throw new IndexLeafBlockCannotPushPointerChildException();

        RowId newChild = RowId.create(this.getHeader().getContainerId(), refToChild.getValue());
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());
        int blockPosition = HeaderIndexBlock.getBlockPosition(newChild);
        int blockTipoOffset = blockPosition + 4;

        TipoBloco tipoBloco = ByteArrayUtils.byteArrayToEnum(ByteArrayUtils.subArray(GerenciadorDeIO.getBytes(indexPath), blockTipoOffset, 1), TipoBloco.values());

        IndexBlock block;
        if (tipoBloco == TipoBloco.INDEX_INNER)
            block = IndexContainer.loadInnerIndexBlock(newChild);
        else if (tipoBloco == TipoBloco.INDEX_LEAF)
            block = IndexContainer.loadLeafIndexBlock(newChild);
        else
            throw new IncorrectTypeToPushPointerException();

        // TODO - verificar se ainda pode adicionar
        int offset = lastPointerChildFree();
//        if (offset < 0)
//            throw new InnerIndexBlockPointerToChildIsFullException();

        GerenciadorDeIO.atualizarBytes(indexPath, offset, ByteArrayUtils.intToBytes(refToChild.getValue()));
    }

    public int getLengthOfChildren() throws IOException, ContainerNoExistent {
        return (lastPointerChildFree() - (this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH)) / InnerHeaderIndexBlock.POINTER_LENGTH;
    }

    private int lastPointerChildFree() throws IOException, ContainerNoExistent {
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH;
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());

        int offsetMax = offset + this.getHeader().getMaxLengthChildren();

        while (offset < offsetMax) {
            int blockIdFromCurrentChild = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, InnerHeaderIndexBlock.POINTER_LENGTH));
            if (blockIdFromCurrentChild == 0 || blockIdFromCurrentChild == -1)
                return offset;
            offset += InnerHeaderIndexBlock.POINTER_LENGTH;
        }

        return offset;
    }

    public IndexBlock getChildren(int i) {
        int offset = 0;
        try {
            offset = getPointerChildOffset(i);

            if (offset < 0)
                throw new IndexBlockNotFoundException();
            String path = IndexFileManager.getDiretorio(this.getHeader().getContainerId());
            int blockIdFinded = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(path, offset, InnerHeaderIndexBlock.POINTER_LENGTH));

            BlocoId block = BlocoId.create(blockIdFinded);
            byte[] blockBytes = IndexContainer.loadIndexBlockBytes(RowId.create(this.getHeader().getContainerId(), blockIdFinded));


            TipoBloco type = ByteArrayUtils.byteArrayToEnum(ByteArrayUtils.subArray(blockBytes, 4, 1), TipoBloco.values());

            if (type == TipoBloco.INDEX_INNER)
                return IndexContainer.loadInnerIndexBlock(RowId.create(this.getHeader().getContainerId(), block.getValue()));
            else if (type == TipoBloco.INDEX_LEAF)
                return IndexContainer.loadLeafIndexBlock(RowId.create(this.getHeader().getContainerId(), block.getValue()));

        } catch (IOException | ContainerNoExistent | IndexBlockNotFoundException e) {
            return null;
        }
        return null;
    }

    private int getPointerChildOffset(int i) throws IOException, ContainerNoExistent {
        int offset = this.getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH;
        String indexPath = IndexFileManager.getDiretorio(this.header.getContainerId());

        int offsetMax = offset + this.getHeader().getMaxLengthChildren();
        int count = 0;

        while (offset < offsetMax && count <= i) {
            int blockIdFromCurrentChild = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, InnerHeaderIndexBlock.POINTER_LENGTH));
            if (blockIdFromCurrentChild != 0 && count == i)
                return offset;
            offset += InnerHeaderIndexBlock.POINTER_LENGTH;
            count++;
        }

        return -1;
    }

    public ArrayList<CollumnValue> getCollumns() {
        ArrayList<CollumnValue> columns = new ArrayList<CollumnValue>();
        try {
            int i = 0;
            int offset = getHeader().getBlockPosition() + InnerHeaderIndexBlock.HEADER_LENGTH  + getHeader().getMaxLengthChildren();
            String indexPath = IndexFileManager.getDiretorio(this.getHeader().getContainerId());

            while (i <  getHeader().getBytesUsedByCollumnValue()) {
                int length = ByteArrayUtils.byteArrayToInt(GerenciadorDeIO.getBytes(indexPath, offset, CollumnValue.LENGTH));

                CollumnValue key = new CollumnValue(GerenciadorDeIO.getBytes(indexPath, offset, length));
                columns.add(key);

                offset += key.getFullLength();
                i += key.getFullLength();
            }
        } catch (ContainerNoExistent | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean haveSpace() {
        return this.getCollumns().size() < getHeader().getNumberMaxKeys();
    }

    public String getCollumnValues() {
        StringBuilder x = new StringBuilder();
        for (CollumnValue col :
                this.getCollumns()) {
            x.append(col.getCollumnValue());
        }

        return x.toString();
    }
}
