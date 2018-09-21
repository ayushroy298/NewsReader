package com.example.ayushroy.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static android.provider.Contacts.SettingsColumns.KEY;
import static java.sql.Types.INTEGER;
import static java.sql.Types.VARCHAR;
import static java.text.Collator.PRIMARY;

public class MainActivity extends AppCompatActivity {

    public class DownloadJson extends AsyncTask<String ,Void ,String>
    {

        @Override
        protected String doInBackground(String... urls)
        {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try
            {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data =reader.read();
                while(data!=-1)
                {
                    char current=(char) data;
                    result+=current;
                    data =reader.read();
                }
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "Failed";
            }

        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            try
            {
                Log.i("Text New", s);
                SQLiteDatabase myDatabase=MainActivity.this.openOrCreateDatabase("New", MODE_PRIVATE, null);
                myDatabase.execSQL("CREATE TABLE IF NOT EXISTS newtable(name VARCHAR,url VARCHAR, id INTEGER PRIMARY KEY)");
                JSONObject jsonObject=new JSONObject(s);
                String tit=jsonObject.getString("title");
                String u=jsonObject.getString("url");

                //String sql="INSERT INTO newtable (name, url) VALUES (?,?)";
                //SQLiteStatement statement=myDatabase.compileStatement(sql);
                //statement.bindString(1,tit);
                //statement.bindString(2,u);
                //statement.execute();

                Cursor c=myDatabase.rawQuery("SELECT * FROM newtable",null);
                int tIndex=c.getColumnIndex("name");
                int uIndex=c.getColumnIndex("url");
                c.moveToFirst();
                while (c!=null)
                {
                    //Log.i("Title",c.getString(tIndex));
                    newsContent.add(c.getString(tIndex));
                    newsURL.add(c.getString(uIndex));
                    arrayAdapter.notifyDataSetChanged();
                    //Log.i("URL",c.getString(uIndex));
                    c.moveToNext();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent intent=new Intent(getApplicationContext(),WebViewActivity.class);
                    intent.putExtra("URL",newsURL.get(position));
                    Log.i("URL intent",newsURL.get(position));
                    startActivity(intent);
                }
            });
        }
    }



    public class DownloadTask extends AsyncTask<String ,Void ,String>
    {

        @Override
        protected String doInBackground(String... urls)
        {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try
            {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data =reader.read();
                while(data!=-1)
                {
                    char current=(char) data;
                    result+=current;
                    data =reader.read();
                }
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "Failed";
            }

        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            splitString=s.split(", ");
            DownloadJson json=new DownloadJson();

            try
            {
                    //Log.i("TEXT",splitString[i]);
                    json.execute("https://hacker-news.firebaseio.com/v0/item/"+ splitString[5] +".json?print=pretty");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    static String[] splitString;
    ListView listView;
    ArrayList<String> newsContent;
    ArrayList<String> newsURL;
    ArrayAdapter<String> arrayAdapter;
    int n=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadTask task=new DownloadTask();
        listView=findViewById(R.id.newsListView);
        newsContent=new ArrayList<>();
        newsURL=new ArrayList<>();
        arrayAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,newsContent);
        listView.setAdapter(arrayAdapter);
        String result;
        result="https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
        try
        {

                task.execute(result).get();
                n++;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }
}
