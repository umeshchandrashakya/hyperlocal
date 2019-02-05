package com.hyperlocal.app.ui.registration.countrypicker;

/**
 * @Author ${Umesh} on 12/04/18.
 */
public class AlphabetItem {

    public int position;
    public String word;
    public boolean isActive;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public AlphabetItem(int pos, String word, boolean isActive) {
        this.position = pos;
        this.word = word;
        this.isActive = isActive;
    }
}
