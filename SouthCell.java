package Board;

import java.awt.*;

public class SouthCell extends GameBoardCell
{
  public SouthCell(int x1Loc, int x2Loc)
  {
    super(new Point(x1Loc, 622), new Point(x2Loc, 715));
    aPlayerLocPoints[0] = new Point(aTopLeft.x + 2,  aTopLeft.y + 25);
    aPlayerLocPoints[1] = new Point(aTopLeft.x + 32, aTopLeft.y + 25);
    aPlayerLocPoints[2] = new Point(aTopLeft.x + 2,  aTopLeft.y + 50);
    aPlayerLocPoints[3] = new Point(aTopLeft.x + 32, aTopLeft.y + 50);
    aPlayerLocPoints[4] = new Point(aTopLeft.x + 2,  aTopLeft.y + 75);
    aPlayerLocPoints[5] = new Point(aTopLeft.x + 32, aTopLeft.y + 75);
    aHouseLocation = new Point(aTopLeft.x - 1, aTopLeft.y);
  }
}
