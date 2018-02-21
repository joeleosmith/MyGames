package Board;

import com.borland.dx.text.Alignment;
import com.borland.jb.util.Trace;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Hashtable;
import javax.swing.*;
import com.borland.jbcl.control.*;
import com.borland.jbcl.layout.*;
import com.heagy.util.*;
import Monopoly.*;
import Property.*;
import Player.*;
import Card.*;

public class GameBoard extends DecoratedFrame implements MovementListener
{
    // Board variables
  public static final int BOARD_CELL_CNT = 40;
  public static final int TITLE_IMAGE_CNT = 40;
  public static JailCell theJailCell;
  public static GameBoardCell theGotoJailCell;
  public static GameBoardCell[] aBoardCells = new GameBoardCell[BOARD_CELL_CNT];

  public int iDiceCnt;
  public TransparentImage CardImage = new TransparentImage();
  public TransparentImage[] TitleImages = new TransparentImage[TITLE_IMAGE_CNT];
  public String[] TitleImageNames = new String[TITLE_IMAGE_CNT];
  public PlayerInfo aPlayerInfo = new PlayerInfo();

  private int iSpecialFee;  // For handling special rents assoicated with Cards
  private boolean bDiceWereRolled;
  private TransparentImage aGameBoardImage;
  private ImageHandler aImageHandler = new ImageHandler();
  private TransparentImage aHouseImages[] = new TransparentImage[BOARD_CELL_CNT];
  private BorderLayout MainWindowLayout = new BorderLayout();
  private XYLayout MainPanelLayout = new XYLayout();
  private BevelPanel MainPanel = new BevelPanel();
  private BevelPanel[] TitlePanels = new BevelPanel[3];
  private GridLayout[] TitlePanelGridLayouts = new GridLayout[3];
  private Board.CommandPanel commandPanelBean = new Board.CommandPanel();
  private TextControl tFreeParking = new TextControl();

    // Constructor
  public GameBoard()
  {
    try {
      jbInit();
    }
    catch (Exception e) { e.printStackTrace(); }
  }

    // Initialize Board Components
  private void jbInit() throws Exception
  {
    this.setLayout(MainWindowLayout);
    this.setResizable(false);
    this.setSize(new Dimension(960, 748));
    this.setTitle("MONOPOLY");
    MainPanel.setBevelInner(BevelPanel.FLAT);
    MainPanel.setLayout(MainPanelLayout);
    this.add(MainPanel, BorderLayout.WEST);

      // Paint the main Game Board
    aGameBoardImage = new TransparentImage();
    aGameBoardImage.setImage(Monopoly.theBoardLoader.getImage("Monopoly Game Board"));
    aGameBoardImage.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    aGameBoardImage.setDrawEdge(false);

      // Define the listener for the command Panel Bean
    commandPanelBean.addActionListener(new java.awt.event.ActionListener()
      { public void actionPerformed(ActionEvent e) {commandPanelBean_actionPerformed(e);} });
    commandPanelBean.setOpaque(false);

      // Define the listener for mouse clicks on the game board
    aGameBoardImage.addMouseListener(new java.awt.event.MouseAdapter()
      { public void mouseClicked(MouseEvent e) {boardMouseClicked(e);} });

      // Define the window listener to handle closing of the window
    this.addWindowListener(new java.awt.event.WindowAdapter()
      { public void windowClosing(WindowEvent e) { this_windowClosing(e); } });

    buildTitlePanels();
    buildHouseImageObjects();
    buildPropertyGroups();
    buildCells();
    buildProperties();
    buildGroupPropertyRelationships();
    buildTitleListeners();
      // Place Free Parking
    tFreeParking.setFont(new Font("Dialog", 1, 15));
    tFreeParking.setText("");
    tFreeParking.setAlignment(com.borland.dx.text.Alignment.CENTER | com.borland.dx.text.Alignment.MIDDLE);
      // Place Chance/Chest Card
    CardImage.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    CardImage.setDrawEdge(false);   // DO NOT delete - deals w/ quirky drawing bug

    placeHouseImages();
    MainPanel.add(tFreeParking, new XYConstraints(2, 73, 62, -1));
    MainPanel.add(commandPanelBean, new XYConstraints(776, 6, -1, 123));
    MainPanel.add(CardImage, new XYConstraints(415, 111, 200, 113));
    MainPanel.add(TitlePanels[0], new XYConstraints(121, 118, 139, 485));
    MainPanel.add(TitlePanels[1], new XYConstraints(271, 118, 139, 485));
    MainPanel.add(TitlePanels[2], new XYConstraints(421, 278, 149, 323));
    MainPanel.add(aPlayerInfo, new XYConstraints(725, 139, 221, 532));
    placePlayerImages();
      // This 'GameBoardImage' must go LAST in the list otherwise it will hide other controls
    MainPanel.add(aGameBoardImage, new XYConstraints(0, 0, 723, 723));
  }

