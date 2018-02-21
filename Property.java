package Property;

import com.borland.dx.text.Alignment;

import com.borland.jbcl.control.*;
import java.awt.*;
import Monopoly.*;
import Player.Player;
import Rent.Landlord;
import Card.*;

public class Property
{
    // Class constants
  public static final int NORMAL = 1;
  public static final int RAILROAD = 2;
  public static final int UTILITY = 3;
  public static final int GO = 4;
  public static final int JAIL = 5;
  public static final int PARKING = 6;
  public static final int GOTO_JAIL = 7;
  public static final int COMMUNITY_CHEST = 8;
  public static final int CHANCE = 9;
  public static final int TAX = 10;
  public static final int MAX_HOUSES = 5;
      // Property name List
  public static final String IMAGE_DIR = "/Property/Titles/";
  public static final String CARD_IMAGE_DIR = IMAGE_DIR + "Card/";
  public static final String CARD_IMAGE_EXT = ".jpg";
  public static final String HEADER_IMAGE_DIR = IMAGE_DIR + "Header/";
  public static final String HOUSE_IMAGE_DIR = "/Property/House Images/";
  public static final String HOUSE_IMAGE_EXT = ".gif";
  public static final int PROP_CNT = 28;
  public static final String[] PROP_NAMES = {"Mediterranean Ave","Baltic Ave","Reading Railroad","Oriental Ave",
                       "Vermont Ave", "Connecticut Ave", "St Charles Place",
                       "Electric Company", "States Ave", "Virginia Ave",
                       "Pennsylvania Railroad", "St James Place", "Tennessee Ave",
                       "New York Ave", "Kentucky Ave", "Indiana Ave", "Illinois Ave",
                       "B & O Railroad", "Atlantic Ave", "Ventnor Ave", "Water Works",
                       "Marvin Gardens", "Pacific Ave", "North Carolina Ave",
                       "Pennsylvania Ave","Short Line Railroad", "Park Place", "Boardwalk"};

    // Class properties
  public String sName;
  public String sImageName;
  public int iPropertyType;  // {see constants above}
  public boolean bRentable;
  public Rent.Landlord aLandlord;
  public int iRents[] = new int[6];
  public int iMortgageValue;
  public int iUnMortgageCost;
  public int iHouseCost;
  public int iPropertyCost;
  public int iPricePaid;
  public int iLandingCount;
  public int iHouseCount;
  public int iConstructionCosts;
  public int iFeesPaid;
  public Player aOwner;
  public boolean bHousesAllowed = true;
  public PropertyGroup aGroup;
  public TransparentImage aHouseImage;
  public String sCellOrientation;

  public Property()
  {
  }

  public Property(String PropertyName, int PropertyType,
                  int PropertyCost, boolean HousesAllowed, int HouseCost,
                  Landlord theLandlord, PropertyGroup aPropertyGroup,
                  TransparentImage aImage, String CellOrientation)
  {
    sName = PropertyName;
    bRentable = true;
    iPropertyType = PropertyType;
    iMortgageValue = PropertyCost / 2;
    iUnMortgageCost = iMortgageValue + (iMortgageValue / 10);
    iHouseCost = HouseCost;
    iPropertyCost = PropertyCost;
    bHousesAllowed = HousesAllowed;
    aLandlord = theLandlord;
    aGroup = aPropertyGroup;
    aHouseImage = aImage;
    aHouseImage.setAlignment(com.borland.dx.text.Alignment.LEFT | com.borland.dx.text.Alignment.TOP);
    sCellOrientation = CellOrientation;
  }

  public Property(int SpecialPropertyType, Rent.Landlord Landlord)
  {
    iPropertyType = SpecialPropertyType;
    bHousesAllowed = false;
    aLandlord = Landlord;
  }

  public void showHouses()
  {
    if (this.bHousesAllowed)
    {
      String sHouseFile;
      if (this.iHouseCount == 0)
        aHouseImage.setVisible(false);
      else
      {
        aHouseImage.setVisible(true);
        sHouseFile = "House" + this.iHouseCount + this.sCellOrientation;
        try{
          aHouseImage.setImage(Monopoly.getHouseImage(sHouseFile));
        }
        catch (Exception e) {}
      }
    }
  }

  public void show(Player aPlayer, boolean bAlwaysShow)
  {
    this.show(aPlayer, Card.NONE, bAlwaysShow);
  }

