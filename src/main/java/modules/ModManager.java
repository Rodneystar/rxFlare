package modules;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ModManager {

    public ModManager() {
        modList = Observable.empty();
    }

    private Observable<ReceivesHeatingInstruction> modList;

    public Single<JsonArray> order(JsonObject request) {
        return modList
                .flatMap( module -> module.receiveInstruction(Single.just(request))
                                        .toObservable() )
                .toList()
                .map(l -> new JsonArray(l));

    }

    public void addModule(ReceivesHeatingInstruction module) {
        modList = modList.mergeWith(Observable.just(module));
    }
}
