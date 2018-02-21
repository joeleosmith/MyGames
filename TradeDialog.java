package Player;

import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.model.*;
import com.heagy.gui.Prompting;
import com.heagy.util.Formatting;
import Monopoly.*;
import Property.*;

public class TradeDialog extends Dialog
{
  private Frame aParentWindow;
  private static final int TREE_WIDTH = 260;
  private static final int TREE_HEIGHT = 400;
  private static final int WINDOW_WIDTH = TREE_WIDTH * 2;
  private static final int WINDOW_HEIGHT = TREE_HEIGHT + 10;
  private boolean bFocusOnTree2 = false;

  Panel MainPanel = new Panel();
  TradeBean aTrader1 = new TradeBean();
  TradeBean aTrader2 = new TradeBean();
  BorderLayout borderLayout1 = new BorderLayout();
  BevelPanel TreePanel = new BevelPanel();
  BevelPanel bevelPanel1 = new BevelPanel();
  ButtonControl cbTransfer = new ButtonControl();
  ButtonControl cbCancel = new ButtonControl();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout MainPanelBorderLayout = new BorderLayout();
  TextControl tDummyForSpacing = new TextControl();

  public TradeDialog(Frame frame)
  {
    super(frame, "Trading", true);
    this.aParentWindow = frame;
    try
    {
      jbInit();
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
    MainPanel.requestFocus();
    if (bFocusOnTree2)
      aTrader2.selectRootNode();
    else
      aTrader1.selectRootNode();
    super.show();
  }

  private void jbInit() throws Exception
  {
    aTrader1.setPreferredSize(new Dimension(TREE_WIDTH, TREE_HEIGHT));
    aTrader1.setInitialPlayer(Monopoly.theCurrentPlayer);
    aTrader2.setPreferredSize(new Dimension(TREE_WIDTH, TREE_HEIGHT));
    aTrader2.setInitialPlayer(Monopoly.getNextAvailablePlayer());
    TreePanel.setBevelInner(BevelPanel.FLAT);
    bevelPanel1.setBevelInner(BevelPanel.FLAT);
    TreePanel.setLayout(borderLayout2);
    cbTransfer.setFont(new Font("Dialog", 1, 12));
    cbTransfer.setLabel("<== Transfer ==>");
    cbTransfer.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cbTransfer_actionPerformed(e);
      }
    });
    cbCancel.setLabel("Cancel");
    cbCancel.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cbCancel_actionPerformed(e);
      }
    });
    borderLayout2.setHgap(3);
    MainPanelBorderLayout.setVgap(7);
    tDummyForSpacing.setText("");
    aTrader1.addActionListener(new java.awt.event.ActionListener()
      {public void actionPerformed(ActionEvent e) {Trader1_actionPerformed(e);} });

    MainPanel.setLayout(MainPanelBorderLayout);
    aParentWindow.setLayout(borderLayout1);
    MainPanel.setSize(new Dimension(506, 330));

    this.addWindowListener(new TradeDialog_this_windowAdapter(this));

      // Center the window
    Dimension frmSize = aParentWindow.getSize();
    Point loc = aParentWindow.getLocation();
    this.setLocation((frmSize.width - this.WINDOW_WIDTH) / 2 + loc.x,
                     (frmSize.height - this.WINDOW_HEIGHT) / 2 + loc.y);
    MainPanel.add(TreePanel, BorderLayout.CENTER);
    TreePanel.add(aTrader1, BorderLayout.CENTER);
    TreePanel.add(aTrader2, BorderLayout.EAST);
    MainPanel.add(bevelPanel1, BorderLayout.SOUTH);
    bevelPanel1.add(cbTransfer, new XYConstraints(199, 0, 121, 30));
    bevelPanel1.add(cbCancel, new XYConstraints(407, 0, 82, 30));
    bevelPanel1.add(tDummyForSpacing, new XYConstraints(5, 7, 53, 29));
  }

  void this_windowClosing(WindowEvent e)
  {
    dispose();
  }

  private void Trader1_actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals(TradeBean.EVENT_FOCUS_ON_TREE))
    {
      bFocusOnTree2 = true;
      this.show();
    }
  }

  private boolean isCashTradeOK(Player aPlayer, int iTradeAmt)
  {
    String sClosingMsg = "This trade has been rejected.  Enter a new selection or press <Cancel>.";
    boolean bTradeOK = true;
    if (aPlayer.iCash < iTradeAmt)
    {
      Prompting.MsgBoxSimple(this.aParentWindow, "Trading Error",
        aPlayer.sName + " is trying to trade "
        + Formatting.money(iTradeAmt)
        + " but only has " + Formatting.money(aPlayer.iCash)
        + ".  " + sClosingMsg);
      bTradeOK = false;
    }
    return bTradeOK;
  }

  private boolean isPropertyTradeOK(Player aPlayer, Property aProperty)
  {
    String sClosingMsg = "This trade has been rejected.  Enter a new selection or press <Cancel>.";
    boolean bTradeOK = true;
    if (aProperty != null)  // a property was part of the trade
    {
      if (! aProperty.isOkToTrade())
      {
        Prompting.MsgBoxSimple(this.aParentWindow, "Trading Error",
          "You may not trade a property that has a house on it or that belongs"
          + " to a group of properties with house(s) on them.  " + sClosingMsg);
        bTradeOK = false;
      }
    }
    return bTradeOK;
  }

  void cbTransfer_actionPerformed(ActionEvent e)
  {
    Player aPlayer1 = aTrader1.getPlayer();
    Player aPlayer2 = aTrader2.getPlayer();

      // check cash involved in trade
    if (isCashTradeOK(aPlayer1, aTrader1.getCashToTrade())
          && isCashTradeOK(aPlayer2, aTrader2.getCashToTrade()))
    {
        // check for a valid property trade
      if (isPropertyTradeOK(aPlayer1, aTrader1.getSelectedProperty())
          && isPropertyTradeOK(aPlayer2, aTrader2.getSelectedProperty()) )
      {
          // Trade Cash first
        aPlayer1.iCash -= aTrader1.getCashToTrade();
        aPlayer2.iCash += aTrader1.getCashToTrade();
        aTrader1.clearCashTrade();
        aPlayer2.iCash -= aTrader2.getCashToTrade();
        aPlayer1.iCash += aTrader2.getCashToTrade();
        aTrader2.clearCashTrade();

          // Now do property
        Property aPropertyToTrade = aTrader1.getSelectedProperty();
        if (aPropertyToTrade != null)
        {
          aPlayer1.removeProperty(aPropertyToTrade);
          aPlayer2.addProperty(aPropertyToTrade, 0);
        }
        aPropertyToTrade = aTrader2.getSelectedProperty();
        if (aPropertyToTrade != null)
        {
          aPlayer2.removeProperty(aPropertyToTrade);
          aPlayer1.addProperty(aPropertyToTrade, 0);
        }

          // Now update the player values on the beans
        aTrader1.updatePlayerValues();
        aTrader2.updatePlayerValues();

          // Change the cancel button to say OK
        cbCancel.setLabel("OK");
      }
    }
  }

  void cbCancel_actionPerformed(ActionEvent e)
  {
    this.dispose();
  }

}

class TradeDialog_this_windowAdapter extends WindowAdapter
{
  TradeDialog adaptee;

  TradeDialog_this_windowAdapter(TradeDialog adaptee)
  {
    this.adaptee = adaptee;
  }

  public void windowClosing(WindowEvent e)
  {
    adaptee.this_windowClosing(e);
  }
}

