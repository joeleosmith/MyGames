package Monopoly;

import com.borland.jbcl.control.*;
import java.awt.*;
import com.heagy.util.ImageHandler;
import java.util.Hashtable;

public class ImageLoader
{
  protected int iMaxNbrOfEntries = 40;  // should be overriden by the descendent constructor
  protected String sImageDir;
  protected String sDefaultExt = "jpg";
  protected String[] sNames;
  protected Hashtable anImageTable;
  protected ImageHandler aImageHandler = new ImageHandler();

  private TransparentImage anImage;

  public ImageLoader()
  {
  }

  public void loadImages()
  {
    try {
      int j = 0;
      while ((j < sNames.length) && (sNames[j] != null))
      {
        anImage = new TransparentImage();
          // if the name doesn't already have an extension, use the default
        String sExt;
        int iDotLocation = sNames[j].lastIndexOf('.');
        if (iDotLocation >  0)
        {
            // set the 'overriden' extension and then remove it from the name
          sExt = sNames[j].substring(iDotLocation + 1);
          sNames[j] = sNames[j].substring(0, iDotLocation);
        }
        else
          sExt = sDefaultExt;
          
        anImage.setImage(aImageHandler.getImage(sImageDir + sNames[j] + "." + sExt));
        anImageTable.put(sNames[j], anImage);
        j++;
      }
    }
    catch (java.io.IOException e) {}
  }

  public Image getImage(String sImageName)
  {
    anImage = (TransparentImage)anImageTable.get(sImageName);
    return anImage.getImage();
  }

}
