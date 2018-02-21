package Board;

import java.awt.*;

public class WestCell extends GameBoardCell
{
  public WestCell(int y1Loc, int y2Loc)
  {
    super(new Point(7, y1Loc), new Point(100, y2Loc));
    aPlayerLocPoints[0] = new Point(aTopLeft.x + 0, aTopLeft.y + 2);
    aPlayerLocPoints[1] = new Point(aTopLeft.x + 25, aTopLeft.y + 2);
    aPlayerLocPoints[2] = new Point(aTopLeft.x + 50, aTopLeft.y + 2);
    aPlayerLocPoints[3] = new Point(aTopLeft.x + 0, aTopLeft.y + 29);
    aPlayerLocPoints[4] = new Point(aTopLeft.x + 25, aTopLeft.y + 29);
    aPlayerLocPoints[5] = new Point(aTopLeft.x + 50, aTopLeft.y + 29);
    aHouseLocation = new Point(aTopLeft.x + 75, aTopLeft.y);
  }
}
