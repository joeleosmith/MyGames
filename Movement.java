package Card;

import Monopoly.*;
import Board.*;
import Player.*;
import Property.*;
import com.heagy.util.Util;

public class Movement extends Card
{
  public static final int RAILROAD = 1;
  public static final int UTILITY = 2;
  private GameBoardCell aToCell;
  private int iNbrOfSpaces;
  private boolean bNearestRailroad;
  private boolean bNearestUtility;
  private boolean bDoubleRent = false;
  private static final int ANIMATION_COMPLETED = 1;

  public Movement(String ImageName, GameBoardCell ToCell)
  {
    sImageName = ImageName;
    aToCell = ToCell;
    this.iSpecialFee = NONE;
  }

  public Movement(String ImageName, GameBoardCell ToCell, boolean DoubleRent)
  {
    sImageName = ImageName;
    aToCell = ToCell;
    bDoubleRent = DoubleRent;
    this.iSpecialFee = NONE;
  }

  public Movement(String ImageName, boolean NearestProp, int NearestPropType)
  {
    sImageName = ImageName;
    if (NearestPropType == RAILROAD)    // find nearest Railroad
      bNearestRailroad = true;
    else                                // find nearest utility
      bNearestUtility = true;
    this.iSpecialFee = Card.NONE;
  }

  public Movement(String ImageName, boolean NearestProp, int NearestPropType,
                  int SpecialFee)
  {
    sImageName = ImageName;
    if (NearestPropType == RAILROAD)    // find nearest Railroad
      bNearestRailroad = true;
    else                                // find nearest utility
      bNearestUtility = true;
     this.iSpecialFee = SpecialFee;
  }

  public Movement(String ImageName, int NbrOfSpaces)
  {
    sImageName = ImageName;
    iNbrOfSpaces = NbrOfSpaces;
    this.iSpecialFee = NONE;
  }

  public int process(GameBoardCell aFromCell, Player aPlayer)
  {
      // Show the card
    this.show();

      // If moving a specified number of spaces, calculate ToCell
    if (iNbrOfSpaces != 0)
    {
      aToCell = aFromCell;
      for (int j=1; j<=Math.abs(iNbrOfSpaces); j++)
      {
        if (iNbrOfSpaces < 0)
          aToCell = aToCell.aPrevCell;
        else
          aToCell = aToCell.aNextCell;
      }
    }

      // Else if moving to nearest railroad
    else if (bNearestRailroad)
    {
      aToCell = aFromCell;
      while (aToCell.aProp.iPropertyType != Property.RAILROAD)
        aToCell = aToCell.aNextCell;
    }

      // Else if moving to nearest utility
    else if (bNearestUtility)
    {
      aToCell = aFromCell;
      while (aToCell.aProp.iPropertyType != Property.UTILITY)
        aToCell = aToCell.aNextCell;
    }

      // Pause for a second before moving icon
    Util.delay(1500);

      // Going to JAIL?
    if (aToCell.aProp.iPropertyType == Property.JAIL)
    {
      aPlayer.goToJail(aPlayer.aCurrentCell);
    }
    else
    {
      Monopoly.theGameBoard.setSpecialFee(iSpecialFee);
      aPlayer.moveIcon(aPlayer.aCurrentCell, aToCell, false, (iNbrOfSpaces >= 0));
    }
    return Card.MOVEMENT;
  }
}
