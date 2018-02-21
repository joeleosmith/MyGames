package Board;

import java.awt.*;

public class CornerCell extends GameBoardCell
{
  public CornerCell(Point TopLeft, Point BottomRight)
  {
    super(TopLeft, BottomRight);
    aPlayerLocPoints[0] = new Point(aTopLeft.x + 5, aTopLeft.y + 21);
    aPlayerLocPoints[1] = new Point(aTopLeft.x + 34, aTopLeft.y + 21);
    aPlayerLocPoints[2] = new Point(aTopLeft.x + 63, aTopLeft.y + 21);
    aPlayerLocPoints[3] = new Point(aTopLeft.x + 5, aTopLeft.y + 54);
    aPlayerLocPoints[4] = new Point(aTopLeft.x + 34, aTopLeft.y + 54);
    aPlayerLocPoints[5] = new Point(aTopLeft.x + 63, aTopLeft.y + 54);
  }
}
