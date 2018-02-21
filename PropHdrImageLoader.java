package Property;

import java.util.Hashtable;

public class PropHdrImageLoader extends Monopoly.ImageLoader
{
  public PropHdrImageLoader()
  {
    sNames = new String[Property.PROP_CNT];
    anImageTable = new Hashtable(Property.PROP_CNT);

    sImageDir = "/Property/Titles/Header/";
    sNames = Property.PROP_NAMES;
  }

}
