package  Board;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Trace;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.view.*;
import com.borland.jbcl.util.BlackBox;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import Player.*;
import com.heagy.gaming.*;

public class CommandPanel extends BeanPanel implements BlackBox
{
  public static final String EVENT_NEXT_PLAYER = "NEXT";
  public static final String EVENT_TRADE = "TRADE";
  public static final String EVENT_PAY_JAIL_FEE = "JAILFEE";
  public static final String EVENT_ROLL_DICE = "ROLL";
  public static final String EVENT_AUCTION = "AUCTION";
  public static final String EVENT_HELP = "HELP";

  private int iDie1Value;
  private int iDie2Value;
  BevelPanel mainBeanPanel = new BevelPanel();
  XYLayout xYLayout1 = new XYLayout();

  public CommandPanel() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void initForPlayer(Player aPlayer)
  {
    cbNext.setEnabled(false);
    cbAuction.setEnabled(false);
    cbRollDice.setEnabled(true);
    cbTrade.setEnabled(true);
    if (aPlayer.bHasJailCardChest | aPlayer.bHasJailCardChance)
    {
      cbPayJailFee.setLabel("Use Jail Card");
      cbPayJailFee.setToolTipText("Use a 'Get Out Of Jail Free' card to get out "
                                  + "of jail without paying a fee.");
    }
    else
    {
      cbPayJailFee.setLabel("Pay Jail Fee");
      cbPayJailFee.setToolTipText("Pay the $50 Jail Fee to get out of jail.");
    }
    cbPayJailFee.setEnabled( (aPlayer.bInJail) &&
          ((aPlayer.iCash > 50) |
            aPlayer.bHasJailCardChest | aPlayer.bHasJailCardChance));
  }

  public void disableAll()
  {
    cbNext.setEnabled(false);
    cbRollDice.setEnabled(false);
    cbPayJailFee.setEnabled(false);
    cbTrade.setEnabled(false);
  }

  public void enableAll()
  {
    cbNext.setEnabled(true);
    cbRollDice.setEnabled(true);
    cbPayJailFee.setEnabled(true);
    cbTrade.setEnabled(true);
    cbAuction.setEnabled(true);
  }

  public boolean isNextPlayerEnabled()
  {
    return cbNext.isEnabled();
  }

  public void disableJailFee()
  {
    cbPayJailFee.setEnabled(false);
  }

  public void disableRollDice()
  {
    cbRollDice.setEnabled(false);
  }

  public void disableNextPlayer()
  {
    cbNext.setEnabled(false);
  }

  public void disableTrade()
  {
    cbTrade.setEnabled(false);
  }

  public void disableAuction()
  {
    cbAuction.setEnabled(false);
  }

  public void enableJailFee()
  {
    cbPayJailFee.setEnabled(true);
  }

  public void enableRollDice()
  {
    this.requestFocus();
    cbRollDice.setEnabled(true);
    cbRollDice.requestFocus();
  }

  public void enableNextPlayer()
  {
    this.requestFocus();
    cbNext.setEnabled(true);
    cbNext.requestFocus();
  }

  public void enableAuction()
  {
    cbAuction.setEnabled(true);
  }

  public void enableTrade()
  {
    cbTrade.setEnabled(true);
  }

  private void jbInit() throws Exception{
    mainBeanPanel.setLayout(xYLayout1);
    this.setLayout(xYLayout2);
    cmdButtonPanel.setLayout(gridLayout1);
    this.addKeyListener(new java.awt.event.KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        this_keyPressed(e);
      }
    });
    mainBeanPanel.setBevelInner(BevelPanel.FLAT);
    cmdButtonPanel.setBevelInner(BevelPanel.FLAT);
    gridLayout1.setRows(5);
    cbNext.setLabel("Next Player");
    cbNext.setToolTipText("Give control to the next player");
    cbNext.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cbNext_actionPerformed(e);
      }
    });
    cbTrade.setLabel("Trade");
    cbTrade.setToolTipText("Initiate a trade with another player");
    cbTrade.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cbTrade_actionPerformed(e);
      }
    });
    cbPayJailFee.setEnabled(false);
    cbPayJailFee.setLabel("Pay Jail Fee");
    cbPayJailFee.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cbPayJailFee_actionPerformed(e);
      }
    });
    cbRollDice.setLabel("Roll Dice");
    cbRollDice.setToolTipText("Roll the dice");
    cbRollDice.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cbRollDice_actionPerformed(e);
      }
    });
    cbAuction.setLabel("Auction");
    cbAuction.setToolTipText("Initiate the Auctioneer for an Unowned Property");
    cbAuction.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cbAuction_actionPerformed(e);
      }
    });
    xYLayout2.setHeight(127);
    xYLayout2.setWidth(157);

    dice.addDiceListener(new com.heagy.gaming.DiceListener()
    {
      public void diceStartedRolling() { }
      public void diceTumbled(DiceEvent e) { }
      public void diceFinishedRolling(DiceEvent e)
      {
        diceFinishedTumbling(e.getDie1Value(), e.getDie2Value());
      }
    });

    this.add(mainBeanPanel, new XYConstraints(0, 0, -1, 300));
    mainBeanPanel.add(cmdButtonPanel, new XYConstraints(3, 1, 93, 123));
    cmdButtonPanel.add(cbNext, null);
    cmdButtonPanel.add(cbTrade, null);
    cmdButtonPanel.add(cbAuction, null);
    cmdButtonPanel.add(cbPayJailFee, null);
    cmdButtonPanel.add(cbRollDice, null);
    this.add(dice, new XYConstraints(106, 10, 47, 105));
  }

  BevelPanel cmdButtonPanel = new BevelPanel();
  GridLayout gridLayout1 = new GridLayout();
  ButtonControl cbNext = new ButtonControl();
  ButtonControl cbTrade = new ButtonControl();
  ButtonControl cbPayJailFee = new ButtonControl();
  ButtonControl cbRollDice = new ButtonControl();
  ButtonControl cbAuction = new ButtonControl();
  DiceBean dice = new DiceBean();
  XYLayout xYLayout2 = new XYLayout();

  private void diceFinishedTumbling(int iValue1, int iValue2)
  {
    iDie1Value = iValue1;
    iDie2Value = iValue2;
    eventButtonClicked(EVENT_ROLL_DICE);
  }

  protected void eventButtonClicked(String sCmd)
  {
    processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, sCmd));
  }

  void cbRollDice_actionPerformed(ActionEvent e)
  {
    this.disableAll();  // disable buttons while rolling dice
    dice.rollDice(20,100);
  }

  void cbPayJailFee_actionPerformed(ActionEvent e)
  {
    this.eventButtonClicked(EVENT_PAY_JAIL_FEE);
  }

  void cbNext_actionPerformed(ActionEvent e)
  {
    this.eventButtonClicked(EVENT_NEXT_PLAYER);
  }

  void cbTrade_actionPerformed(ActionEvent e)
  {
    this.eventButtonClicked(EVENT_TRADE);
  }

  void cbAuction_actionPerformed(ActionEvent e)
  {
    this.eventButtonClicked(EVENT_AUCTION);
  }

  void this_keyPressed(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_F1)
      processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, this.EVENT_HELP));
  }

  public int getDie1Value()
  {
    return iDie1Value;
  }

  public int getDie2Value()
  {
    return iDie2Value;
  }

}
