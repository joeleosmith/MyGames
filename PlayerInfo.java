package  Player;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Trace;
import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.view.*;
import com.borland.jbcl.util.BlackBox;
import Card.*;
import com.heagy.util.*;
import Monopoly.*;

public class PlayerInfo extends BeanPanel implements BlackBox{

  private int iRightAlign = com.borland.dx.text.Alignment.RIGHT | com.borland.dx.text.Alignment.MIDDLE;
  private int iLeftAlign = com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.MIDDLE;

  BevelPanel bevelPanel1 = new BevelPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  XYLayout xYLayout1 = new XYLayout();
  TextControl textControl1 = new TextControl();
  TextControl textControl2 = new TextControl();
  TextControl textControl3 = new TextControl();
  TextControl textControl4 = new TextControl();
  TextControl textControl5 = new TextControl();
  TextControl textControl6 = new TextControl();
  TextControl textControl7 = new TextControl();
  TextControl textControl8 = new TextControl();
  TextControl textControl9 = new TextControl();
  TextControl textControl10 = new TextControl();
  TransparentImage JailCardChance = new TransparentImage();
  TransparentImage JailCardChest = new TransparentImage();
  TextControl tPlayer = new TextControl();
  TextControl tCash = new TextControl();
  TextControl tAssets = new TextControl();
  TextControl tNetWorth = new TextControl();
  TextControl tRentCollected = new TextControl();
  TextControl tRentPaid = new TextControl();
  TextControl tFeesPaid = new TextControl();
  TextControl tFeesReceived = new TextControl();
  TextControl tPayReceived = new TextControl();
  TransparentImage PlayerIcon = new TransparentImage();
  TextControl tDaysLeftInJail = new TextControl();

