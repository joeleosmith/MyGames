package Property;

import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import Monopoly.*;
import Card.*;
import Player.*;
import com.heagy.util.*;

public class PropertyInfoDialog extends Dialog
{

    // private CONSTANTS
  private static final int NOTHING = 0;
  private static final int BUY_PROPERTY = 1;
  private static final int BUY_HOUSE = 2;
  private static final int SELL_HOUSE = 3;
  private static final int MORTGAGE = 4;
  private static final int UNMORTGAGE = 5;
  private static final int PAY_RENT = 6;
  private static final int WINDOW_WIDTH = 400;
  private static final int WINDOW_HEIGHT = 300;

  private static final int FIELD_CNT = 8;
  private static final int LABEL_START_X = 210;
  private static final int VALUE_START_X = 314;
  private static final int FIELD_START_Y = 17;
  private static final int FIELD_HEIGHT = 21;
  private static final int FIELD_WIDTH = 93;
  private static final int VERTICAL_SPACE = 25;

    // private attributes
  private int       iActionTaken = NOTHING;
  private int       iSpecialFee;    // From Card Class
  private Frame     aParentWindow;
  private Property  aProperty;
  private Player    aRequestingPlayer;
  private int       iRentAmount;
  private boolean   bRentPaid = true;

    // Bean Controls
  MortgagedControl aMortgagedControl;
  OwnedControl     aOwnedControl;
  UnOwnedControl   aUnOwnedControl;

    // Other Window Controls
  Panel            MainPanel = new Panel();
  XYLayout         MainPanelLayout = new XYLayout();
  ButtonControl    cbOK = new ButtonControl();
  TransparentImage aOwnerImage = new TransparentImage();
  Label            tLabels[] = new Label[FIELD_CNT];
  Label            tValues[] = new Label[FIELD_CNT];


  public PropertyInfoDialog(Frame frame, String title, boolean modal,
                            Property PropertyToShow, Player RequestingPlayer,
                            int iSpecialFee)
  {
    super(frame, title, modal);
    aParentWindow = frame;
    aProperty = PropertyToShow;
    aRequestingPlayer = RequestingPlayer;
    this.iSpecialFee = iSpecialFee;
    try
    {
      jbInit();
    }
    catch (Exception e) {e.printStackTrace();}
    add(MainPanel);
    pack();
  }

