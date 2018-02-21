package Rent;

public class LuxuryTax extends Landlord
{

  public LuxuryTax()
  {
    super(75, 0,0,0,0,0);
  }

  public int calculateRent()
  {
    return 75;
  }

}