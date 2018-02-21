package Monopoly;

import javax.swing.UIManager;
import com.borland.jbcl.control.*;
import java.awt.*;
import java.util.Random;
import Player.*;
import Property.*;
import Card.*;
import Board.*;
import com.heagy.util.*;
import com.heagy.gui.SplashWindow;

public class Monopoly
{
  private Random randomizer = new Random();
    // Game Constants
  public static final int MAX_PLAYERS = 6;
  public static final int START_MONEY = 1250;
  public static final int PAY_AMOUNT = 200;
  public static final int GROUP_CNT = 10;
  public static final int CARD_CHANCE_CNT = 16;
  public static final int CARD_CHEST_CNT = 16;
  public static final int MAX_OWNABLE_PROPERTIES = 28;
  public static final boolean ALWAYS_SHOW_PROPERTY = false;
  public static final String SAVED_GAME_FILE_NAME = "Monopoly.game";

    // Game Variables (class properties)
  public static int             theNbrOfPlayers;
  public static Player          theBanker;
  public static Player          theCurrentPlayer;
  public static Player[]        thePlayers = new Player[MAX_PLAYERS];
  public static PropertyGroup[] thePropGroups = new PropertyGroup[GROUP_CNT];
  private static int            theNextChanceCardNbr;
  private static Card[]         theChanceCards = new Card[CARD_CHANCE_CNT];
  private static int            theNextChestCardNbr;
  private static Card[]         theChestCards = new Card[CARD_CHEST_CNT];
  public static Board.GameBoard theGameBoard;

    // Image Loaders
  public static BoardImageLoader theBoardLoader = new BoardImageLoader();
  public static PropHdrImageLoader thePropHdrLoader = new PropHdrImageLoader();
  public static PropCardImageLoader thePropCardLoader = new PropCardImageLoader();
  public static HouseImageLoader theHouseLoader = new HouseImageLoader();
  public static CardImageLoader theCardLoader = new CardImageLoader();
  public static PlayerImageLoader thePlayerLoader = new PlayerImageLoader();

  private static int[] iStartingBoardPositions = new int[MAX_PLAYERS];
  private static String[] sDefaultNames = new String[MAX_PLAYERS];

  public Monopoly()
  {
//    SplashWindow aSplasher = new SplashWindow(new Frame());
    SplashWindow aSplasher = new SplashWindow(new Frame(), "/Board/Splash Background.jpg",
                                              new Dimension(411, 246), 1000);
    try {
      theBoardLoader.loadImages();
      thePropHdrLoader.loadImages();
      thePropCardLoader.loadImages();
      theHouseLoader.loadImages();
      theCardLoader.loadImages();
      thePlayerLoader.loadImages();
        // Define a Player object for Free Parking
      theBanker = new Player("FreeParking", 0, "");
    }
    catch (Exception e) { e.printStackTrace(); }
    aSplasher.setVisible(false);

      // Ask about restoring previous game
    boolean bRestoreGame = false;
    if (Files.fileExists(SAVED_GAME_FILE_NAME))
    {
      MessageDialog aMsgBox = new MessageDialog(new Frame(), "Monopoly", "Would you like to restore the previous game?", Message.YES_NO_CANCEL);
      aMsgBox.show();
      if (aMsgBox.getResult() == Message.CANCEL)
        System.exit(0);
      else
        bRestoreGame = (aMsgBox.getResult() == Message.YES);
    }

    aSplasher.setVisible(true);
    if (bRestoreGame)  // restore values from the game file
    {
      if (! restoreGameFromFile())
        System.exit(-1);
    }
    else  // Open the Game Setup Window
    {
      if (! setupTheGame(sDefaultNames))
        System.exit(0);
    }

      // Game board has been created.  Define remaining game Elements
    createGameCards();

      //*** PLAYERS HAVE NOW BEEN CREATED (but not assigned to the board)

      //Validate frames that have preset sizes
    theGameBoard.validate();
      //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = theGameBoard.getSize();
    if (frameSize.height > screenSize.height)
      frameSize.height = screenSize.height;
    if (frameSize.width > screenSize.width)
      frameSize.width = screenSize.width;
    theGameBoard.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    aSplasher.setVisible(false);
    aSplasher.dispose();
    theGameBoard.setVisible(true);

    if (bRestoreGame)
    {
        // move players to their respective last positions
      for (int j = 0; j < theNbrOfPlayers; j++)
      {
          // Assign Players CurrentCell
        thePlayers[j].aCurrentCell = theGameBoard.aBoardCells[iStartingBoardPositions[j]];
        if (thePlayers[j].bInJail)
          thePlayers[j].positionInJailCell();
        else
          thePlayers[j].positionInCell(thePlayers[j].aCurrentCell);
      }
        // Show any houses/hotels that may exist on the properties
      for (int j = 0; j < GameBoard.BOARD_CELL_CNT; j++)
      {
        GameBoardCell aCell = theGameBoard.aBoardCells[j];
        aCell.aProp.showHouses();
      }
    }
    else
    {
        // Move all players to the first board cell
      for (int j = 0; j < theNbrOfPlayers; j++)
        thePlayers[j].moveIcon(null, theGameBoard.aBoardCells[0], true, true);
    }

      // Select the first player and show his information on the board
    theGameBoard.nextPlayer();
  }


//**************** PUBLIC METHODS ******************

