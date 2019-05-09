package inf112.skeleton.common.utility;

import com.google.gson.Gson;

public class Tools {

    public static final Gson GSON = new Gson();

    public static int coordToIndex(int x, int y, int width){
        return x + width * y;
    }

    public static int coordToIndex(float x, float y, int width) {

        return coordToIndex((int) x, (int) y, width);
    }
}