  private void placePlayerImages()
  {
      // Get Player Icons (TransparentImages) and place on MainPanel
    for (int j=0; j < Monopoly.theNbrOfPlayers; j++)
    {
      if (! Monopoly.thePlayers[j].bBankrupt)
      {
        TransparentImage aPlayerImage = Monopoly.thePlayers[j].getBoardIcon();
//      MainPanel.add(aPlayerImage, new XYConstraints(816, 238, -1, -1));
        MainPanel.add(Monopoly.thePlayers[j].getBoardIcon(), new XYConstraints(816, 238, -1, -1));
      }
    }
  }

  private void placeHouseImages()
  {
    for (int j=1; j < 10; j++)
      MainPanel.add(aHouseImages[j], new XYConstraints
        (aBoardCells[j].aHouseLocation.x, aBoardCells[j].aHouseLocation.y, 60, 20));
    for (int j=11; j < 20; j++)
      MainPanel.add(aHouseImages[j], new XYConstraints
        (aBoardCells[j].aHouseLocation.x, aBoardCells[j].aHouseLocation.y, 20, 60));
    for (int j=21; j < 30; j++)
      MainPanel.add(aHouseImages[j], new XYConstraints
        (aBoardCells[j].aHouseLocation.x, aBoardCells[j].aHouseLocation.y, 60, 20));
    for (int j=31; j < 40; j++)
      MainPanel.add(aHouseImages[j], new XYConstraints
        (aBoardCells[j].aHouseLocation.x, aBoardCells[j].aHouseLocation.y, 20, 60));
  }

  private void buildGroupPropertyRelationships()
  {
    Monopoly.thePropGroups[0].aProperties[0] = this.aBoardCells[1].aProp;
    Monopoly.thePropGroups[0].aProperties[1] = this.aBoardCells[3].aProp;
    Monopoly.thePropGroups[1].aProperties[0] = this.aBoardCells[5].aProp;
    Monopoly.thePropGroups[1].aProperties[1] = this.aBoardCells[15].aProp;
    Monopoly.thePropGroups[1].aProperties[2] = this.aBoardCells[25].aProp;
    Monopoly.thePropGroups[1].aProperties[3] = this.aBoardCells[35].aProp;
    Monopoly.thePropGroups[2].aProperties[0] = this.aBoardCells[6].aProp;
    Monopoly.thePropGroups[2].aProperties[1] = this.aBoardCells[8].aProp;
    Monopoly.thePropGroups[2].aProperties[2] = this.aBoardCells[9].aProp;
    Monopoly.thePropGroups[3].aProperties[0] = this.aBoardCells[11].aProp;
    Monopoly.thePropGroups[3].aProperties[1] = this.aBoardCells[13].aProp;
    Monopoly.thePropGroups[3].aProperties[2] = this.aBoardCells[14].aProp;
    Monopoly.thePropGroups[4].aProperties[0] = this.aBoardCells[12].aProp;
    Monopoly.thePropGroups[4].aProperties[1] = this.aBoardCells[28].aProp;
    Monopoly.thePropGroups[5].aProperties[0] = this.aBoardCells[16].aProp;
    Monopoly.thePropGroups[5].aProperties[1] = this.aBoardCells[18].aProp;
    Monopoly.thePropGroups[5].aProperties[2] = this.aBoardCells[19].aProp;
    Monopoly.thePropGroups[6].aProperties[0] = this.aBoardCells[21].aProp;
    Monopoly.thePropGroups[6].aProperties[1] = this.aBoardCells[23].aProp;
    Monopoly.thePropGroups[6].aProperties[2] = this.aBoardCells[24].aProp;
    Monopoly.thePropGroups[7].aProperties[0] = this.aBoardCells[26].aProp;
    Monopoly.thePropGroups[7].aProperties[1] = this.aBoardCells[27].aProp;
    Monopoly.thePropGroups[7].aProperties[2] = this.aBoardCells[29].aProp;
    Monopoly.thePropGroups[8].aProperties[0] = this.aBoardCells[31].aProp;
    Monopoly.thePropGroups[8].aProperties[1] = this.aBoardCells[32].aProp;
    Monopoly.thePropGroups[8].aProperties[2] = this.aBoardCells[34].aProp;
    Monopoly.thePropGroups[9].aProperties[0] = this.aBoardCells[37].aProp;
    Monopoly.thePropGroups[9].aProperties[1] = this.aBoardCells[39].aProp;
  }