  public static Image getBoardImage(String sImageName)
  {
    return theBoardLoader.getImage(sImageName);
  }

  public static Image getPropHdrImage(String sPropertyName)
  {
    return thePropHdrLoader.getImage(sPropertyName);
  }

  public static Image getPropCardImage(String sPropertyName)
  {
    return thePropCardLoader.getImage(sPropertyName);
  }

  public static Image getHouseImage(String sHouseName)
  {
    return theHouseLoader.getImage(sHouseName);
  }

  public static Image getCardImage(String sCardName)
  {
    return theCardLoader.getImage(sCardName);
  }

  public static Image getPlayerImage(String sIconName, boolean isAnimated)
  {
    if (isAnimated)
      return thePlayerLoader.getImage(sIconName + "_anim");
    else
      return thePlayerLoader.getImage(sIconName);
  }

  public static void performAuction(Property aPropertyToAuction)
  {
    AuctionDialog aWindow = new AuctionDialog(theGameBoard, "Property Auction", aPropertyToAuction);
    aWindow.show();
    if (aWindow.bOK)
    {
      Player aWinningBidder = aWindow.getWinningBidder();
      if (aWinningBidder != null) // we have a winner - let them buy it
      {
        aWinningBidder.buyProperty(aPropertyToAuction, aWindow.getWinningBid());
          // if the winning bidder is the current player, then update their stats
        if (aWinningBidder == Monopoly.theCurrentPlayer)
          theGameBoard.aPlayerInfo.update(Monopoly.theCurrentPlayer);
      }
    }
    aWindow = null;
  }

  public static Card getNextChestCard()
  {
    Card aCard = theChestCards[theNextChestCardNbr];
    if (theNextChestCardNbr == (CARD_CHEST_CNT - 1))
      theNextChestCardNbr = 0;
    else
      theNextChestCardNbr++;
    return aCard;
  }

  public static Card getNextChanceCard()
  {
    Card aCard = theChanceCards[theNextChanceCardNbr];
    if (theNextChanceCardNbr == (CARD_CHANCE_CNT - 1))
      theNextChanceCardNbr = 0;
    else
      theNextChanceCardNbr++;
    return aCard;
  }

//**************** PRIVATE METHODS ******************

  private static boolean setupTheGame(String[] defaultNames)
  {
    SetupDialog aWindow = new SetupDialog(new Frame(), "Player Setup", defaultNames);
    aWindow.show();
    theNbrOfPlayers = aWindow.getPlayerCount();
    thePlayers = aWindow.getPlayers();
    boolean bOK = aWindow.bOK;
    aWindow = null;
    if (bOK)
      theGameBoard = new GameBoard();   // Create Game Board
    return bOK;
  }

  private int randomCard(int iDeckSize)
  {
    int cardNbr = randomizer.nextInt();
    if (cardNbr < 0)
      cardNbr = 0 - cardNbr;
    return cardNbr % iDeckSize;
  }

