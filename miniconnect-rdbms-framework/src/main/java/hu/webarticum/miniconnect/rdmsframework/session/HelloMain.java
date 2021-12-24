package hu.webarticum.miniconnect.rdmsframework.session;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.util.data.ImmutableList;

public class HelloMain {

    public static void main(String[] args) throws IOException {
        try (MiniSession session = new FakeFrameworkSession()) {
            MiniResult result = session.execute("Hello");
            if (!result.success()) {
                System.out.println("oops");
                System.out.println(result.error().message());
            } else {
                System.out.println("OK");
                for (ImmutableList<MiniValue> row : result.resultSet()) {
                    for (MiniValue value : row) {
                        System.out.print(value.contentAccess().get().toString()); // FIXME
                        System.out.print(" | ");
                    }
                    System.out.println();
                }
            }
        }
    }
    
}
