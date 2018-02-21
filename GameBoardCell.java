package Board;

import java.awt.*;
import com.borland.jbcl.control.*;
import Property.*;
import Player.*;

public class GameBoardCell
{
  public static final String SOUTH = "south";
  public static final String WEST = "west";
  public static final String NORTH = "north";
  public static final String EAST = "east";

  public BevelPanel aPanel;
  public Point aTopLeft;
  public Point aBottomRight;
  public Point aHouseLocation;
  public Property aProp;
  public GameBoardCell aNextCell;
  public GameBoardCell aPrevCell;
  public Point[] aPlayerLocPoints = new Point[6];
  public Player[] aUsedImageLocations = new Player[6];
  public int iPlayerCnt = 0;

  public GameBoardCell(Point TopLeft, Point BottomRight)
  {
    aTopLeft = TopLeft;
    aBottomRight = BottomRight;
  }

  public int findImageLocation(Player aPlayer)
  {
    int iLocNbr = 0;
    while ((iLocNbr < 6) && (aUsedImageLocations[iLocNbr] != aPlayer))
      iLocNbr++;
    return iLocNbr;
  }

  public int nextAvailImageLocation()
  {
    int iLocNbr = 0;
    while ((iLocNbr < 6) && (aUsedImageLocations[iLocNbr] != null))
      iLocNbr++;
    return iLocNbr;
  }

  public Point nextAvailImagePoint()
  {
    int iLocNbr = nextAvailImageLocation();
    if (iLocNbr < 6)
      return aPlayerLocPoints[iLocNbr];
    else
      return (new Point(0,0));
  }

  public void showPlayerIcon(Player aPlayer)
  {
    iPlayerCnt++;
      // Set the location based on the property type and the number of players
      // already on this cell
    int iLocNbr = nextAvailImageLocation();
    if (iLocNbr < 6)
    {
      aUsedImageLocations[iLocNbr] = aPlayer;
      aPlayer.setBoardIconLocation(aPlayerLocPoints[iLocNbr]);
    }
    aPlayer.showBoardIcon();
  }

  public void removePlayerIcon(Player aPlayer)
  {
    iPlayerCnt--;
    aPlayer.hideBoardIcon();
    int iImageLocation = findImageLocation(aPlayer);
    if (iImageLocation < 6)
      aUsedImageLocations[iImageLocation] = null;
  }

  public boolean isPlayerOn(Player aPlayer)
  {
      // determine if the queried player is on this cell
    int iPos = 0;
    while ((iPos < 6) && (this.aUsedImageLocations[iPos] != aPlayer))
      iPos++;
    return (iPos < 6);
  }

}