  private void shuffleDeck(Card[] aDeck, int iDeckSize)
  {
      //repeat for as many cards are in the deck
    for (int cardNbr=0; cardNbr < iDeckSize; cardNbr++)
    {
        // Randomly exchange 2 cards
      int j = randomCard(iDeckSize);
      int k = randomCard(iDeckSize);
      Card tempCard = aDeck[j];
      aDeck[j] = aDeck[k];
      aDeck[k] = tempCard;
    }
  }

  private void createGameCards()
  {
      // Chance Cards
    theChanceCards[0] = new OutOfJail("Chance00");
    theChanceCards[1] = new Card("Chance01", -50, Card.PLAYERS);
    theChanceCards[2] = new Card("Chance02", -15, Card.BANK);
    theChanceCards[3] = new Card("Chance03", 50, Card.BANK);
    theChanceCards[4] = new Card("Chance04", 150, Card.BANK);
    theChanceCards[5] = new Movement("Chance12", true,
                                Movement.RAILROAD, Card.DOUBLE_RENT);
    theChanceCards[6] = new Movement("Chance13", true,
                                Movement.RAILROAD, Card.DOUBLE_RENT);
    theChanceCards[7] = new Movement("Chance14", true, Movement.UTILITY);
    theChanceCards[8] = new Movement("Chance11", -3);
    theChanceCards[9] = new Movement("Chance05", theGameBoard.aBoardCells[10]);
    theChanceCards[10] = new Movement("Chance06", theGameBoard.aBoardCells[11]);
    theChanceCards[11] = new Movement("Chance07", theGameBoard.aBoardCells[24]);
    theChanceCards[12] = new Movement("Chance08", theGameBoard.aBoardCells[0]);
    theChanceCards[13] = new Movement("Chance09", theGameBoard.aBoardCells[5]);
    theChanceCards[14] = new Movement("Chance10", theGameBoard.aBoardCells[39]);
    theChanceCards[15] = new Assessment("Chance15", 25, 100);
    shuffleDeck(theChanceCards, this.CARD_CHANCE_CNT);
    theNextChanceCardNbr = 0;
      // Community Chest Cards
    theChestCards[0] = new OutOfJail("Chest00");
    theChestCards[1] = new Card("Chest01", -50, Card.BANK);
    theChestCards[2] = new Card("Chest02", -100, Card.BANK);
    theChestCards[3] = new Card("Chest03", -150, Card.BANK);
    theChestCards[4] = new Card("Chest04", 45, Card.BANK);
    theChestCards[5] = new Card("Chest05", 25, Card.BANK);
    theChestCards[6] = new Card("Chest06", 20, Card.BANK);
    theChestCards[7] = new Card("Chest07", 100, Card.BANK);
    theChestCards[8] = new Card("Chest08", 100, Card.BANK);
    theChestCards[9] = new Card("Chest09", 10, Card.BANK);
    theChestCards[10] = new Card("Chest10", 100, Card.BANK);
    theChestCards[11] = new Card("Chest11", 200, Card.BANK);
    theChestCards[12] = new Card("Chest12", 50, Card.PLAYERS);
    theChestCards[13] = new Movement("Chest13", theGameBoard.aBoardCells[10]);
    theChestCards[14] = new Movement("Chest14", theGameBoard.aBoardCells[0]);
    theChestCards[15] = new Assessment("Chest15", 40, 115);
    shuffleDeck(theChestCards, this.CARD_CHEST_CNT);
    theNextChestCardNbr = 0;
  }

  private static Player getPreviousPlayer()
  {
    Player aPlayer;
    int iPlayerNbr = getPlayerNbr(theCurrentPlayer) - 1;
    iPlayerNbr--;  // go to previous player
    if (iPlayerNbr < 0)
      aPlayer = thePlayers[theNbrOfPlayers - 1];  // wrap around
    else
      aPlayer = thePlayers[iPlayerNbr];

        // skip past bankrupt players
    while (aPlayer.bBankrupt)
    {
      iPlayerNbr--;
      if (iPlayerNbr < 0)
        aPlayer = thePlayers[theNbrOfPlayers - 1];  // wrap around
      else
        aPlayer = thePlayers[iPlayerNbr];
    }
    return aPlayer;
  }

