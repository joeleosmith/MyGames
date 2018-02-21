package Monopoly;

import java.awt.*;
import java.awt.event.*;
import com.borland.jbcl.layout.*;
import com.borland.jbcl.control.*;
import javax.swing.*;
import Player.*;

public class SetupDialog extends Dialog
{
  public boolean bOK = false;
  public boolean bCanceled = false;

  private static final String ICON_IMAGE_PATH = "/Player/Images/";
  private static final String ICON_IMAGE_EXT = ".gif";
  private static final String[] PLAYER_ICONS = new String[] {"Player1", "Player2", "Player3", "Player4", "Player5", "Player6"};

  Frame aParentWindow;
  Panel MainPanel = new Panel();
  XYLayout MainPanelLayout = new XYLayout();
  Button cbOK = new Button();
  Button cbCancel = new Button();
  LabelControl[] lcPlayer = new LabelControl[Monopoly.MAX_PLAYERS];
  LabelControl[] lcImage = new LabelControl[Monopoly.MAX_PLAYERS];
  TextFieldControl[] tbPlayer = new TextFieldControl[Monopoly.MAX_PLAYERS];
  ChoiceControl[] ddlbImage = new ChoiceControl[Monopoly.MAX_PLAYERS];
  String[] sDefaultNames;

  public SetupDialog(Frame frame, String title, String[] defaultNames)
  {
    super(frame, title, true);
    aParentWindow = frame;
    sDefaultNames = defaultNames;
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    add(MainPanel);
    pack();
  }

  private void jbInit() throws Exception
  {
    final int iWidth = 464;
    final int iHeight = 302;
    cbCancel.addActionListener(new SetupDialog_cbCancel_actionAdapter(this));
    this.addWindowListener(new SetupDialog_this_windowAdapter(this));
    MainPanel.setLayout(MainPanelLayout);
    MainPanel.add(cbOK, new XYConstraints(154, 259, 74, 25));
    MainPanel.add(cbCancel, new XYConstraints(250, 259, 77, 25));

    Font aLabelFont = new Font("Dialog", 1, 12);
    MainPanelLayout.setWidth(iWidth);
    MainPanelLayout.setHeight(iHeight);
    cbOK.setLabel("OK");
    cbOK.addActionListener(new SetupDialog_cbOK_actionAdapter(this));
    cbCancel.setLabel("Cancel");

    int yPos = 39;
    for (int j=0; j < Monopoly.MAX_PLAYERS; j++)
    {
      lcPlayer[j] = new LabelControl();
      lcPlayer[j].setAlignment(Label.RIGHT);
      lcPlayer[j].setFont(aLabelFont);
      lcPlayer[j].setText("Player " + (j + 1) + ":");

      lcImage[j] = new LabelControl();
      lcImage[j].setAlignment(Label.RIGHT);
      lcImage[j].setFont(aLabelFont);
      lcImage[j].setText("Image " + (j + 1) + ":");

      tbPlayer[j] = new TextFieldControl();

      ddlbImage[j] = new ChoiceControl();
      ddlbImage[j].setItems(PLAYER_ICONS);
      ddlbImage[j].select(j);

      MainPanel.add(lcPlayer[j], new XYConstraints(34, yPos, -1, -1));
      MainPanel.add(lcImage[j], new XYConstraints(224, yPos, -1, -1));
      MainPanel.add(tbPlayer[j], new XYConstraints(101, yPos, 119, 20));
      MainPanel.add(ddlbImage[j], new XYConstraints(292, yPos, -1, -1));
      yPos += 30;
    }

    if (sDefaultNames != null)
    {
      for (int j=0; j < sDefaultNames.length; j++)
        tbPlayer[j].setText(sDefaultNames[j]);
    }

      // Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Point loc = aParentWindow.getLocation();
    this.setLocation((screenSize.width / 2) - (iWidth / 2),
                     (screenSize.height / 2) - (iHeight / 2));
    this.setResizable(false);
  }

  // OK
  void cbOK_actionPerformed(ActionEvent e)
  {
    bOK = true;
    dispose();
  }

  // Cancel
  void cbCancel_actionPerformed(ActionEvent e)
  {
    bCanceled = true;
    dispose();
  }

  void this_windowClosing(WindowEvent e)
  {
    dispose();
  }

  public int getPlayerCount()
  {
    int iCnt = 0;
    while ((iCnt < Monopoly.MAX_PLAYERS) && (tbPlayer[iCnt].getText().length() > 0))
      iCnt++;
    return iCnt;
  }

  public Player[] getPlayers()
  {
    int iCnt = this.getPlayerCount();
    Player[] aPlayerList = new Player[iCnt];

    try
    {
      for (int j = 0; j < iCnt; j++)
      {
        String sIconName = ddlbImage[j].getSelectedItem();
        aPlayerList[j] = new Player(tbPlayer[j].getText(), Monopoly.START_MONEY, sIconName);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return aPlayerList;
  }
}

class SetupDialog_cbOK_actionAdapter implements ActionListener{
  SetupDialog adaptee;

  SetupDialog_cbOK_actionAdapter(SetupDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.cbOK_actionPerformed(e);
  }
}

class SetupDialog_cbCancel_actionAdapter implements ActionListener{
  SetupDialog adaptee;

  SetupDialog_cbCancel_actionAdapter(SetupDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.cbCancel_actionPerformed(e);
  }
}

class SetupDialog_this_windowAdapter extends WindowAdapter {
  SetupDialog adaptee;

  SetupDialog_this_windowAdapter(SetupDialog adaptee) {
    this.adaptee = adaptee;
  }

  public void windowClosing(WindowEvent e) {
    adaptee.this_windowClosing(e);
  }
}