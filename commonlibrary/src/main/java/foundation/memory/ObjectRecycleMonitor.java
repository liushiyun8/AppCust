package foundation.memory;



import java.lang.ref.WeakReference;
import java.util.ArrayList;

import foundation.util.ThreadUtils;

/**
 * Created by alfred on 15/7/1.
 * <p/>
 * 对象生命周期监控
 */
public class ObjectRecycleMonitor<T> {

    private static class MonitorObject<T> {
        public String tag;
        public String description;
        public WeakReference<T> weakReference;
    }

    private ArrayList<MonitorObject<T>> _monitorObjects = new ArrayList<>();
    private boolean _stop = false;
    private boolean _started = false;
    private final int ScanTimeInterval = 1000;
    private final int PrintNotRecycledObjectTimeInterval = 1000 * 10;
    private String tagName;

    public ObjectRecycleMonitor(String tag) {
        tagName = tag;
    }

    public synchronized void addObject(T t, String tag, String description) {

        // 添加对象监控

        MonitorObject<T> monitorObject = new MonitorObject<>();
        monitorObject.tag = tag;
        monitorObject.description = description;
        monitorObject.weakReference = new WeakReference<>(t);

        _monitorObjects.add(monitorObject);
    }

    public synchronized void printNotRecycleObjects() {

        for (int i = _monitorObjects.size() - 1; i >= 0; i--) {

            MonitorObject<T> item = _monitorObjects.get(i);

            if (item.weakReference.get() != null) {
                // 被监控对象内存未回收
            }
        }
    }

    private synchronized void removeRecycledObjects() {

        for (int i = _monitorObjects.size() - 1; i >= 0; i--) {

            MonitorObject<T> item = _monitorObjects.get(i);

            if (item.weakReference.get() == null) {

                // 被监控对象内存已回收

                _monitorObjects.remove(i);
            }
        }
    }

    private synchronized void printNotRecycleCount() {
        // 打印目前尚未释放的资源对象数
    }

    public void startMonitor() {

        ThreadUtils.runOnWorkThread(new Runnable() {
            @Override
            public void run() {

                int timeInterval = 0;

                while (!_stop) {
                    System.gc();
                    removeRecycledObjects();
                    printNotRecycleCount();
                    ThreadUtils.sleep(ScanTimeInterval);

                    timeInterval += ScanTimeInterval;
                    if (timeInterval >= PrintNotRecycledObjectTimeInterval) {
                        printNotRecycleObjects();
                        timeInterval = 0;
                    }
                }
            }
        });
    }

    public void stopMonitor() {

        if (!_started)
            return;

        _stop = true;

        while (!_started) {
            ThreadUtils.sleep(ScanTimeInterval);
        }
    }
}