  public static Player getNextAvailablePlayer()
  {
    Player aNextAvailPlayer;
    if (theCurrentPlayer == null)
      aNextAvailPlayer = thePlayers[0];
    else
    {
        // Find the current player element
      int j = 0;
      while ((j < theNbrOfPlayers) && (thePlayers[j] != theCurrentPlayer))
        j++;
      j++;  // go to next player
      if (j >= theNbrOfPlayers)
				j = 0;    //wrap around
      aNextAvailPlayer = thePlayers[j];

        // skip past bankrupt players
      while (aNextAvailPlayer.bBankrupt)
      {
        j++;
        if (j >= theNbrOfPlayers)
					j = 0;    //wrap around
        aNextAvailPlayer = thePlayers[j];
      }
    }
    return aNextAvailPlayer;
  }

  public static Player nextPlayer()
  {
    theCurrentPlayer = getNextAvailablePlayer();
    return theCurrentPlayer;
  }

  private static int getPlayerNbr(Player aPlayer)
  {
    if (aPlayer == theBanker)
      return 0;
    else if (aPlayer == null)
      return -1;
    else
    {
      int iPlayerNbr=0;
      while ((iPlayerNbr < theNbrOfPlayers) && (thePlayers[iPlayerNbr] != aPlayer) )
        iPlayerNbr++;
      if (iPlayerNbr >= theNbrOfPlayers)
        return -1;
      else
        return iPlayerNbr + 1;
    }
  }

  private static int getCellNbr(GameBoardCell aCell)
  {
    int iCellNbr=0;
    while ( (iCellNbr < theGameBoard.BOARD_CELL_CNT)
       && (theGameBoard.aBoardCells[iCellNbr] != aCell) )
      iCellNbr++;
    if (iCellNbr >= theGameBoard.BOARD_CELL_CNT)
      return -1;
    else
      return iCellNbr;
  }

  private static int getPropertyNbr(Property aProperty)
  {
    int iCellNbr=0;
    while ( (iCellNbr < theGameBoard.BOARD_CELL_CNT)
       && (theGameBoard.aBoardCells[iCellNbr].aProp != aProperty) )
      iCellNbr++;
    if (iCellNbr >= theGameBoard.BOARD_CELL_CNT)
      return -1;
    else
      return iCellNbr;
  }

  private static void savePlayerStats(Player aPlayer, java.io.PrintWriter aFile)
  {
    if (aPlayer == theBanker)
    {
      aFile.println("[BANKER]");
      aFile.println("iFeesReceived=" + aPlayer.iFeesReceived);
      aFile.println();
    }
    else
    {
      aFile.println("[PLAYER_" + getPlayerNbr(aPlayer) + "]");
      aFile.println("sIconName=" + aPlayer.sIconName);
      aFile.println("sName=" + aPlayer.sName);
      aFile.println("iDoublesRolled=" + aPlayer.iDoublesRolled);
      aFile.println("iCash=" + aPlayer.iCash);
      aFile.println("iCashNeeded=" + aPlayer.iCashNeeded);
      aFile.println("iPropertyCnt=" + aPlayer.iPropertyCnt);
      aFile.println("iRailroadsOwned=" + aPlayer.iRailroadsOwned);
      aFile.println("bHasJailCardChest=" + aPlayer.bHasJailCardChest);
      aFile.println("bHasJailCardChance=" + aPlayer.bHasJailCardChance);
      aFile.println("iDaysLeftInJail=" + aPlayer.iDaysLeftInJail);
      aFile.println("bInJail=" + aPlayer.bInJail);
      aFile.println("iRentCollected=" + aPlayer.iRentCollected);
      aFile.println("iRentPaid=" + aPlayer.iRentPaid);
      aFile.println("iFeesPaid=" + aPlayer.iFeesPaid);
      aFile.println("iFeesReceived=" + aPlayer.iFeesReceived);
      aFile.println("iPayReceived=" + aPlayer.iPayReceived);
      aFile.println("bBankrupt=" + aPlayer.bBankrupt);
      aFile.println("iCurrentCell=" + getCellNbr(aPlayer.aCurrentCell));
      aFile.println("iPlayerOwed=" + getPlayerNbr(aPlayer.aPlayerOwed));
      aFile.println();
    }
  }