  public void show(Player aPlayer, int iSpecialFee, boolean bAlwaysShow)
  {
    if (this.iPropertyType == this.TAX)
    {
      if (aPlayer != null)
      {
        int iRent = this.aLandlord.calculateRent();
        aPlayer.payFee(iRent, Monopoly.theBanker, Player.REASON_FEE);
      }
    }

    else if (this.iPropertyType == this.PARKING)
    {
      if (aPlayer != null)
      {
        int iAmount = this.aLandlord.calculateRent();
        aPlayer.receivePay(iAmount, Player.REASON_FEE);
        Monopoly.theBanker.iCash = 0;
      }
    }

    else if ((sName != null) && (sName.length() > 0))
    {
      if ( (bAlwaysShow) | (this.aOwner == null) |
           ((this.aOwner != null) && (this.aOwner != aPlayer) && (this.bRentable)) )
      {
        PropertyInfoDialog aWindow = new PropertyInfoDialog(Monopoly.theGameBoard,
                                            "Property Information", true, this,
                                            aPlayer, iSpecialFee);
        aWindow.show();
        aWindow = null;
      }
    }
  }

  public boolean isOkToTrade()
  {
    return (getHouseCntForGroup(this.aGroup) == 0);
  }

  public int getHouseCntForGroup(PropertyGroup aGroup)
  {
    int iCnt=0;
    for (int j=0; j < aGroup.iPropertyCnt; j++)
      iCnt += aGroup.aProperties[j].iHouseCount;
    return iCnt;
  }

  public boolean isOkToAuction()
  {
    return ( (this.aOwner == null) && (this.sName != null) );
  }

  public boolean isOkToBuyHouse(Player aPlayer)
  {
    boolean bOK = false;

      // Does the requesting player own this property?
      // ... AND is the property NOT a Utility
      // ... AND does the owner of this property own all the other properties in the group?
      // ... AND does the owner have enough money to build?
      // ... AND the maximum number of houses hasn't already been built
    if ( (this.aOwner == aPlayer) &&
         (this.iPropertyType != this.UTILITY) &&
         (this.aOwner.ownsAllPropsInGroup(this.aGroup)) &&
         (this.aOwner.iCash >= this.iHouseCost) &&
         (this.iHouseCount < this.MAX_HOUSES) )
    {
        // Are the houses evenly built on all properties in group?
      int iMost = this.aGroup.maxHouseCnt();
      int iLeast = this.aGroup.minHouseCnt();
      bOK = ( (iMost == iLeast) | (this.iHouseCount < iMost) );
    }

    return bOK;
  }

  public boolean isOkToSellHouse(Player aPlayer)
  {
    boolean bOK = false;

      // Does the requesting player own this property?
      // ... AND there is at least one house on this property
    if ( (this.aOwner == aPlayer) &&
         (this.iHouseCount > 0) )
    {
        // Are the houses evenly built on all properties in group?
      int iMost = this.aGroup.maxHouseCnt();
      int iLeast = this.aGroup.minHouseCnt();
      bOK = ( (iMost == iLeast) | (this.iHouseCount > iLeast) );
    }

    return bOK;
  }

  public boolean isOkToMortgage(Player aPlayer)
  {
      // Does the requesting player own this property?
      // ... AND are there 0 houses on all of the properties in the group
    return ( (this.aOwner == aPlayer) && (this.aGroup.maxHouseCnt() == 0) );
  }

  public void buy(Player aFromPlayer, int iPricePaid)
  {
    this.aOwner = aFromPlayer;
    this.iPricePaid = iPricePaid;
  }

  public void mortgage()
  {
    this.bRentable = false;
  }

  public void unMortgage()
  {
    this.bRentable = true;
    this.iPricePaid += this.iUnMortgageCost;
  }

  public void buildHouse()
  {
    this.iHouseCount++;
    this.iConstructionCosts += this.iHouseCost;
    this.showHouses();
  }

  public void sellHouse()
  {
    this.iHouseCount--;
    this.iConstructionCosts -= (this.iHouseCost / 2);
    this.showHouses();
  }

  public void payAssesment(int iAmount)
  {
    this.iFeesPaid += iAmount;
  }

  public void returnToBank()
  {
    this.bRentable = true;
    this.iPricePaid = 0;
    this.iConstructionCosts = 0;
    this.iFeesPaid = 0;
    this.aOwner = null;
  }
}
