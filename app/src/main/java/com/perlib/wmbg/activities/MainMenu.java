package com.perlib.wmbg.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.perlib.wmbg.R;
import com.perlib.wmbg.book.Book;
import com.perlib.wmbg.interfaces.OnDownloadComplete;
import com.perlib.wmbg.misc.CommonLib;
import com.perlib.wmbg.misc.SharedPreferencesHelper;

import java.util.ArrayList;

public class MainMenu extends Activity implements OnDownloadComplete {

	Button btnSearchBook;
	Button btnScanBook;
	Button btnManualAddBook;
    Button btnSettings;
    Button btnHelp;
	
	IntentIntegrator scanIntegrator = new IntentIntegrator(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.activity_menu);

        //View references
	    btnSearchBook = (Button) findViewById(R.id.btnSeachBooks);
	    btnScanBook = (Button) findViewById(R.id.btnScanBook);
	    btnManualAddBook = (Button) findViewById(R.id.btnManualAddBook);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnHelp = (Button) findViewById(R.id.btnHelp);

        //Create book list if doesn't exist.
        if(new SharedPreferencesHelper(this).getBookList() == null) {
            new SharedPreferencesHelper(this).setBookList(new ArrayList<Book>());
        }

        //Search book listener
	    btnSearchBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Go to main list activity
				Intent searchBook = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(searchBook);
			}
		});

        //Scan book listener
	    btnScanBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    scanIntegrator.initiateScan();
				
			}
		});

        //Manual add book
	    btnManualAddBook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                //Go to addbook in manual mode
				Intent addBook = new Intent(getApplicationContext(), AddBook.class);
				addBook.putExtra("mode", AddBook.MODE_MANUAL);
				startActivity(addBook);
			}
		});

        //Settings
        btnSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to settings
                Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settings);
            }
        });

        //Help
        btnHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to help
                Intent help = new Intent(MainMenu.this, HelpActivity.class);
                startActivity(help);
            }
        });
	}

    //Handles response from barcode scanner
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
        //Use zxing integration to read data from intent
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {
			String contents = scanResult.getContents();
			if(contents != null)
			{
                //Handle isbn code and use this as listener for download completion
				CommonLib.handleISBN(contents, getApplicationContext(), this);
                //Notify user of download in progress
                Toast.makeText(getApplicationContext(), R.string.bookDownloadStarted, Toast.LENGTH_LONG).show();
			}
		}
	}

    //Called when the book info download for the scanned isbn code is completed
	@Override
	public void onBookInfoDownloadComplete(Book result) {
		//Null check
		if(result == null)
		{
			Toast.makeText(getApplicationContext(), getString(R.string.InvalidISBN) , Toast.LENGTH_SHORT).show();
			return;
		}

        //Go to scan book activity to handle scan results.
		Intent scanBook = new Intent(getApplicationContext(), ScanBook.class);
		scanBook.putExtra("result", (Parcelable) result);
		startActivity(scanBook);
	}

}
