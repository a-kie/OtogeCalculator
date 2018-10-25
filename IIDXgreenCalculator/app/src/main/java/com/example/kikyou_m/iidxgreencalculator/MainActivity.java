package com.example.kikyou_m.iidxgreencalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {
    boolean select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonGreen = (Button)findViewById(R.id.buttongreen);
        final Button buttonWhite = (Button)findViewById(R.id.buttonwhite);
        Button buttonSelectLeft = (Button)findViewById(R.id.buttonSelectL);
        Button buttonSelectRight = (Button)findViewById(R.id.buttonSelectR);

        final Switch selectSwitch = (Switch)findViewById(R.id.switchReCalc); //固定選択スイッチ

        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcGre();
                selectSwitch.setChecked(false);
            }
        });

        buttonWhite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                calcSUD();
                selectSwitch.setChecked(true);
            }
        });

        buttonSelectLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectSpinner(-1);
            }
        });
        buttonSelectRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectSpinner(1);
            }
        });
        selectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    select = true; //緑固定
                }else{
                    select = false; //白固定
                }
            }
        });
    }
    private void selectSpinner(int sel){
        Spinner Spinnerhisp = (Spinner)findViewById(R.id.Spinnerhisp); //HI-SPEED

        // Spinnerの選択されているアイテムのIndexを取得
        int idx = Spinnerhisp.getSelectedItemPosition();
        if(sel > 0) {
            if (idx < Spinnerhisp.getCount() - 1) {
                idx++;
            }
        }else{
            if(idx > 0){
                idx--;
            }
        }
        Spinnerhisp.setSelection(idx);

        //再計算
        if(select){
            calcSUD();
        }else{
            calcGre();
        }
        //固定を緑と白選べるようにする
    }

    private void calcSUD(){
        EditText editTextBPM = (EditText)findViewById(R.id.editTextBPM); //BPM
        EditText editTextSudplus = (EditText)findViewById(R.id.editTextSudplus); //SUD+
        EditText editTextgreen = (EditText) findViewById(R.id.editTextgreen); //緑数字
        Spinner Spinnerhisp = (Spinner)findViewById(R.id.Spinnerhisp); //HI-SPEED
        String res;
        //BPMと緑数字入力されている
        if(editTextBPM.length() != 0 && editTextgreen.length() != 0) {
            Calculate calc = new Calculate();
            res = calc.calcWhiteIn(editTextBPM.getText().toString(), (String) Spinnerhisp.getSelectedItem(), editTextgreen.getText().toString());
            editTextSudplus.setText(res);
        }
    }
    private void calcGre(){
        EditText editTextBPM = (EditText)findViewById(R.id.editTextBPM); //BPM
        EditText editTextSudplus = (EditText)findViewById(R.id.editTextSudplus); //SUD+
        EditText editTextgreen = (EditText) findViewById(R.id.editTextgreen); //緑数字
        Spinner Spinnerhisp = (Spinner)findViewById(R.id.Spinnerhisp); //HI-SPEED
        String res;
        //SUD+入力されていない場合0を代入
        if(editTextSudplus.length() == 0) {
            editTextSudplus.setText("0");
        }
        //BPMが空欄でない
        if(editTextBPM.length() != 0) {
            Calculate green = new Calculate();
            res = green.calcGreenIn(editTextBPM.getText().toString(), (String) Spinnerhisp.getSelectedItem(), editTextSudplus.getText().toString());
            editTextgreen.setText(res);
        }
    }
}

class Calculate extends AppCompatActivity{
    static int bpm;
    static int Sudplus;
    static int green;
    static double hs;

    Calculate(){
        //コンストラクタ
    }

    //数値型に変換
    private void parseNumericBPM(String textBPM){
        bpm = Integer.parseInt(textBPM);
    }
    private void parseNumericHISP(String texths){
        hs = Double.parseDouble(texths);
    }
    private void parseNumericSDpl(String textSudplus){
        Sudplus = Integer.parseInt(textSudplus);
    }
    private void parseNumericGreen(String textgreen){
        green = Integer.parseInt(textgreen);
    }

    private void RatetoSPEED(double hisp){
        //段階値を実速度に変換(5th～Lincle)
        if(hisp <= 1.0){
            hs = 1.0 + hisp;
        }else if(hisp < 3){
            hs = hisp + ((3 - hisp)/2);
        }else if(hisp > 3){
            hs = hisp - ((hisp - 3) / 2);
        }
    }
    public String calcGreenIn(String bpm, String hisp, String SDpl){
        parseNumericBPM(bpm);
        parseNumericHISP(hisp);
        RatetoSPEED(hs); //5th~Lincle
        parseNumericSDpl(SDpl);
        return parseSt(calcGreen());
    }
    public String calcWhiteIn(String bpm, String hisp, String gl){
        parseNumericBPM(bpm);
        parseNumericHISP(hisp);
        RatetoSPEED(hs); //5th~Lincle
        parseNumericGreen(gl);
        return parseSt(calcWhite());
    }
    private String parseSt(int res){
        //文字列に変換
        String result = ""+ res;
        return  result;
    }

    private int calcGreen(){
        //計算
        double speed = bpm * hs * (double)1000 / (1000 - Sudplus);
        double s = speed*10;
        s = Math.floor(s); //小数点第2位以下を切り捨て
        s = s/10; //桁を戻す
        //int result = (int)Math.ceil(174000/s); //切り上げ
        int result = (int)Math.round(174000/s); //四捨五入
        return result;
    }
    private int calcWhite(){
        //計算
        int white = 1000 - (int)((green * bpm * hs) / 174);
        int result = white;
        return result;
    }
}
