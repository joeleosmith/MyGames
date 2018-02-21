package Player;

import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import java.awt.*;
import java.util.Vector;
import com.heagy.util.*;
import com.heagy.threading.*;
import Monopoly.*;
import Property.*;
import Board.*;
import Card.*;

public class Player implements MovementListener
{
  public static final int REASON_PASSING_GO = 1;
  public static final int REASON_RENT = 2;
  public static final int REASON_FEE = 3;

  public String sIconName;
  public String sName;
  public int iDoublesRolled = 0;
  public int iCash;
  public int iCashNeeded = 0;
  public int iPropertyCnt = 0;
  public Property[] aProperties = new Property[Monopoly.MAX_OWNABLE_PROPERTIES];
  public int iRailroadsOwned;
  public boolean bHasJailCardChest;
  public boolean bHasJailCardChance;
  public int iDaysLeftInJail;
  public boolean bInJail;
  public int iRentCollected;
  public int iRentPaid;
  public int iFeesPaid;
  public int iFeesReceived;
  public int iPayReceived;
  public boolean bBankrupt;
  public GameBoardCell aCurrentCell;
  private TransparentImage aBoardIcon;
  private Image aInactiveIconImage;
  public Player aPlayerOwed;  // The player (or bank) this player currently owes money to

  private GameBoardCell aDestinationCell;
  private transient Vector movementListeners;

  public Player(String Name, int StartingCash, String sIconName)
  {
    sName = Name;
    iCash = StartingCash;
    if (sIconName.length() > 0)
    {
      this.sIconName = sIconName;
      aInactiveIconImage = Monopoly.getPlayerImage(sIconName, false);

      aBoardIcon = new TransparentImage();
      aBoardIcon.setDrawEdge(false);
      aBoardIcon.setVisible(false);
      aBoardIcon.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
      try {
        aBoardIcon.setImage(aInactiveIconImage);  // Images are the same initially
      }
      catch (java.io.IOException e) {}
    }
  }

  public void activateIcon()
  {
    try {
      aBoardIcon.setImage(Monopoly.getPlayerImage(sIconName, true));
    }
    catch (java.io.IOException e) {}
  }

  public void deactivateIcon()
  {
    try {
      aBoardIcon.setImage(Monopoly.getPlayerImage(sIconName, false));
    }
    catch (java.io.IOException e) {}
  }

  public Point getBoardIconLocation()
  {
    return aBoardIcon.getLocation();
  }

  public TransparentImage getBoardIcon()
  {
    return aBoardIcon;
  }

  public void showBoardIcon()
  {
    aBoardIcon.setVisible(true);
  }

  public void hideBoardIcon()
  {
    aBoardIcon.setVisible(false);
  }

  public void setBoardIconLocation(Point aNewLocation)
  {
    aBoardIcon.setLocation(aNewLocation);
  }

  public Image getInactiveIconImage()
  {
    return aInactiveIconImage;
  }

  public void transferAllAssets(Player aToPlayer, int iReason)
  {
    for (int j=0; j < this.iPropertyCnt; j++)
    {
      if (aToPlayer == Monopoly.theBanker)  // return property to bank
        this.aProperties[j].returnToBank();
      else // send to another player
        aToPlayer.buyProperty(this.aProperties[j], 0);
    }

		  // Remove references of these previously owned properties
    for (int j=0; j < this.iPropertyCnt; j++)
      this.aProperties[j] = null;
		this.iPropertyCnt = 0;
		this.iRailroadsOwned = 0;

      // Transfer remaining cash
    aToPlayer.receivePay(this.iCash, iReason);
		this.iCash = 0;
  }

  public void openTrade(Frame aFrame)
  {
    TradeDialog aWindow = new TradeDialog(aFrame);
    aWindow.show();
    aWindow = null;
    showTitles(Monopoly.theGameBoard);
  }

