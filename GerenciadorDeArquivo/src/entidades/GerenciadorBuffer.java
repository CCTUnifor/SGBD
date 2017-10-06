package entidades;

import entidades.LeastRecentlyUsed.LRU;
import entidades.blocos.BlocoDado;

public class GerenciadorBuffer {
    private BlocoDado[] blocks;
    private int countPages;
    private LRU lru;

    public GerenciadorBuffer(int sizeBuffer)
    {
        this.blocks = new BlocoDado[sizeBuffer];
        this.lru = new LRU();
        this.countPages = 0;
    }

    public void addBlock(BlocoDado block) {
        if(checkSize())
        {
            block.setPosicaoLRU((this.countPages+1));
            this.blocks[this.countPages] = block;
            this.lru.addBlock(block);
            this.countPages++;
        }
        else
        {
            int positionBlockDelete = this.lru.removeBlock(block);
            if(positionBlockDelete != -1)
            {
                this.blocks[positionBlockDelete] = block;
            }
            else
            {
                System.out.println("LRU vazia !!!");
            }
        }
    }
    public boolean checkSize(){
        return this.countPages < this.blocks.length ? true : false;
    }
}
