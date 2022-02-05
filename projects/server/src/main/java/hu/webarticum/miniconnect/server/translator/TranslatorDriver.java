package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.server.MessageEncoder;

interface TranslatorDriver extends MessageEncoder {

    public Message decode(HeaderData headerData, ByteString payload);
    
}
