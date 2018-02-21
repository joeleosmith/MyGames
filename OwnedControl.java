package Property;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Trace;
import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.view.*;
import com.borland.jbcl.util.BlackBox;
import com.heagy.util.*;
import Card.*;
import Player.*;
import Monopoly.*;

public class OwnedControl extends BeanPanel implements BlackBox
{

  public static final String EVENT_MORTGAGE = "MORTGAGE";
  public static final String EVENT_BUY_HOUSE = "BUY_HOUSE";
  public static final String EVENT_SELL_HOUSE = "SELL_HOUSE";
  public static final String EVENT_PAY_RENT = "PAY_RENT";

  private Property aProperty;
  private Player aPlayer;
  private int iSpecialFee;
  private int iRentAmount=0;
  private int iSpecialRent;

  BevelPanel    MainPanel = new BevelPanel();
  BorderLayout  borderLayout1 = new BorderLayout();
  XYLayout      xYLayout1 = new XYLayout();
  XYLayout      xYLayout2 = new XYLayout();
  BevelPanel    ImagePanel = new BevelPanel();
  ButtonControl cbAction = new ButtonControl();
  ButtonControl cbHouseMinus = new ButtonControl();
  ButtonControl cbHousePlus = new ButtonControl();
  ImageControl  aPropertyImage = new ImageControl();

  public OwnedControl(Property aProperty, Player aPlayer, int iSpecialFee, int iSpecialRent)
  {
    this.aProperty = aProperty;
    this.aPlayer = aPlayer;
    this.iSpecialFee = iSpecialFee;
    this.iSpecialRent = iSpecialRent;
    try
    {
      jbInit();
    }
    catch (Exception e) {e.printStackTrace();}
  }

  private void jbInit() throws Exception
  {
    this.setLayout(borderLayout1);
    this.addFocusListener(new java.awt.event.FocusAdapter()
      { public void focusGained(FocusEvent e) {this_focusGained(e);} });

    MainPanel.setLayout(xYLayout1);
    MainPanel.setBevelInner(BevelPanel.FLAT);
    MainPanel.setEnabled(false);

    ImagePanel.setLayout(xYLayout2);
    ImagePanel.setSoft(true);
    ImagePanel.setEnabled(false);
    ImagePanel.setBevelOuter(BevelPanel.RAISED);

      // if the requesting player is not the "owner", then pay rent
    boolean bSameOwnerForAllPropsInGroup = aProperty.aOwner.ownsAllPropsInGroup(aProperty.aGroup);
    if (aPlayer != null)
    {
      if (aPlayer != aProperty.aOwner)
      {
        if (aProperty.iPropertyType == Property.NORMAL)
          iRentAmount = aProperty.aLandlord.calculateRent(aProperty.iHouseCount, bSameOwnerForAllPropsInGroup);
        else if (aProperty.iPropertyType == Property.RAILROAD)
        {
          Rent.Railroad aLandlord = (Rent.Railroad)aProperty.aLandlord;
          iRentAmount = aLandlord.calculateRent(aProperty.aOwner.railroadsOwned());
        }
        else if (aProperty.iPropertyType == Property.UTILITY)
        {
          Rent.Utility aLandlord = (Rent.Utility)aProperty.aLandlord;
          iRentAmount = aLandlord.calculateRent(aProperty.aOwner.utilitiesOwned(),
                                                    Monopoly.theGameBoard.iDiceCnt);
        }

          // Handle Special Fee
        if (this.iSpecialFee == Card.DOUBLE_RENT)
          iRentAmount += iRentAmount;
        else if (this.iSpecialFee == Card.ROLL_DICE_TIMES_10)
          iRentAmount = iSpecialRent;

        cbAction.setLabel("Pay " + Formatting.money(iRentAmount) + " for Rent");
        cbHouseMinus.setVisible(false);
        cbHousePlus.setVisible(false);
        MainPanel.add(cbAction, new XYConstraints(0, 233, 204, 35));
      }
      else  // else allow mortgage, house purchase etc.
      {
        cbAction.setLabel("Mortgage");
        cbAction.setEnabled(aProperty.isOkToMortgage(aPlayer));
        cbHouseMinus.setEnabled(aProperty.isOkToSellHouse(aPlayer));
        cbHousePlus.setEnabled(aProperty.isOkToBuyHouse(aPlayer));

        if ((bSameOwnerForAllPropsInGroup) &&
            (aProperty.iPropertyType != Property.RAILROAD) &&
            (aProperty.iPropertyType != Property.UTILITY))
        {
          cbHouseMinus.setImage(Monopoly.getHouseImage("House"));
          cbHouseMinus.setItemMargins(new Insets(0, 5, 0, 0));
          cbHouseMinus.setImageFirst(false);
          cbHouseMinus.setFont(new Font("Dialog", 1, 20));
          cbHouseMinus.setLabel("-");
          cbHousePlus.setImage(Monopoly.getHouseImage("House"));
          cbHousePlus.setItemMargins(new Insets(0, 5, 0, 0));
          cbHousePlus.setImageFirst(false);
          cbHousePlus.setFont(new Font("Dialog", 1, 20));
          cbHousePlus.setLabel("+");
          MainPanel.add(cbAction, new XYConstraints(41, 233, 123, 35));
        }
        else
        {
          MainPanel.add(cbAction, new XYConstraints(0, 233, 204, 35));
          cbHouseMinus.setVisible(false);
          cbHousePlus.setVisible(false);
        }
      }

      cbAction.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(ActionEvent e) {cbAction_actionPerformed(e);} });
      cbHouseMinus.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(ActionEvent e) {cbHouseMinus_actionPerformed(e);} });
      cbHousePlus.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(ActionEvent e) {cbHousePlus_actionPerformed(e);} });
      MainPanel.add(cbHouseMinus, new XYConstraints(1, 233, 39, 35));
      MainPanel.add(cbHousePlus, new XYConstraints(165, 233, 39, 35));
    }

    aPropertyImage.setImage(Monopoly.getPropCardImage(aProperty.sName));
    aPropertyImage.setAlignment(Formatting.ALIGN_LEFT);
    aPropertyImage.setFlat(true);
    aPropertyImage.addFocusListener(new java.awt.event.FocusAdapter()
      { public void focusGained(FocusEvent e) {aPropertyImage_focusGained(e);} });
    this.add(MainPanel, BorderLayout.CENTER);
    MainPanel.add(ImagePanel, new XYConstraints(0, 0, -1, -1));
    ImagePanel.add(aPropertyImage, new XYConstraints(0, 0, -1, -1));
  }

  void cbAction_actionPerformed(ActionEvent e)
  {
    if (aProperty.aOwner == aPlayer)
      processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_MORTGAGE));
    else
      processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_PAY_RENT));
  }

  void cbHouseMinus_actionPerformed(ActionEvent e)
  {
    processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_SELL_HOUSE));
  }

  void cbHousePlus_actionPerformed(ActionEvent e)
  {
    processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_BUY_HOUSE));
  }

  void aPropertyImage_focusGained(FocusEvent e)
  {
    cbAction.requestFocus();
  }

  void this_focusGained(FocusEvent e)
  {
    cbAction.requestFocus();
  }

  public int getRent()
  {
    return iRentAmount;
  }

}
