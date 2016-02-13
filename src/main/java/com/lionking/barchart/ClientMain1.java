package com.lionking.barchart;

/**
 * Created by nzikos on 13/2/2016.
 */

import com.barchart.feed.api.Agent;
import com.barchart.feed.api.MarketObserver;
import com.barchart.feed.api.Marketplace;
import com.barchart.feed.api.connection.Connection;
import com.barchart.feed.api.model.data.Market;
import com.barchart.feed.api.model.data.Trade;
import com.barchart.feed.client.provider.BarchartMarketplace;


public class ClientMain1 {

    public static void main(final String[] args) throws Exception {

        String username = args[0];
        String password = args[1];

        // Create the client
        Marketplace feed = new BarchartMarketplace(username, password);

        // Connect and start event reactor
        feed.startup();


        Connection.Monitor listener = new Connection.Monitor() {
            public void handle(Connection.State state, Connection connection) {
                System.out.println(state.name());
            }
        };

        feed.bindConnectionStateListener(listener);
        Thread.sleep(3000);
        System.out.println("ok");


        // Create an event handler
        MarketObserver<Trade> observer = new MarketObserver<Trade>() {
            public void onNext(Trade trade) {
                System.out.println(trade.instrument().symbol() + "=" + trade.price());
            }
        };

        // Subscribe to trade events for GOOG
        Agent agent = feed.subscribe(Trade.class, observer, "GOOG");
        //Agent agent = feed.subscribe(Trade.class, observer, Exchanges.fromName("NYSE"));

        // Block for processing here...

        Thread.sleep(2000);

        Market market = feed.snapshot("GOOG");
        System.out.println(" "+ market.lastPrice().price().asDouble());

        // Cancel subscription
        agent.terminate();

        // Disconnect feed client
        feed.shutdown();
    }
}
