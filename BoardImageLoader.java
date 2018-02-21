package Board;

import java.util.Hashtable;

public class BoardImageLoader extends Monopoly.ImageLoader
{
  private int MAX_ENTRIES = 6;

  public BoardImageLoader()
  {
    sNames = new String[MAX_ENTRIES];
    anImageTable = new Hashtable(MAX_ENTRIES);

    sImageDir = "/Board/";
    sDefaultExt = "gif";
    sNames[0] = "Monopoly Game Board.jpg";
    sNames[1] = "Monopoly Logo";
    sNames[2] = "You";
    sNames[3] = "Are";
    sNames[4] = "Bankrupt";
    sNames[5] = "Tom";
  }

}
