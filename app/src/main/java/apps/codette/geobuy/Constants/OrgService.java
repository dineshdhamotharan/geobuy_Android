package apps.codette.geobuy.Constants;

import android.content.Context;

import java.util.Map;

import apps.codette.forms.Organization;
import apps.codette.utils.SessionManager;

public class OrgService {

   Context context;
   SessionManager sessionManager;

   public static Organization organization = null;

   public int getCartItems(Context context) {
      int items = 0;
      sessionManager = new SessionManager(context);
      Map<String, ?> userDetails = sessionManager.getUserDetails();
      String products = null;
      if (userDetails.get("cart") != null && !userDetails.get("cart").toString().trim().isEmpty()) {
         products = (String)userDetails.get("cart");
         items = products.split(",").length;
      }
      return items;
   }

}
