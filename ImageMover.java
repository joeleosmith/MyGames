package Board;

import java.awt.Point;
import java.util.*;
import com.heagy.threading.*;
import com.heagy.gui.Imaging;
import Player.Player;
import Property.Property;

public class ImageMover extends BasicThread
{
  public static final int DEFAULT_STEP_SIZE = 1;
  public static final int DEFAULT_STEP_DELAY = 10;
  private int iStepSize;
  private int iStepDelay;
  private transient Vector movementListeners;
  private Player aPlayer;
  private GameBoardCell aFromCell, aToCell;
  private boolean bDirectRoute, bMoveForward;

  public ImageMover(Player aPlayer, GameBoardCell aFromCell, GameBoardCell aToCell,
                    boolean bDirectRoute, boolean bMoveForward,
                    int iStepSize, int iStepDelay, MovementListener aListener)
  {
    super("Image Mover");
    this.aPlayer = aPlayer;
    this.aFromCell = aFromCell;
    this.aToCell = aToCell;
    this.bDirectRoute = bDirectRoute;
    this.bMoveForward = bMoveForward;
    if (iStepSize < 0) iStepSize = DEFAULT_STEP_SIZE;
    this.iStepSize = iStepSize;
    if (iStepDelay < 0) iStepDelay = DEFAULT_STEP_DELAY;
    this.iStepDelay = iStepDelay;
    this.addMovementListener(aListener);
    this.start();
  }

  public void run()
  {
    this.isRunning = true;
      // if aFromCell != null then animate movement
    if (aFromCell != null)
    {
      this.fireImageStartedMoving();

        // Remove the icon from the original cell but make it visible again
      aFromCell.removePlayerIcon(aPlayer);
      aPlayer.showBoardIcon();

        // Get current coordinates of icon
      Point aPoint = aPlayer.getBoardIconLocation();

        // if using a direct route then move directly to the destination cell
      if (bDirectRoute)
        Imaging.moveImage(aPlayer.getBoardIcon(), aPoint, aToCell.nextAvailImagePoint(), iStepSize, iStepDelay);
      else  // move by going from cell to cell
      {
        GameBoardCell aCell = aFromCell;
        while (isOKtoRun && (aCell != aToCell))
        {
            // Get the next cell
          if (bMoveForward)
            aCell = aCell.aNextCell;
          else
            aCell = aCell.aPrevCell;
            // Get coordinates of next cell and animate to that location
          Imaging.moveImage(aPlayer.getBoardIcon(), aPoint, aCell.nextAvailImagePoint(), iStepSize, iStepDelay);
            // if object landed on start (GO) fire event
          if (aCell.aProp.iPropertyType == Property.GO)
            this.fireImageLandedOnStart();
            // Check to see if this thread has been suspended
          this.checkForSuspended();
          thisThread.yield();
        }
      }   // end of 'moving cell to cell'

      this.fireImageFinishedMoving();
    }   // end of 'if from cell not null'

    this.isRunning = false;
  }

//=============================================================================
// Handle objects that are attempting to listen to us via MovementListener
//=============================================================================

  private synchronized void addMovementListener(MovementListener l)
  {
    Vector v = movementListeners == null ? new Vector(2) : (Vector) movementListeners.clone();
    if(!v.contains(l))
    {
      v.addElement(l);
      movementListeners = v;
    }
  }

//=============================================================================
// Fire the events associated with MovementListener
//=============================================================================

  protected void fireImageStartedMoving()
  {
    if(movementListeners != null)
    {
      Vector listeners = movementListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++)
      {
        ((MovementListener) listeners.elementAt(i)).imageStartedMoving();
      }
    }
  }

  protected void fireImageLandedOnStart()
  {
    if(movementListeners != null)
    {
      Vector listeners = movementListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++)
      {
        ((MovementListener) listeners.elementAt(i)).imageLandedOnStart();
      }
    }
  }

  protected void fireImageFinishedMoving()
  {
    if(movementListeners != null)
    {
      Vector listeners = movementListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++)
      {
        ((MovementListener) listeners.elementAt(i)).imageFinishedMoving();
      }
    }
  }

}