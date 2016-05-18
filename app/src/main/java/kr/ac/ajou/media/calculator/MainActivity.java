package kr.ac.ajou.media.calculator;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = (GridView)findViewById(R.id.gridView);

        // Activity onDestroy()시 저장된 데이터 복구(가로모드 세로모드 전환시)
        if(savedInstanceState != null)
        {
            adapter = new GridAdapter(this,
                    String.valueOf(savedInstanceState.get("process")),
                    String.valueOf(savedInstanceState.get("result")),
                    (double)savedInstanceState.get("memory"),
                    (double)savedInstanceState.get("temp"),
                    (boolean)savedInstanceState.get("clearFlag"),
                    (boolean)savedInstanceState.get("rootFlag"),
                    (boolean)savedInstanceState.get("operatorFlag"),
                    (int)savedInstanceState.get("rootSize"),
                    String.valueOf(savedInstanceState.get("lastOperator")));
        }
        else
            adapter = new GridAdapter(this);
        gridView.setAdapter(adapter);
    }

    // Activiyt onDestroy()시 Data 저장
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("process", adapter.getProcess().getText().toString());
        outState.putString("result", adapter.getResult().getText().toString());
        outState.putDouble("memory", adapter.getMemory().doubleValue());
        outState.putDouble("temp", adapter.getTemp().doubleValue());
        outState.putBoolean("clearFlag", adapter.isClearFlag());
        outState.putBoolean("rootFlag", adapter.isRootFlag());
        outState.putBoolean("operatorFlag", adapter.isOperatorFlag());
        outState.putInt("rootSize", adapter.getRootSize());
        outState.putString("lastOperator", adapter.getLastOperator());
    }
}
