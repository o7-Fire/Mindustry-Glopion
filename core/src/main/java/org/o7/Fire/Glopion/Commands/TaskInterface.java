/*******************************************************************************
 * Copyright 2021 Itzbenz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/* o7 Inc 2021 Copyright

  Licensed under the o7 Inc License, Version 1.0.1, ("the License");
  You may use this file but only with the License. You may obtain a
  copy of the License at
  
  https://github.com/o7-Fire/Mindustry-Ozone/Licenses
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the license for the specific language governing permissions and
  limitations under the License.
*/

package org.o7.Fire.Glopion.Commands;

import Atom.Reflect.Reflect;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Queue;
import mindustry.Vars;
import mindustry.gen.Player;
import mindustry.world.Tile;
import org.o7.Fire.Glopion.Internal.Shared.WarningHandler;
import org.o7.Fire.Glopion.Module.Module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Future;

public class TaskInterface implements Module {
    public static final HashMap<Integer, Queue<Future<?>>> taskQueue = new HashMap<>();
    
    public static void addTask(Future<?> task) {
        addTask(task, Vars.player);
    }
    
    
    public static void addTask(Future<?> task, Player vars) {
        if (taskQueue.get(vars.id) == null) taskQueue.put(vars.id, new Queue<>());
        taskQueue.get(vars.id).addLast(task);
        //Log.debug("Task: " + task.getName() + " has been added to queue : " + taskQueue.get(vars.id).size);
    }
    
    public static void setMov(Position targetTile, Player player) {
        Vec2 vec = new Vec2();
        if (player.unit() == null) return;
        vec.trns(player.unit().angleTo(targetTile), player.unit().type().speed);
        if (Reflect.debug && !Vars.disableUI && Vars.ui.scriptfrag.shown()){
            if (Vars.ui.scriptfrag.shown()){
                Vars.ui.scriptfrag.addMessage("Ozone-AI DriveX: " + vec.x);
                Vars.ui.scriptfrag.addMessage("Ozone-AI DriveY: " + vec.y);
            }
        }
        player.unit().moveAt(vec);
    }
    
    public static ArrayList<Tile> getNearby(Tile tile, int rotation, int range) {
        ArrayList<Tile> tiles = new ArrayList<>();
        if (rotation == 0){
            for (int i = 0; i < range; i++) {
                tiles.add(Vars.world.tile(tile.x + 1 + i, tile.y));
            }
        }else if (rotation == 1){
            for (int i = 0; i < range; i++) {
                tiles.add(Vars.world.tile(tile.x, tile.y + 1 + i));
            }
        }else if (rotation == 2){
            for (int i = 0; i < range; i++) {
                tiles.add(Vars.world.tile(tile.x - 1 - i, tile.y));
            }
        }else if (rotation == 3){
            for (int i = 0; i < range; i++) {
                tiles.add(Vars.world.tile(tile.x, tile.y - 1 - i));
            }
        }
        return tiles;
    }
    
    public static Vec2 getCurrentPos() {
        return new Vec2(Vars.player.x, Vars.player.y);
    }
    
    public static Vec2 getCurrentPos(Tile t) {
        return new Vec2(t.x * 8, t.y * 8);
    }
    
    public static Tile getCurrentTilePos() {
        return Vars.player.tileOn();
    }
    
    public static Vec2 getCurrentTilePos(Vec2 ref) {
        return new Vec2(Math.round(ref.x / 8), Math.round(ref.y / 8));
    }
    
    public static boolean samePos(Position pos1, Position pos2, boolean tolerance) {
        if (tolerance)
            return Math.round(pos1.getX()) == Math.round(pos2.getX()) && Math.round(pos1.getY()) == Math.round(pos2.getY());
        else return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY();
    }
    
    public void init() {
    
    }
    
    public void update() {
        if (taskQueue.isEmpty()) return;
        Collection<Queue<Future<?>>> queues = taskQueue.values();
        for (Queue<Future<?>> queue : queues) {
            
            if (queue.isEmpty()) continue;
            try {
                if (!queue.first().isDone()){
                    queue.first().get();
                    continue;
                }
                queue.removeFirst();
            }catch(Exception t){
                WarningHandler.handleMindustry(t);
            }
            
        }
    }
    
    public void reset() {
        taskQueue.clear();
    }
    
    
}
