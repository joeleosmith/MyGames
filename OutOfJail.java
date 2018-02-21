package Card;

import Player.*;
import Property.*;

public class OutOfJail extends Card
{
  public OutOfJail(String ImageName)
  {
    sImageName = ImageName;
  }

  public int process(Board.GameBoardCell aFromCell, Player aPlayer)
  {
      // Show the card
    this.show();

      // Store the card
    if (aFromCell.aProp.iPropertyType == Property.CHANCE)
      aPlayer.bHasJailCardChance = true;
    else if (aFromCell.aProp.iPropertyType == Property.COMMUNITY_CHEST)
      aPlayer.bHasJailCardChest = true;

    return Card.CARD;
  }
}