package Board;

import java.awt.*;
import Player.*;

public class JailCell extends GameBoardCell
{
  public static final int FEE = 50;
  private InJailCell aInJailCell;

  public JailCell(Point TopLeft, Point BottomRight)
  {
    super(TopLeft, BottomRight);
    aPlayerLocPoints[0] = new Point(aTopLeft.x + 0, aTopLeft.y + 2);
    aPlayerLocPoints[1] = new Point(aTopLeft.x + 0, aTopLeft.y + 37);
    aPlayerLocPoints[2] = new Point(aTopLeft.x + 0, aTopLeft.y + 72);
    aPlayerLocPoints[3] = new Point(aTopLeft.x + 25, aTopLeft.y + 72);
    aPlayerLocPoints[4] = new Point(aTopLeft.x + 50, aTopLeft.y + 72);
    aPlayerLocPoints[5] = new Point(aTopLeft.x + 75, aTopLeft.y + 72);
    aInJailCell = new InJailCell(new Point(30,613), new Point(100,681));
  }

  public void putInJail(Player aPlayer)
  {
    iPlayerCnt++;
    aInJailCell.showPlayerIcon(aPlayer);
  }

  public void removeFromJail(Player aPlayer)
  {
    aInJailCell.removePlayerIcon(aPlayer);
    iPlayerCnt--;
    showPlayerIcon(aPlayer);
  }

}