  public int maintainAssets(Frame aFrame, int iReason)
  {
    int iAmountTransferred = 0;

    AssetMaintDialog aWindow = new AssetMaintDialog(aFrame, this, iReason);
    aWindow.show();

      // If Bankrupt transfer all assets to player owed
    if (this.bBankrupt)
    {
      iAmountTransferred = this.iCash;
      this.transferAllAssets(this.aPlayerOwed, iReason);
      this.aCurrentCell.removePlayerIcon(this);   // remove Icon from playing board
    }

    aWindow = null;
    return iAmountTransferred;
  }

  public void addProperty(Property aPropertyToAdd, int iPricePaid)
  {
    iPropertyCnt++;
    aProperties[iPropertyCnt - 1] = aPropertyToAdd;
    aPropertyToAdd.aOwner = this;
    aPropertyToAdd.iPricePaid = iPricePaid;
      // if the property is a railroad then increment the railroad count
    if (aPropertyToAdd.iPropertyType == Property.RAILROAD)
      this.iRailroadsOwned++;
  }

  public void removeProperty(Property aPropertyToRemove)
  {
    int iPropNbr = 0;
    while ( (iPropNbr < this.iPropertyCnt) &&
            (this.aProperties[iPropNbr] != aPropertyToRemove) )
      iPropNbr++;
      // if found, then take the last property in the array and move it to this position
    if (iPropNbr < iPropertyCnt)
    {
      iPropertyCnt--;
      if (aPropertyToRemove.iPropertyType == Property.RAILROAD)
        this.iRailroadsOwned--;
      if ( (iPropertyCnt > 0) && (iPropNbr != iPropertyCnt) )
      {
        aProperties[iPropNbr] = aProperties[iPropertyCnt];
        aProperties[iPropertyCnt] = null;
      }
      else
        aProperties[iPropNbr] = null;
      aPropertyToRemove.aOwner = null;
    }
  }

  public void loadAssets(TreeControl aTree)
  {
    com.borland.jbcl.model.GraphLocation aRoot, aBranch;

      // First clear the tree
    aTree.removeAllItems();
    aRoot = aTree.addChild(null, new TreeNode("Property Assets for " + sName));
      // Go through list of groups/properties and add to tree
    for (int iGroupNbr=0; iGroupNbr < Monopoly.GROUP_CNT; iGroupNbr++)
    {
      PropertyGroup aGroup = Monopoly.thePropGroups[iGroupNbr];
      int iPropertyCnt = 0;
      int iNameCnt = 0;
      String[] sNames = new String[4];   // up to 4 properties per group
        // FOR EACH PROPERTY IN THE GROUP
      for (int iPropNbr=0; iPropNbr < aGroup.iPropertyCnt; iPropNbr++)
      {
        Property aProp = aGroup.aProperties[iPropNbr];
          // If this Player owns the property...
        if (aProp.aOwner == this)
        {
          aBranch = aTree.addChild(aRoot, new TreeNode(aProp));
          if (aProp.iHouseCount == 5)
            aTree.addChild(aBranch, new TreeNode(aProp, TreeNode.HOTEL));
          else
            for (int j=0; j < aProp.iHouseCount; j++)
              aTree.addChild(aBranch, new TreeNode(aProp, TreeNode.HOUSE));
        }
      }
    }
  }

  public void positionInCell(GameBoardCell aDestCell)
  {
      // Set this players current cell to the destination cell
    this.aCurrentCell = aDestCell;
      // Inform of destination cell of the current players occupation
    this.aCurrentCell.showPlayerIcon(this);
  }

  public void moveIcon(GameBoardCell aFromCell, GameBoardCell aDestCell,
                       boolean bDirectRoute, boolean bMoveForward)
  {
    this.aDestinationCell = aDestCell;
    if (aFromCell != null)  // if actual image movement is necessary
    {
      ImageMover aImageMover = new ImageMover(this, aFromCell, aDestCell, bDirectRoute,
                                              bMoveForward, 1, 10, this);
    }
    else
      this.positionInCell(this.aDestinationCell);
  }

// *******************************************************************
// Methods for handling objects wishing to listen to Movement events
// *******************************************************************

