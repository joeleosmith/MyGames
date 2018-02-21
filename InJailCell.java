package Board;

import java.awt.*;

public class InJailCell extends GameBoardCell
{

  public InJailCell(Point TopLeft, Point BottomRight)
  {
    super(TopLeft, BottomRight);
    aPlayerLocPoints[0] = new Point(TopLeft.x + 8,  TopLeft.y + 8);
    aPlayerLocPoints[1] = new Point(TopLeft.x + 39, TopLeft.y + 8);
    aPlayerLocPoints[2] = new Point(TopLeft.x + 8,  TopLeft.y + 40);
    aPlayerLocPoints[3] = new Point(TopLeft.x + 39, TopLeft.y + 40);
    aPlayerLocPoints[4] = new Point(TopLeft.x + 8,  TopLeft.y + 72);
    aPlayerLocPoints[5] = new Point(TopLeft.x + 39, TopLeft.y + 72);
  }
}
