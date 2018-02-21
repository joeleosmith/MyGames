package Property;

import java.util.Hashtable;

public class PropCardImageLoader extends Monopoly.ImageLoader
{
//  private static final String HOUSE_IMAGE_DIR = "/Property/House Images/";
//  private static final String HOUSE_IMAGE_EXT = ".gif";

  public PropCardImageLoader()
  {
    sNames = new String[Property.PROP_CNT];
    anImageTable = new Hashtable(Property.PROP_CNT);

    sImageDir = "/Property/Titles/Card/";
    sNames = Property.PROP_NAMES;
  }

}
