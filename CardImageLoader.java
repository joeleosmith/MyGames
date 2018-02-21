package Card;

import java.util.Hashtable;

public class CardImageLoader extends Monopoly.ImageLoader
{
  private static final String CHEST = "Chest";
  private static final String CHANCE = "Chance";

  public CardImageLoader()
  {
    sNames = new String[32];
    anImageTable = new Hashtable(32);

    sImageDir = "/Card/Images/";
    int iNbr = 0;

    for (int j = 0; j < 10; j++)
    {
      sNames[iNbr++] = CHEST + "0" + j;
      sNames[iNbr++] = CHANCE + "0" + j;
    }

    for (int j = 10; j < 16; j++)
    {
      sNames[iNbr++] = CHEST + j;
      sNames[iNbr++] = CHANCE + j;
    }
  }

}
