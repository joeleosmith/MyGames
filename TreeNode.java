package Player;

import Property.*;

public class TreeNode
{
  public static final int BRANCH=1;
  public static final int PROPERTY=2;
  public static final int HOUSE=3;
  public static final int HOTEL=4;
  public Property aProperty = null;
  private String sNodeDesc = "";
  private int iNodeType;
  private int iSaleValue = 0;

  public TreeNode(Property aProperty)
  {
    this.aProperty = aProperty;
    this.sNodeDesc = aProperty.sName;
    this.iNodeType = PROPERTY;
    if (aProperty.bRentable)
      this.iSaleValue = aProperty.iMortgageValue;
    else
      this.iSaleValue = 0;
  }

  public TreeNode(Property aParentProperty, int iNodeType)
  {
    this.iNodeType = iNodeType;
    this.aProperty = aParentProperty;
    if (iNodeType == HOUSE)
    {
      this.sNodeDesc = "House";
      this.iSaleValue = aParentProperty.iHouseCost / 2;
    }
    else if (iNodeType == HOTEL)
    {
      this.sNodeDesc = "HOTEL";
      this.iSaleValue = aParentProperty.iHouseCost * 5 / 2;
    }
  }

  public TreeNode(String sBranchDesc)
  {
    this.iNodeType = BRANCH;
    this.sNodeDesc = sBranchDesc;
  }

  public int getSaleValue()
  {
    return iSaleValue;
  }

  public void setSaleValue(int iSaleValue)
  {
    this.iSaleValue = iSaleValue;
  }

  public int getNodeType()
  {
    return iNodeType;
  }

  public Property getProperty()
  {
    return this.aProperty;
  }

  public String toString()
  {
    return sNodeDesc;
  }
}
