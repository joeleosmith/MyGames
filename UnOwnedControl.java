package Property;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Trace;
import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.view.*;
import com.borland.jbcl.util.BlackBox;
import com.heagy.util.*;
import Monopoly.*;
import Player.*;


public class UnOwnedControl extends BeanPanel implements BlackBox
{

  public static final String EVENT_BUY_PROPERTY = "BUY_PROPERTY";

  private Property aProperty;
  private Player aPlayer;

  BevelPanel    MainPanel = new BevelPanel();
  BorderLayout  borderLayout1 = new BorderLayout();
  XYLayout      xYLayout1 = new XYLayout();
  XYLayout      xYLayout2 = new XYLayout();
  BevelPanel    ImagePanel = new BevelPanel();
  ButtonControl cbAction = new ButtonControl();
  ImageControl  aPropertyImage = new ImageControl();

  public UnOwnedControl(Property aProperty, Player aPlayer)
  {
    this.aProperty = aProperty;
    this.aPlayer = aPlayer;
    try
    {
      jbInit();
    }
    catch (Exception e) {e.printStackTrace();}
  }

  private void jbInit() throws Exception
  {
    this.setLayout(borderLayout1);
    this.addFocusListener(new java.awt.event.FocusAdapter()
      { public void focusGained(FocusEvent e) {this_focusGained(e);} });

    MainPanel.setLayout(xYLayout1);
    MainPanel.setBevelInner(BevelPanel.FLAT);
    MainPanel.setEnabled(false);

    ImagePanel.setLayout(xYLayout2);
    ImagePanel.setSoft(true);
    ImagePanel.setEnabled(false);
    ImagePanel.setBevelOuter(BevelPanel.RAISED);

    if (aPlayer != null)
    {
      cbAction.setLabel("Buy for $" + aProperty.iPropertyCost);
      cbAction.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(ActionEvent e) {cbAction_actionPerformed(e);} });
      if (aPlayer.iCash < aProperty.iPropertyCost)
        cbAction.setEnabled(false);
      MainPanel.add(cbAction, new XYConstraints(0, 233, 204, 35));
    }

    aPropertyImage.setImage(Monopoly.getPropCardImage(aProperty.sName));
    aPropertyImage.setAlignment(Formatting.ALIGN_LEFT);
    aPropertyImage.setFlat(true);
    aPropertyImage.addFocusListener(new java.awt.event.FocusAdapter()
      { public void focusGained(FocusEvent e) {aPropertyImage_focusGained(e);} });

    this.add(MainPanel, BorderLayout.CENTER);
    MainPanel.add(ImagePanel, new XYConstraints(0, 0, -1, -1));
    ImagePanel.add(aPropertyImage, new XYConstraints(0, 0, -1, -1));
  }

  void cbAction_actionPerformed(ActionEvent e)
  {
    processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_BUY_PROPERTY));
  }

  void aPropertyImage_focusGained(FocusEvent e)
  {
    cbAction.requestFocus();
  }

  void this_focusGained(FocusEvent e)
  {
    cbAction.requestFocus();
  }

}