  public synchronized void addMovementListener(MovementListener l)
  {
    Vector v = movementListeners == null ? new Vector(2) : (Vector) movementListeners.clone();
    if(!v.contains(l))
    {
      v.addElement(l);
      movementListeners = v;
    }
  }

  public synchronized void removeMovementListener(MovementListener l)
  {
    if(movementListeners != null && movementListeners.contains(l))
    {
      Vector v = (Vector) movementListeners.clone();
      v.removeElement(l);
      movementListeners = v;
    }
  }

  protected void fireImageStartedMoving()
  {
    if(movementListeners != null)
    {
      Vector listeners = movementListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++)
      {
        ((MovementListener) listeners.elementAt(i)).imageStartedMoving();
      }
    }
  }

  protected void fireImageFinishedMoving()
  {
    if(movementListeners != null)
    {
      Vector listeners = movementListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++)
      {
        ((MovementListener) listeners.elementAt(i)).imageFinishedMoving();
      }
    }
  }

  protected void fireImageLandedOnStart()
  {
    if(movementListeners != null)
    {
      Vector listeners = movementListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++)
      {
        ((MovementListener) listeners.elementAt(i)).imageLandedOnStart();
      }
    }
  }

// *******************************************************************


// *******************************************************************
// Methods for Movement Listener Interface (Respond to the event)
// *******************************************************************
  public void imageStartedMoving()
  {
    this.fireImageStartedMoving();
  }

  public void imageFinishedMoving()
  {
    boolean isReallyFinishedMoving = true;

    this.positionInCell(this.aDestinationCell);

      // Process the destination cell
    if (aCurrentCell == Monopoly.theGameBoard.theGotoJailCell)
    {
      this.goToJail(aCurrentCell);
      this.iDoublesRolled = 0;
      isReallyFinishedMoving = false;   // Animation still needs to take place
    }
    else if (aCurrentCell.aProp.iPropertyType == Property.COMMUNITY_CHEST)
    {
      Card aCard = Monopoly.getNextChestCard();
      isReallyFinishedMoving = (aCard.process(aCurrentCell, this) != Card.MOVEMENT);
    }
    else if (aCurrentCell.aProp.iPropertyType == Property.CHANCE)
    {
      Card aCard = Monopoly.getNextChanceCard();
      isReallyFinishedMoving = (aCard.process(aCurrentCell, this) != Card.MOVEMENT);
    }

    if (isReallyFinishedMoving)
    {
        // If the player just moved into jail- check to see if he is visiting or not
      if (this.iDaysLeftInJail > 0)
        this.positionInJailCell();
        // Tell anyone else listening to this object that we're finished moving
      this.fireImageFinishedMoving();
    }
  }

  public void imageLandedOnStart()
  {
    System.out.println("Player landed on GO.");
    this.receivePay(Monopoly.PAY_AMOUNT, REASON_PASSING_GO);
    Monopoly.theGameBoard.aPlayerInfo.update(this);
    this.fireImageLandedOnStart();
  }
