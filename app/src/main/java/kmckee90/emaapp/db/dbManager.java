package kmckee90.emaapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.database.DatabaseUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



import kmckee90.emaapp.db.csv.CSVWriter;
import kmckee90.emaapp.phd.HoldButtonInterface;

import org.apache.commons.io.FileUtils;
/**
 * Created by Work on 9/8/2016.
 */
public class dbManager {


    public static final String DATABASE_NAME = "EMAdat";
    public static final int DATABASE_VERSION = 1;

    public static final String FOLDER_NAME = "EMAData";
    public static final String FILE_NAME = "EMAData";
    public static final String FILE_NAME_ENC = "EMAData_enc";
    public static final String FILE_DESTINATION = "http://192.168.1.4:8080/TestUploadFolder/";

    //Fuck SQLite's foreign key thing that doesn't work. I'm using an integer and going to bed.
    public static int t;

    //Encryption key
    private static String cryptoPass = "password";

    private static final String createSubmitTable =
            "CREATE TABLE submissions"
                    + "(submission_id integer primary key autoincrement, "
                    + "timestamp_open long not null, "
                    + "timestamp_submit long not null); ";

    private static final String createItemTable =

            "CREATE TABLE items"
                    + "(item_id integer primary key autoincrement, "
                    + "name string not null, "
                    + "timestamp_touch long not null, "
                    + "value double not null, "
                    + "skip boolean not null, "
                    + "submission_id integer)";

    private static final String selectItems = "select * from submissions s, items i where i.submission_id = s.submission_id";


    private static final String SQL_DELETE_SUBMISSIONS =
            "DROP TABLE IF EXISTS submissions";
    private static final String SQL_DELETE_ITEMS =
            "DROP TABLE IF EXISTS items";

    private final Context context;
    private dbHelper mDbHelper;
    private SQLiteDatabase db;


    public dbManager(Context ctx) {
        this.context = ctx;
        mDbHelper = new dbHelper(context);
    }

    public dbManager open() throws SQLException {
        db = mDbHelper.getWritableDatabase();
        return this;
    }


    public void close() {
        mDbHelper.close();
    }

    //Selects all data and outputs to console.
    public void outputDB() {
        db = mDbHelper.getReadableDatabase();

       /*
        Cursor cursor1 = db.query("items", new String[] {"item_id", "name", "value", "submission_id","skip"}, null, null, null, null, null, null);
        Cursor cursor2 = db.query("submissions", new String[] {"submission_id", "timestamp"}, null, null, null, null, null, null);
        DatabaseUtils.dumpCursor(cursor1);
        cursor1.close();
        DatabaseUtils.dumpCursor(cursor2);
        cursor2.close();
        */
        Cursor cursor = db.rawQuery(selectItems, null);
        DatabaseUtils.dumpCursor(cursor);
        cursor.close();
        db.close();
    }

    //Takes items as parameters and puts their data in the database.
    //Would be better if it took an array of items.

    public void enterData(HoldButtonInterface[] items, long timestamp_open) {
        ContentValues currentTime = new ContentValues();
        open();
        db.execSQL("PRAGMA foreign_keys = ON;");

        for (int i = 0; i < items.length; i++) {
            ContentValues newData = new ContentValues();
            newData.put("name", items[i].getName());
            newData.put("timestamp_touch", items[i].getTimestamp());
            newData.put("value", items[i].getDuration());
            newData.put("skip", items[i].isFlagged());
            newData.put("submission_id", t);
            db.insert("items", null, newData);
        }
        currentTime.put("timestamp_open", timestamp_open);
        currentTime.put("timestamp_submit", System.currentTimeMillis());
        db.insert("submissions", null, currentTime);

        Log.i("DB", "DATA ENTERED");
        close();
        t++;
    }

