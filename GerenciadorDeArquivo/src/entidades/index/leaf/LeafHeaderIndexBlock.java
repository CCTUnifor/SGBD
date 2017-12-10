package entidades.index.leaf;

import entidades.blocos.TipoBloco;
import entidades.index.abstrations.HeaderIndexBlock;

public class LeafHeaderIndexBlock extends HeaderIndexBlock {
    public LeafHeaderIndexBlock(byte[] byteArray) {
       super(TipoBloco.INDEX_LEAF);
    }
}
