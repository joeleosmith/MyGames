package Property;

import java.util.Hashtable;

public class HouseImageLoader extends Monopoly.ImageLoader
{
  private static final String BASE_NAME = "House";

  public HouseImageLoader()
  {
    sNames = new String[21];
    anImageTable = new Hashtable(21);

    sImageDir = "/Property/House Images/";
    sDefaultExt = "gif";
    int iNbr = 0;
    sNames[iNbr++] = BASE_NAME;
    for (int j = 1; j <= 5; j++)
    {
      sNames[iNbr++] = BASE_NAME + j + "east";
      sNames[iNbr++] = BASE_NAME + j + "north";
      sNames[iNbr++] = BASE_NAME + j + "south";
      sNames[iNbr++] = BASE_NAME + j + "west";
    }
  }

}
