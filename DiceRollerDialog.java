package Property;

import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import javax.swing.*;
import com.heagy.gaming.*;

public class DiceRollerDialog extends Dialog
{
  private static final int WINDOW_HEIGHT = 210;
  private static final int WINDOW_WIDTH = 310;

  private int iDieValue1 = 0;
  private int iDieValue2 = 0;
  private Frame aParentWindow;
  private boolean isDiceRolled = false;

  JPanel panel1 = new JPanel();
  XYLayout xYLayout1 = new XYLayout();

  Button cbRoll = new Button();
  LabelControl tLabel1 = new LabelControl();
  LabelControl tLabel2 = new LabelControl();
  LabelControl tLabel3 = new LabelControl();
  DiceBean diceBean1 = new DiceBean();

  public DiceRollerDialog(Frame frame, String title)
  {
    super(frame, title, true);
    aParentWindow = frame;
    try
    {
      jbInit();
      pack();
    }
    catch (Exception e) { e.printStackTrace(); }
  }

  void jbInit() throws Exception
  {
    panel1.setLayout(xYLayout1);

    cbRoll.setLabel("Roll Dice");
    cbRoll.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(ActionEvent e)
          {cbRoll_actionPerformed(e);}
        });
    tLabel1.setForeground(Color.blue);
    tLabel1.setFont(new Font("Dialog", 1, 14));
    tLabel1.setText("Your rent is equal to the value of the");
    tLabel2.setFont(new Font("Dialog", 1, 14));
    tLabel2.setForeground(Color.blue);
    tLabel2.setText("dice multiplied by 10.");
    tLabel3.setFont(new Font("Dialog", 1, 14));
    tLabel3.setForeground(Color.blue);
    tLabel3.setText("Press the button below to roll the dice.");

    xYLayout1.setHeight(WINDOW_HEIGHT);
    xYLayout1.setWidth(WINDOW_WIDTH);

    diceBean1.addDiceListener(new com.heagy.gaming.DiceListener()
    {
      public void diceStartedRolling()  { }
      public void diceTumbled(DiceEvent e) { }
      public void diceFinishedRolling(DiceEvent e)
      {
        diceBean1_diceFinishedRolling(e);
      }
    });

    add(panel1);
    panel1.add(tLabel1, new XYConstraints(19, 29, 280, 26));
    panel1.add(tLabel2, new XYConstraints(19, 52, 280, 26));
    panel1.add(tLabel3, new XYConstraints(19, 95, 280, 26));
    panel1.add(diceBean1, new XYConstraints(169, 128, 97, 56));
    panel1.add(cbRoll, new XYConstraints(37, 138, 120, 34));

      // Center the window
    Dimension frmSize = aParentWindow.getSize();
    Point loc = aParentWindow.getLocation();
    this.setLocation((frmSize.width - WINDOW_WIDTH) / 2 + loc.x,
                     (frmSize.height - WINDOW_HEIGHT) / 2 + loc.y);
    this.setResizable(false);
  }

  void this_windowClosing(WindowEvent e)
  {
    dispose();
  }

  public int getDiceTotal()
  {
    return iDieValue1 + iDieValue2;
  }

  void cbRoll_actionPerformed(ActionEvent e)
  {
    if (isDiceRolled)
      dispose();
    else
    {
      cbRoll.setEnabled(false);
      diceBean1.rollDice(20,100);
      cbRoll.setLabel("Pay ??? for rent");
    }
  }

  void diceBean1_diceFinishedRolling(DiceEvent e)
  {
    iDieValue1 = e.getDie1Value();
    iDieValue2 = e.getDie2Value();
    isDiceRolled = true;
    cbRoll.setLabel("Pay $" + (getDiceTotal() * 10) + " for rent");
    cbRoll.setEnabled(true);
  }

}
