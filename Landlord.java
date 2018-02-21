package Rent;

public class Landlord
{

  public int iRentCollected;
  int iRents[] = new int[6];

  public Landlord()
  {
  }

  public Landlord(int NormalRent, int Rent1House, int Rent2Houses,
                  int Rent3Houses, int Rent4Houses, int RentHotel)
  {
    iRents[0] = NormalRent;
    iRents[1] = Rent1House;
    iRents[2] = Rent2Houses;
    iRents[3] = Rent3Houses;
    iRents[4] = Rent4Houses;
    iRents[5] = RentHotel;
  }

  public int calculateRent()
  {
    return iRents[0];
  }

  public int calculateRent(int iHouseCnt, boolean bPlayerOwnsAllPropsInGroup)
  {
    if ( (bPlayerOwnsAllPropsInGroup) && (iHouseCnt == 0) )
      return (iRents[iHouseCnt] * 2);
    else
      return iRents[iHouseCnt];
  }

  public void incrementRentPaid(int iAmount)
  {
    iRentCollected += iAmount;
  }
}
