package Player;

import java.util.Hashtable;

public class PlayerImageLoader extends Monopoly.ImageLoader
{
  private int MAX_ENTRIES = 12;

  public PlayerImageLoader()
  {
    sNames = new String[MAX_ENTRIES];
    anImageTable = new Hashtable(MAX_ENTRIES);

    sImageDir = "/Player/Images/";
    sDefaultExt = "gif";
    sNames[0] = "Player1";
    sNames[1] = "Player2";
    sNames[2] = "Player3";
    sNames[3] = "Player4";
    sNames[4] = "Player5";
    sNames[5] = "Player6";
    sNames[6] = "Player1_anim";
    sNames[7] = "Player2_anim";
    sNames[8] = "Player3_anim";
    sNames[9] = "Player4_anim";
    sNames[10] = "Player5_anim";
    sNames[11] = "Player6_anim";
  }

}