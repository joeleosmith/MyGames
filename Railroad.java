package Rent;

public class Railroad extends Landlord
{

  public Railroad()
  {
    super(25, 25,50,100,200,0);
  }

  public int calculateRent(int iRailroadCnt)
  {
    return iRents[iRailroadCnt];
  }

}