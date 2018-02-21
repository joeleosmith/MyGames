package Card;

import Player.*;
import Monopoly.*;

public class Card
{
    // Special Fees
  public static final int NONE = 0;
  public static final int DOUBLE_RENT = 1;
  public static final int ROLL_DICE_TIMES_10 = 2;
    // Destinations of Fees
  public static final int PLAYERS = 1;
  public static final int BANK = 2;
    // Types of Card Processes
  public static final int MOVEMENT = 1;
  public static final int FEE = 2;
  public static final int PAYMENT = 3;
  public static final int CARD = 4;
  public static final int ASSESMENT = 5;

  public int iCashAmount;
  public int iOtherParty;
  public int iSpecialFee;
  public String sImageName;

  public Card()
  {
  }

  public Card(String ImageName, int CashAmount, int OtherParty)
  {
    sImageName = ImageName;
    iCashAmount = CashAmount;
    iOtherParty = OtherParty;
    iSpecialFee = NONE;
  }

  public void show()
  {
    Monopoly.theGameBoard.showCard(this.sImageName);
  }

  public static void hide()
  {
    Monopoly.theGameBoard.CardImage.setVisible(false);
  }

  public int process(Board.GameBoardCell aFromCell, Player aPlayer)
  {
    int iReturnType = 0;

    this.show();
      // If the amount was a fee (negative)
    if (iCashAmount < 0)
    {
      if (iOtherParty == this.BANK)
        aPlayer.payFee(Math.abs(iCashAmount), Monopoly.theBanker, Player.REASON_FEE);
      else  // Pay Each Player
      {
        for (int j = 0; j < Monopoly.theNbrOfPlayers; j++)
        {
          if ( (Monopoly.thePlayers[j] != aPlayer) &&
               (! Monopoly.thePlayers[j].bBankrupt) )
            aPlayer.payFee(Math.abs(iCashAmount), Monopoly.thePlayers[j], Player.REASON_FEE);
        }
      }
      iReturnType = Card.FEE;
    }

      // else if the amount represents a payment to the current player
    else
    {
      if (iOtherParty == this.BANK)
        aPlayer.receivePay(iCashAmount, Player.REASON_FEE);
      else  // Payment from each player
      {
        for (int j = 0; j < Monopoly.theNbrOfPlayers; j++)
        {
          if ( (Monopoly.thePlayers[j] != aPlayer) &&
               (! Monopoly.thePlayers[j].bBankrupt) )
            Monopoly.thePlayers[j].payFee(Math.abs(iCashAmount), aPlayer, Player.REASON_FEE);
        }
      }
      iReturnType = Card.PAYMENT;
    }

    return iReturnType;
  }
}