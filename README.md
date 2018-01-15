# InventoryApp
**Version 1.0 2016/07/01**

The app allows the user to store business product related data as an inventory items  

stored in a list on their smartphone. Product's details include price, quantity, image and more  

![alt text](https://github.com/skorudzhiev/InventoryApp/blob/master/InventoryApp%20-%20Nexus_5X_API_24_5554.png)

## General Usage Notes

```java
defaultConfig {
  minSdkVersion 16
  targetSdkVersion 25
}
```

* The app allows the user to store and maintain business product /type of data on device's local storage
* User can store the following product's individual information: 
  * product's name
  * image chosen from the local device Gallery
  * quantity
  * price
  * supplier contact details

* Created inventory is accessible on the Main Actviity of the app as a List Layout of products
* Product's quantitiy can be decreased from the Main Activity upon closing a sale
* Detailed information is available on selecting specific list item
  * product information can be visualized and edited from the Detailed Layout
  * from the Detailed screen, user has could send pre-defined e-mail template with product specifics to the supplier

### Features 

* Integrated SQLite database which Read/Writes local storage
* Cursor adapter populate and track position of items in the list
* Content [Encapsulation](https://www.tutorialspoint.com/java/java_encapsulation.htm) through the implementation of Content Provider
* App content is generated and maintained solely by the user
* Intent to either a phone app or an email app to contact the supplier using the information stored in the database
* Input validation
  * empty product information is not accepted upon saving a product into the DB (Toast message prompts the user for an input)
  ```java  
String imageString = "";
     if (chosenImage != null) {
          imageString = chosenImage.toString();
     }

      if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
             TextUtils.isEmpty(quantityString) || chosenImage == null &&
             TextUtils.isEmpty(suppNameString) && TextUtils.isEmpty(emailString) && currentUri == null) {
         Toast.makeText(getBaseContext(), R.string.toast_missing_info, Toast.LENGTH_LONG).show();
         return;
     }
```
