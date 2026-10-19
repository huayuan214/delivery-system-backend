package com.delivery.delivery;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class ConcurrentGrabTest {

    public static void main(String[] args) throws InterruptedException {
        int riderCount = 10;
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(riderCount);

        for (long riderId = 1; riderId <= riderCount; riderId++) {
            final long id = riderId;
            new Thread(() -> {
                try {
                    latch.await();
                    URL url = new URL("http://localhost:8080/order/grab/1?riderId=" + id);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    int code = conn.getResponseCode();
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream()));
                    String response = reader.readLine();
                    System.out.println("骑手 " + id + " 返回：" + response);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    done.countDown();
                }
            }).start();
        }

        latch.countDown();
        done.await();
        System.out.println("全部请求发送完毕");
    }
}