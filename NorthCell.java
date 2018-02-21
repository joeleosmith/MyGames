package Board;

import java.awt.*;

public class NorthCell extends GameBoardCell
{
  public NorthCell(int x1Loc, int x2Loc)
  {
    super(new Point(x1Loc, 7), new Point(x2Loc, 100));
    aPlayerLocPoints[0] = new Point(aTopLeft.x + 2,  aTopLeft.y + 0);
    aPlayerLocPoints[1] = new Point(aTopLeft.x + 32, aTopLeft.y + 0);
    aPlayerLocPoints[2] = new Point(aTopLeft.x + 2,  aTopLeft.y + 25);
    aPlayerLocPoints[3] = new Point(aTopLeft.x + 32, aTopLeft.y + 25);
    aPlayerLocPoints[4] = new Point(aTopLeft.x + 2,  aTopLeft.y + 50);
    aPlayerLocPoints[5] = new Point(aTopLeft.x + 32, aTopLeft.y + 50);
    aHouseLocation = new Point(aTopLeft.x - 1, aTopLeft.y + 70);
  }
}
