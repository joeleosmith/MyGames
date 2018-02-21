package Player;

import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.model.*;
import Property.*;
import com.heagy.util.*;

public class AssetMaintDialog extends Dialog
{
  private Frame aParentWindow;
  private GraphLocation aCurrentItem;
  private Player aPlayer;
  private static final int WINDOW_WIDTH = 390;
  private static final int WINDOW_HEIGHT = 330;


  Panel MainPanel = new Panel();
  Button cbOK = new Button();
  Button cbSell = new Button();
  SplitPanel splitPanel1 = new SplitPanel();
  TreeControl aTree = new TreeControl();
  BevelPanel InfoPanel = new BevelPanel();
  LabelControl tPlayerLabel = new LabelControl();
  LabelControl tPlayerValue = new LabelControl();
  LabelControl tCashLabel = new LabelControl();
  LabelControl tCashValue = new LabelControl();
  LabelControl tAmtOwedLabel = new LabelControl();
  LabelControl tAmtOwedValue = new LabelControl();
  LabelControl tNetWorthLabel = new LabelControl();
  LabelControl tNetWorthValue = new LabelControl();
  LabelControl tAmtShortLabel = new LabelControl();
  LabelControl tAmtShortValue = new LabelControl();
  GraphLocation aRoot, aBranch;
  BorderLayout MainPanelLayout = new BorderLayout();
  BevelPanel InfoDataPanel = new BevelPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  LabelControl tSaleValue = new LabelControl();

  public AssetMaintDialog(Frame frame, Player aPlayer, int iReason)
  {
    super(frame, "Asset Maintenance", true);
  	this.aParentWindow = frame;
    this.aPlayer = aPlayer;
    tPlayerValue.setText(aPlayer.sName);
    tCashValue.setText(Formatting.money(aPlayer.iCash));
    tAmtOwedValue.setText(Formatting.money(aPlayer.iCashNeeded));
    tNetWorthValue.setText(Formatting.money(aPlayer.getNetWorth()));
    try
    {
      jbInit();
      aPlayer.loadAssets(aTree);
      aRoot = aTree.getRoot();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    add(MainPanel);
    pack();
  }

  public void show()
  {
      // Select the first item
    aTree.setSubfocus(aRoot);
    aTree_selectionChanged(new GraphSelectionEvent(aTree.getSelection(), 1));
    if (! aPlayer.bBankrupt)
    {
      MainPanel.requestFocus();
      aTree.requestFocus();
      super.show();
    }
  }

  private void jbInit() throws Exception
  {
      // declare Fonts to be used
    Font aNormalFont = new Font("Dialog", 1, 12);

    MainPanel.setLayout(MainPanelLayout);
    MainPanel.setSize(new Dimension(510, 427));
    cbOK.setLabel("OK");
    cbSell.addActionListener(new AssetMaintDialog_cbSell_actionAdapter(this));
    cbOK.addActionListener(new AssetMaintDialog_cbOK_actionAdapter(this));
    splitPanel1.setBackground(SystemColor.control);
    aTree.setEditInPlace(false);
    aTree.setExpandByDefault(true);
    aTree.addKeyListener(new java.awt.event.KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        aTree_keyPressed(e);
      }
    });
    aTree.addSelectionListener(new com.borland.jbcl.model.GraphSelectionAdapter()
    {
      public void selectionChanged(GraphSelectionEvent e)
      { aTree_selectionChanged(e); }
    });
    tPlayerLabel.setAlignment(Label.RIGHT);
    tPlayerLabel.setForeground(Color.blue);
    tPlayerLabel.setFont(aNormalFont);
    tPlayerLabel.setText("Player:");
    tPlayerValue.setFont(aNormalFont);
    tAmtOwedLabel.setFont(aNormalFont);
    tAmtOwedLabel.setForeground(Color.blue);
    tAmtOwedLabel.setAlignment(Label.RIGHT);
    tAmtOwedLabel.setText("Amount Owed:");
    tAmtOwedValue.setFont(aNormalFont);
    tNetWorthLabel.setFont(aNormalFont);
    tNetWorthLabel.setForeground(Color.blue);
    tNetWorthLabel.setAlignment(Label.RIGHT);
    tNetWorthLabel.setText("Net Worth:");
    tNetWorthValue.setFont(aNormalFont);
    InfoDataPanel.setBevelOuter(BevelPanel.LOWERED);
    tSaleValue.setAlignment(Label.CENTER);
    tSaleValue.setText("");
    InfoDataPanel.setBevelInner(BevelPanel.LOWERED);
    tCashLabel.setFont(aNormalFont);
    tCashLabel.setForeground(Color.blue);
    tCashLabel.setAlignment(Label.RIGHT);
    tCashLabel.setText("Available Cash:");
    tCashValue.setFont(aNormalFont);
    tAmtShortLabel.setFont(aNormalFont);
    tAmtShortLabel.setForeground(Color.red);
    tAmtShortLabel.setAlignment(Label.RIGHT);
    tAmtShortLabel.setText("Cash Needed:");
    tAmtShortValue.setForeground(Color.red);
    tAmtShortValue.setFont(aNormalFont);

