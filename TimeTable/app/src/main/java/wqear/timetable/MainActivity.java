package wqear.timetable;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView lvSimple;
    SimpleAdapter sAdapter;
    Ask gettimetable;
    ArrayList<Map<String, Object>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.bgAB));
        Intent intent = getIntent();
        gettimetable = new Ask();
        gettimetable.execute(getResources().getString(R.string.timetableURL)+intent.getStringExtra("dateStart")+"&to="+intent.getStringExtra("dateEnd")+"&group="+intent.getStringExtra("group")+"&prep="+intent.getStringExtra("lecturer"));
        buildList();

    }
    private void buildList()
    {
        try {
            if(gettimetable.get()==null)
            {
                TextView tv=(TextView) findViewById(R.id.textView);
                tv.setText(getResources().getString(R.string.error_string));
            }
            else {
                data = new ArrayList<Map<String, Object>>(
                        gettimetable.get().length);
                Map<String, Object> m;

                for (String str : gettimetable.get()) {
                    String[] buf = str.split(";");
                    m = new HashMap<String, Object>();
                    m.put("date", buf[0]);
                    m.put("time", buf[1]);
                    m.put("subject", buf[2]);
                    m.put("group", buf[3]);
                    m.put("place", buf[4]);
                    m.put("prep", buf[5]);
                    m.put("type", buf[6]);
                    data.add(m);
                }

                String[] from = {"date", "time", "subject", "group", "place", "prep", "type"};
                int[] to = {R.id.date, R.id.time, R.id.subject, R.id.group, R.id.place, R.id.prep, R.id.type};
                sAdapter = new SimpleAdapter(this, data, R.layout.listitem,
                        from, to);
                lvSimple = (ListView) findViewById(R.id.listView);
                lvSimple.setAdapter(sAdapter);
            }
        }
        catch (Exception ex){}
    }

    class Ask extends AsyncTask<String, Void, String[]> {

        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            try {
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler rez = new BasicResponseHandler();
                HttpGet http = new HttpGet(params[0]);
                response = (String) hc.execute(http, rez);
            } catch (Exception e) {

            }
            if(response==null||response.contains("Превышен объем запрошеных данных")||response.contains("Расписание не найдено"))
            {
                return null;
            }
            else {
                String[] res = response.split("\n");
                return res;
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
        }
    }
}