  public static void saveGame(boolean bIsCurrentPlayerFinished)
  {
    java.io.PrintWriter aFile = Files.createASCIIfile(SAVED_GAME_FILE_NAME);
    if (aFile != null)
    {
      aFile.println("[GAME]");
      aFile.println("iNbrOfPlayers=" + theNbrOfPlayers);
      aFile.println("iNextChanceCardNbr=" + theNextChanceCardNbr);
      aFile.println("iNextChestCardNbr=" + theNextChestCardNbr);
      if (bIsCurrentPlayerFinished)
        aFile.println("iCurrentPlayer=" + getPlayerNbr(theCurrentPlayer));
      else
        aFile.println("iCurrentPlayer=" + getPlayerNbr(getPreviousPlayer()));
      aFile.println();

      savePlayerStats(theBanker, aFile);
      for (int j=0; j < theNbrOfPlayers; j++)
        savePlayerStats(thePlayers[j], aFile);

      aFile.println("[PLAYER_PROPERTIES]");
      for (int j=0; j < theNbrOfPlayers; j++)
      {
        aFile.print((j+1) + "=");
        for (int k=0; k < thePlayers[j].iPropertyCnt; k++)
        {
          if (k > 0)
            aFile.print(",");
          aFile.print(getPropertyNbr(thePlayers[j].aProperties[k]) + "");
        }
        aFile.println();
      }
      aFile.println();

      for (int j=0; j < theGameBoard.BOARD_CELL_CNT; j++)
      {
        Property aProp = theGameBoard.aBoardCells[j].aProp;
        if (aProp.iLandingCount > 0)
        {
          aFile.println("[PROPERTY_" + j + "]");
          aFile.println("iOwner=" + getPlayerNbr(aProp.aOwner));
          aFile.println("bRentable=" + aProp.bRentable);
          aFile.println("iPricePaid=" + aProp.iPricePaid);
          aFile.println("iLandingCount=" + aProp.iLandingCount);
          aFile.println("iHouseCount=" + aProp.iHouseCount);
          aFile.println("iConstructionCosts=" + aProp.iConstructionCosts);
          aFile.println("iFeesPaid=" + aProp.iFeesPaid);
          aFile.println();
        }
      }

    }
    aFile.close();
  }

