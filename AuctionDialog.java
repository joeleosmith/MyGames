package Monopoly;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.borland.dx.text.Alignment;
import com.borland.dx.dataset.Variant;
import com.borland.jb.util.Trace;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.util.*;
import com.heagy.gui.Prompting;
import com.heagy.util.Formatting;
import Player.*;
import Property.*;

public class AuctionDialog extends Dialog
{
  public boolean bOK = false;
  public boolean bCanceled = false;

  private Property aProperty;
  private int iBids[] = {0,0,0,0,0,0};
  private Player aWinningBidder;
  private int iWinningBid;

  Frame aParentWindow;
  Panel MainPanel = new Panel();
  XYLayout MainPanelLayout = new XYLayout();
  BevelPanel PlayersPanel = new BevelPanel();
  ShapeControl shapeControl1 = new ShapeControl();
  ShapeControl shapeControl2 = new ShapeControl();
  LabelControl[] tPlayerNames = new LabelControl[Monopoly.MAX_PLAYERS];
  MaskableTextItemEditor tBids[] = new MaskableTextItemEditor[Monopoly.MAX_PLAYERS];
  TransparentImage Icons[] = new TransparentImage[Monopoly.MAX_PLAYERS];
  ButtonControl cbSubmits[] = new ButtonControl[Monopoly.MAX_PLAYERS];

  LabelControl tPropertyLabel = new LabelControl();
  LabelControl tPropertyName = new LabelControl();
  LabelControl tPropertyValue = new LabelControl();
  LabelControl tPlayerHeading = new LabelControl();
  LabelControl tBidHeading = new LabelControl();
  ButtonControl cbOK = new ButtonControl();
  ButtonControl cbCancel = new ButtonControl();

  public AuctionDialog(Frame frame, String title, Property aProperty)
  {
    super(frame, title, true);
    this.aParentWindow = frame;
    this.aProperty = aProperty;
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    add(MainPanel);
    pack();
  }

  private void jbInit() throws Exception
  {
    final int iWidth = 361;
    final int iHeight = 361;
    MainPanel.setLayout(MainPanelLayout);
    Font aPropertyFont = new Font("Dialog", 1, 16);
    Font aPlayerFont = new Font("Dialog", 1, 15);
    MainPanelLayout.setWidth(361);
    MainPanelLayout.setHeight(361);
    PlayersPanel.setBevelInner(BevelPanel.FLAT);

    tPropertyLabel.setForeground(new Color(178, 0, 0));
    tPropertyLabel.setAlignment(Label.RIGHT);
    tPropertyLabel.setFont(aPropertyFont);
    tPropertyLabel.setText("Property for Auction:");
    tPropertyName.setForeground(Color.blue);
    tPropertyName.setFont(aPropertyFont);
    tPropertyName.setText(this.aProperty.sName);
    tPropertyValue.setForeground(Color.black);
    tPropertyValue.setFont(aPlayerFont);
    tPropertyValue.setText("Retail Value:  " + Formatting.money(this.aProperty.iPropertyCost));
    cbOK.setLabel("OK");
    cbOK.setEnabled(false);
    cbOK.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cbOK_actionPerformed(e);
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
    MainPanel.add(tPropertyLabel, new XYConstraints(3, 15, 175, 21));
    MainPanel.add(tPropertyName, new XYConstraints(187, 15, 211, 21));
    MainPanel.add(tPropertyValue, new XYConstraints(117, 39, 149, 21));
    MainPanel.add(cbOK, new XYConstraints(86, 322, 78, 28));
    MainPanel.add(cbCancel, new XYConstraints(204, 322, 78, 28));
    MainPanel.add(PlayersPanel, new XYConstraints(27, 67, 330, 254));

    tPlayerHeading.setFont(aPlayerFont);
    tPlayerHeading.setForeground(Color.blue);
    tPlayerHeading.setText("Player");
    tBidHeading.setFont(aPlayerFont);
    tBidHeading.setForeground(Color.blue);
    tBidHeading.setText("Bid ($)");
    shapeControl1.setType(ShapeControl.HORZ_LINE);
    shapeControl2.setType(ShapeControl.HORZ_LINE);
    PlayersPanel.add(tPlayerHeading, new XYConstraints(1, 0, 63, 21));
    PlayersPanel.add(tBidHeading, new XYConstraints(165, 0, 63, 21));
    PlayersPanel.add(shapeControl1, new XYConstraints(1, 22, 157, 7));
    PlayersPanel.add(shapeControl2, new XYConstraints(165, 22, 55, 7));

    int yPos = 34;
    for (int j=0; j < Monopoly.theNbrOfPlayers; j++)
    {
      tPlayerNames[j] = new LabelControl();
      tPlayerNames[j].setFont(aPlayerFont);
      tPlayerNames[j].setText(Monopoly.thePlayers[j].sName);
      Icons[j] = new TransparentImage();
      Icons[j].setImage(Monopoly.thePlayers[j].getInactiveIconImage());
      Icons[j].setDrawEdge(false);
      Icons[j].setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
      tBids[j] = new MaskableTextItemEditor();
      tBids[j].setFont(aPlayerFont);
      tBids[j].setEchoChar('*');
      cbSubmits[j] = new ButtonControl("Submit Bid");
      switch (j)
      {
        case 0:
          cbSubmits[j].addActionListener(new java.awt.event.ActionListener()
          {public void actionPerformed(ActionEvent e) {cbSubmits00_actionPerformed(e);} });
          break;
        case 1:
          cbSubmits[j].addActionListener(new java.awt.event.ActionListener()
          {public void actionPerformed(ActionEvent e) {cbSubmits01_actionPerformed(e);} });
          break;
        case 2:
          cbSubmits[j].addActionListener(new java.awt.event.ActionListener()
          {public void actionPerformed(ActionEvent e) {cbSubmits02_actionPerformed(e);} });
          break;
        case 3:
          cbSubmits[j].addActionListener(new java.awt.event.ActionListener()
          {public void actionPerformed(ActionEvent e) {cbSubmits03_actionPerformed(e);} });
          break;
        case 4:
          cbSubmits[j].addActionListener(new java.awt.event.ActionListener()
          {public void actionPerformed(ActionEvent e) {cbSubmits04_actionPerformed(e);} });
          break;
        case 5:
          cbSubmits[j].addActionListener(new java.awt.event.ActionListener()
          {public void actionPerformed(ActionEvent e) {cbSubmits05_actionPerformed(e);} });
          break;
      }

      PlayersPanel.add(tPlayerNames[j], new XYConstraints(1, yPos, 118, 28));
      PlayersPanel.add(Icons[j], new XYConstraints(129, yPos, 27, 28));
      PlayersPanel.add(tBids[j], new XYConstraints(165, yPos, 55, 28));
      PlayersPanel.add(cbSubmits[j], new XYConstraints(231, yPos, 79, 28));
      yPos += 34;
    }

      // Center the window
    Dimension frmSize = aParentWindow.getSize();
    Point loc = aParentWindow.getLocation();
    this.setLocation((frmSize.width - iWidth) / 2 + loc.x, (frmSize.height - iHeight) / 2 + loc.y);
    this.setResizable(false);
  }

