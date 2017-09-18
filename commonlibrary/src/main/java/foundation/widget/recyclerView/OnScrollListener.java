package foundation.widget.recyclerView;


/**
 * Created by lzx on 2016/1/5.
 * RecyclerView
 */
public interface OnScrollListener {

    void onScrollUp();//scroll down to up

    void onScrollDown();//scroll from up to down

    void onBottom();//load next page

    void onScrolled(int distanceX, int distanceY);// moving state,you can get the move distance

    void onScrollStateChanged(int newState);// scrolling state,you can get the move distance
}
