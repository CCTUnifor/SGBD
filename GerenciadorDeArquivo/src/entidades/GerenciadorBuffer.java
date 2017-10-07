package entidades;

import entidades.LeastRecentlyUsed.LRU;
import entidades.blocos.BlocoDado;

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

    private Block addBlock(Block block) {
        if(checkSize())
        {
            block.setPosition((this.countPages));
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
    public Block searchBlock(Block block) {
        Block result = this.lru.search(block);
        if(result == null)
        {
            this.miss++;
            return this.addBlock(block);
        }
        else{
            this.hit++;
            return this.blocks[result.getPosition()];
        }
    }

    public void viewBuffer_LRU()
    {
        this.lru.viewLRU();
    }

    private boolean checkSize(){
        return this.countPages < this.blocks.length ? true : false;
    }

    public float taxaAcerto(){
        return 1 - ((float)this.hit / (float)(this.hit+this.miss));
    }

    public int getHit(){
        return this.hit;
    }

    public int getMiss()
    {
        return this.miss;
    }
}
