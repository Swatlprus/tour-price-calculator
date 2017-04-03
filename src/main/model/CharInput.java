package main.model;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

/** Класс описывающий поток символов из клавиатуры */
public class CharInput {
    public static final int FROM = 0;
    public static final int TO = 1;

    private int type;
    private PublishSubject<CharInput> charStream;
    private String fromString;
    private String toString;

    public CharInput() {
        fromString = "";
        toString = "";
        charStream = PublishSubject.create();
    }

    public int getType() {
        return type;
    }

    public String getInput(){
        switch (type){
            case FROM: return fromString;
            case TO: return toString;
            default: return null;
        }
    }

    /** Получение потока с отсрочкой в 1 сек (позволяет пропускать быстро вводимые символы вводимые) */
    public Observable<CharInput> getCharStream() {
        return charStream.debounce(1000, TimeUnit.MILLISECONDS);
    }

    /** Добавление символа к соответсвующей результирующей строке
     * @param character символ
     * @param type тип строки
     */
    public void event(String character, int type) {
        this.type = type;
        if (type == FROM)
            fromString = streamEvent(character, fromString);
        else if (type ==TO)
            toString = streamEvent(character, toString);
    }

    private String streamEvent(String character, String resultString){
        if (!character.equals("\b") && !character.equals("")) {
            resultString += character;
            // Отправка обьекта в поток
            charStream.onNext(this);
        } else if (resultString.length() > 0)
            resultString = resultString.substring(0, resultString.length() - 1);

        return resultString;
    }

    /** Сброс результирующей строки
     * @param type тип строки
     */
    public void reset(int type){
        switch (type){
            case FROM: fromString = ""; break;
            case TO: toString = ""; break;
        }
    }

}
