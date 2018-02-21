package Player;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Trace;
import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.model.*;
import Monopoly.*;
import Property.*;
import com.borland.jbcl.view.*;
import com.borland.jbcl.util.BlackBox;
import com.heagy.util.*;

public class TradeBean extends BeanPanel implements BlackBox{
  public final static String EVENT_FOCUS_ON_TREE = "EventFocusOnTree";
  public final static String EVENT_OK = "EventOK";
  public final static String EVENT_CANCEL = "EventCancel";
  public final static String EVENT_SELECTION_CHANGED = "EventSelectionChanged";

  private GraphLocation aCurrentItem;
  private Player aPlayer;

  GraphLocation aRoot, aBranch;
  BevelPanel HdrPanel = new BevelPanel();
  TreeControl aTree = new TreeControl();
  BorderLayout borderLayout1 = new BorderLayout();
  ChoiceControl aPlayerList = new ChoiceControl();
  TextFieldControl tCashToTradeValue = new TextFieldControl();
  LabelControl tPlayerLabel = new LabelControl();
  LabelControl tCashToTradeLabel = new LabelControl();
  LabelControl tCashValue = new LabelControl();
  LabelControl tOfLabel = new LabelControl();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout MainBorderLayout = new BorderLayout();
  TextControl tDummyForSpacing = new TextControl();

  public TradeBean()
  {
    try
    { jbInit(); }
    catch (Exception e)
    { e.printStackTrace(); }
  }

  public void setInitialPlayer(Player aPlayer)
  {
    aPlayerList.select(aPlayer.sName);
    this.aPlayer = aPlayer;
    this.updatePlayerValues();
  }

  public void updatePlayerValues()
  {
    tCashValue.setText(Formatting.money(aPlayer.iCash));
    aPlayer.loadAssets(this.aTree);
  }

  public void selectRootNode()
  {
    aTree.requestFocus();
    aTree.setSubfocus(aRoot);
  }

  public void clearCashTrade()
  {
    tCashToTradeValue.setText("");
  }

  private void jbInit() throws Exception
  {
      // declare Fonts to be used
    Font aNormalFont = new Font("Dialog", 1, 12);

    this.setLayout(MainBorderLayout);
    borderLayout1.setVgap(5);

      // Header Controls
    tPlayerLabel.setAlignment(Label.RIGHT);
    tPlayerLabel.setFont(aNormalFont);
    tPlayerLabel.setForeground(Color.blue);
    tPlayerLabel.setText("Player:");
    aPlayerList.addItemListener(new java.awt.event.ItemListener()
      {public void itemStateChanged(ItemEvent e) {aPlayerList_itemStateChanged(e);} });
    tCashToTradeLabel.setForeground(Color.blue);
    tCashToTradeLabel.setFont(aNormalFont);
    tCashToTradeLabel.setAlignment(Label.RIGHT);
    tCashToTradeLabel.setText("Amount to Trade:");
    tCashToTradeValue.setText("");
    tOfLabel.setForeground(Color.blue);
    tOfLabel.setFont(aNormalFont);
    tOfLabel.setText("of");
    tCashValue.setText("$");

      // Command Buttons
    borderLayout2.setVgap(5);
    MainBorderLayout.setVgap(1);
    tDummyForSpacing.setText("");

      // Header Panel
    HdrPanel.add(aPlayerList, new XYConstraints(110, 7, 128, 22));
    HdrPanel.add(tCashToTradeValue, new XYConstraints(110, 37, 51, 22));
    HdrPanel.add(tPlayerLabel, new XYConstraints(19, 7, 86, 22));
    HdrPanel.add(tCashToTradeLabel, new XYConstraints(1, 37, 104, 22));
    HdrPanel.add(tCashValue, new XYConstraints(190, 37, 51, 22));
    HdrPanel.add(tOfLabel, new XYConstraints(172, 37, 15, 22));
    HdrPanel.add(tDummyForSpacing, new XYConstraints(13, 52, 36, 17));
    HdrPanel.setBevelOuter(BevelPanel.LOWERED);

      // Tree Control
    aTree.setExpandByDefault(true);
    aTree.setEditInPlace(false);
    aTree.addSelectionListener(new com.borland.jbcl.model.GraphSelectionAdapter()
      { public void selectionChanged(GraphSelectionEvent e) { aTree_selectionChanged(e); } });
    aTree.addFocusListener(new java.awt.event.FocusAdapter()
      {public void focusGained(FocusEvent e) {aTree_focusGained(e);} });

      // BEAN
    this.add(HdrPanel, BorderLayout.NORTH);
    this.add(aTree, BorderLayout.CENTER);

      // Load player names into choice control
    for (int j=0; j < Monopoly.theNbrOfPlayers; j++)
      if (! Monopoly.thePlayers[j].bBankrupt)
        aPlayerList.add(Monopoly.thePlayers[j].sName);
  }

  void aTree_focusGained(FocusEvent e)
  {
    System.out.println("TREE Focus Gained");
    aTree.setSubfocus(aRoot);
    aTree.requestFocus();
    processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_FOCUS_ON_TREE));
  }

  private void aPlayerList_itemStateChanged(ItemEvent e)
  {
    String sNewPlayerName = aPlayerList.getSelectedItem();
      // Find the new player (by name)
    int j=0;
    while ( (j < Monopoly.theNbrOfPlayers) && (Monopoly.thePlayers[j].sName.compareTo(sNewPlayerName) != 0) )
      j++;
    if (j < Monopoly.theNbrOfPlayers)
    {
      this.aPlayer = Monopoly.thePlayers[j];
      tCashValue.setText(Formatting.money(aPlayer.iCash));
      aPlayer.loadAssets(this.aTree);
    }
  }

  private TreeNode getTreeNodeFromItem(GraphLocation aItem)
  {
    return (TreeNode)aTree.get(aItem);
  }

  private int getNodeType(GraphLocation aItem)
  {
    TreeNode aTreeNode = (TreeNode)aTree.get(aItem);
    return aTreeNode.getNodeType();
  }

  public Property getSelectedProperty()
  {
    Property aProperty = null;
    if (aCurrentItem != null)
    {
      if (getNodeType(aCurrentItem) == TreeNode.PROPERTY)
      {
        TreeNode aTreeNode = getTreeNodeFromItem(aCurrentItem);
        aProperty = aTreeNode.getProperty();
      }
    }
    return aProperty;
  }

  public int getCashToTrade()
  {
    return Formatting.stringToInt(tCashToTradeValue.getText());
  }

  public Player getPlayer()
  {
    return this.aPlayer;
  }

  void aTree_selectionChanged(GraphSelectionEvent e)
  {
    GraphLocation[] aItems = aTree.getSelection().getAll();
    if (aItems.length > 0)
    {
      aCurrentItem = aItems[0];
      processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_SELECTION_CHANGED));
    }
  }
}