  private void buildTitleListeners()
  {
    TitleImages[0].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(0, e);} });
    TitleImages[1].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(1, e);} });
    TitleImages[2].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(2, e);} });
    TitleImages[3].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(3, e);} });
    TitleImages[4].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(4, e);} });
    TitleImages[5].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(5, e);} });
    TitleImages[6].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(6, e);} });
    TitleImages[7].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(7, e);} });
    TitleImages[8].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(8, e);} });
    TitleImages[9].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(9, e);} });
    TitleImages[10].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(10, e);} });
    TitleImages[11].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(11, e);} });
    TitleImages[12].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(12, e);} });
    TitleImages[13].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(13, e);} });
    TitleImages[14].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(14, e);} });
    TitleImages[15].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(15, e);} });
    TitleImages[16].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(16, e);} });
    TitleImages[17].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(17, e);} });
    TitleImages[18].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(18, e);} });
    TitleImages[19].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(19, e);} });
    TitleImages[20].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(20, e);} });
    TitleImages[21].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(21, e);} });
    TitleImages[22].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(22, e);} });
    TitleImages[23].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(23, e);} });
    TitleImages[24].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(24, e);} });
    TitleImages[25].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(25, e);} });
    TitleImages[26].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(26, e);} });
    TitleImages[27].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(27, e);} });
    TitleImages[28].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(28, e);} });
    TitleImages[29].addMouseListener(new java.awt.event.MouseAdapter()
      {public void mouseClicked(MouseEvent e) {TitleImages_mouseClicked(29, e);} });
  }

  private void buildHouseImageObjects()
  {
    for (int j=0; j < BOARD_CELL_CNT; j++)
    {
      aHouseImages[j] = new TransparentImage();
      aHouseImages[j].setDrawEdge(false);
    }
  }

  private void buildTitlePanels()
  {
      // Setup the Title Panels
    for (int j=0; j < 3; j++)
    {
      TitlePanelGridLayouts[j] = new GridLayout(15,1);
      TitlePanels[j] = new BevelPanel(BevelPanel.FLAT, BevelPanel.FLAT);
      TitlePanels[j].setOpaque(false);
      TitlePanels[j].setLayout(TitlePanelGridLayouts[j]);
    }
    TitlePanelGridLayouts[2].setRows(10);   // Change the last panel to only have 10 items
    for (int j=0; j < 40; j++)
    {
      TitleImages[j] = new TransparentImage();
      TitleImages[j].setDrawEdge(false);
      TitleImages[j].setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    }
    for (int j=0; j < 15; j++)
      TitlePanels[0].add(TitleImages[j], null);
    for (int j=15; j < 30; j++)
      TitlePanels[1].add(TitleImages[j], null);
    for (int j=30; j < 40; j++)
      TitlePanels[2].add(TitleImages[j], null);
  }

  private void buildPropertyGroups()
  {
    for (int j=0; j < Monopoly.GROUP_CNT; j++)
    {
      int iPropertyCnt;
      switch (j)
      {
        case 0:
        case 4:
        case 9: iPropertyCnt = 2;
                break;
        case 1: iPropertyCnt = 4;
                break;
        default: iPropertyCnt = 3;
      }
      Monopoly.thePropGroups[j] = new PropertyGroup(iPropertyCnt);
    }
  }

  private void buildCells()
  {
    aBoardCells[0]  = new CornerCell(new Point(622,623), new Point(715,715));
    aBoardCells[1]  = new SouthCell(566, 623);
    aBoardCells[2]  = new SouthCell(506, 566);
    aBoardCells[3]  = new SouthCell(449, 506);
    aBoardCells[4]  = new SouthCell(392, 449);
    aBoardCells[5]  = new SouthCell(334, 392);
    aBoardCells[6]  = new SouthCell(277, 334);
    aBoardCells[7]  = new SouthCell(218, 277);
    aBoardCells[8]  = new SouthCell(160, 218);
    aBoardCells[9]  = new SouthCell(102, 160);
    aBoardCells[10] = new JailCell(new Point(7,622), new Point(100,715));
    aBoardCells[11] = new WestCell(565, 611);
    aBoardCells[12] = new WestCell(506, 565);
    aBoardCells[13] = new WestCell(448, 506);
    aBoardCells[14] = new WestCell(389, 448);
    aBoardCells[15] = new WestCell(332, 389);
    aBoardCells[16] = new WestCell(272, 332);
    aBoardCells[17] = new WestCell(212, 272);
    aBoardCells[18] = new WestCell(155, 212);
    aBoardCells[19] = new WestCell(100, 155);
    aBoardCells[20] = new CornerCell(new Point(7,5), new Point(102,98));
    aBoardCells[21] = new NorthCell(100, 157);
    aBoardCells[22] = new NorthCell(157, 214);
    aBoardCells[23] = new NorthCell(214, 274);
    aBoardCells[24] = new NorthCell(274, 334);
    aBoardCells[25] = new NorthCell(334, 389);
    aBoardCells[26] = new NorthCell(389, 448);
    aBoardCells[27] = new NorthCell(448, 506);
    aBoardCells[28] = new NorthCell(506, 565);
    aBoardCells[29] = new NorthCell(565, 626);
    aBoardCells[30] = new CornerCell(new Point(622,3), new Point(715,98));
    aBoardCells[31] = new EastCell(98, 155);
    aBoardCells[32] = new EastCell(155, 212);
    aBoardCells[33] = new EastCell(212, 272);
    aBoardCells[34] = new EastCell(272, 332);
    aBoardCells[35] = new EastCell(332, 389);
    aBoardCells[36] = new EastCell(389, 448);
    aBoardCells[37] = new EastCell(448, 506);
    aBoardCells[38] = new EastCell(506, 565);
    aBoardCells[39] = new EastCell(565, 622);

      // Build links to cells
    for (int j=0; j < 39; j++)
      this.aBoardCells[j].aNextCell = this.aBoardCells[j+1];
    this.aBoardCells[39].aNextCell = this.aBoardCells[0];
    for (int j=1; j < 40; j++)
      this.aBoardCells[j].aPrevCell = this.aBoardCells[j-1];
    this.aBoardCells[0].aPrevCell = this.aBoardCells[39];
  }

  private void buildProperties()
  {
      // Build Board Cell/Properties
    String[] sNames = Property.PROP_NAMES;
    aBoardCells[0].aProp = new Property(Property.GO, new Rent.Go(200));
    aBoardCells[1].aProp = new Property(sNames[0], Property.NORMAL,
        60,true,50,new Rent.Landlord(2,10,30,90,160,250),Monopoly.thePropGroups[0],
        aHouseImages[1], GameBoardCell.SOUTH);
    aBoardCells[2].aProp = new Property(Property.COMMUNITY_CHEST, new Rent.CommunityChest());
    aBoardCells[3].aProp = new Property(sNames[1], Property.NORMAL,
        60,true,50,new Rent.Landlord(4,20,60,180,320,450),Monopoly.thePropGroups[0],
        aHouseImages[3], GameBoardCell.SOUTH);
    aBoardCells[4].aProp = new Property(Property.TAX, new Rent.IncomeTax());
    aBoardCells[5].aProp = new Property(sNames[2], Property.RAILROAD,
        200,false,0,new Rent.Railroad(),Monopoly.thePropGroups[1],
        aHouseImages[5], GameBoardCell.SOUTH);
    aBoardCells[6].aProp = new Property(sNames[3], Property.NORMAL,
        100,true,50,new Rent.Landlord(6,30,90,270,400,550),Monopoly.thePropGroups[2],
        aHouseImages[6], GameBoardCell.SOUTH);
    aBoardCells[7].aProp = new Property(Property.CHANCE, new Rent.Chance());
    aBoardCells[8].aProp = new Property(sNames[4], Property.NORMAL,
        100,true,50,new Rent.Landlord(6,30,90,270,400,550),Monopoly.thePropGroups[2],
        aHouseImages[8], GameBoardCell.SOUTH);
    aBoardCells[9].aProp = new Property(sNames[5], Property.NORMAL,
        120,true,50,new Rent.Landlord(8,40,100,300,450,600),Monopoly.thePropGroups[2],
        aHouseImages[9], GameBoardCell.SOUTH);
    aBoardCells[10].aProp = new Property(Property.JAIL, new Rent.None());
    theJailCell = (JailCell)aBoardCells[10];
    aBoardCells[11].aProp = new Property(sNames[6], Property.NORMAL,
        140,true,100,new Rent.Landlord(10,50,150,450,625,750),Monopoly.thePropGroups[3],
        aHouseImages[11], GameBoardCell.WEST);
    aBoardCells[12].aProp = new Property(sNames[7], Property.UTILITY,
        150,false,0,new Rent.Utility(),Monopoly.thePropGroups[4],
        aHouseImages[12], GameBoardCell.WEST);
    aBoardCells[13].aProp = new Property(sNames[8], Property.NORMAL,
        140,true,100,new Rent.Landlord(10,50,150,450,625,750),Monopoly.thePropGroups[3],
        aHouseImages[13], GameBoardCell.WEST);
    aBoardCells[14].aProp = new Property(sNames[9], Property.NORMAL,
        160,true,100,new Rent.Landlord(12,60,180,500,700,900),Monopoly.thePropGroups[3],
        aHouseImages[14], GameBoardCell.WEST);
    aBoardCells[15].aProp = new Property(sNames[10], Property.RAILROAD,
        200,false,0,new Rent.Railroad(),Monopoly.thePropGroups[1],
        aHouseImages[15], GameBoardCell.WEST);
    aBoardCells[16].aProp = new Property(sNames[11], Property.NORMAL,
        180,true,100,new Rent.Landlord(14,70,200,550,750,950),Monopoly.thePropGroups[5],
        aHouseImages[16], GameBoardCell.WEST);
    aBoardCells[17].aProp = new Property(Property.COMMUNITY_CHEST, new Rent.CommunityChest());
    aBoardCells[18].aProp = new Property(sNames[12], Property.NORMAL,
        180,true,100,new Rent.Landlord(14,70,200,550,750,950),Monopoly.thePropGroups[5],
        aHouseImages[18], GameBoardCell.WEST);
    aBoardCells[19].aProp = new Property(sNames[13], Property.NORMAL,
        200,true,100,new Rent.Landlord(16,80,220,600,800,1000),Monopoly.thePropGroups[5],
        aHouseImages[19], GameBoardCell.WEST);
    aBoardCells[20].aProp = new Property(Property.PARKING, new Rent.FreeParking());
    aBoardCells[21].aProp = new Property(sNames[14], Property.NORMAL,
        220,true,150,new Rent.Landlord(18,90,250,700,875,1050),Monopoly.thePropGroups[6],
        aHouseImages[21], GameBoardCell.NORTH);
    aBoardCells[22].aProp = new Property(Property.CHANCE, new Rent.Chance());
    aBoardCells[23].aProp = new Property(sNames[15], Property.NORMAL,
        220,true,150,new Rent.Landlord(18,90,250,700,875,1050),Monopoly.thePropGroups[6],
        aHouseImages[23], GameBoardCell.NORTH);
    aBoardCells[24].aProp = new Property(sNames[16], Property.NORMAL,
        240,true,150,new Rent.Landlord(20,100,300,750,925,1100),Monopoly.thePropGroups[6],
        aHouseImages[24], GameBoardCell.NORTH);
    aBoardCells[25].aProp = new Property(sNames[17], Property.RAILROAD,
        200,false,0,new Rent.Railroad(),Monopoly.thePropGroups[1],
        aHouseImages[25], GameBoardCell.NORTH);
    aBoardCells[26].aProp = new Property(sNames[18], Property.NORMAL,
        260,true,150,new Rent.Landlord(22,110,330,800,975,1150),Monopoly.thePropGroups[7],
        aHouseImages[26], GameBoardCell.NORTH);
    aBoardCells[27].aProp = new Property(sNames[19], Property.NORMAL,
        260,true,150,new Rent.Landlord(22,110,330,800,975,1150),Monopoly.thePropGroups[7],
        aHouseImages[27], GameBoardCell.NORTH);
    aBoardCells[28].aProp = new Property(sNames[20], Property.UTILITY,
        150,false,0,new Rent.Utility(),Monopoly.thePropGroups[4],
        aHouseImages[28], GameBoardCell.NORTH);
    aBoardCells[29].aProp = new Property(sNames[21], Property.NORMAL,
        280,true,150,new Rent.Landlord(24,120,360,850,1025,1200),Monopoly.thePropGroups[7],
        aHouseImages[29], GameBoardCell.NORTH);
    aBoardCells[30].aProp = new Property(Property.GOTO_JAIL, new Rent.None());
    theGotoJailCell = aBoardCells[30];
    aBoardCells[31].aProp = new Property(sNames[22], Property.NORMAL,
        300,true,200,new Rent.Landlord(26,130,390,900,1100,1275),Monopoly.thePropGroups[8],
        aHouseImages[31], GameBoardCell.EAST);
    aBoardCells[32].aProp = new Property(sNames[23], Property.NORMAL,
        300,true,200,new Rent.Landlord(26,130,390,900,1100,1275),Monopoly.thePropGroups[8],
        aHouseImages[32], GameBoardCell.EAST);
    aBoardCells[33].aProp = new Property(Property.COMMUNITY_CHEST, new Rent.CommunityChest());
    aBoardCells[34].aProp = new Property(sNames[24], Property.NORMAL,
        320,true,200,new Rent.Landlord(28,150,450,1000,1200,1400),Monopoly.thePropGroups[8],
        aHouseImages[34], GameBoardCell.EAST);
    aBoardCells[35].aProp = new Property(sNames[25], Property.RAILROAD,
        200,false,0,new Rent.Railroad(),Monopoly.thePropGroups[1],
        aHouseImages[35], GameBoardCell.EAST);
    aBoardCells[36].aProp = new Property(Property.CHANCE, new Rent.Chance());
    aBoardCells[37].aProp = new Property(sNames[26], Property.NORMAL,
        350,true,200,new Rent.Landlord(35,175,500,1100,1300,1500),Monopoly.thePropGroups[9],
        aHouseImages[37], GameBoardCell.EAST);
    aBoardCells[38].aProp = new Property(Property.TAX, new Rent.LuxuryTax());
    aBoardCells[39].aProp = new Property(sNames[27], Property.NORMAL,
        400,true,200,new Rent.Landlord(50,200,600,1400,1700,2000),Monopoly.thePropGroups[9],
        aHouseImages[39], GameBoardCell.EAST);
  }

  public void showCard(String sCardName)
  {
    try
    {
      this.CardImage.setImage(Monopoly.getCardImage(sCardName));
      this.CardImage.setVisible(true);
    }
    catch (Exception e) { e.printStackTrace(); }
  }

  public void showAboutBox()
  {
    AboutBox aWindow = new AboutBox(this);
    aWindow.show();
    aWindow = null;
  }

  private void boardMouseClicked(MouseEvent e)
  {
    GameBoardCell aStartingCell = aBoardCells[0];
    GameBoardCell aCell = aStartingCell;
    int x = e.getX();
    int y = e.getY();

      // Search through the cells until the correct coordinates are found
    while ( (aCell != null) &&
           !( (x >= aCell.aTopLeft.x) && (x <= aCell.aBottomRight.x) &&
              (y >= aCell.aTopLeft.y) && (y <= aCell.aBottomRight.y) ) )
    {
      if (aCell.aNextCell == aStartingCell)
        aCell = null;
      else
        aCell = aCell.aNextCell;
    }

      // Show the property
    if (aCell != null)
    {
      if ( (aCell.aProp.aOwner == Monopoly.theCurrentPlayer) ||
           ((aCell.isPlayerOn(Monopoly.theCurrentPlayer)) & (bDiceWereRolled)
                & (aCell.aProp.aOwner == null)) )
      {
        aCell.aProp.show(Monopoly.theCurrentPlayer, true);
          // update any changes that may have occurred to the current player
        aPlayerInfo.update(Monopoly.theCurrentPlayer);
      }
      else
      {
        aCell.aProp.show(null, true);
        if (aCell.aProp.iPropertyType == Property.GO)
          showAboutBox();
      }
    }

    else    // show the about box
      showAboutBox();
  }

  public void resetTitleImages()
  {
    for (int j = 0; j < TITLE_IMAGE_CNT; j++)
      this.TitleImages[j].setVisible(false);
  }

  public void nextPlayer()
  {
    if (Monopoly.theCurrentPlayer != null)
      Monopoly.theCurrentPlayer.deactivateIcon();
    Player aPlayer = Monopoly.nextPlayer();
    aPlayer.activateIcon();
    aPlayer.showTitles(this);
    aPlayerInfo.update(aPlayer);
    commandPanelBean.initForPlayer(aPlayer);
    this.iSpecialFee = Card.NONE;
  }

  public void setSpecialFee(int iSpecialFee)
  {
    this.iSpecialFee = iSpecialFee;
  }

  public void addImageToBoard(TransparentImage aImage, Point aPoint, int iWidth, int iHeight)
  {
    this.MainPanel.add(aImage, new XYConstraints(aPoint.x, aPoint.y, iWidth, iHeight));
  }

  void commandPanelBean_actionPerformed(ActionEvent e)
  {
    Player aPlayer = Monopoly.theCurrentPlayer;
    Card.hide();
    if (e.getActionCommand().equals(CommandPanel.EVENT_ROLL_DICE))
    {
        // disable all other play until we get instructions through the "imageFinishedMoving" method
      commandPanelBean.disableAll();
      bDiceWereRolled = true;

      iDiceCnt = commandPanelBean.getDie1Value() + commandPanelBean.getDie2Value();
      boolean bDoubles = (commandPanelBean.getDie1Value() == commandPanelBean.getDie2Value());
        // Check for doubles
      if (bDoubles)
      {
          // if player is in jail then get him out but ignore the double
        if (aPlayer.bInJail)
        {
          aPlayer.getOutOfJail();
          bDoubles = false;
          aPlayer.iDoublesRolled = 0;
        }

        if (aPlayer.iDoublesRolled == 2)  // send Player directly to JAIL
        {
          aPlayer.addMovementListener(this);  // notified via imageFinishedMoving
          aPlayer.goToJail(aPlayer.aCurrentCell);
        }
        else if (bDoubles)
          aPlayer.iDoublesRolled++;
      }
      else  // doubles not thrown
      {
        if (aPlayer.bInJail)
        {
          aPlayer.iDaysLeftInJail--;
            // if the players time in Jail is up and he didn't roll doubles, then
            // he must pay the fine.
          if (aPlayer.iDaysLeftInJail == 0)
          {
            aPlayer.payFee(JailCell.FEE, Monopoly.theBanker, Player.REASON_FEE);
            tFreeParking.setText(Formatting.money(Monopoly.theBanker.iCash));
            aPlayerInfo.update(Monopoly.theCurrentPlayer);
            aPlayer.getOutOfJail();
          }
          commandPanelBean.enableTrade();
          commandPanelBean.enableNextPlayer();
        }
        aPlayer.iDoublesRolled = 0;
      }

      if (! aPlayer.bInJail)
      {
          // find destination cell
        GameBoardCell aToCell = aPlayer.aCurrentCell;
        for (int j=1; j <= iDiceCnt; j++)
          aToCell = aToCell.aNextCell;
          // relocate player to destination cell.  imageFinishedMoving is called when ready to continue
        aPlayer.addMovementListener(this);  // notified via imageFinishedMoving
        aPlayer.moveIcon(aPlayer.aCurrentCell, aToCell, false, true);
      }
    }

    else if (e.getActionCommand().equals(CommandPanel.EVENT_PAY_JAIL_FEE))
    {
      if (aPlayer.bHasJailCardChest)
        aPlayer.bHasJailCardChest = false;
      else if (aPlayer.bHasJailCardChance)
        aPlayer.bHasJailCardChance = false;
      else    // Pay fee
      {
        aPlayer.payFee(JailCell.FEE, Monopoly.theBanker, Player.REASON_FEE);
        tFreeParking.setText(Formatting.money(Monopoly.theBanker.iCash));
      }
      if (! aPlayer.bBankrupt)
        aPlayer.getOutOfJail();
      aPlayerInfo.update(Monopoly.theCurrentPlayer);
      commandPanelBean.disableJailFee();
     }

    else if (e.getActionCommand().equals(CommandPanel.EVENT_NEXT_PLAYER))
    {
      bDiceWereRolled = false;
      commandPanelBean.disableNextPlayer();
      commandPanelBean.enableRollDice();
      commandPanelBean.enableTrade();
      this.nextPlayer();
    }

    else if (e.getActionCommand().equals(CommandPanel.EVENT_TRADE))
    {
      Monopoly.theCurrentPlayer.openTrade(this);
      aPlayerInfo.update(Monopoly.theCurrentPlayer);
    }

    else if (e.getActionCommand().equals(CommandPanel.EVENT_AUCTION))
    {
      Property aProperty = Monopoly.theCurrentPlayer.aCurrentCell.aProp;
      if (aProperty.sName != null)
        Monopoly.performAuction(aProperty);
        // if the property now belongs to someone, disable the auction button
      if (aProperty.aOwner != null)
        this.commandPanelBean.disableAuction();
    }

    else if (e.getActionCommand().equals(CommandPanel.EVENT_HELP))
    {
    }
  }


