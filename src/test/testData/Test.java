// Copyright ...

import java.util.ArrayList;

/**
 *
 */
public abstract class Test extends ArrayList<String> implements Runnable {

    public static final String CONSTANT = "Text";

    protected Boolean booleanField;

    @Override
    public void run() {
        while (true) {
            if (!booleanField) {
                booleanField = true;
            } else {
                booleanField = false;
            }
        }
    }


}
