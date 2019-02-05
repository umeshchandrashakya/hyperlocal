package com.hyperlocal.app.ui.registration.countrypicker;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author ${Umesh} on 07/10/15.
 */

/**
 * @author ${Umesh} on 07/10/15.
 */
public class DataHelper {
    private Context context;

    public DataHelper(Context context) {
        this.context = context;
    }

    public List<CountryDetails> getAlphabetData() {
        ArrayList<CountryDetails> countryDetails = new ArrayList<>();
        try {
            JSONArray obj = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < obj.length(); i++) {
                JSONObject jsonInside = obj.getJSONObject(i);
                String name = jsonInside.getString("name");
                String dialCode = jsonInside.getString("dial_code");
                String code = jsonInside.getString("code");
                countryDetails.add(new CountryDetails(name, code, dialCode));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return countryDetails;
    }


    public Map<String,ArrayList<CountryDetails>> getData(){
        Map<String,ArrayList<CountryDetails>> hashMap = new TreeMap<>();
        String arr[]={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q"
                ,"R","S","T","U","V","W","Y","Z"};
        ArrayList<CountryDetails> arrayList= (ArrayList<CountryDetails>) getAlphabetData();
        for (int i=0;i<arr.length;i++){
            ArrayList<CountryDetails> arrayList1=   getFilterArray(arrayList,arr[i]);
            hashMap.put(arr[i],arrayList1);
        }
        return hashMap;
    }

    private ArrayList<CountryDetails> getFilterArray(ArrayList<CountryDetails> arrayList, String ch) {
        ArrayList<CountryDetails> newArray=new ArrayList<>();
        for (CountryDetails a:arrayList) {
            if(a.getCountryName().startsWith(ch)){
                newArray.add(a);
            }
        }
        return newArray;

    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = context.getAssets().open("countryCodes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