  public PlayerInfo() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception{
    Font aFont = new Font("Dialog", 1, 14);

    bevelPanel1.setLayout(xYLayout1);
    this.setLayout(borderLayout1);
    bevelPanel1.setBevelInner(BevelPanel.FLAT);

    textControl1.setAlignment(iRightAlign);
    textControl2.setAlignment(iRightAlign);
    textControl3.setAlignment(iRightAlign);
    textControl4.setAlignment(iRightAlign);
    textControl5.setAlignment(iRightAlign);
    textControl6.setAlignment(iRightAlign);
    textControl7.setAlignment(iRightAlign);
    textControl8.setAlignment(iRightAlign);
    textControl9.setAlignment(iRightAlign);
    textControl10.setAlignment(iRightAlign);

    textControl1.setFont(aFont);
    textControl2.setFont(aFont);
    textControl3.setFont(aFont);
    textControl4.setFont(aFont);
    textControl5.setFont(aFont);
    textControl6.setFont(aFont);
    textControl7.setFont(aFont);
    textControl8.setFont(aFont);
    textControl9.setFont(aFont);
    textControl10.setFont(aFont);

    textControl1.setForeground(Color.blue);
    textControl2.setForeground(Color.blue);
    textControl3.setForeground(Color.blue);
    textControl4.setForeground(Color.blue);
    textControl5.setForeground(Color.blue);
    textControl6.setForeground(Color.blue);
    textControl7.setForeground(Color.blue);
    textControl8.setForeground(Color.blue);
    textControl9.setForeground(Color.blue);
    textControl10.setForeground(Color.blue);

    textControl1.setText("Player:");
    textControl2.setText("Cash:");
    textControl3.setText("Assets:");
    textControl4.setText("Net Worth:");
    textControl5.setText("Rent Collected:");
    textControl6.setText("Rent Paid:");
    textControl7.setText("Fees Paid:");
    textControl8.setText("Fees Received:");
    textControl9.setText("Pay Received:");
    textControl10.setText("Jail Time Left:");

    tPlayer.setFont(aFont);
    tCash.setFont(aFont);
    tAssets.setFont(aFont);
    tNetWorth.setFont(aFont);
    tRentCollected.setFont(aFont);
    tRentPaid.setFont(aFont);
    tFeesPaid.setFont(aFont);
    tFeesReceived.setFont(aFont);
    tPayReceived.setFont(aFont);
    tDaysLeftInJail.setFont(aFont);

    JailCardChance.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    JailCardChance.setDrawEdge(false);
    JailCardChest.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    JailCardChest.setDrawEdge(false);

    PlayerIcon.setAlignment(iLeftAlign);
    PlayerIcon.setDrawEdge(false);

    this.add(bevelPanel1, BorderLayout.WEST);
    bevelPanel1.add(textControl1, new XYConstraints(53, 4, 61, 18));
    bevelPanel1.add(textControl2, new XYConstraints(63, 24, 51, 19));
    bevelPanel1.add(textControl3, new XYConstraints(51, 46, 63, 19));
    bevelPanel1.add(textControl4, new XYConstraints(31, 67, 83, 19));
    bevelPanel1.add(textControl5, new XYConstraints(-1, 88, 115, 19));
    bevelPanel1.add(textControl6, new XYConstraints(31, 109, 83, 19));
    bevelPanel1.add(textControl7, new XYConstraints(32, 131, 82, 19));
    bevelPanel1.add(textControl8, new XYConstraints(-1, 152, 115, 19));
    bevelPanel1.add(textControl9, new XYConstraints(5, 173, 109, 19));
    bevelPanel1.add(textControl10, new XYConstraints(5, 194, 109, -1));
    bevelPanel1.add(tPlayer, new XYConstraints(119, 3, 84, 19));
    bevelPanel1.add(tCash, new XYConstraints(119, 24, 84, 19));
    bevelPanel1.add(tAssets, new XYConstraints(119, 46, 84, 19));
    bevelPanel1.add(tNetWorth, new XYConstraints(119, 67, 84, 19));
    bevelPanel1.add(tRentCollected, new XYConstraints(119, 88, 84, 19));
    bevelPanel1.add(tRentPaid, new XYConstraints(119, 109, 84, 19));
    bevelPanel1.add(tFeesPaid, new XYConstraints(119, 131, 84, 19));
    bevelPanel1.add(tFeesReceived, new XYConstraints(119, 152, 84, 19));
    bevelPanel1.add(tPayReceived, new XYConstraints(119, 173, 84, 19));
    bevelPanel1.add(tDaysLeftInJail, new XYConstraints(119, 192, 84, 19));
    bevelPanel1.add(PlayerIcon, new XYConstraints(23, 8, 32, 30));
    bevelPanel1.add(JailCardChance, new XYConstraints(1, 230, 200, 114));
    bevelPanel1.add(JailCardChest, new XYConstraints(1, 347, 200, 114));
  }

  public void update(Player aPlayer)
  {
    tPlayer.setText(aPlayer.sName);
    tCash.setText(Formatting.money(aPlayer.iCash));
    int iAssets = aPlayer.getAssets();
    tAssets.setText(Formatting.money(iAssets));
    tNetWorth.setText(Formatting.money(aPlayer.getNetWorth()));
    tRentCollected.setText(Formatting.money(aPlayer.iRentCollected));
    tRentPaid.setText(Formatting.money(aPlayer.iRentPaid));
    tFeesPaid.setText(Formatting.money(aPlayer.iFeesPaid));
    tFeesReceived.setText(Formatting.money(aPlayer.iFeesReceived));
    tPayReceived.setText(Formatting.money(aPlayer.iPayReceived));
    tDaysLeftInJail.setText(aPlayer.iDaysLeftInJail + "");

    JailCardChance.setVisible(aPlayer.bHasJailCardChance);
//    JailCardChance.setDrawEdge(true);
    JailCardChest.setVisible(aPlayer.bHasJailCardChest);
//    JailCardChest.setDrawEdge(true);
    try
    {
      if (aPlayer.bHasJailCardChance)
        JailCardChance.setImage(Monopoly.getCardImage("Chance00"));
      if (aPlayer.bHasJailCardChest)
        JailCardChest.setImage(Monopoly.getCardImage("Chest00"));
      PlayerIcon.setImage(aPlayer.getInactiveIconImage());
    }
    catch (Exception e) { e.printStackTrace(); }
  }

}
