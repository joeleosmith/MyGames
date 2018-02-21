package Rent;

import Monopoly.*;

public class IncomeTax extends Landlord
{

  public IncomeTax()
  {
    super();
  }

  public int calculateRent()
  {
    int iNetWorth = Monopoly.theCurrentPlayer.getNetWorth();
    if (iNetWorth > 2000)
      return 200;
    else
      return (int)((double)iNetWorth * 0.10);
  }

}