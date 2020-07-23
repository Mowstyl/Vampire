package com.clanjhoo.vampire.keyproviders;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum TradingMessageKeys implements MessageKeyProvider {
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
        this.key = MessageKey.of(key);
    }

    private final MessageKey key;

    @Override
    public MessageKey getMessageKey() {
        return key;
    }
}
