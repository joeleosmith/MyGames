package Rent;

import Monopoly.*;

public class FreeParking extends Landlord
{

  public FreeParking()
  {
    super();
  }

  public int calculateRent()
  {
      // return the amount of money in the tax hopper
    return Monopoly.theBanker.iCash;
  }

}