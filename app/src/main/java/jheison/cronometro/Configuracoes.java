package jheison.cronometro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class Configuracoes extends AppCompatActivity {
    private EditText EdtMinutos, EdtSegundo, EdtHora, EdtPlacarCasa, EdtPlacarVisitante;
    private RadioButton RadCrescente, RadDecrescente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracoes);

        EdtMinutos = (EditText) findViewById(R.id.CfEdtMinutos);
        EdtSegundo = (EditText) findViewById(R.id.CfEdtSegundos);
        EdtHora = (EditText) findViewById(R.id.CfEdtHora);
        EdtPlacarCasa = (EditText) findViewById(R.id.CfeEdtCasa);
        EdtPlacarVisitante = (EditText) findViewById(R.id.CfEdtVisitante);
        RadCrescente = (RadioButton) findViewById(R.id.RadCrescente);
        RadDecrescente = (RadioButton) findViewById(R.id.RadDecrescente);

        if (MainActivity.PlacarCasa != 0) {
            EdtPlacarCasa.setText(String.valueOf(MainActivity.PlacarCasa));
        }
        if (MainActivity.PlacarVisitante != 0) {
            EdtPlacarVisitante.setText(String.valueOf(MainActivity.PlacarVisitante));
        }

        if(MainActivity.Crescente){
            RadCrescente.setChecked(true);
        } else {
            RadDecrescente.setChecked(true);
        }
    }

    private void Salvar() {
        String hora = (EdtHora.getText().toString().equals("")) ? "0" : EdtHora.getText().toString();
        String minutos = (EdtMinutos.getText().toString().equals("")) ? "0" : EdtMinutos.getText().toString();
        String segundo = (EdtSegundo.getText().toString().equals("")) ? "0" : EdtSegundo.getText().toString();
        String PlacarCasa = (EdtPlacarCasa.getText().toString().equals("")) ? "0" : EdtPlacarCasa.getText().toString();
        String PlacarVisitante = (EdtPlacarVisitante.getText().toString().equals("")) ? "0" : EdtPlacarVisitante.getText().toString();
        Boolean crescente = RadCrescente.isChecked();

        MainActivity.MiliFinal = (Long.parseLong(hora) * 60 * 60 * 1000 ) + (Long.parseLong(minutos) * 60000) + (Long.parseLong(segundo) * 1000);
        MainActivity.Crescente = crescente;
        MainActivity.PlacarCasa = Integer.parseInt(PlacarCasa);
        MainActivity.PlacarVisitante = Integer.parseInt(PlacarVisitante);

        if (MainActivity.MiliFinal == 0 && MainActivity.Crescente) {
            MainActivity.MiliFinal = 1000;
        }

        setResult(0);
        finish();
    }

    public void onClickRadio (View view){
        if (view.getId() == R.id.RadCrescente) {
            RadDecrescente.setChecked(false);
        } else {
            RadCrescente.setChecked(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.configuracoes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MnCfSalvar:
               Salvar();
            break;
        }

        return true;
    }

}
