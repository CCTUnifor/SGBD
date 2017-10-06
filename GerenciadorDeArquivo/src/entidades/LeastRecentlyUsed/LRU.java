package entidades.LeastRecentlyUsed;

import entidades.blocos.BlocoDado;

public class LRU {
    private Node first;
    private Node last;

    public LRU() {
        this.first = null;
        this.last = null;
    }

    public void addBlock(BlocoDado block)
    {
        Node newNodeLRU = new Node(block);
        if(this.first == null)
        {
            this.first = newNodeLRU;
            this.last = newNodeLRU;
        }
        else
        {
            newNodeLRU.setNextNode(this.first);
            this.first = newNodeLRU;
        }
    }

    public int removeBlock(BlocoDado blockAdd)
    {
        int positionBlockDelete = -1;
        if(!empty())
        {
            if(this.first == this.last)
            {
                blockAdd.setPosicaoLRU(1);
                this.first = null;
                this.last = null;
                positionBlockDelete = 0;
                addBlock(blockAdd);
            }
            else
            {
                Node node = this.first;
                while((node.getNextNode() != null) && (node.getNextNode() != this.last)) node = node.getNextNode();
                positionBlockDelete = node.getNextNode().getBlock().getPosicaoLRU();
                node.setNextNode(null);
                this.last = node;
                blockAdd.setPosicaoLRU(positionBlockDelete);
                addBlock(blockAdd);
            }
            return  positionBlockDelete;
        }
        else
        {
            return positionBlockDelete;
        }
    }

    public boolean empty()
    {
        return this.first == null ? true : false;
    }
}
