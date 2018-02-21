package Board;

import java.util.*;

public interface MovementListener extends EventListener
{
  public void imageStartedMoving();
  public void imageLandedOnStart();
  public void imageFinishedMoving();
}