// *******************************************************************
// Methods for Movement Listener Interface (Respond to the event)
// *******************************************************************
  public void imageStartedMoving()
  {
  }

  public void imageFinishedMoving()
  {
    Player aPlayer = Monopoly.theCurrentPlayer;
    aPlayer.removeMovementListener(this);   // We're finished listening
    GameBoardCell aToCell = aPlayer.aCurrentCell;
    commandPanelBean.enableTrade();

    if (aToCell.aProp != null)
      aToCell.aProp.iLandingCount++;

      // If the cell has a property, then show it
    if (aToCell.aProp != null)
      aToCell.aProp.show(aPlayer, iSpecialFee, Monopoly.ALWAYS_SHOW_PROPERTY);

    aPlayerInfo.update(Monopoly.theCurrentPlayer);

      // if doubles were rolled, then make player go again (unless he is bankrupt)
    if ( (aPlayer.iDoublesRolled > 0) && (! aPlayer.bBankrupt) )
      commandPanelBean.enableRollDice();
    else
      commandPanelBean.enableNextPlayer();

      // Enable the auction button if the property is eligible
    if (aToCell.aProp.isOkToAuction())
      commandPanelBean.enableAuction();
    else
      commandPanelBean.disableAuction();

      // Check if the player is now in jail
    if ((aPlayer.aCurrentCell == theJailCell) && (aPlayer.iDaysLeftInJail > 0))
    {
      aPlayerInfo.update(Monopoly.theCurrentPlayer);
      commandPanelBean.disableRollDice();
      commandPanelBean.enableNextPlayer();
      commandPanelBean.disableAuction();
    }

      // Override the commandPanel if a player just went Bankrupt
    if (Monopoly.theCurrentPlayer.bBankrupt)
      commandPanelBean_actionPerformed(new ActionEvent("", 0, CommandPanel.EVENT_NEXT_PLAYER));

    if (Monopoly.theBanker.iCash > 0)
      tFreeParking.setText(Formatting.money(Monopoly.theBanker.iCash));
    else
      tFreeParking.setText("");

  }

  public void imageLandedOnStart()
  {
  }
// *******************************************************************

  private boolean namesEqual(String sName1, String sName2)
  {
    if (sName1 == null)
      sName1 = "";
    if (sName2 == null)
      sName2 = "";
    return (sName1.compareTo(sName2) == 0);
  }

  void TitleImages_mouseClicked(int iImageNbr, MouseEvent e)
  {
      // Search for a  matching name
    int j=0;
    while ((! namesEqual(TitleImageNames[iImageNbr], aBoardCells[j].aProp.sName))
              && (j < BOARD_CELL_CNT))
      j++;

    if (j <= BOARD_CELL_CNT)   // if found
      aBoardCells[j].aProp.show(Monopoly.theCurrentPlayer, true);
      // update any changes that may have occurred to the current player
    aPlayerInfo.update(Monopoly.theCurrentPlayer);
  }

  void this_windowClosing(WindowEvent e)
  {
    MessageDialog aMsgBox = new MessageDialog(this, "Monopoly- EXIT", "Would you like to save the current game?",
                                Message.YES_NO);
    aMsgBox.show();
    if (aMsgBox.getResult() == Message.YES)
      Monopoly.saveGame(commandPanelBean.isNextPlayerEnabled());
  }

}

