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
import Player.*;

public class MortgagedControl extends BeanPanel implements BlackBox
{

  public static final String EVENT_UNMORTGAGE = "UnMortgage";

  private Property  aProperty;
  private Player    aPlayer;

  BevelPanel    MainPanel = new BevelPanel();
  BorderLayout  borderLayout1 = new BorderLayout();
  XYLayout      xYLayout1 = new XYLayout();
  XYLayout      xYLayout2 = new XYLayout();
  BevelPanel    ImagePanel = new BevelPanel();
  ButtonControl cbAction = new ButtonControl();
  TextControl   tHighlight = new TextControl();
  TextControl   tName = new TextControl();
  TextControl   tValue = new TextControl();

  public MortgagedControl(Property aProperty, Player aPlayer)
  {
    this.aPlayer = aPlayer;
    this.aProperty = aProperty;
    try
    {
      jbInit();
    }
    catch (Exception e) { e.printStackTrace(); }
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
    ImagePanel.setBackground(Color.white);
    ImagePanel.setBevelOuter(BevelPanel.RAISED);

    tHighlight.setText("MORTGAGED");
    tHighlight.setFont(new Font("Helvetica", 1, 14));
    tHighlight.setForeground(Color.red);
    tHighlight.setAlignment(Formatting.ALIGN_CENTER);

    tName.setFont(new Font("Helvetica", 1, 15));
    tName.setText(aProperty.sName);
    tName.setAlignment(Formatting.ALIGN_CENTER);

    tValue.setFont(new Font("Helvetica", 1, 14));
    tValue.setText(Formatting.money(aProperty.iMortgageValue));
    tValue.setAlignment(Formatting.ALIGN_CENTER);

    if ((aPlayer != null) && (aPlayer == aProperty.aOwner))
    {
      cbAction.setLabel("Un-mortgage for " + Formatting.money(aProperty.iUnMortgageCost));
      cbAction.addActionListener(new java.awt.event.ActionListener()
        { public void actionPerformed(ActionEvent e) {cbAction_actionPerformed(e);} });

      if (aPlayer.iCash < aProperty.iUnMortgageCost)
        cbAction.setEnabled(false);

      MainPanel.add(cbAction, new XYConstraints(0, 233, 196, 35));
    }

    this.add(MainPanel, BorderLayout.CENTER);
    MainPanel.add(ImagePanel, new XYConstraints(0, 0, 196, 220));
    ImagePanel.add(tValue, new XYConstraints(0, 155, 190, -1));
    ImagePanel.add(tName, new XYConstraints(0, 33, 192, -1));
    ImagePanel.add(tHighlight, new XYConstraints(52, 95, -1, -1));
  }

  void cbAction_actionPerformed(ActionEvent e)
  {
    processActionEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, EVENT_UNMORTGAGE));
  }

  void this_focusGained(FocusEvent e)
  {
    cbAction.requestFocus();
  }

}
