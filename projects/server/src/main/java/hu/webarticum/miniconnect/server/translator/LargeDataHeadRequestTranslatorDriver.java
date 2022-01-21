package hu.webarticum.miniconnect.server.translator;

import hu.webarticum.miniconnect.messenger.message.Message;
import hu.webarticum.miniconnect.messenger.message.request.LargeDataHeadRequest;
import hu.webarticum.miniconnect.server.HeaderData;
import hu.webarticum.miniconnect.transfer.Packet;
import hu.webarticum.miniconnect.util.data.ByteString;

public class LargeDataHeadRequestTranslatorDriver implements TranslatorDriver {

    @Override
    public Message decode(HeaderData header, ByteString payload) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Packet encode(Message message) {
        LargeDataHeadRequest largeDataHeadRequest = (LargeDataHeadRequest) message;
        HeaderData header = HeaderData.ofMessage(message);
        
        // TODO variableName;
        // TODO length;

        // TODO Auto-generated method stub
        return null;
    }

}
