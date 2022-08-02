package com.btcex.sdk.ws;

import java.util.concurrent.CountDownLatch;

public class WsContext {



    private CountDownLatch countDownLatch;


    private String result;

    public WsContext(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


}
