package com.volt.event.impl.player;

import com.volt.event.types.CancellableEvent;
import lombok.Getter;
import net.minecraft.entity.Entity;

@Getter
public class EventAttack extends CancellableEvent {
    Entity target;
    public EventAttack(Entity target){
        this.target = target;
    }
}
