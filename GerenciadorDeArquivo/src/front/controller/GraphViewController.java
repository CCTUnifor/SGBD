package front.controller;

import entidades.arvoreBMais.ArvoreBPlus;
import entidades.blocos.RowId;
import entidades.blocos.TipoBloco;
import entidades.index.IndexContainer;
import entidades.index.IndexFileManager;
import entidades.index.abstrations.IndexBlock;
import entidades.index.inner.InnerIndexBlock;
import entidades.index.leaf.LeafIndexBlock;
import exceptions.ContainerNoExistent;
import exceptions.innerBlock.IndexBlockNotFoundException;
import factories.ContainerId;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GraphViewController implements Initializable {


    public static final double HEIGHT = 700;
    public static final double WIDTH = 1300;

    static GraphicsContext gc;

    private ContainerId indexSelected;
    private IndexFileManager indexFileManager;

    @FXML
    TreeView treeView;
    private ArvoreBPlus arvoreBMais;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.indexFileManager = new IndexFileManager();


        try {
            IndexContainer container = IndexContainer.getJustContainer(this.indexSelected);
            InnerIndexBlock root = (InnerIndexBlock) container.getRoot();

            TreeItem<String> rootItem = new TreeItem<String>(root.getCollumnValues());

            int quantidadeDeFilhos = root.getNumberMaxChildren();
            for (int i = 0; i < quantidadeDeFilhos; i++) {
                IndexBlock child = root.getChildren(i);
                if (child == null)
                    continue;

                if (child.getHeader().getBlockType() == TipoBloco.INDEX_LEAF) {
                    RowId x = RowId.create(child.getHeader().getContainerId(), child.getHeader().getBlockId());
                    LeafIndexBlock leafIndexBlock = (LeafIndexBlock) IndexContainer.loadLeafIndexBlock(x);

                    TreeItem<String> leaf = new TreeItem<String>("RowId: " + leafIndexBlock.getHeader().getRowIdData().toString());
                    leaf.setExpanded(false);
                    rootItem.getChildren().add(leaf);
                } else if (child.getHeader().getBlockType() == TipoBloco.INDEX_INNER) {

                }
            }


            this.treeView.setRoot(rootItem);

        } catch (IOException | ContainerNoExistent | IndexBlockNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void print(int nivel, String value) {

    }

    public void setIndex(ContainerId index) {
        this.indexSelected = index;
    }
}