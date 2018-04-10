package wqear.timetable;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


/**
 * activity ввода инфы
 */
public class InputScreen extends AppCompatActivity implements View.OnClickListener {
    Button btnOk;
    EditText group;
    Spinner spinner,startDate,endDate;
    MyTask getlecturer,dateMT;
    ArrayAdapter<String> lecturerlist,datelist;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inputscreenview);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.bgAB));

        btnOk=(Button) findViewById(R.id.find);
        btnOk.setOnClickListener(this);

        group=(EditText) findViewById(R.id.groupEd);

        getlecturer=new MyTask();
        dateMT=new MyTask();

        try {

            getlecturer.execute("lecturers");

            lecturerlist = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getlecturer.get());

            dateMT.execute("date");

            datelist = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dateMT.get());
        }
        catch (Exception ex){}

        lecturerlist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        datelist.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);
        startDate=(Spinner) findViewById(R.id.spinner2);
        endDate=(Spinner) findViewById(R.id.spinner3);

        spinner.setAdapter(lecturerlist);
        startDate.setAdapter(datelist);
        endDate.setAdapter(datelist);

        spinner.setSelection(0);
        startDate.setSelection(0);
        endDate.setSelection(0);
    }

    public void onClick(View v)
    {

        String[] pr=spinner.getSelectedItem().toString().split(" ");
        showTable(group.getText().toString(),pr[0]+"%20"+pr[1].substring(0,pr[1].length()-1),startDate.getSelectedItem().toString(),endDate.getSelectedItem().toString());
    }
    private void showTable(String group, String prep,String dateStart, String dateEnd)
    {
        Intent intent =new Intent(this,MainActivity.class);
        intent.putExtra("group",group);

        if (group.isEmpty())
            intent.putExtra("lecturer",prep);
        else
            intent.putExtra("lecturer","");

        intent.putExtra("dateStart",dateStart.substring(0,dateStart.length()-1));
        intent.putExtra("dateEnd",dateEnd.substring(0,dateStart.length()-1));

        startActivity(intent);
    }

    class MyTask extends AsyncTask<String, Void, String[]> {

        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {
            try {
                HttpGet http=null;
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler rez = new BasicResponseHandler();
                if(params[0].equals("lecturers"))
                    http = new HttpGet(getResources().getString(R.string.lecturerURL));
                else
                    http=new HttpGet(getResources().getString(R.string.dateURL));
                //получаем ответ от сервера
                response = (String) hc.execute(http, rez);
            } catch (Exception e) {
                Log.d("InputScreen error",e.toString());
            }
            String[] result=response.split("\n");
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
        }
    }
}
