package modules;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

public interface ReceivesHeatingInstruction {

    Maybe<JsonObject> receiveInstruction(Single<JsonObject> instruction);
    Observable<SwitchEvent> observe();
}
