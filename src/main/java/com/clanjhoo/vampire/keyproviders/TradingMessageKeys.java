package com.clanjhoo.vampire.keyproviders;


public enum TradingMessageKeys implements KeyProvider {
    SELF("trading.self"),
    NOT_CLOSE("trading.notClose"),
    OFFER_OUT("trading.offerOut"),
    OFFER_IN("trading.offerIn"),
    ACCEPT_HELP("trading.acceptHelp"),
    ACCEPT_NONE("trading.acceptNone"),
    LACKING_OUT("trading.lackingOut"),
    LACKING_IN("trading.lackingIn"),
    TRANSFER_OUT("trading.transferOut"),
    TRANSFER_IN("trading.transferIn"),
    SEEN("trading.seen")
    ;

    TradingMessageKeys(String key) {
        this.key = key;
    }

    private final String key;

    @Override
    public String getKey() {
        return key;
    }
}
