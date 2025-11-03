/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package i18n;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author jorge
 */
public class Messages {
    private ResourceBundle bundle;

    public Messages(Locale locale) {
        this.bundle = ResourceBundle.getBundle("resources.messages", locale);
    }

    public String get(String key, Object... args) {
        return MessageFormat.format(bundle.getString(key), args);
    }

    public ResourceBundle raw() { return bundle; }
}