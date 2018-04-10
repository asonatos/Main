package com.example.bis.affectedarea;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by BIS on 22.01.2018.
 */
public class PoisonListActivity extends AppCompatActivity {
    ListView listView;
    String[] poisonNames;
    @Override
    public void onCreate(Bundle savedInstantState)
    {
        super.onCreate(savedInstantState);
        setContentView(R.layout.poisol_list_activity);

        Button btn = (Button)findViewById(R.id.poison_listActivity_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray sbArray = listView.getCheckedItemPositions();
                ArrayList<Integer> poisonList = new ArrayList<>();
                for (int i = 0; i < sbArray.size(); i++) {
                    int key = sbArray.keyAt(i);
                    if (sbArray.get(key))
                        poisonList.add(key);
                }
                Intent intent = new Intent();
                intent.putExtra("names", poisonList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        listView = (ListView)findViewById(R.id.poison_listActivity_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<CharSequence> adapter =ArrayAdapter.createFromResource(this,R.array.names,android.R.layout.simple_list_item_multiple_choice);
        listView.setAdapter(adapter);
        poisonNames = getResources().getStringArray(R.array.names);
    }
}
