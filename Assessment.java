package Card;

import Player.*;
import Monopoly.Monopoly;

public class Assessment extends Card
{
  int iFeePerHouse;
  int iFeePerHotel;

  public Assessment(String ImageName, int perHouse, int perHotel)
  {
    sImageName = ImageName;
    iFeePerHouse = perHouse;
    iFeePerHotel = perHotel;
  }

  public int process(Board.GameBoardCell aFromCell, Player aPlayer)
  {
      // Show the card
    this.show();

      // Count up all the houses and hotels owned by the player
    int iHouseCnt = aPlayer.getHouseCount();
    int iHotelCnt = aPlayer.getHotelCount();

      // Calculate the assessment value
    int iFee = (iHouseCnt * iFeePerHouse) + (iHotelCnt * iFeePerHotel);

      // Have the player pay the fee
    aPlayer.payFee(iFee, Monopoly.theBanker, Player.REASON_FEE);

    return Card.ASSESMENT;
  }
}