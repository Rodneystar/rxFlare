package modules;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ModManager {

    TargetModule activeModule;
    Switchable switchable;
    private Disposable disposable;
    private Observable<ReceivesHeatingInstruction> modList;
    private Scheduler sched;

    public ModManager() {
        this(new DefaultLoggingSwitchable());
    }

    public ModManager(Switchable switchable) {
      this(switchable, Schedulers.io());
    }

    public ModManager(Switchable switchable, Scheduler sched) {
        this.sched = sched;
        this.switchable = switchable;
        modList = Observable.just(new OnMod(), new OffMod());
        setActiveMod(TargetModule.OFF);
    }



    public void setActiveMod(TargetModule activeMod) {
        if( this.activeModule != activeMod ) {
            this.activeModule = activeMod;
            reSubSwitchableToNewMod();
        }
    }

    private void reSubSwitchableToNewMod() {
        if(disposable!=null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = modList.flatMap( ml -> ml.observe() )
                .filter( s -> s.getSource().equals(activeModule))
                .subscribeOn(sched)
                .subscribe( e -> {

                    System.out.println("in modManSubHandler ------ " + e.getSource().name());
                    if(e.getShouldBeOn()) {
                        switchable.on();
                    } else {
                        switchable.off();
                    }
                }, e-> e.printStackTrace());
    }

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

    private class OnMod implements ReceivesHeatingInstruction {
        @Override
        public Maybe<JsonObject> receiveInstruction(Single<JsonObject> instruction) {
            return Maybe.empty();
        }

        @Override
        public Observable<SwitchEvent> observe() {
            return Observable.just(new SwitchEvent( TargetModule.ON, true));
        }
    }
    private class OffMod implements ReceivesHeatingInstruction {
        @Override
        public Maybe<JsonObject> receiveInstruction(Single<JsonObject> instruction) {
            return Maybe.empty();
        }

        @Override
        public Observable<SwitchEvent> observe() {
            return Observable.just(new SwitchEvent( TargetModule.OFF, false));
        }
    }
}
