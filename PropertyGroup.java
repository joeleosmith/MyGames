package Property;

public class PropertyGroup
{
  public int iPropertyCnt;
  public Property[] aProperties = new Property[4];

  public PropertyGroup(int PropertyCnt)
  {
    iPropertyCnt = PropertyCnt;
  }

  public int maxHouseCnt()
  {
    int iCnt = 0;
    for (int j=0; j < iPropertyCnt; j++)
    {
      if (aProperties[j].iHouseCount > iCnt)
        iCnt = aProperties[j].iHouseCount;
    }
    return iCnt;
  }

  public int minHouseCnt()
  {
    int iCnt = 999;
    for (int j=0; j < iPropertyCnt; j++)
    {
      if (aProperties[j].iHouseCount < iCnt)
        iCnt = aProperties[j].iHouseCount;
    }
    return iCnt;
  }

}