  public Player getWinningBidder()
  {
      // determine the winning bidder
    aWinningBidder = null;
    iWinningBid = 0;
    for (int j=0; j < Monopoly.theNbrOfPlayers; j++)
    {
      if (iBids[j] > iWinningBid)
      {
        iWinningBid = iBids[j];
        aWinningBidder = Monopoly.thePlayers[j];
      }
    }

    return aWinningBidder;
  }

  public int getWinningBid()
  {
    if (aWinningBidder == null)   // we may not have determined the winner yet
    {
      Player aWinner;
      aWinner = this.getWinningBidder();
    }
    return iWinningBid;
  }

  private void checkBid(int iBiddingPlayerNbr)
  {
    Player aBiddingPlayer = Monopoly.thePlayers[iBiddingPlayerNbr];
    int iCurrentBid = Formatting.stringToInt(tBids[iBiddingPlayerNbr].getText());

      // See if the player has enough money to cover the bid
    if (aBiddingPlayer.iCash < iCurrentBid)
    {
      Prompting.MsgBoxSimple(this.aParentWindow, aBiddingPlayer.sName + ": Bidding Error",
        "You bid more than your available cash of "
        + Formatting.money(aBiddingPlayer.iCash)
        + ".  Please enter a new bid.");
      tBids[iBiddingPlayerNbr].setText("");
    }

    else
    {
        // If the current bid matches anyone elses, then reject it.
      boolean bValueMatched = false;
      int iMatchedBidder=0;
      int j=0;
      while ( (! bValueMatched) && (j < Monopoly.theNbrOfPlayers) )
      {
        if ( (j != iBiddingPlayerNbr) && (! cbSubmits[j].isEnabled()) )
        bValueMatched = ( (iCurrentBid > 0) && (iCurrentBid == iBids[j]) );
      if (bValueMatched)
        iMatchedBidder = j;
      j++;
      }

      if (bValueMatched)  // show message
      {
        MessageDialog aMsgBox = new MessageDialog(this.aParentWindow, "Bidding Error",
          "The bid you entered matches another persons bid.  Please try again.", Message.OK);
        aMsgBox.show();
        aMsgBox = null;
        tBids[iMatchedBidder].setText("");
        iBids[iMatchedBidder] = 0;
        cbSubmits[iMatchedBidder].setEnabled(true);
        tBids[iBiddingPlayerNbr].setText("");
      }
      else  // accept the bid
      {
        cbSubmits[iBiddingPlayerNbr].setEnabled(false);
        iBids[iBiddingPlayerNbr] = iCurrentBid;
        tBids[iBiddingPlayerNbr].setText("******");
      }

        // Check for everyone having completed their bidding
      j = 0;
      while ( (j < Monopoly.theNbrOfPlayers) && (! cbSubmits[j].isEnabled()))
        j++;

        // if we made it through all the players, then everyone has submitted a bid.
      if (j == Monopoly.theNbrOfPlayers)
      {
          // Show all the bids
        for (int k=0; k < Monopoly.theNbrOfPlayers; k++)
        {
          char cNormal = 0;
          tBids[k].setEchoChar(cNormal);
          tBids[k].setText(Formatting.money(iBids[k]));
        }

        cbOK.setEnabled(true);
      }
    }
  }

  // OK
  void cbOK_actionPerformed(ActionEvent e)
  {
    bOK = true;
    dispose();
  }

  // Cancel
  void cbCancel_actionPerformed(ActionEvent e)
  {
    bCanceled = true;
    aWinningBidder = null;
    dispose();
  }

  // Submits Buttons
  void cbSubmits00_actionPerformed(ActionEvent e)
  {
    checkBid(0);
  }
  void cbSubmits01_actionPerformed(ActionEvent e)
  {
    checkBid(1);
  }
  void cbSubmits02_actionPerformed(ActionEvent e)
  {
    checkBid(2);
  }
  void cbSubmits03_actionPerformed(ActionEvent e)
  {
    checkBid(3);
  }
  void cbSubmits04_actionPerformed(ActionEvent e)
  {
    checkBid(4);
  }
  void cbSubmits05_actionPerformed(ActionEvent e)
  {
    checkBid(5);
  }

  void this_windowClosing(WindowEvent e)
  {
    dispose();
  }
}
