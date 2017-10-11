package entidades;

import entidades.LeastRecentlyUsed.LRU;
import entidades.blocos.BlocoDado;
import entidades.blocos.RowId;

public class GerenciadorBuffer {
    private BlocoDado[] blocks;
    private int countPages;
    private LRU lru;
    private int hit;
    private int miss;

    public GerenciadorBuffer(int sizeBuffer)
    {
        this.blocks = new BlocoDado[sizeBuffer];
        this.lru = new LRU();
        this.countPages = 0;
        this.hit = 0;
        this.miss = 0;
    }

    public BlocoDado addBlock(BlocoDado block) {
        if(checkSize())
        {
            block.setPosicaoLRU((this.countPages));
            this.blocks[this.countPages] = block;
            this.lru.addBlock(block);
            return this.blocks[this.countPages++];
        }
        else
        {
            int positionBlockDelete = this.lru.removeBlock(block);
            if(positionBlockDelete != -1)
            {
                this.blocks[positionBlockDelete] = block;
                return this.blocks[positionBlockDelete];
            }
            else//este else não ocorre, pois a lru só é chama quando o buffer está cheio e neste caso lru já tem elementos incluidos
            {
                System.out.println("LRU vazia !!!");
            }
        }
        return null;
    }

    public BlocoDado existRowId(RowId rowId) {
        BlocoDado result = this.lru.search(rowId);
        if (result != null)
            this.hit++;
        else if (!checkSize())
            this.miss++;

        return result;
    }

    public void viewBuffer_LRU()
    {
        this.lru.viewLRU();
    }

    private boolean checkSize(){
        return this.countPages < this.blocks.length ? true : false;
    }

    public float taxaAcerto(){
        return ((float)this.hit / (float)(this.hit + this.miss))*100;
    }

    public int getHit(){
        return this.hit;
    }

    public int getMiss()
    {
        return this.miss;
    }
}