    this.addWindowListener(new AssetMaintDialog_this_windowAdapter(this));
    InfoPanel.setBevelInner(BevelPanel.FLAT);
    InfoPanel.setEnabled(false);
    InfoPanel.setLayout(borderLayout1);
    MainPanel.add(splitPanel1, BorderLayout.CENTER);
    splitPanel1.add(aTree, new PaneConstraints("treeControl1", "treeControl1", PaneConstraints.ROOT, 0.54f));
    splitPanel1.add(InfoPanel, new PaneConstraints("InfoPanel", "treeControl1", PaneConstraints.RIGHT, 0.55f));
    InfoPanel.add(InfoDataPanel, BorderLayout.CENTER);
    InfoDataPanel.add(tPlayerLabel, new XYConstraints(0, 107, 110, 21));
    InfoDataPanel.add(tPlayerValue, new XYConstraints(116, 107, 90, 21));
    InfoDataPanel.add(tNetWorthLabel, new XYConstraints(0, 129, 110, 21));
    InfoDataPanel.add(tNetWorthValue, new XYConstraints(116, 129, 90, 21));
    InfoDataPanel.add(tCashLabel, new XYConstraints(0, 151, 110, 21));
    InfoDataPanel.add(tCashValue, new XYConstraints(116, 151, 90, 21));
    InfoDataPanel.add(tAmtOwedLabel, new XYConstraints(0, 173, 110, 21));
    InfoDataPanel.add(tAmtOwedValue, new XYConstraints(116, 173, 90, 21));
    InfoDataPanel.add(tAmtShortLabel, new XYConstraints(0, 195, 110, 21));
    InfoDataPanel.add(tAmtShortValue, new XYConstraints(116, 195, 90, 21));
    InfoDataPanel.add(cbOK, new XYConstraints(4, 8, 199, 25));
    InfoDataPanel.add(cbSell, new XYConstraints(4, 39, 199, 25));
    InfoDataPanel.add(tSaleValue, new XYConstraints(5, 69, 197, 24));
    splitPanel1.setPreferredSize(new Dimension(this.WINDOW_WIDTH,this.WINDOW_HEIGHT));

