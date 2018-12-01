package modules;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

public interface ReceivesHeatingInstruction {

    public Maybe<JsonObject> receiveInstruction(Single<JsonObject> instruction);
}
