package Monopoly;

import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;

public class AboutBox extends Dialog
{
  public static final String VERSION = "Version 1.4";

  Panel panel1 = new Panel();
  XYLayout xYLayout1 = new XYLayout();
  Label label2 = new Label();
  Label label3 = new Label();
  Button cbOK = new Button();
  TransparentImage transparentImage1 = new TransparentImage();
  Frame aParentWindow;
  Label label4 = new Label();

  public AboutBox(Frame frame)
  {
    super(frame, "About Monopoly", true);
    aParentWindow = frame;
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    add(panel1);
    pack();
  }

  private void jbInit() throws Exception
  {
    int iWidth = 360;
    int iHeight = 230;
    xYLayout1.setWidth(360);
    xYLayout1.setHeight(240);
    label2.setFont(new Font("Dialog", 1, 16));
    label2.setText(VERSION);
    label3.setAlignment(1);
    label3.setText("Copyright (c) 1999 Thomas R. Heagy Jr.");
    cbOK.setActionCommand("");
    transparentImage1.setDrawEdge(false);
    transparentImage1.setImage(Monopoly.getBoardImage("Monopoly Logo"));
    label4.setAlignment(1);
    label4.setText("Monopoly is a Trademark (TM) of Parker Brothers Inc.");
    cbOK.setLabel("OK");
    cbOK.addActionListener(new AboutBox_cbOK_actionAdapter(this));
    this.addWindowListener(new AboutBox_this_windowAdapter(this));
    panel1.setLayout(xYLayout1);
    panel1.add(label2, new XYConstraints(133, 117, 101, 21));
    panel1.add(label3, new XYConstraints(3, 143, 353, 21));
    panel1.add(cbOK, new XYConstraints(133, 195, 98, 32));
    panel1.add(transparentImage1, new XYConstraints(12, 11, 330, 89));
    panel1.add(label4, new XYConstraints(13, 167, 337, 21));

      // Center the window
    Dimension frmSize = aParentWindow.getSize();
    Point loc = aParentWindow.getLocation();
    this.setLocation((frmSize.width - iWidth) / 2 + loc.x, (frmSize.height - iHeight) / 2 + loc.y);
    this.setResizable(false);
  }

    //Close the dialog
  void cbOK_actionPerformed(ActionEvent e) {
     dispose();
  }

  void this_windowClosing(WindowEvent e) {
    dispose();
  }
}

class AboutBox_cbOK_actionAdapter implements ActionListener{
  AboutBox adaptee;

  AboutBox_cbOK_actionAdapter(AboutBox adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.cbOK_actionPerformed(e);
  }
}

class AboutBox_this_windowAdapter extends WindowAdapter {
  AboutBox adaptee;

  AboutBox_this_windowAdapter(AboutBox adaptee) {
    this.adaptee = adaptee;
  }

  public void windowClosing(WindowEvent e) {
    adaptee.this_windowClosing(e);
  }
}
