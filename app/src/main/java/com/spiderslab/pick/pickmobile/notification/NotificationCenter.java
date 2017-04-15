package com.spiderslab.pick.pickmobile.notification;

import com.spiderslab.pick.pickmobile.Order;

public enum NotificationCenter implements WorkerThread.Callback {
    INSTANCE;

    private Listener listener = null;

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public void unregisterListener() {
        this.listener = null;
    }

    private void notifyToListener() {
        if (null == listener) {
            return;
        }
        listener.onOrderReceived();
    }

    @Override
    public void onOrdersAvailable(Order newOrder, Order availableOrder) {

    }

    public interface Listener {
        void onOrderReceived();
    }
}