      // Center the window
    Dimension frmSize = aParentWindow.getSize();
    Point loc = aParentWindow.getLocation();
    this.setLocation((frmSize.width - this.WINDOW_WIDTH) / 2 + loc.x,
                     (frmSize.height - this.WINDOW_HEIGHT) / 2 + loc.y);
  }

  void cbOK_actionPerformed(ActionEvent e)
  {

    dispose();
  }

  void this_windowClosing(WindowEvent e)
  {
      // Override and do NOTHING - User must use the OK button
  }

  private int removeItemFromTree(GraphLocation aItem)
  {
    int iCashValue=0;
    TreeNode aTreeNode = (TreeNode)aTree.get(aItem);

    if ( (aTreeNode.getNodeType() == TreeNode.PROPERTY) |
         (aTreeNode.getNodeType() == TreeNode.BRANCH) )
    {
      GraphLocation aChildren[] = aItem.getChildren();
      for (int j=0; j < aChildren.length; j++)
        iCashValue += removeItemFromTree(aChildren[j]);
        // Repeat the delete children operation in case a prior delete involved a HOTEL
        // This is necessary because the delete of a HOTEL actually causes House nodes to be created.
      aChildren = aItem.getChildren();
      for (int j=0; j < aChildren.length; j++)
        iCashValue += removeItemFromTree(aChildren[j]);
    }

    else if (aTreeNode.getNodeType() == TreeNode.HOTEL)
    {
      for (int j = 0; j < 4; j++)
        this.addHouseToTree(aItem.getParent());
      aTree.remove(aItem);  // remove the hotel
    }

    else if (aTreeNode.getNodeType() == TreeNode.HOUSE)
    {
      aTree.remove(aItem);
    }

      // Define new current item
    aTree_selectionChanged(new GraphSelectionEvent(aTree.getSelection(), 1));

    if (aRoot.hasChildren() <= 0)
      cbSell.setEnabled(false);

    return iCashValue + aTreeNode.getSaleValue();
  }

  private void addPropertyToTree(Property aProperty)
  {
    aTree.addChild(aTree.getRoot(), new TreeNode(aProperty));
  }

  private void addHouseToTree(GraphLocation aPropertyItem)
  {
    TreeNode aTreeNode = (TreeNode)aTree.get(aPropertyItem);
    aTree.addChild(aPropertyItem, new TreeNode(aTreeNode.aProperty, TreeNode.HOUSE));
  }

  private void aTree_keyPressed(KeyEvent e)
  {
    if (aCurrentItem != null)
    {
      if ( (e.getKeyCode() == KeyEvent.VK_DELETE) && (cbSell.isEnabled()) )
        sellItem(aCurrentItem);
    }
  }

  private void updateSelectionInfo()
  {
    GraphLocation aItems[] = aTree.getSelection().getAll();
    if (aItems.length > 0)
    {
      aCurrentItem = aItems[0];
      int iNodeType = getNodeType(aCurrentItem);
      TreeNode aCurrentTreeNode = (TreeNode)aTree.get(aCurrentItem);
      int iValue = getValueFromItem(aCurrentItem);
      if (iNodeType == TreeNode.PROPERTY)
      {
        if (iValue == 0)
          cbSell.setLabel(aTree.get(aCurrentItem) + " Mortgaged");
        else
          cbSell.setLabel("Mortgage " + aTree.get(aCurrentItem));
      }
      else
        cbSell.setLabel("Sell " + aTree.get(aCurrentItem));
      tSaleValue.setText("for " + Formatting.money(iValue));
      cbSell.setEnabled( (iValue > 0) && (iNodeType != TreeNode.BRANCH) );
      if (cbSell.isEnabled())
      {
        if (iNodeType == TreeNode.PROPERTY)
          cbSell.setEnabled(aCurrentTreeNode.aProperty.isOkToMortgage(aPlayer));
        else if ( (iNodeType == TreeNode.HOTEL) | (iNodeType == TreeNode.HOUSE) )
          cbSell.setEnabled(aCurrentTreeNode.aProperty.isOkToSellHouse(aPlayer));
      }

      tSaleValue.setVisible(iValue > 0);
    }

    else
    {
      aCurrentItem = null;
      cbSell.setLabel("SELL");
      cbSell.setEnabled(false);
    }
    cbOK.setEnabled(aPlayer.iCashNeeded <= aPlayer.iCash);
    tAmtShortLabel.setVisible(aPlayer.iCash < aPlayer.iCashNeeded);
    tAmtShortValue.setText(Formatting.money(aPlayer.iCashNeeded - aPlayer.iCash));
    tAmtShortValue.setVisible(aPlayer.iCash < aPlayer.iCashNeeded);

      // Handle Bankruptcy
    if ( (aPlayer.iCash < aPlayer.iCashNeeded) && (aPlayer.getAssets() == 0) )
    {
      Monopoly.BankruptDialog aWindow = new Monopoly.BankruptDialog(aParentWindow, aPlayer);
      aWindow.show();
      aWindow = null;
      this.dispose();
    }
  }

  private TreeNode treeNodeFromItem(GraphLocation aItem)
  {
    return (TreeNode)aTree.get(aItem);
  }

  private void sellItem(GraphLocation aCurrentItem)
  {
    TreeNode aNode = (TreeNode)aTree.get(aCurrentItem);
    int iNodeType = aNode.getNodeType();

    if (iNodeType != TreeNode.BRANCH)
    {
      if (iNodeType == TreeNode.PROPERTY)
      {
          // look for children (hotel/houses) and sell
        GraphLocation aChildren[] = aCurrentItem.getChildren();

          // first check for Hotel - sell if exists
          // note: this process will create 4 new house nodes
        if ( (aChildren.length == 1) &&
             (treeNodeFromItem(aChildren[0]).getNodeType() == TreeNode.HOTEL ) )
        {
          sellItem(aChildren[0]); // recursive call to delete the hotel
            // get new set of children
          aChildren = aCurrentItem.getChildren();
        }

          // Now delete all the houses
        for (int j=0; j < aChildren.length; j++)
          sellItem(aChildren[j]);  // recursive call to delete the houses

          // finally mortgage the property
        aPlayer.mortgageProperty(aNode.getProperty());

          // Set the property value to 0
        aNode.setSaleValue(0);
      }

      else  // House or Hotel
      {
        aPlayer.sellHouse(aNode.getProperty());
        removeItemFromTree(aCurrentItem);
      }

      tCashValue.setText(Formatting.money(aPlayer.iCash));
      tNetWorthValue.setText(Formatting.money(aPlayer.getNetWorth()));
      updateSelectionInfo();
      this.repaint();
    }
  }

  private void aTree_selectionChanged(GraphSelectionEvent e)
  {
    updateSelectionInfo();
  }

  private int getNodeType(GraphLocation aItem)
  {
    TreeNode aTreeNode = (TreeNode)aTree.get(aItem);
    return aTreeNode.getNodeType();
  }

  private int getValueFromItem(GraphLocation aItem)
  {
    int iCashValue=0;

    TreeNode aTreeNode = (TreeNode)aTree.get(aItem);

    if ( (aTreeNode.getNodeType() == TreeNode.PROPERTY) |
         (aTreeNode.getNodeType() == TreeNode.BRANCH) )
    {
      GraphLocation aChildren[] = aItem.getChildren();
      for (int j=0; j < aChildren.length; j++)
        iCashValue += getValueFromItem(aChildren[j]);
    }

    return iCashValue + aTreeNode.getSaleValue();
  }

  void cbSell_actionPerformed(ActionEvent e)
  {
    sellItem(aCurrentItem);
  }

}

class AssetMaintDialog_cbOK_actionAdapter implements ActionListener{
  AssetMaintDialog adaptee;

  AssetMaintDialog_cbOK_actionAdapter(AssetMaintDialog adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.cbOK_actionPerformed(e);
  }
}

class AssetMaintDialog_this_windowAdapter extends WindowAdapter
{
  AssetMaintDialog adaptee;

  AssetMaintDialog_this_windowAdapter(AssetMaintDialog adaptee)
  {
    this.adaptee = adaptee;
  }

  public void windowClosing(WindowEvent e)
  {
    adaptee.this_windowClosing(e);
  }
}

class AssetMaintDialog_cbSell_actionAdapter implements java.awt.event.ActionListener{
  AssetMaintDialog adaptee;


  AssetMaintDialog_cbSell_actionAdapter(AssetMaintDialog adaptee)
  {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e)
  {
    adaptee.cbSell_actionPerformed(e);
  }
}
