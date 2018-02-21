package Monopoly;

import com.borland.dx.text.Alignment;

import com.borland.jb.util.Trace;

import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.model.*;
import Player.*;

public class BankruptDialog extends Dialog
{
  private Frame aParentWindow;
  private static final int WINDOW_WIDTH = 390;
  private static final int WINDOW_HEIGHT = 330;


  Panel MainPanel = new Panel();
  XYLayout MainPanelLayout = new XYLayout();
  TransparentImage transparentImage1 = new TransparentImage();
  TransparentImage transparentImage2 = new TransparentImage();
  TransparentImage transparentImage3 = new TransparentImage();
  LabelControl labelControl1 = new LabelControl();
  LabelControl labelControl2 = new LabelControl();
  LabelControl labelControl3 = new LabelControl();
  TransparentImage transparentImage4 = new TransparentImage();

  public BankruptDialog(Frame frame, Player aPlayer)
  {
    super(frame, aPlayer.sName + " - YOU'RE BANKRUPT!!", true);
  	this.aParentWindow = frame;
      // Declare BANKRUPT
    aPlayer.bBankrupt = true;
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    add(MainPanel);
    pack();
  }

  private void jbInit() throws Exception
  {
    transparentImage2.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    transparentImage2.setDrawEdge(false);
    transparentImage2.setTransparent(false);
    transparentImage2.setImage(Monopoly.getBoardImage("You"));
    transparentImage3.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    transparentImage3.setDrawEdge(false);
    transparentImage3.setTransparent(false);
    transparentImage3.setImage(Monopoly.getBoardImage("Are"));
    labelControl1.setFont(new Font("TimesRoman", 0, 18));
    labelControl1.setText("Any remaining assets will now be transfered to");
    labelControl2.setText("whomever you owed the money.");
    labelControl3.setText("I hope you enjoyed the game!");
    transparentImage4.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    transparentImage4.setDrawEdge(false);
    transparentImage4.setTransparent(false);
    transparentImage4.setImage(Monopoly.getBoardImage("Tom"));
    labelControl3.setFont(new Font("TimesRoman", 2, 18));
    labelControl2.setFont(new Font("TimesRoman", 0, 18));
      // declare Fonts to be used
    Font aNormalFont = new Font("Dialog", 1, 12);

    MainPanel.setLayout(MainPanelLayout);
    MainPanel.setSize(new Dimension(510, 427));
    MainPanelLayout.setHeight(381);
    MainPanelLayout.setWidth(372);
    transparentImage1.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    transparentImage1.setDrawEdge(false);
    transparentImage1.setTransparent(false);
    transparentImage1.setImage(Monopoly.getBoardImage("Bankrupt"));
    this.addWindowListener(new BankruptDialog_this_windowAdapter(this));

      // Center the window
    Dimension frmSize = aParentWindow.getSize();
    Point loc = aParentWindow.getLocation();
    this.setLocation((frmSize.width - this.WINDOW_WIDTH) / 2 + loc.x,
                     (frmSize.height - this.WINDOW_HEIGHT) / 2 + loc.y);
    this.setResizable(false);
    MainPanel.add(transparentImage1, new XYConstraints(29, 131, 311, 80));
    MainPanel.add(transparentImage4, new XYConstraints(254, 305, 92, 53));
    MainPanel.add(transparentImage2, new XYConstraints(67, 36, 90, 74));
    MainPanel.add(transparentImage3, new XYConstraints(207, 36, 86, 74));
    MainPanel.add(labelControl1, new XYConstraints(11, 238, 351, 26));
    MainPanel.add(labelControl2, new XYConstraints(11, 263, 268, 26));
    MainPanel.add(labelControl3, new XYConstraints(13, 291, 228, 28));
  }

  void this_windowClosing(WindowEvent e)
  {
    dispose();
      // Override and do NOTHING - User must use the OK button
  }

}

class BankruptDialog_this_windowAdapter extends WindowAdapter
{
  BankruptDialog adaptee;

  BankruptDialog_this_windowAdapter(BankruptDialog adaptee)
  {
    this.adaptee = adaptee;
  }

  public void windowClosing(WindowEvent e)
  {
    adaptee.this_windowClosing(e);
  }
}

