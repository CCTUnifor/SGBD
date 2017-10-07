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
        if(this.first == null) {
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
            if(this.first == this.last)//este if não vai ser executado devido remove acontecer quando o buffer estiver cheio
            {
                blockAdd.setPosicaoLRU(0);
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
                if(node.getNextNode() != null)
                {
                    node.setNextNode(node.getNextNode().getNextNode());
                }
                else
                {
                    node.setNextNode(null);
                }
                this.last = node;
                blockAdd.setPosicaoLRU(positionBlockDelete);
                addBlock(blockAdd);
            }
            return  positionBlockDelete;
        }
        else //este else não ocorre, pois a lru só é chama quando o buffer está cheio e neste caso lru já tem elementos incluidos
        {
            return positionBlockDelete;
        }
    }

    public BlocoDado search(BlocoDado block)
    {
        if(!empty())
        {
            if (this.first.getBlock().equals(block)) {
                return this.first.getBlock();
            } else {
                Node node = this.first.getNextNode();
                Node ant = this.first;
                while (node != null) {
                    if (node.getBlock().equals(block)) {
                        if (node.getNextNode() != null) {
                            this.addBlock(node.getBlock());
                            ant.setNextNode(node.getNextNode());
                            node.setNextNode(null);
                        } else {
                            this.addBlock(node.getBlock());
                            ant.setNextNode(null);
                            this.last = ant;
                        }
                        return this.first.getBlock();
                    }
                    ant = node;
                    node = node.getNextNode();
                }
            }
        }
        return null;
    }
    public void viewLRU()
    {
        for (Node node = this.first; node != null; node = node.getNextNode())
        {
            System.out.println(node.getBlock().getHeader().getBlocoId()+" - "+node.getBlock().getPosicaoLRU());
        }
    }
    private boolean empty()
    {
        return this.first == null ? true : false;
    }
}