// *******************************************************************

  public int getAssets()
  {
    int iTotal = 0;

      // Add up value of all properties
    for (int j=0; j < this.iPropertyCnt; j++)
    {
      Property aProp = this.aProperties[j];

      if (aProp.bRentable)
        iTotal += (aProp.iPropertyCost + (aProp.iHouseCost * aProp.iHouseCount));
    }

    return iTotal;
  }

  public int getNetWorth()
  {
    return (this.iCash + this.getAssets());
  }

  public void positionInJailCell()
  {
      // remove player from the visiting jail portion and into the IN jail portion
    Monopoly.theGameBoard.theJailCell.removePlayerIcon(this);
    Monopoly.theGameBoard.theJailCell.putInJail(this);
  }

  public void goToJail(GameBoardCell aFromCell)
  {
    this.bInJail = true;
    this.iDaysLeftInJail = 3;
    this.moveIcon(aFromCell, Monopoly.theGameBoard.theJailCell, true, true);
  }

  public void getOutOfJail()
  {
    this.bInJail = false;
    this.iDaysLeftInJail = 0;
      // remove player from the IN jail portion and into the Just Visiting jail portion
    Monopoly.theGameBoard.theJailCell.removeFromJail(this);
  }

  public void showTitles(GameBoard theGameBoard)
  {
    final int PANEL2_START = 15;
    final int PANEL3_START = 30;
    int iCurrentPanel = 1;
    int iPanel1Rows = 15;
    int iPanel2Rows = 15;
    int iPanel3Rows = 10;
    int iAvailRows = iPanel1Rows;

      // ONLY EXECUTE IF THE CURRENT GAME PLAYER IS THIS PLAYER
    if (Monopoly.theCurrentPlayer == this)
    {
      theGameBoard.resetTitleImages();

        // Traverse list of property groups / properties and build the image list
        // For each change in group, skip a title image
        // If there aren't enough remaining spaces on the current panel, carry all
        // property images onto the next one.

        // FOR EACH GROUP
      for (int iGroupNbr=0; iGroupNbr < Monopoly.GROUP_CNT; iGroupNbr++)
      {
        PropertyGroup aGroup = Monopoly.thePropGroups[iGroupNbr];
        int iPropertyCnt = 0;
        int iNameCnt = 0;
        String[] sNames = new String[4];   // up to 4 properties per group
          // FOR EACH PROPERTY IN THE GROUP
        for (int iPropNbr=0; iPropNbr < aGroup.iPropertyCnt; iPropNbr++)
        {
           Property aProp = aGroup.aProperties[iPropNbr];
            // If this Player owns the property...
          if (aProp.aOwner == this)
          {
            sNames[iNameCnt] = aProp.sName;
            iNameCnt++;
            iPropertyCnt++;
          }
        }
          // If any properties in the current group are owned by this player,
          // then transfer the images onto the appropriate panel
        if (iNameCnt > 0)
        {
            // Find the correct panel
          if (iNameCnt > iAvailRows)
          {
            iCurrentPanel++;
            if (iCurrentPanel == 2) iAvailRows = iPanel2Rows;
            else if (iCurrentPanel == 3) iAvailRows = iPanel3Rows;
          }
            // Calculate the Title Image Nbr
          int iTitleImageNbr = -1;
          if (iCurrentPanel == 1)
            iTitleImageNbr = (iPanel1Rows - iAvailRows);
          else if (iCurrentPanel == 2)
            iTitleImageNbr = (iPanel2Rows - iAvailRows + PANEL2_START);
          else if (iCurrentPanel == 3)
            iTitleImageNbr = (iPanel3Rows - iAvailRows + PANEL3_START);

          if (iTitleImageNbr >= 0)
          {
              // Assign the images and make them visible
            for (int j=0; j < iNameCnt; j++)
            {
              TransparentImage anImage = theGameBoard.TitleImages[iTitleImageNbr];
              theGameBoard.TitleImageNames[iTitleImageNbr] = sNames[j];
              try {
                anImage.setImage(Monopoly.getPropHdrImage(sNames[j]));
              }
              catch (Exception e) {e.printStackTrace();}
              iTitleImageNbr++;
              anImage.setVisible(true);
            }

              // Hide the next row (if possible) and decrement the available rows by
              // the number of properties owned + an additional row to act as a separator
            iAvailRows = iAvailRows - (iNameCnt + 1);
          }
        }
      }
    }
  };

  public int payFee(int iAmount, Player aDestination, int iReason)
  {
    int iAmountPaid = iAmount;

    if ((iCash - iAmount) < 0)    // if not enough cash to pay bill
    {
        // try to mortgage properties etc.
        // if still insufficient funds after transfer GO BANKRUPT
      this.aPlayerOwed = aDestination;
      this.iCashNeeded = iAmount;
      iAmountPaid = this.maintainAssets(Monopoly.theGameBoard, iReason);
      if (iAmountPaid == 0)  // NOT gone bankrupt - haven't paid fee yet
      {
        iAmountPaid = iAmount;
        iCash -= iAmountPaid;
      }
      this.aPlayerOwed = null;
    }
    else
      iCash -= iAmountPaid;

    if (iReason == REASON_FEE)
      this.iFeesPaid += iAmountPaid;
    else if (iReason == REASON_RENT)
      this.iRentPaid += iAmountPaid;

    if (! this.bBankrupt)   // if Bankrupt, transfer has already occurred
      aDestination.receivePay(iAmountPaid, iReason);

    return iAmountPaid;
  }

  public void receivePay(int iAmount, int iReason)
  {
    iCash += iAmount;
    switch (iReason)
    {
      case REASON_PASSING_GO: this.iPayReceived += iAmount; break;
      case REASON_FEE:        this.iFeesReceived += iAmount; break;
      case REASON_RENT:       this.iRentCollected += iAmount; break;
    }
  }

  public boolean buyProperty(Property aProperty, int iPrice)
  {
    boolean bBought = false;

    if (iPrice <= iCash)
    {
      this.iCash -= iPrice;
      this.aProperties[iPropertyCnt++] = aProperty;
      aProperty.buy(this, iPrice);
        // if the property is a railroad then increment the railroad count
      if (aProperty.iPropertyType == Property.RAILROAD)
        this.iRailroadsOwned++;
      bBought = true;
      showTitles(Monopoly.theGameBoard);
    }

    return bBought;
  }

  public boolean unMortgageProperty(Property aProperty, int iCost)
  {
    boolean bOK = false;

    if (iCost <= iCash)
    {
      this.iCash -= iCost;
      aProperty.unMortgage();
        // if the property is a railroad then increment the railroad count
      if (aProperty.iPropertyType == Property.RAILROAD)
        this.iRailroadsOwned--;
      bOK = true;
      showTitles(Monopoly.theGameBoard);
    }

    return bOK;
  }

  public void mortgageProperty(Property aProperty)
  {
    this.iCash += aProperty.iMortgageValue;
      // Update the player values on the game board
    //????????????????
    aProperty.mortgage();
  }

  public void buildHouse(Property aProperty)
  {
    if (aProperty.iHouseCost <= iCash)
    {
      this.iCash -= aProperty.iHouseCost;
      aProperty.buildHouse();
    }
  }

  public void sellHouse(Property aProperty)
  {
    this.iCash += (aProperty.iHouseCost / 2);
    aProperty.sellHouse();
  }

  public int railroadsOwned()
  {
    int iCnt = 0;
    for (int j=0; j < this.iPropertyCnt; j++)
      if (this.aProperties[j].iPropertyType == Property.RAILROAD)
        iCnt++;
    return iCnt;
  }

  public int utilitiesOwned()
  {
    int iCnt = 0;
    for (int j=0; j < this.iPropertyCnt; j++)
      if (this.aProperties[j].iPropertyType == Property.UTILITY)
        iCnt++;
    return iCnt;
  }

  public boolean ownsAllPropsInGroup(PropertyGroup aGroup)
  {
    boolean bAllOwned = true;
    int iPropNbr = 0;
    while ((bAllOwned) && (iPropNbr < aGroup.iPropertyCnt))
    {
      bAllOwned = ((aGroup.aProperties[iPropNbr].bRentable) &&
                   (aGroup.aProperties[iPropNbr].aOwner == this));
      iPropNbr++;
    }
    return bAllOwned;
  }

  public int getHouseCount()
  {
    int iCnt=0;
    for (int j = 0; j < this.iPropertyCnt; j++)
      if (this.aProperties[j].iHouseCount < 5)
        iCnt += this.aProperties[j].iHouseCount;
    return iCnt;
  }

  public int getHotelCount()
  {
    int iCnt=0;
    for (int j = 0; j < this.iPropertyCnt; j++)
      if (this.aProperties[j].iHouseCount == 5)
        iCnt++;
    return iCnt;
  }
}
