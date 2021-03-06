package jheison.cronometro;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private TextView TvwPlacarCasa, TvwPlacarVisitante, TvwCronometro;
    private long MiliSegundo, Mili = 0, Milipausado = 0;
    private Runnable CronometroCrescente, CronometroDecrecente;
    private Handler CronometroHandler;
    private MediaPlayer Beep;
    private Boolean FirstClick = true;
    private Timer TimerBeep;
    private TimerTask TimerBeepTask;
    private Animation PiscarTvw;

    public static long MiliFinal = 10000;
    public static boolean EstadoCronometro = false, Crescente = true;
    public static int PlacarCasa = 0, PlacarVisitante = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PiscarTvw = new AlphaAnimation(0.0f, 1.0f);
        PiscarTvw.setDuration(500);
        PiscarTvw.setStartOffset(20);
        PiscarTvw.setRepeatMode(Animation.REVERSE);
        PiscarTvw.setRepeatCount(Animation.INFINITE);

        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        TvwPlacarCasa = (TextView) findViewById(R.id.MnTvwPlacarCasa);
        TvwPlacarVisitante = (TextView) findViewById(R.id.MnTvwPlacarVisitante);
        TvwCronometro = (TextView) findViewById(R.id.MnTvwCronometro);

        CronometroHandler = new Handler();

        CronometroCrescente = new Runnable() {
            @Override
            public void run() {
                Mili = System.currentTimeMillis() - MiliSegundo;
                TvwCronometro.setText(FormatarTempo(Mili));

                if (Mili < MiliFinal) {
                        CronometroHandler.post(CronometroCrescente);

                } else {
                    EstadoCronometro = !EstadoCronometro;
                    FirstClick = true;
                    TvwCronometro.startAnimation(PiscarTvw);
                    Mili = 0;
                }
            }
        };

        CronometroDecrecente = new Runnable() {
            @Override
            public void run() {
                Mili = MiliFinal - (System.currentTimeMillis() - MiliSegundo);
                TvwCronometro.setText(FormatarTempo(Mili));

                if(Mili > 0){
                    CronometroHandler.post(CronometroDecrecente);
                } else {
                    EstadoCronometro = !EstadoCronometro;
                    FirstClick = true;
                    TvwCronometro.startAnimation(PiscarTvw);
                    Mili = 0;
                }
            }
        };


        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (view.getId() == R.id.MnTvwPlacarCasa) {
                    PlacarCasa++;
                    TvwPlacarCasa.setText(String.format("%02d", PlacarCasa));
                } else {
                    PlacarVisitante++;
                    TvwPlacarVisitante.setText(String.format("%02d", PlacarVisitante));
                }

                vibrator.vibrate(300);
                return false;
            }
        };

        TvwPlacarCasa.setOnLongClickListener(onLongClickListener);
        TvwPlacarVisitante.setOnLongClickListener(onLongClickListener);

        TvwCronometro.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!EstadoCronometro) {
                    MiliSegundo = (FirstClick) ? System.currentTimeMillis() : MiliSegundo;
                    InicialTimer(MiliFinal);

                    if (Crescente) {
                        CronometroHandler.post(CronometroCrescente);
                    } else {
                        CronometroHandler.post(CronometroDecrecente);
                    }

                    FirstClick = false;
                }

                EstadoCronometro = !EstadoCronometro;
                vibrator.vibrate(300);
                return false;
            }
        });
    }

    public void onClickScreen(View view) {
        if (Beep != null) {
            TimerBeep.cancel();
            TimerBeep = null;
            Beep.release();
            Beep = null;
            TvwCronometro.clearAnimation();
        }
    }

    private void InicialTimer(long delay) {
        TimerBeep = new Timer();

        TimerBeepTask = new TimerTask() {

            @Override
            public void run() {
               PlayBeep();
            }
        };

        TimerBeep.schedule(TimerBeepTask, delay, 1000);
    }

    private void PlayBeep() {
        if (Beep == null) {
            Beep = MediaPlayer.create(this, R.raw.beep);
            Beep.setScreenOnWhilePlaying(true);
            Beep.start();

        } else {
            Beep.start();
        }
    }

    private void StopBeep () {
        if (TimerBeep != null) {
            TimerBeep.cancel();
            TimerBeep = null;
        }
        if (Beep != null) {
            Beep.release();
            Beep = null;
        }
    }

    private void ResetarPlacar() {
        PlacarCasa = 0;
        PlacarVisitante = 0;
        TvwPlacarCasa.setText("00");
        TvwPlacarVisitante.setText("00");
    }

    private void ResetarCronometro() {
        if (Crescente) {
            CronometroHandler.removeCallbacks(CronometroCrescente);
            TvwCronometro.setText(FormatarTempo(0));
        } else {
            CronometroHandler.removeCallbacks(CronometroDecrecente);
            TvwCronometro.setText(FormatarTempo(MiliFinal));
        }

        FirstClick = true;
        EstadoCronometro = false;
        StopBeep();

    }

    private String FormatarTempo(long mili){
        long hora = (mili / (1000 * 60 * 60)) % 24;
        long minutos = (mili / (1000 * 60)) % 60;
        long segundos = (mili / (1000)) % 60;

        return String.format("%02d:%02d:%02d", hora, minutos, segundos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MnMaCronometro:
                ResetarCronometro();
                break;
            case R.id.MnMaPlacar:
                ResetarPlacar();
                break;
            case R.id.MnMaConfig:
                Intent intent = new Intent(this, Configuracoes.class);
                startActivityForResult(intent, 0);
                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Crescente) {
            TvwCronometro.setText(FormatarTempo(MiliFinal));
        }

        TvwPlacarCasa.setText(String.format("%02d", PlacarCasa));
        TvwPlacarVisitante.setText(String.format("%02d", PlacarVisitante));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopBeep();
    }
}