  private void jbInit() throws Exception
  {
      // declare Fonts to be used
    Font aNormalFont = new Font("Dialog", 1, 12);
    Font aBigFont = new Font("Dialog", 1, 20);

      // set window dimensions
    MainPanelLayout.setWidth(this.WINDOW_WIDTH);
    MainPanelLayout.setHeight(this.WINDOW_HEIGHT);
    MainPanel.setLayout(MainPanelLayout);
    MainPanel.add(cbOK, new XYConstraints(296, 240, 80, 35));
    cbOK.setLabel("OK");
    cbOK.addActionListener(new PropertyInfoDialog_cbOK_actionAdapter(this));

      // setup generic property information controls
    aOwnerImage.setDrawEdge(false);

    for (int j=0; j < FIELD_CNT; j++)
    {
      tLabels[j] = new Label();
      tLabels[j].setForeground(Color.blue);
      tLabels[j].setFont(aNormalFont);
      tLabels[j].setAlignment(2);

      tValues[j] = new Label();
      tValues[j].setFont(aNormalFont);
    }

      // Add elements to Main Panel
    MainPanel.add(aOwnerImage, new XYConstraints(306, 13, 26, 26));
    int iYpos = FIELD_START_Y;
    for (int j = 0; j < FIELD_CNT; j++)
    {
      MainPanel.add(tLabels[j],
        new XYConstraints(LABEL_START_X, iYpos, FIELD_WIDTH, FIELD_HEIGHT));
      if (j == 0)
        MainPanel.add(tValues[j],
          new XYConstraints(VALUE_START_X + 27, iYpos, FIELD_WIDTH, FIELD_HEIGHT));
      else
        MainPanel.add(tValues[j],
          new XYConstraints(VALUE_START_X, iYpos, FIELD_WIDTH, FIELD_HEIGHT));
      iYpos += VERTICAL_SPACE;
    }

    tLabels[0].setText("Owner:");
    tLabels[1].setText("Price Paid:");
    tLabels[2].setText("Rent Collected:");
    tLabels[3].setText("Visitors:");
    tLabels[4].setText("Improvements:");
    tLabels[5].setText("Building Costs:");
    tLabels[6].setText("Building Fees:");
    tLabels[7].setText("Profit/(Loss):");

    tValues[3].setText(aProperty.iLandingCount + "");

      // setup specific controls for different states of property
    if (aProperty.aOwner != null)
    {
      aOwnerImage.setImage(aProperty.aOwner.getInactiveIconImage());
      tValues[0].setText(aProperty.aOwner.sName);
      tValues[1].setText(Formatting.money(aProperty.iPricePaid));
      tValues[2].setText(Formatting.money(aProperty.aLandlord.iRentCollected));
      tValues[5].setText(Formatting.money(aProperty.iConstructionCosts));
      tValues[6].setText(Formatting.money(aProperty.iFeesPaid));
      int iProfitLoss = aProperty.aLandlord.iRentCollected
                      - aProperty.iPricePaid
                      - aProperty.iConstructionCosts
                      - aProperty.iFeesPaid;
      tValues[7].setText(Formatting.money(iProfitLoss));
      if (aProperty.bRentable)    // OWNED AND NOT MORTGAGED
      {

        int iSpecialRent = 0;
        if ( (iSpecialFee == Card.ROLL_DICE_TIMES_10) &&
             (aRequestingPlayer != null) && (aProperty.aOwner != aRequestingPlayer) )
        {
          DiceRollerDialog aWindow = new DiceRollerDialog(aParentWindow, "Rent calculator");
          aWindow.show();
          iSpecialRent = aWindow.getDiceTotal() * 10;
          aWindow = null;
        }

        aOwnedControl = new OwnedControl(aProperty, aRequestingPlayer,
                                iSpecialFee, iSpecialRent);
        aOwnedControl.addActionListener(new java.awt.event.ActionListener()
          { public void actionPerformed(ActionEvent e) {aOwnedControl_actionPerformed(e);} });
        MainPanel.add(aOwnedControl, new XYConstraints(1, 7, 225, 269));
        if (aProperty.iHouseCount == 0)
          tValues[4].setText("Nothing");
        else if (aProperty.iHouseCount == 1)
          tValues[4].setText("1 House");
        else if (aProperty.iHouseCount < 5)
          tValues[4].setText(aProperty.iHouseCount + " Houses");
        else
          tValues[4].setText("1 HOTEL");

          // if the owner is someone else (rent to be paid) then disable the OK button
        if ( (aRequestingPlayer != null) && (aProperty.aOwner != aRequestingPlayer))
        {
          this.bRentPaid = false;
          cbOK.setEnabled(false);
        }
      }
      else                        // OWNED BUT MORTGAGED
      {
        aMortgagedControl = new MortgagedControl(aProperty, aRequestingPlayer);
        aMortgagedControl.addActionListener(new java.awt.event.ActionListener()
          { public void actionPerformed(ActionEvent e) {aMortgagedControl_actionPerformed(e);} });
        MainPanel.add(aMortgagedControl, new XYConstraints(1, 7, 225, 269));
      }
    }

    else  // NOBODY OWNS IT
    {
      aUnOwnedControl = new UnOwnedControl(aProperty, aRequestingPlayer);
      MainPanel.add(aUnOwnedControl, new XYConstraints(1, 7, 225, 269));
      aUnOwnedControl.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(ActionEvent e) {aUnOwnedControl_actionPerformed(e);} });
    }

    this.addWindowListener(new PropertyInfoDialog_this_windowAdapter(this));

      // Center the window
    Dimension frmSize = aParentWindow.getSize();
    Point loc = aParentWindow.getLocation();
    this.setLocation((frmSize.width - this.WINDOW_WIDTH) / 2 + loc.x,
                     (frmSize.height - this.WINDOW_HEIGHT) / 2 + loc.y);
    this.setResizable(false);
  }

  void aUnOwnedControl_actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals(UnOwnedControl.EVENT_BUY_PROPERTY))
      this.takeAction(this.BUY_PROPERTY);
  }

  void aOwnedControl_actionPerformed(ActionEvent e)
  {
    String sAction = e.getActionCommand();
    if (sAction.equals(OwnedControl.EVENT_BUY_HOUSE))
      this.takeAction(this.BUY_HOUSE);
    else if (sAction.equals(OwnedControl.EVENT_SELL_HOUSE))
      this.takeAction(this.SELL_HOUSE);
    else if (sAction.equals(OwnedControl.EVENT_MORTGAGE))
      this.takeAction(this.MORTGAGE);
    else if (sAction.equals(OwnedControl.EVENT_PAY_RENT))
    {
      iRentAmount = aOwnedControl.getRent();
      this.takeAction(this.PAY_RENT);
      this.bRentPaid = true;
    }
  }

  void aMortgagedControl_actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals(MortgagedControl.EVENT_UNMORTGAGE))
      this.takeAction(this.UNMORTGAGE);
  }

  void cbOK_actionPerformed(ActionEvent e)
  {
      // If nobody owns the property (even after the initial offer) then initiate AUCTION
    if ( (aProperty.aOwner == null) && (aRequestingPlayer == Monopoly.theCurrentPlayer) )
      Monopoly.performAuction(aProperty);
    dispose();  //Close the dialog
  }

  void this_windowClosing(WindowEvent e)
  {
    if (bRentPaid)
      dispose();  //Close the dialog
  }

  void takeAction(int iActionToTake)
  {
    boolean bOKtoExit = true;

    switch (iActionToTake)
    {
      case PropertyInfoDialog.NOTHING:
        break;
      case PropertyInfoDialog.BUY_PROPERTY:
          // attempt to buy it
        bOKtoExit = this.aRequestingPlayer.buyProperty(aProperty, aProperty.iPropertyCost);
        break;
      case PropertyInfoDialog.MORTGAGE:
        this.aRequestingPlayer.mortgageProperty(aProperty);
        break;
      case PropertyInfoDialog.UNMORTGAGE:
        bOKtoExit = this.aRequestingPlayer.unMortgageProperty(aProperty, aProperty.iUnMortgageCost);
        break;
      case PropertyInfoDialog.PAY_RENT:
        int iRentPaid = this.aRequestingPlayer.payFee(iRentAmount, aProperty.aOwner, Player.REASON_RENT);
        aProperty.aLandlord.incrementRentPaid(iRentPaid);
        break;
      case PropertyInfoDialog.BUY_HOUSE:
        this.aRequestingPlayer.buildHouse(aProperty);
        break;
      case PropertyInfoDialog.SELL_HOUSE:
        this.aRequestingPlayer.sellHouse(aProperty);
        break;
    }

    if (bOKtoExit)
      dispose();    //Close the dialog
  }

}


class PropertyInfoDialog_cbOK_actionAdapter implements ActionListener
{
  PropertyInfoDialog adaptee;
  PropertyInfoDialog_cbOK_actionAdapter(PropertyInfoDialog adaptee) { this.adaptee = adaptee; }
  public void actionPerformed(ActionEvent e) { adaptee.cbOK_actionPerformed(e); }
}

class PropertyInfoDialog_this_windowAdapter extends WindowAdapter
{
  PropertyInfoDialog adaptee;
  PropertyInfoDialog_this_windowAdapter(PropertyInfoDialog adaptee) {this.adaptee = adaptee;}
  public void windowClosing(WindowEvent e) {adaptee.this_windowClosing(e);}
}
