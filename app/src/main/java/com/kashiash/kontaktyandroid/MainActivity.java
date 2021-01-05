// MainActivity.java
// Hosts the app's fragments and handles communication between them
package com.kashiash.kontaktyandroid;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity
   implements ContactsFragment.ContactsFragmentListener,
        com.kashiash.kontaktyandroid.DetailFragment.DetailFragmentListener,
        com.kashiash.kontaktyandroid.AddEditFragment.AddEditFragmentListener {

   // key for storing a contact's Uri in a Bundle passed to a fragment
   public static final String CONTACT_URI = "contact_uri";

   private ContactsFragment contactsFragment; // displays contact list

   // display ContactsFragment when MainActivity first loads
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      // if layout contains fragmentContainer, the phone layout is in use;
      // create and display a ContactsFragment
      if (savedInstanceState == null &&
         findViewById(R.id.fragmentContainer) != null) {
         // create ContactsFragment
         contactsFragment = new ContactsFragment();

         // add the fragment to the FrameLayout
         FragmentTransaction transaction =
            getSupportFragmentManager().beginTransaction();
         transaction.add(R.id.fragmentContainer, contactsFragment);
         transaction.commit(); // display ContactsFragment
      }
      else {
         contactsFragment =
            (ContactsFragment) getSupportFragmentManager().
               findFragmentById(R.id.contactsFragment);
      }
   }

   // display DetailFragment for selected contact
   @Override
   public void onContactSelected(Uri contactUri) {
      if (findViewById(R.id.fragmentContainer) != null) // phone
         displayContact(contactUri, R.id.fragmentContainer);
      else { // tablet
         // removes top of back stack
         getSupportFragmentManager().popBackStack();

         displayContact(contactUri, R.id.rightPaneContainer);
      }
   }

   // display AddEditFragment to add a new contact
   @Override
   public void onAddContact() {
      if (findViewById(R.id.fragmentContainer) != null) // phone
         displayAddEditFragment(R.id.fragmentContainer, null);
      else // tablet
         displayAddEditFragment(R.id.rightPaneContainer, null);
   }

   // display a contact
   private void displayContact(Uri contactUri, int viewID) {
      DetailFragment detailFragment = new DetailFragment();

      // specify contact's Uri as an argument to the DetailFragment
      Bundle arguments = new Bundle();
      arguments.putParcelable(CONTACT_URI, contactUri);
      detailFragment.setArguments(arguments);

      // use a FragmentTransaction to display the DetailFragment
      FragmentTransaction transaction =
         getSupportFragmentManager().beginTransaction();
      transaction.replace(viewID, detailFragment);
      transaction.addToBackStack(null);
      transaction.commit(); // causes DetailFragment to display
   }

   // display fragment for adding a new or editing an existing contact
   private void displayAddEditFragment(int viewID, Uri contactUri) {
      AddEditFragment addEditFragment = new AddEditFragment();

      // if editing existing contact, provide contactUri as an argument
      if (contactUri != null) {
         Bundle arguments = new Bundle();
         arguments.putParcelable(CONTACT_URI, contactUri);
         addEditFragment.setArguments(arguments);
      }

      // use a FragmentTransaction to display the AddEditFragment
      FragmentTransaction transaction =
         getSupportFragmentManager().beginTransaction();
      transaction.replace(viewID, addEditFragment);
      transaction.addToBackStack(null);
      transaction.commit(); // causes AddEditFragment to display
   }

   // return to contact list when displayed contact deleted
   @Override
   public void onContactDeleted() {
      // removes top of back stack
      getSupportFragmentManager().popBackStack();
      contactsFragment.updateContactList(); // refresh contacts
   }

   // display the AddEditFragment to edit an existing contact
   @Override
   public void onEditContact(Uri contactUri) {
      if (findViewById(R.id.fragmentContainer) != null) // phone
         displayAddEditFragment(R.id.fragmentContainer, contactUri);
      else // tablet
         displayAddEditFragment(R.id.rightPaneContainer, contactUri);
   }

   // update GUI after new contact or updated contact saved
   @Override
   public void onAddEditCompleted(Uri contactUri) {
      // removes top of back stack
      getSupportFragmentManager().popBackStack();
      contactsFragment.updateContactList(); // refresh contacts

      if (findViewById(R.id.fragmentContainer) == null) { // tablet
         // removes top of back stack
         getSupportFragmentManager().popBackStack();

         // on tablet, display contact that was just added or edited
         displayContact(contactUri, R.id.rightPaneContainer);
      }
   }
}