    public void deleteDB() {
        db = mDbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_SUBMISSIONS);
        db.execSQL(SQL_DELETE_ITEMS);
        Log.i("DB", "DELETING DATABASE");
        mDbHelper.onCreate(db);
        t = 1;
    }


    private static class dbHelper extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.
        public dbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.e("DB", "Trying to create table...");
            db.execSQL("PRAGMA foreign_keys = ON;");
            db.execSQL(createItemTable);
            db.execSQL(createSubmitTable);
            t = 1;

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_SUBMISSIONS);
            db.execSQL(SQL_DELETE_ITEMS);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }

    public void exportCSV(Context context) {
        File dbFile = context.getDatabasePath("EMAdat.db");
        File exportDir = new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME);
        if (!exportDir.exists()) {
            Log.i("Export", "Directory does not exist.");
            boolean dirResult = exportDir.mkdirs();
            Log.i("Export", "Did directory create? " + Boolean.toString(dirResult));
        } else {
            Log.i("Export", "Directory exists.");
        }

        File file = new File(exportDir, FILE_NAME);
        try {
            boolean fileResult = file.createNewFile();
            Log.i("Export", "Did file create? " + Boolean.toString(fileResult));

            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            db = mDbHelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery(selectItems, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String arrStr[] = {
                        curCSV.getString(0),
                        curCSV.getString(1),
                        curCSV.getString(2),
                        curCSV.getString(3),
                        curCSV.getString(4),
                        curCSV.getString(5),
                        curCSV.getString(6),
                        curCSV.getString(7),
                        curCSV.getString(8)
                };
                csvWrite.writeNext(arrStr);
            }
            Log.i("Export", "Wrote CSV file.");

            csvWrite.close();
            curCSV.close();
            //EncryptFile();
            UploadFile(FILE_NAME);
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }


    /*File upload. May edit this to be ssl instead of bothering with encryption above.
    *SSL requires a server with a valid security certificate. Using HTTP alone for now for testing purposes.
     */
    public void UploadFile(String filename){
        try {
            // Set your file path here
            FileInputStream fstrm = new FileInputStream(Environment.getExternalStorageDirectory() + File.separator +FOLDER_NAME+File.separator +filename);

            // Set your server page url (and the file title/description)
            HttpFileUpload hfu = new HttpFileUpload(FILE_DESTINATION, filename, "EMA App data file.");

            hfu.execute(fstrm);

        } catch (FileNotFoundException e) {
            Log.e("Upload", "File not found!", e);
        }
    }



/*
    FILE ENCRYPTION
    I HAVE NO IDEA WHAT I'M DOING
    TODO: LOOK UP AES, SHA1PRNG, CIPHER, UTF-8
    If I just use SSL then I don't need to bother with the encryption shit.

 */



    public static byte[] generateKey(String password) throws Exception
    {
        byte[] keyStart = password.getBytes("UTF-8");   //Gets bytes of pw string
        KeyGenerator kgen = KeyGenerator.getInstance("AES"); //instantiates a key generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto"); //instantiates a secure random with "SHA1PRNG" as the random number algorithm "Crypto" as provider
        sr.setSeed(keyStart); //sets secure random's seed to byte array of pw string
        kgen.init(128, sr); //starts keygen at 128 bits, secureRandom
        SecretKey skey = kgen.generateKey(); //generates key from key generator.
        return skey.getEncoded();
    }

    public static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }

    public void EncryptFile(){
        File myFile = new File(Environment.getExternalStorageDirectory() + File.separator + FOLDER_NAME, FILE_NAME);

        try {
            byte[] fileBytes = FileUtils.readFileToByteArray(myFile);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myFile+"_enc"));
            byte[] yourKey = generateKey(cryptoPass);
            fileBytes = encodeFile(yourKey, fileBytes);
            bos.write(fileBytes);
            bos.flush();
            bos.close();
        }catch(FileNotFoundException e){
            Log.e("Encrypt", "File not found!", e);
        }catch(IOException e){
            Log.e("Encrypt", "IO Exception!", e);
        }catch(Exception e){
            Log.e("Encrypt", "Exception!?", e);
        }
    }




}