  private static boolean restoreGameFromFile()
  {
    boolean bRestoreOK = false;

    java.io.BufferedReader aReader = Files.openASCIIfile(SAVED_GAME_FILE_NAME);
    if (aReader != null)
      try
      {
        String sLine = aReader.readLine();
        while (! sLine.equals("[GAME]")) sLine = aReader.readLine();
        theNbrOfPlayers = Files.intValueAfterToken('=', aReader.readLine());
        theNextChanceCardNbr = Files.intValueAfterToken('=', aReader.readLine());
        theNextChestCardNbr = Files.intValueAfterToken('=', aReader.readLine());
        int theCurrentPlayerNbr = Files.intValueAfterToken('=', aReader.readLine()) - 1; // update actual player later

        while (! sLine.equals("[BANKER]")) sLine = aReader.readLine();
        theBanker.iFeesReceived = Files.intValueAfterToken('=', aReader.readLine());

        int[] iPlayersOwed = new int[6];
        for (int j=0; j < theNbrOfPlayers; j++)
        {
          while (! sLine.equals("[PLAYER_" + (j + 1) + "]")) sLine = aReader.readLine();

          String sFileName = Files.stringValueAfterToken('=', aReader.readLine());
          thePlayers[j] = new Player(Files.stringValueAfterToken('=', aReader.readLine()),
                                   Monopoly.START_MONEY, sFileName);
          thePlayers[j].iDoublesRolled = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].iCash = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].iCashNeeded = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].iPropertyCnt = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].iRailroadsOwned = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].bHasJailCardChest = Files.booleanValueAfterToken('=', aReader.readLine());
          thePlayers[j].bHasJailCardChance = Files.booleanValueAfterToken('=', aReader.readLine());
          thePlayers[j].iDaysLeftInJail = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].bInJail = Files.booleanValueAfterToken('=', aReader.readLine());
          thePlayers[j].iRentCollected = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].iRentPaid = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].iFeesPaid = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].iFeesReceived = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].iPayReceived = Files.intValueAfterToken('=', aReader.readLine());
          thePlayers[j].bBankrupt = Files.booleanValueAfterToken('=', aReader.readLine());
          iStartingBoardPositions[j] = Files.intValueAfterToken('=', aReader.readLine());
          iPlayersOwed[j] = Files.intValueAfterToken('=', aReader.readLine());
        }

          // All players are now created.  Go back and update the "aPlayerOwed" attributes
          // as well as the current player
        for (int j=0; j < theNbrOfPlayers; j++)
        {
          if (iPlayersOwed[j] == -1)
            thePlayers[j].aPlayerOwed = null;
          else
            thePlayers[j].aPlayerOwed = thePlayers[j];
        }
        theCurrentPlayer = thePlayers[theCurrentPlayerNbr];

          // The creation of the game board is dependant on the existance of the players
          // therefor this line can't be moved up.  The following lines have references
          // to elements on the gameboard so this line can't be moved down either.
          // I'm ashamed at this part of the design.  I'll need to fix this sometime.  -trh
        theGameBoard = new GameBoard();   // Create Game Board

        while (! sLine.equals("[PLAYER_PROPERTIES]")) sLine = aReader.readLine();
        for (int j=0; j < theNbrOfPlayers; j++)
        {
          int[] aPropList = Files.getIntArrayFromString(',', Files.stringValueAfterToken('=', aReader.readLine()));
          for (int k=0; k < thePlayers[j].iPropertyCnt; k++)
            thePlayers[j].aProperties[k] = theGameBoard.aBoardCells[aPropList[k]].aProp;
        }

        sLine = aReader.readLine();  // skip blank line
        while ( (sLine != null) && (sLine.length() == 0) )
          sLine = aReader.readLine();  // skip blank lines

        while (sLine != null)
        {
             // get the property number
          int iPropNbr = Formatting.stringToInt(sLine.substring(sLine.indexOf('_') + 1, sLine.length()-1));
          Property aProp = theGameBoard.aBoardCells[iPropNbr].aProp;
          int iOwner = Files.intValueAfterToken('=', aReader.readLine());
          if (iOwner > 0)
            aProp.aOwner = thePlayers[iOwner - 1];
          aProp.bRentable = Files.booleanValueAfterToken('=', aReader.readLine());
          aProp.iPricePaid = Files.intValueAfterToken('=', aReader.readLine());
          aProp.iLandingCount = Files.intValueAfterToken('=', aReader.readLine());
          aProp.iHouseCount = Files.intValueAfterToken('=', aReader.readLine());
          aProp.iConstructionCosts = Files.intValueAfterToken('=', aReader.readLine());
          aProp.iFeesPaid = Files.intValueAfterToken('=', aReader.readLine());
          sLine = aReader.readLine();  // skip blank line
          while ( (sLine != null) && (sLine.length() == 0) )
            sLine = aReader.readLine();  // skip blank lines
        }

        aReader.close();
        bRestoreOK = true;
      }
      catch (java.io.IOException e)
      {
        System.out.println("IO Exception reading file.");
      }

    return bRestoreOK;
  }


// Main method
  public static void main(String[] args)
  {
    for (int j=0; j < args.length; j++)
    {
      sDefaultNames[j] = new String(args[j]);
    }

    try
    {
      UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.motif.MotifLookAndFeel());
      //UIManager.setLookAndFeel(new javax.swing.plaf.metal.MetalLookAndFeel());
    }
    catch (Exception e)
    {
    }
      // Create the main Object
    new Monopoly();
  }
}

