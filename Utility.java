package Rent;

public class Utility extends Landlord
{

  public Utility()
  {
    super();
  }

  public int calculateRent(int iNbrOfUtilitiesOwned, int iDiceAmount)
  {
    if (iNbrOfUtilitiesOwned == 1)
      return (iDiceAmount * 4);
    else if (iNbrOfUtilitiesOwned == 2)
      return (iDiceAmount * 10);
    else
      return 0;
  }

}